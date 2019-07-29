package net.nextlogic.airsim.api.simulators

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.utils.{Constants, Vector3r}

object SimulationSetup {

  def setup(evader: AirSimBaseClient, pursuer: AirSimBaseClient, captureL: Double): Unit = {
    // 2 * captureL * Math.cos(theta_init)
    val evaderInitialPosition = Vector3r( (captureL * 2 * Math.cos(0)).toFloat, 0, Constants.altitude - Constants.planeHeight)
    val pursuerInitialPosition = Vector3r(0, 0, Constants.altitude)

    Array(evader, pursuer).foreach{vehicle =>
      vehicle.confirmConnection()
      println(s"${vehicle.settings.name}: Start Position ${vehicle.getPosition}")

      if (!vehicle.isApiControlEnabled) vehicle.enableApi(true)

      vehicle.armDisarm(true)

      if (vehicle.isLanded) vehicle.takeoff(Constants.setupWaitTime)
    }

    println("Waiting for drones to take-off...")
    Thread.sleep(5000)

    evader.moveToPosition(evaderInitialPosition, Constants.setupVelocity)
    pursuer.moveToPosition(pursuerInitialPosition, Constants.setupVelocity)

    println("Waiting for the drones to reach the initial position...")

    Thread.sleep(10000)
  }

}
