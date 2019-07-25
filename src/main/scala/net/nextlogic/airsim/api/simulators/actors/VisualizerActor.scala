package net.nextlogic.airsim.api.simulators.actors

import java.awt.geom.{Line2D, Point2D}

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.{NewPosition, Path}
import net.nextlogic.airsim.api.simulators.actors.PilotActor.PilotType
import net.nextlogic.airsim.api.simulators.actors.VisualizerActor._
import net.nextlogic.airsim.visualizer.GlobalField
import javax.swing.SwingUtilities
import scala.collection.JavaConverters._

import scala.collection.immutable.Queue
import scala.collection.mutable

object VisualizerActor {
  def props(captureDistance: Double): Props = Props(new VisualizerActor(captureDistance))
  case object Start
  case object Stop
}

class VisualizerActor(val captureDistance: Double) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)
  val positions: mutable.Map[PilotType, Point2D] = mutable.Map()

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start =>
      logger.debug("Starting visualizer...")
      context.become(startedReceive(), discardOld = true)

  }

  def startedReceive(vis: GlobalField = new GlobalField(1600, 20, captureDistance)): Receive = {
    case NewPosition(position, pilotType) =>
      val point = new Point2D.Double(position.x, position.y)
      val line = positions.get(pilotType).flatMap(p => Some(new Line2D.Double(p, point)))
      positions.put(pilotType, point)
      updatePlot(vis, pilotType, new Point2D.Double(position.x, position.y), line)

    case Path(pilotType, path) =>
      setPath(vis, pilotType, path)
    case Stop =>
      logger.debug("Stopping visualizer...")
      //context.unbecome()

      // val win = SwingUtilities.getWindowAncestor(vis)
      // win.dispose()
      // maybe save the image
  }


  def updatePlot(vis: GlobalField, pilotType: PilotType, position: Point2D, lastMovement: Option[Line2D]): Unit = {
    if (pilotType == PilotActor.Evade) {
      vis.setEvaderState(position, 0)
      lastMovement.foreach(line => vis.addEvaderSegment(line))
    } else {
      vis.setPursuerState(position, 0)
      lastMovement.foreach(line => vis.addPursuerSegment(line))
    }

    lastMovement.foreach(_ => vis.resetBoundaryForMax())
    vis.repaint()
  }

  def setPath(vis: GlobalField, pilotType: PilotType, path: mutable.Queue[Point2D]): Unit = {
    val asList = path.toList.asJava
    logger.debug(s"Setting path for $pilotType with ${asList.size()} elements")
    if (pilotType == PilotActor.Evade) {
      vis.setEvaderPath(asList)
    } else {
      vis.setPursuerPath(asList)
    }
  }
}
