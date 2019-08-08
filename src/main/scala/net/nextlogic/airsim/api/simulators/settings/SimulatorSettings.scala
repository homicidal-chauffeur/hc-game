package net.nextlogic.airsim.api.simulators.settings

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings.GameType
import net.nextlogic.airsim.api.utils.Constants
import play.api.libs.json.{Format, JsValue, Json, Writes}


object SimulatorSettings {
  trait GameType
  case object AgileSimulation extends GameType
  case object HCMerzSimulation extends GameType
  case object HomicidalChauffeurSimulation extends GameType

  implicit object GameTypeWrites extends Writes[GameType] {
    def writes(vt: GameType): JsValue = Json.toJson(vt.toString)
  }

  def gameTypeFromString(gt: String): GameType = gt match {
    case "0" => AgileSimulation
    case "1" => HomicidalChauffeurSimulation
    case "6" => HCMerzSimulation
    case other => throw new NoSuchElementException(s"No simulation for code $other")
  }

  implicit val formats: Writes[SimulatorSettings] = Json.writes[SimulatorSettings]

}

case class Capture(distance: Double, time: Long = System.currentTimeMillis())

case class SimulatorSettings(ip: String = Constants.IP,
                             port: Int = Constants.PORT,
                             gameType: GameType = SimulatorSettings.HCMerzSimulation,
                             gamma: Double = Constants.defaultGamma,
                             beta: Double = Constants.defaultBeta,
                             maxVelocityPursuer: Double = Constants.pursuerVelocity,
                             locationUpdateDelay: Int = Constants.locationUpdateDelay,
                             gameTime: Int = Constants.gameTime,
                             pilotSettings: Seq[PilotSettings] = Seq()) {
  def captureDistance: Double = beta * Constants.turningRadius

  def maxVelocityEvader: Double = gamma * maxVelocityPursuer
}

