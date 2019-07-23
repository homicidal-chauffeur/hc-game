package net.nextlogic.airsim.api.simulations

import net.nextlogic.airsim.api.gameplay.agile.AgileDronePlayer
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionTracker
import net.nextlogic.airsim.api.utils.Constants
import scala.util.control.Breaks._

object SimulationRunner {

  def run(evader: AgileDronePlayer, pursuer: AgileDronePlayer,
          gama: Double, beta: Double): Unit = {

    val captureL: Double = beta * Constants.baseR

    updatePosition(evader, pursuer)

    val relativePosition = RelativePositionTracker(pursuer.tracker, evader.tracker, captureL)

    breakable {
      for (t <- 0 to 60) {
        println("Time " + t)
        evader.evade(relativePosition)
        pursuer.pursue(relativePosition)

        // updatePlot(vis)

        Thread.sleep(100)
        updatePosition(evader, pursuer)

        if (relativePosition.gameOver) break()
      }
    }

    if (relativePosition.gameOver) {
      println(s"Captured ")
    } else {
      println("Escaped...")
    }

    evader.vehicle.hover()
    pursuer.vehicle.hover()
  }

  def updatePosition(evader: AgileDronePlayer, pursuer: AgileDronePlayer): Unit = {
    pursuer.tracker.updatePositionData()
    evader.tracker.updatePositionData()
  }
}
