package net.nextlogic.airsim.api.simulators

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.simulators.settings.PilotSettings.{Evade, Pursue}
import net.nextlogic.airsim.api.utils.{Constants, Vector3r}

object SimulationSetup {

  def setup(vehicles: Seq[AirSimBaseClient], captureL: Double): Unit = {
    // 2 * captureL * Math.cos(theta_init)
    val evaderInitialPosition = Vector3r( (captureL * 2 * Math.cos(0)).toFloat, 0, Constants.altitude - Constants.planeHeight)
    val pursuerInitialPosition = Vector3r(0, 0, Constants.altitude)

    vehicles.foreach{vehicle =>
      vehicle.confirmConnection()
      println(s"${vehicle.settings.name}: Start Position ${vehicle.getPosition}")

      if (!vehicle.isApiControlEnabled) vehicle.enableApi(true)

//      vehicle.armDisarm(true)

      if (vehicle.isLanded) vehicle.takeoff(Constants.setupWaitTime)
    }

    println("Waiting for drones to take-off...")
    Thread.sleep(5000)

    vehicles.filter(_.settings.actionType == Evade)
      .foreach(v => v.moveToPosition(evaderInitialPosition, Constants.setupVelocity))
    vehicles.filter(_.settings.actionType == Pursue)
      .foreach(v => v.moveToPosition(pursuerInitialPosition, Constants.setupVelocity))

    println("Waiting for the drones to reach the initial position...")

    Thread.sleep(10000)
  }

}
