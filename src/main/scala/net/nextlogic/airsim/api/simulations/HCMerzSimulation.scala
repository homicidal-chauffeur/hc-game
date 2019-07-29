package net.nextlogic.airsim.api.simulations

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.hcm.HCMerzPlayer
import net.nextlogic.airsim.api.simulators.{SimulationRunner, SimulationSetup, SimulatorSettings}
import net.nextlogic.airsim.api.utils.{Constants, VehicleSettings}

case class HCMerzSimulation(settings: SimulatorSettings) extends Simulation {
  def run(): Unit = {
    val evader = HCMerzPlayer(
      AirSimBaseClient(VehicleSettings(settings.ip, Constants.eVehicle, settings.maxVelocityEvader))
    )
    val pursuer = HCMerzPlayer(
      AirSimBaseClient(VehicleSettings(settings.ip, Constants.pVehicle, settings.maxVelocityPursuer))
    )

    SimulationSetup.setup(evader.vehicle, pursuer.vehicle, settings.captureDistance)
    evader.theta = Math.acos(settings.gamma)
    SimulationRunner.run(evader, pursuer, settings.captureDistance)
  }

}
