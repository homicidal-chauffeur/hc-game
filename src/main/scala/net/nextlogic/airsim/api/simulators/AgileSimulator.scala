package net.nextlogic.airsim.api.simulators

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.agile.AgileDronePlayer
import net.nextlogic.airsim.api.simulations.{SimulationRunner, SimulationSetup}
import net.nextlogic.airsim.api.utils.{Constants, VehicleSettings}

object AgileSimulator extends App {
  val gamma = args(0).toDouble
  val beta = args(1).toDouble

  val eMaxV = gamma * Constants.baseV
  val pMaxV = Constants.baseV

  val evader = AgileDronePlayer(
    AirSimBaseClient(VehicleSettings(Constants.IP, Constants.eVehicle, eMaxV))
  )
  val pursuer = AgileDronePlayer(
    AirSimBaseClient(VehicleSettings(Constants.IP, Constants.pVehicle, eMaxV))
  )

  SimulationSetup.setup(evader.vehicle, pursuer.vehicle)


  SimulationRunner.run(evader, pursuer, gamma, beta)

}
