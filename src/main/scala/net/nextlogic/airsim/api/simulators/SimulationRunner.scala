package net.nextlogic.airsim.api.simulators

import akka.actor.{ActorRef, ActorSystem, Props}
import net.nextlogic.airsim.api.gameplay.DronePlayer
import net.nextlogic.airsim.api.gameplay.telemetry.{PositionTrackerActor, RelativePositionActor}
import net.nextlogic.airsim.api.simulators.actors.{PilotActor, RefereeActor, VisualizerActor}
import net.nextlogic.airsim.api.simulators.actors.RefereeActor.GameSettings
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._

import scala.concurrent.duration._

object SimulationRunner {

  def run(evader: DronePlayer, pursuer: DronePlayer, captureDistance: Double): Unit = {
    val system = ActorSystem("SimulationRunner")

    val relativePositionAct = system.actorOf(Props[RelativePositionActor], "relative-position")
    val visualizer = system.actorOf(VisualizerActor.props(captureDistance))
    val (evaderTracker, pursuerTracker) =
      initTrackers(evader, pursuer, Seq(relativePositionAct, visualizer), relativePositionAct, system)

    val evaderPilot = system.actorOf(PilotActor.props(Evade, evader, pursuer, relativePositionAct))
    val pursuerPilot = system.actorOf(PilotActor.props(Pursue, pursuer, evader, relativePositionAct))

    val gameSettings = GameSettings(
      Seq(evaderPilot, pursuerPilot),
      relativePositionAct,
      visualizer,
      captureDistance,
      60.seconds
    )

    val referee = system.actorOf(RefereeActor.props(gameSettings))
    referee ! RefereeActor.Start
  }

  private def initTrackers(evader: DronePlayer, pursuer: DronePlayer,
                           observers: Seq[ActorRef],
                           relativePositionActor: ActorRef,
                           system: ActorSystem): (ActorRef, ActorRef) = {
    val evaderTracker = system.actorOf(
      PositionTrackerActor.props(Evade, evader.vehicle, observers),
      "evader-tracker"
    )

    val pursuerTracker = system.actorOf(
      PositionTrackerActor.props(Pursue, pursuer.vehicle, observers),
      "pursuer-tracker"
    )

    relativePositionActor ! RelativePositionActor.Start(Seq(evaderTracker, pursuerTracker))

    (evaderTracker, pursuerTracker)
  }
}
