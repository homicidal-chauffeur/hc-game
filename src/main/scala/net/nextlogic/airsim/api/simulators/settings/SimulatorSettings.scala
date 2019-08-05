package net.nextlogic.airsim.api.simulators.settings

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings.GameType
import net.nextlogic.airsim.api.utils.Constants


object SimulatorSettings {
  trait GameType
  case object AgileSimulation extends GameType
  case object HCMerzSimulation extends GameType
  case object HomicidalChauffeurSimulation extends GameType

  def gameTypeFromString(gt: String): GameType = gt match {
    case "0" => AgileSimulation
    case "1" => HomicidalChauffeurSimulation
    case "6" => HCMerzSimulation
    case other => throw new NoSuchElementException(s"No simulation for code $other")
  }

}

case class SimulatorSettings(ip: String = Constants.IP,
                             port: Int = Constants.PORT,
                             gameType: GameType = SimulatorSettings.HCMerzSimulation,
                             gamma: Double = Constants.defaultGamma,
                             beta: Double = Constants.defaultBeta,
                             maxVelocityPursuer: Double = Constants.pursuerVelocity,
                             locationUpdateDelay: Int = Constants.locationUpdateDelay,
                             pilotSettings: Seq[PilotSettings] = Seq()) {
 def captureDistance: Double = beta * Constants.turningRadius

  def maxVelocityEvader: Double = gamma * maxVelocityPursuer
}

