package net.nextlogic.airsim.api.gameplay.telemetry

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor._
import net.nextlogic.airsim.api.utils.{MultirotorState, MultirotorStateUtils, Vector3r, VehicleSettings}

import scala.collection.mutable
import scala.concurrent.duration._

object PositionTrackerActor {
  def props(locationUpdateDelay: Int, vehicle: AirSimBaseClient, observers: Seq[ActorRef]): Props =
    Props(new PositionTrackerActor(locationUpdateDelay, vehicle, observers))

  case object Start
  case object Stop
  case object UpdatePosition

  case class NewPosition(position: Vector3r, vehicleSettings: VehicleSettings)
  case class NewMultiRotorState(state: MultirotorState, vehicleSettings: VehicleSettings)

  case class Path(path: Seq[MultirotorState], vehicleSettings: VehicleSettings)

  case class PositionTrackerTimerKey(vehicle: VehicleSettings)
}

class PositionTrackerActor(locationUpdateDelay: Int, vehicle: AirSimBaseClient, observers: Seq[ActorRef]) extends Actor with Timers with ActorLogging {
  val logger = Logging(context.system, this)
  val path: mutable.Queue[MultirotorState] = mutable.Queue[MultirotorState]()

  override def receive: Receive = stoppedReceive

  def startedReceive: Receive = {
    case Stop =>
      observers.foreach(o => o ! Path(path, vehicle.settings))
      context.unbecome()
      logger.debug(s"${vehicle.settings.name}: Stopping position tracker...")

    case UpdatePosition =>
      val vehicleState = MultirotorStateUtils.getMultirotorState(vehicle.getMultirotorState)
      this.path.enqueue(vehicleState.copy(timestamp = System.currentTimeMillis()))
      timers.startSingleTimer(PositionTrackerTimerKey(vehicle.settings), UpdatePosition, locationUpdateDelay.millis)

      observers.foreach { o =>
        o ! NewPosition(vehicleState.kinematicsEstimated.position, vehicle.settings)
        // maybe not necessary
        // o ! NewMultiRotorState(vehicleState, vehicle.settings)
      }
  }

  def stoppedReceive: Receive = {
    case Start =>
      context.become(startedReceive, discardOld = true)
      timers.startSingleTimer(PositionTrackerTimerKey(vehicle.settings), UpdatePosition, locationUpdateDelay.millis)
      logger.debug(s"${vehicle.settings.name}: Starting position tracker...")
  }
}
