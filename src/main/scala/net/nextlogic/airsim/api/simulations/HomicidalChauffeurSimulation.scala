package net.nextlogic.airsim.api.simulations

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.agile.AgileDronePlayer
import net.nextlogic.airsim.api.gameplay.chauffeur.ChauffeurDronePlayer
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.simulators.{SimulationRunner, SimulationSetup}
import net.nextlogic.airsim.api.utils.{Constants, VehicleSettings}

case class HomicidalChauffeurSimulation(settings: SimulatorSettings) extends Simulation {
  def run() {
    val evader = AgileDronePlayer(
      AirSimBaseClient(settings.ip, VehicleSettings(Constants.eVehicle, Evade, settings.maxVelocityEvader))
    )
    val pursuer = ChauffeurDronePlayer(
      AirSimBaseClient(settings.ip, VehicleSettings(Constants.pVehicle, Pursue, settings.maxVelocityPursuer))
    )

    SimulationSetup.setup(evader.vehicle, pursuer.vehicle, settings.captureDistance)
    evader.theta = Math.acos(settings.gamma)

    SimulationRunner.run(evader, pursuer, settings.captureDistance)
  }
}
