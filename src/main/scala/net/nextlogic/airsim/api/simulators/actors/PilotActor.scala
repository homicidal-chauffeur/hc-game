package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor.{NewTheta, RelativePositionWithThetas}
import net.nextlogic.airsim.api.simulators.actors.PilotActor._
import net.nextlogic.airsim.api.simulators.actors.ResultsWriterActor.MoveDetails
import net.nextlogic.airsim.api.simulators.settings.PilotSettings.Evade
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

  case class RelativePositionWithThetaFuture(relPos: Option[RelativePositionWithThetas], opponentsTheta: Option[Double])

}

class PilotActor(player: PlayerRouter.Player, resultsWriter: ActorRef) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)

  implicit val timeout: Timeout = 1.second
  implicit val executionContext: ExecutionContext = context.dispatcher

  if (player.actionType == Evade) context.parent ! NewTheta(Math.acos(0.5), player.vehicle.settings)

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start =>
      logger.debug(s"${player.vehicle.settings.name}: Starting the game...")
      context.become(startedReceive, discardOld = true)
      timers.startSingleTimer(
        PilotTimerKey(player.vehicle.settings), Play(player.vehicle.settings),
        scala.util.Random.nextInt(Constants.pilotDelay).millis // this is to make them start at slightly different time
      )

    case Reset =>
      player.vehicle.reset()
  }

  def startedReceive: Receive = {
    case Stop =>
      logger.debug(s"${player.vehicle.settings.name}: Stopping the game...")
      context.unbecome()
    case Play(vehicleSettings) =>
      val request = RelativePositionActor.ForVehicle(vehicleSettings)

      val relPositionFuture = (context.parent ? request).mapTo[Option[RelativePositionWithThetas]]
      relPositionFuture.map {
        case Some(relPosition) =>
          val moveInfo = MoveInfo(
            relPosition.myTheta, relPosition.relativePosition, relPosition.opponentsTheta,
            relPosition.myPosition, relPosition.oppPosition,
            player.maxVelocity,
            player.turningRadius
          )
          val newTheta = PlayerRouter.moveWithTheta(
            player,
            moveInfo
          )
          // logger.debug(s"${player.vehicle.settings.name}: ${player.actionType} with theta ${newTheta} and relative position ${relPosition.relativePosition}...")
          resultsWriter ! MoveDetails(
            player, moveInfo.copy(myTheta = newTheta)
          )
          context.parent ! NewTheta(newTheta, player.vehicle.settings)
          timers.startSingleTimer(PilotTimerKey(player.vehicle.settings), Play(vehicleSettings), player.pilotDelay.millis)

        case None =>
          logger.debug(s"${player.vehicle.settings.name}: not moving because don't have relative position ")
          timers.startSingleTimer(PilotTimerKey(player.vehicle.settings), Play(vehicleSettings), player.pilotDelay.millis)
      }
  }

  override def postStop(): Unit = {
    player.vehicle.disconnect()
  }
}
