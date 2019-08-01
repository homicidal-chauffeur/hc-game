package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import net.nextlogic.airsim.api.gameplay.DronePlayer
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor
import net.nextlogic.airsim.api.simulators.actors.PilotActor._
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Vector3r

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext


object PilotActor {
  // HCM needs access to opponent's theta
  def props(pilotType: PilotType, vehicle: DronePlayer, opponent: DronePlayer, relativePositionActor: ActorRef): Props =
    Props(new PilotActor(pilotType, vehicle, opponent, relativePositionActor))

  case object Start
  case object Stop
  case object Reset
  case class EvaderTimerKey(vehicle: DronePlayer)

}

class PilotActor(pilotType: PilotType, vehicle: DronePlayer, opponent: DronePlayer, relativePositionActor: ActorRef) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)

  implicit val timeout: Timeout = 1.second
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start =>
      logger.debug(s"${vehicle.vehicle.settings.name}: Starting the game...")
      timers.startPeriodicTimer(EvaderTimerKey(vehicle), pilotType, 100.millis)
      context.become(startedReceive, discardOld = true)

    case Reset =>
      vehicle.vehicle.reset()
  }

  def startedReceive: Receive = {
    case Stop =>
      logger.debug(s"${vehicle.vehicle.settings.name}: Stopping the game...")
      context.unbecome()
    case Evade =>
      (relativePositionActor ? RelativePositionActor.ForEvader(vehicle.theta))
          .mapTo[Option[Vector3r]].foreach{ relativePositionOpt =>
        relativePositionOpt.foreach { p =>
          vehicle.evade(p, opponent.theta)
          logger.debug(s"${vehicle.vehicle.settings.name}: Evading with theta ${vehicle.theta} and relative position $p...")
        }
      }
      timers.startSingleTimer(EvaderTimerKey(vehicle), Evade, 100.millis)
    case Pursue =>
      (relativePositionActor ? RelativePositionActor.ForPursuer(vehicle.theta))
          .mapTo[Option[Vector3r]].foreach{relativePositionOpt =>
        relativePositionOpt.foreach { p =>
          vehicle.pursue(p, opponent.theta)
          logger.debug(s"${vehicle.vehicle.settings.name}: Pursuing with theta ${vehicle.theta} and relative position $p...")
        }
      }
      timers.startSingleTimer(EvaderTimerKey(vehicle), Pursue, 100.millis)
  }
}
