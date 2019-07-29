package net.nextlogic.airsim.api.gameplay.telemetry

import java.awt.geom.Point2D
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor._
import net.nextlogic.airsim.api.simulators.actors.PilotActor.PilotType
import net.nextlogic.airsim.api.utils.Vector3r

import scala.collection.mutable
import scala.concurrent.duration._

object PositionTrackerActor {
  def props(pilotType: PilotType, vehicle: AirSimBaseClient, observers: Seq[ActorRef]): Props =
    Props(new PositionTrackerActor(pilotType, vehicle, observers))

  case object Start
  case object Stop
  case object UpdatePosition
  case object GetPosition

  case class NewPosition(position: Vector3r, pilotType: PilotType)

  case class Path(pilotType: PilotType, path: mutable.Queue[Point2D])

  case class PositionTrackerTimerKey(vehicle: AirSimBaseClient)
}

class PositionTrackerActor(pilotType: PilotType, vehicle: AirSimBaseClient, observers: Seq[ActorRef]) extends Actor with Timers with ActorLogging {
  val logger = Logging(context.system, this)

  val path: mutable.Queue[Point2D] = mutable.Queue[Point2D]()
  var lastPosition: Option[Point2D.Double] = None
  var currentPosition: Option[Point2D.Double] = None
  var positionVector = Vector3r()

  override def receive: Receive = stoppedReceive

  def startedReceive: Receive = {
    case Stop =>
      observers.foreach(o => o ! Path(pilotType, path))
      context.unbecome()
      logger.debug(s"${vehicle.settings.name}: Stopping position tracker...")

    case UpdatePosition =>
      positionVector = vehicle.getPosition
      val pos = new Point2D.Double(positionVector.x, positionVector.y)
      lastPosition = currentPosition
      currentPosition = Some(pos)
      this.path.enqueue(pos)
      timers.startSingleTimer(PositionTrackerTimerKey(vehicle), UpdatePosition, 100.millis)

      observers.foreach(o =>
          o ! NewPosition(positionVector, pilotType)
      )

    case GetPosition => sender() ! positionVector
  }

  def stoppedReceive: Receive = {
    case Start =>
      context.become(startedReceive, discardOld = false)
      timers.startSingleTimer(PositionTrackerTimerKey(vehicle), UpdatePosition, 100.millis)
      logger.debug(s"${vehicle.settings.name}: Starting position tracker...")
    case GetPosition => sender() ! positionVector
  }
}
