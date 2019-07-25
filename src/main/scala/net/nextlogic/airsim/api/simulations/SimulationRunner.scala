package net.nextlogic.airsim.api.simulations

import akka.actor.ActorSystem
import net.nextlogic.airsim.api.gameplay.{AirSimBaseClient, DronePlayer}
import net.nextlogic.airsim.api.gameplay.agile.AgileDronePlayer
import net.nextlogic.airsim.api.gameplay.telemetry.{PositionTracker, RelativePositionTracker}
import net.nextlogic.airsim.api.utils.Constants
import net.nextlogic.airsim.visualizer.GlobalField

import scala.util.control.Breaks._

object SimulationRunner {

  def run(evader: DronePlayer, pursuer: DronePlayer, captureL: Double): Unit = {
    val system = ActorSystem("Simulation")

    updatePosition(evader, pursuer)

    val relativePosition = RelativePositionTracker(evader.tracker, pursuer.tracker, captureL)

    val vis = new GlobalField(1600, 20, captureL)

    breakable {
      for (t <- 0 to 30) {
        println("Time " + t)
        evader.evade(relativePosition.relativePosForEvader(evader.theta))
        pursuer.pursue(relativePosition.relativePosForPursuer(pursuer.theta))

        updatePlot(vis, evader, pursuer)

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

    evader.vehicle.reset()
    pursuer.vehicle.reset()
  }

  def updatePosition(evader: DronePlayer, pursuer: DronePlayer): Unit = {
    pursuer.tracker.updatePositionData()
    evader.tracker.updatePositionData()
  }

  def updatePlot(g: GlobalField, evader: DronePlayer, pursuer: DronePlayer): Unit = {
    g.setPursuerState(pursuer.tracker.get2DPos, pursuer.theta)
    g.setEvaderState(evader.tracker.get2DPos, evader.theta)
    //		relativeVis.setEvaderState(pursuer.getCurrentRelativePos(), evader.getTheta() - pursuer.getTheta());
    pursuer.tracker.getLastMovement.foreach(s => g.addPursuerSegment(s))
    evader.tracker.getLastMovement.foreach(s => g.addEvaderSegment(s))
    //		relativeVis.setEvaderPath(pursuer.getRelativeTrajectory());
    g.resetBoundaryForMax()
    g.repaint()
    //		relativeVis.repaint();
  }

}
