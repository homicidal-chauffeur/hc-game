package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.NewPosition
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor.{NewTheta, RelativePositionWithThetas}
import net.nextlogic.airsim.api.gameplay.telemetry.{PositionTrackerActor, RelativePositionActor}
import net.nextlogic.airsim.api.simulators.SimulationRunner.{createVehicles, system}
import net.nextlogic.airsim.api.simulators.SimulationSetup
import net.nextlogic.airsim.api.simulators.actors.PilotActor.CurrentTheta
import net.nextlogic.airsim.api.simulators.actors.RefereeActor.GameSettings
import net.nextlogic.airsim.api.simulators.actors.SimulationActor._
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.visualizer.SimulationPanel
import net.nextlogic.airsim.api.utils.VehicleSettings

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


object SimulationActor {
  def props(settings: SimulatorSettings, visualizer: Option[SimulationPanel]): Props
  = Props(new SimulationActor(settings, visualizer))

  case object StartSimulation
  case object StopSimulation
}

class SimulationActor(settings: SimulatorSettings, visualizerPanel: Option[SimulationPanel]) extends Actor with ActorLogging {
  val logger = Logging(context.system, this)
  implicit val timeout: Timeout = 1.second
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case StartSimulation =>
      val vehicles = createVehicles(settings)
      // TODO refactor somewhere else
      SimulationSetup.setup(vehicles, settings.captureDistance)

      val relativePositionAct = context.actorOf(Props[RelativePositionActor], "relative-position")
      val visualizer = context.actorOf(VisualizerActor.props(visualizerPanel, settings.captureDistance), "visualizer")
      val resultsWriter = context.actorOf(ResultsWriterActor.props(settings), "results-writer")

      val trackers = vehicles
        .map(v => context.actorOf(
          PositionTrackerActor.props(settings.locationUpdateDelay, v,
            Seq(relativePositionAct, visualizer, resultsWriter))
        )
        )

      val pilots = generatePilots(vehicles, resultsWriter)

      val gameSettings = GameSettings(
        pilots.values.toList,
        relativePositionAct,
        visualizer,
        settings.captureDistance,
        settings.gameTime.seconds
      )

      val referee = context.actorOf(RefereeActor.props(gameSettings))
      val startWithTime = RefereeActor.Start()

      relativePositionAct ! RelativePositionActor.Start(trackers)
      resultsWriter ! startWithTime

      referee ! startWithTime

      context.become(staredReceive(pilots, relativePositionAct, visualizer), discardOld = true)

  }

  def staredReceive(pilots: Map[VehicleSettings, ActorRef], relativePositionActor: ActorRef, visualizer: ActorRef): Receive = {
    case StopSimulation =>
      logger.debug("Stopping simulation")
      self ! PoisonPill

    case q: RelativePositionActor.ForVehicle =>
      val s = sender()
      ask(relativePositionActor, q).mapTo[Option[RelativePositionWithThetas]].pipeTo(s)

    case newTheta: NewTheta =>
      relativePositionActor ! newTheta
  }

  private def generatePilots(vehicles: Seq[AirSimBaseClient], resultsWriter: ActorRef) = {
    val pilots = settings.pilotSettings.zip(vehicles)
      .foldLeft(Map[VehicleSettings, ActorRef]())((acc, settingsWithVehicle) =>
        acc.updated(
          settingsWithVehicle._2.settings,
          context.actorOf(
            PilotActor.props(
              PlayerRouter.Player(
                settingsWithVehicle._1.actionType,
                settingsWithVehicle._1.pilotStrategy,
                settingsWithVehicle._1.velocityType.fromPursuerVelocity(settings.maxVelocityPursuer, settings.gamma),
                settingsWithVehicle._1.turningRadius,
                settingsWithVehicle._1.pilotDelay,
                settingsWithVehicle._2
              ),
              resultsWriter
            ),
            settingsWithVehicle._2.settings.name
          )
        )
      )
    logger.debug(s"Created ${pilots.size} pilots from ${settings.pilotSettings} settings")
    pilots
  }



}
