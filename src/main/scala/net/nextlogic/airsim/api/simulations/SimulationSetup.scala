package net.nextlogic.airsim.api.simulations

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.utils.{Constants, Vector3r}

object SimulationSetup {
  val evaderInitialPosition = Vector3r(0, 0, -20)
  val pursuerInitialPosition = Vector3r(4, 4, -20)

  def setup(evader: AirSimBaseClient, pursuer: AirSimBaseClient): Unit = {
    Array(evader, pursuer).foreach{vehicle =>
      vehicle.confirmConnection()
      if (!vehicle.isApiControlEnabled) vehicle.enableApi(true)

      if (vehicle.isLanded) vehicle.takeoff(Constants.setupWaitTime)
    }

    println("Waiting for drones to take-off...")
    Thread.sleep(10000)

    evader.moveToPosition(evaderInitialPosition, Constants.setupVelocity)
    pursuer.moveToPosition(pursuerInitialPosition, Constants.setupVelocity)

    println("Waiting for the drones to reach the initial position...")

    (0 to Constants.setupWaitTime).foreach{t =>
      println(s"At $t Evader: ${evader.getPosition} Pursuer: ${pursuer.getPosition}")
       Thread.sleep(100)
    }

  }

}
