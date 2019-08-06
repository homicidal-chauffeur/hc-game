package net.nextlogic.airsim.api.simulators.settings

import java.awt.Color

import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Constants

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


  sealed trait ActionType
  case object Evade extends ActionType
  case object Pursue extends ActionType

  sealed trait PilotStrategy
  case object Agile extends PilotStrategy
  case object Chauffeur extends PilotStrategy
  case object HCMerz extends PilotStrategy

  sealed trait PilotColor {
    val color: Color
  }
  case object Blue extends PilotColor {
    lazy val color = new Color(0, 0, 1, 0.2f)
  }
  case object Red extends PilotColor {
    lazy val color = new Color(1, 0, 0, 0.2f)
  }

}

case class PilotSettings(actionType: ActionType,
                         pilotStrategy: PilotStrategy,
                         name: String,
                         color: PilotColor,
                         velocityType: VelocityType,
                         turningRadius: Double = Constants.turningRadius)
