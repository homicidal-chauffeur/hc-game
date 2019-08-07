package net.nextlogic.airsim.api.simulators.settings

import java.awt.Color

import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Constants
import play.api.libs.json.{JsValue, Json, Writes}

object PilotSettings {

  trait VelocityType {
    def fromPursuerVelocity(pursuerVelocity: Double, gamma: Double): Double
  }
  case object PursuerVelocity extends VelocityType {
    override def toString: String = "Pursuer Velocity from Settings"
    def fromPursuerVelocity(pursuerVelocity: Double, gamma: Double): Double = pursuerVelocity
  }
  case object EvaderVelocity extends VelocityType {
    override def toString: String = "Pursuer Velocity * Gamma"
    def fromPursuerVelocity(pursuerVelocity: Double, gamma: Double): Double = pursuerVelocity * gamma
  }
  implicit object VelocityTypeWrites extends Writes[VelocityType] {
    def writes(vt: VelocityType): JsValue = Json.toJson(vt.toString)
  }

  sealed trait ActionType
  case object Evade extends ActionType
  case object Pursue extends ActionType

  implicit object ActionTypeWrites extends Writes[ActionType] {
    def writes(vt: ActionType): JsValue = Json.toJson(vt.toString)
  }

  sealed trait PilotStrategy
  case object Agile extends PilotStrategy
  case object Chauffeur extends PilotStrategy
  case object HCMerz extends PilotStrategy
  implicit object PilotStrategyWrites extends Writes[PilotStrategy] {
    def writes(vt: PilotStrategy): JsValue = Json.toJson(vt.toString)
  }

  sealed trait PilotColor {
    val color: Color
  }
  case object Blue extends PilotColor {
    lazy val color = new Color(0, 0, 1, 0.2f)
  }
  case object Red extends PilotColor {
    lazy val color = new Color(1, 0, 0, 0.2f)
  }
  implicit object PilotColorWrites extends Writes[PilotColor] {
    def writes(vt: PilotColor): JsValue = Json.toJson(vt.toString)
  }

//  implicit val writes: Writes[PilotSettings] = (ps: PilotSettings) => Json.obj(
//    "actionType" -> ps.actionType.toString,
//    "pilotStrategy" -> ps.actionType.toString,
//    "name" -> ps.name,
//    "velocityType" -> ps.velocityType.toString,
//    "turningRadius" -> ps.turningRadius,
//    "pilotDelay" -> ps.pilotDelay
//  )
  implicit val pilotSettingsWrites: Writes[PilotSettings] = Json.writes[PilotSettings]
}

case class PilotSettings(actionType: ActionType,
                         pilotStrategy: PilotStrategy,
                         name: String,
                         color: PilotColor,
                         velocityType: VelocityType,
                         turningRadius: Double = Constants.turningRadius,
                         pilotDelay: Int = Constants.pilotDelay
                        )
