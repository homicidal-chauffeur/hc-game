package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.{NewPosition, Path}
import net.nextlogic.airsim.api.simulators.actors.RefereeActor.Start
import net.nextlogic.airsim.api.simulators.actors.VisualizerActor._
import net.nextlogic.airsim.api.ui.MainWindow
import net.nextlogic.airsim.api.ui.visualizer.{PathSegment, SimulationPanel}
import net.nextlogic.airsim.api.utils.{Vector3r, VehicleSettings}

import scala.collection.mutable

object VisualizerActor {
  def props(visualizer: Option[SimulationPanel], captureDistance: Double): Props =
    Props(new VisualizerActor(visualizer, captureDistance))
  case object Stop
}

class VisualizerActor(val visualizer: Option[SimulationPanel], val captureDistance: Double) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)
  val positions: mutable.Map[VehicleSettings, Vector3r] = mutable.Map()

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start(startTime) =>
      logger.debug("Starting visualizer...")
      context.become(startedReceive(startTime), discardOld = true)

  }

  def startedReceive(startTime: Long): Receive = {
    case NewPosition(position, orientation, vehicleSettings) =>
      positions.put(vehicleSettings, position)
      visualizer.foreach( _.addSegment(PathSegment(vehicleSettings, position, orientation, System.currentTimeMillis - startTime)) )

    case Path(path, vehicleSettings) =>
      // setPath(vis, pilotType, path)
    case Stop =>
      MainWindow.settings.setEnabled(true)
      logger.debug("Stopping visualizer...")
      context.unbecome()

  }

}

