package net.nextlogic.airsim.api.gameplay.telemetry

import java.awt.geom.Point2D

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.NewPosition
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor._
import net.nextlogic.airsim.api.simulators.actors.PilotActor
import net.nextlogic.airsim.api.simulators.actors.PilotActor.PilotType
import net.nextlogic.airsim.api.utils.Vector3r


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

  var positionEvader: Option[Vector3r] = None
  var positionPursuer: Option[Vector3r] = None

  override def receive: Receive = stoppedReceive

  def startedReceive: Receive = {
    case ForPilotType(pilotType, theta) => // implement later


    case ForEvader(theta) =>
      if (positionPursuer.isDefined && positionPursuer.isDefined) {
        sender() ! Some(
          RelativePosition.relativePosTo2D(
            toPoint(positionEvader),
            toPoint(positionPursuer),
            theta
          )
        )
      } else {
        sender() ! None
      }
    case ForPursuer(theta) =>
      if (positionPursuer.isDefined && positionPursuer.isDefined) {
        sender() ! Some(
          RelativePosition.relativePosTo2D(
            toPoint(positionPursuer),
            toPoint(positionEvader),
            theta
          )
        )
      } else {
        sender() ! None
      }

    case Distance =>
      val dist = if (positionPursuer.isDefined && positionPursuer.isDefined) {
        Some(positionPursuer.get.distance(positionEvader.get))
      } else {
        None
      }

      sender() ! dist


    case NewPosition(position, pilotType) =>
      if (pilotType == PilotActor.Evade)
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

  def toPoint(position: Option[Vector3r]): Point2D =
    position.map(p => new Point2D.Double(p.x, p.y)).get
}
