package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor.RelativePositionWithOpponent
import net.nextlogic.airsim.api.simulators.actors.PilotActor._
import net.nextlogic.airsim.api.simulators.actors.ResultsWriterActor.MoveDetails
import net.nextlogic.airsim.api.simulators.actors.SimulationActor.GetTheta
import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.utils.{Constants, Vector3r, VehicleSettings}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


object PilotActor {
  def props(player: PlayerRouter.Player, resultsWriter: ActorRef): Props =
    Props(new PilotActor(player, resultsWriter))

  case object Start
  case object Stop
  case object Reset
  case class Play(vehicleSettings: VehicleSettings)
  case class PilotTimerKey(vehicle: VehicleSettings)
  case object CurrentTheta

  case class RelativePositionWithThetaFuture(relPos: Option[RelativePositionWithOpponent], opponentsTheta: Option[Double])

}

class PilotActor(player: PlayerRouter.Player, resultsWriter: ActorRef) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)

  implicit val timeout: Timeout = 1.second
  implicit val executionContext: ExecutionContext = context.dispatcher

  var theta: Double = 0

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start =>
      logger.debug(s"${player.vehicle.settings.name}: Starting the game...")
      context.become(startedReceive, discardOld = true)
      timers.startSingleTimer(
        PilotTimerKey(player.vehicle.settings), Play(player.vehicle.settings), Constants.pilotDelay.millis
      )

    case Reset =>
      player.vehicle.reset()
  }

  def startedReceive: Receive = {
    case Stop =>
      logger.debug(s"${player.vehicle.settings.name}: Stopping the game...")
      context.unbecome()
    case CurrentTheta => sender() ! theta
    case Play(vehicleSettings) =>
      val request = RelativePositionActor.ForVehicle(vehicleSettings, theta)

      val relOptionWithOppThetaFuture = for {
        relPositionOption <- (context.parent ? request).mapTo[Option[RelativePositionWithOpponent]]
        opponentsTheta <- relPositionOption match {
          case Some(relPosition) => (context.parent ? GetTheta(relPosition.opponent) ).mapTo[Some[Double]]
          case None => Future(None)
        }
      } yield RelativePositionWithThetaFuture(relPositionOption, opponentsTheta)

      relOptionWithOppThetaFuture.map{ relOptionWithOppTheta =>
        if (relOptionWithOppTheta.relPos.isDefined && relOptionWithOppTheta.opponentsTheta.isDefined) {
          val relativePosition = relOptionWithOppTheta.relPos.get

          val moveInfo = MoveInfo(
            theta, relativePosition.relativePosition, relOptionWithOppTheta.opponentsTheta.get,
            relativePosition.myPosition, relativePosition.oppPosition,
            player.maxVelocity,
            player.turningRadius
          )
          val newTheta = PlayerRouter.moveWithTheta(
            player,
            moveInfo
          )
          logger.debug(s"${player.vehicle.settings.name}: ${player.actionType} with theta ${newTheta} and relative position ${relOptionWithOppTheta.relPos.get.relativePosition}...")
          resultsWriter ! MoveDetails(
            player, moveInfo
          )
          theta = newTheta
          timers.startSingleTimer(PilotTimerKey(player.vehicle.settings), Play(vehicleSettings), Constants.pilotDelay.millis)
        } else {
          logger.debug(s"${player.vehicle.settings.name}: not moving because don't have relative position (or opponent's theta?)")
          timers.startSingleTimer(PilotTimerKey(player.vehicle.settings), Play(vehicleSettings), Constants.pilotDelay.millis)
        }
      }

  }

  override def postStop(): Unit = {
    player.vehicle.disconnect()
  }
}
