package net.nextlogic.airsim.api.simulators

import net.nextlogic.airsim.api.simulations.{AgileSimulation, HomicidalChauffeurSimulation}
import net.nextlogic.airsim.api.utils.Constants

class Simulator extends App {
  if (args.length != 3) {
    println("Please specify speed ratio (gamma), and radius ratio (beta).")
    System.exit(1)
  }

  val gameType = args(0).toInt
  val gamma = args(1).toDouble
  val beta = args(2).toDouble

  val settings = SimulatorSettings(
    Constants.IP,
    gameType,
    gamma,
    beta,
    gamma * Constants.pursuerVelocity,
    Constants.pursuerVelocity,
    beta * Constants.turningRadius
  )

  println(s"Gamma: $gamma, Beta: $beta")

  val simulation = gameType match {
    case 0 => AgileSimulation(settings)
    case 1 => HomicidalChauffeurSimulation(settings)
    case _ => throw new Exception("Wrong simulation code")
  }

  simulation.run()
}


