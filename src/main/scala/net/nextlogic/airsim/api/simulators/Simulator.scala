package net.nextlogic.airsim.api.simulators

import net.nextlogic.airsim.api.simulations.{AgileSimulation, HCMerzSimulation, HomicidalChauffeurSimulation}
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings.GameType
import net.nextlogic.airsim.api.utils.Constants

object Simulator extends App {
  if (args.length != 3) {
    println("Please specify speed ratio (gamma), and radius ratio (beta).")
    System.exit(1)
  }

  val gameType = SimulatorSettings.gameTypeFromString(args(0))
  val gamma = args(1).toDouble
  val beta = args(2).toDouble

  val settings = SimulatorSettings(
    Constants.IP,
    gameType,
    gamma,
    beta,
    Constants.pursuerVelocity
  )

  println(s"Gamma: $gamma, Beta: $beta")

  val simulation = gameType match {
    case SimulatorSettings.AgileSimulation => AgileSimulation(settings)
    case SimulatorSettings.HomicidalChauffeurSimulation => HomicidalChauffeurSimulation(settings)
    case SimulatorSettings.HCMerzSimulation => HCMerzSimulation(settings)
    case _ => throw new Exception("Wrong simulation code")
  }

  simulation.run()
}


