package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.{NewPosition, Path}
import net.nextlogic.airsim.api.simulators.actors.VisualizerActor._
import net.nextlogic.airsim.api.ui.visualizer.{PathSegment, SimulationPanel}
import net.nextlogic.airsim.api.utils.{Vector3r, VehicleSettings}
import scala.collection.mutable

object VisualizerActor {
  def props(visualizer: Option[SimulationPanel], captureDistance: Double): Props =
    Props(new VisualizerActor(visualizer, captureDistance))
  case object Start
  case object Stop
}

class VisualizerActor(val visualizer: Option[SimulationPanel], val captureDistance: Double) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)
  val positions: mutable.Map[VehicleSettings, Vector3r] = mutable.Map()

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start =>
      logger.debug("Starting visualizer...")
      context.become(startedReceive(), discardOld = true)

  }

  def startedReceive(): Receive = {
    case NewPosition(position, vehicleSettings) =>
      positions.put(vehicleSettings, position)
      visualizer.foreach( _.addSegment(PathSegment(vehicleSettings, position)) )

    case Path(path, vehicleSettings) =>
      logger.debug(s"Path for ${vehicleSettings.name}:")
      logger.debug("\n" + path.map(p => s"(${p.x},${p.y})").mkString(", "))
      // setPath(vis, pilotType, path)
    case Stop =>
      logger.debug("Stopping visualizer...")
      context.unbecome()

  }

}

