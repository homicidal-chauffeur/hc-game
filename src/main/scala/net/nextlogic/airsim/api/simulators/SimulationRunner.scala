package net.nextlogic.airsim.api.simulators

import akka.actor.{ActorRef, ActorSystem, Props}
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.simulators.actors.SimulationActor.StartSimulation
import net.nextlogic.airsim.api.simulators.actors.{SimulationActor, VisualizerActor}
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.visualizer.SimulationPanel
import net.nextlogic.airsim.api.utils.VehicleSettings

object SimulationRunner {
  val system = ActorSystem(s"SimulationRunner")

  def run(settings: SimulatorSettings, visualizer: Option[SimulationPanel]): ActorRef = {

    val simulation = system.actorOf(SimulationActor.props(settings, visualizer), s"simulation-${settings.ip}-${settings.port}")
    simulation ! StartSimulation

    simulation
  }

  def createVehicles(settings: SimulatorSettings): Seq[AirSimBaseClient] =
    settings
      .pilotSettings.map(p => VehicleSettings(p.name, p.actionType))
      .map(vs => AirSimBaseClient(settings.ip, settings.port, vs))
}
