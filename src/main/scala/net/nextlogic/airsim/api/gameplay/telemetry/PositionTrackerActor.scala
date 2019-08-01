package net.nextlogic.airsim.api.gameplay.telemetry

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor._
import net.nextlogic.airsim.api.simulators.settings.PilotSettings.PilotType
import net.nextlogic.airsim.api.utils.{Constants, Vector3r}

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

  case class Path(pilotType: PilotType, path: mutable.Queue[Vector3r])

  case class PositionTrackerTimerKey(vehicle: AirSimBaseClient)
}

class PositionTrackerActor(pilotType: PilotType, vehicle: AirSimBaseClient, observers: Seq[ActorRef]) extends Actor with Timers with ActorLogging {
  val logger = Logging(context.system, this)

  val path: mutable.Queue[Vector3r] = mutable.Queue[Vector3r]()
  var positionVector = Vector3r()

  override def receive: Receive = stoppedReceive

  def startedReceive: Receive = {
    case Stop =>
      observers.foreach(o => o ! Path(pilotType, path))
      context.unbecome()
      logger.debug(s"${vehicle.settings.name}: Stopping position tracker...")

    case UpdatePosition =>
      positionVector = vehicle.getPosition
      this.path.enqueue(positionVector)
      timers.startSingleTimer(PositionTrackerTimerKey(vehicle), UpdatePosition, Constants.locationUpdateDelay.millis)

      observers.foreach(o =>
          o ! NewPosition(positionVector, pilotType)
      )

    case GetPosition => sender() ! positionVector
  }

  def stoppedReceive: Receive = {
    case Start =>
      context.become(startedReceive, discardOld = true)
      timers.startSingleTimer(PositionTrackerTimerKey(vehicle), UpdatePosition, Constants.locationUpdateDelay.millis)
      logger.debug(s"${vehicle.settings.name}: Starting position tracker...")
    case GetPosition => sender() ! positionVector
  }
}
