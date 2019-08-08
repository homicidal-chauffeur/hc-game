package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor
import net.nextlogic.airsim.api.simulators.actors.RefereeActor._
import net.nextlogic.airsim.api.simulators.settings.Capture

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

object RefereeActor {
  def props(settings: GameSettings): Props =
    Props(new RefereeActor(settings))

  case class GameSettings(pilots: Seq[ActorRef], relativePosition: ActorRef,
                          visualizer: ActorRef,
                          resultsWriter: ActorRef,
                          captureDistance: Double, playTime: FiniteDuration)
  case class Start(startTime: Long = System.currentTimeMillis())
  case object Stop
  case object PlayTimeReached
  case object CheckIfGameOver
}

class RefereeActor(settings: GameSettings) extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)

  implicit val timeout: Timeout = 500.millis
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case Start(_) =>
      settings.visualizer ! VisualizerActor.Start
      settings.pilots.foreach(p => p ! PilotActor.Start)
      timers.startSingleTimer(PlayTimeReached, PlayTimeReached, settings.playTime)
      timers.startPeriodicTimer(CheckIfGameOver, CheckIfGameOver, 100.millis)

    case Stop =>
      timers.cancel(CheckIfGameOver)
      timers.cancel(PlayTimeReached)
      settings.pilots.foreach(p => p ! PilotActor.Stop)
      settings.relativePosition ! RelativePositionActor.Stop
      settings.pilots.head ! PilotActor.Reset
      settings.visualizer ! VisualizerActor.Stop

    case PlayTimeReached =>
      logger.debug(s"Escaped - play time ${settings.playTime} reached")
      self ! Stop

    case CheckIfGameOver =>
      val distFuture = (settings.relativePosition ? RelativePositionActor.Distance).mapTo[Option[Double]]
      distFuture.map(distanceOpt => distanceOpt.foreach{ distance =>
        if (distance < settings.captureDistance) {
          logger.debug(
            s"""
               |****************************************************************
               |Captured at distance $distance (required: ${settings.captureDistance})
               |****************************************************************""".stripMargin)
          // self ! Stop
          settings.resultsWriter ! Capture(distance)
        }
      })
  }
}
