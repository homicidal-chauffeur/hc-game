package net.nextlogic.airsim.api.gameplay.telemetry

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.NewPosition
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor._
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Vector3r

import scala.collection.mutable


object RelativePositionActor {
  case class ForEvader(theta: Double)
  case class ForPursuer(theta: Double)
  case class ForPilotType(pilotType: PilotType, theta: Double)
  case object CheckEvaderPosition
  case object CheckPursuerPosition
  case object Distance
  case object Stop
  case class Start(trackers: Seq[ActorRef])
}

class RelativePositionActor() extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)

  //  var positions: mutable.Map[PilotType, Vector3r] = mutable.Map[PilotType, Vector3r]()
  var positionEvader: Option[Vector3r] = None
  var positionPursuer: Option[Vector3r] = None

  override def receive: Receive = stoppedReceive

  def startedReceive: Receive = {
    case ForPilotType(pilotType, theta) => // implement later
      sender() ! calculateRelPosition(pilotType, theta)

    case ForEvader(theta) =>
      sender() ! calculateRelPosition(Evade, theta)

    case ForPursuer(theta) =>
      sender() ! calculateRelPosition(Pursue, theta)

    case Distance =>
      val dist = if (positionPursuer.isDefined && positionPursuer.isDefined) {
        Some(positionPursuer.get.distance(positionEvader.get))
      } else {
        None
      }

      sender() ! dist


    case NewPosition(position, pilotType) =>
      if (pilotType == Evade)
        positionEvader = Some(position)
      else
        positionPursuer = Some(position)

    case Stop =>
      logger.debug("Stopping relative position...")
      context.unbecome()

  }

  def stoppedReceive: Receive = {
    case NewPosition(_, _) => sender() ! PositionTrackerActor.Stop

    case Distance => None
    case ForEvader => None
    case ForPursuer => None
    case Start(trackers) =>
      context.become(startedReceive)
      trackers.foreach(t => t ! PositionTrackerActor.Start)
  }

  def calculateRelPosition(pilotType: PilotType, theta: Double): Option[Vector3r] =
    if (positionPursuer.isDefined && positionPursuer.isDefined) {
      val relPos = if (pilotType == Evade) {
        RelativePosition.relativePosTo2D(
          positionEvader.get,
          positionPursuer.get,
          theta
        )
      } else {
        RelativePosition.relativePosTo2D(
          positionPursuer.get,
          positionEvader.get,
          theta
        )
      }
      Some(relPos)
    } else {
      None
    }

}
