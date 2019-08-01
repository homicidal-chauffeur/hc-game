package net.nextlogic.airsim.api.simulators.settings

import java.awt.Color

import net.nextlogic.airsim.api.simulators.settings.PilotSettings._

object PilotSettings {
  trait VelocityType
  case object PursuerVelocity extends VelocityType {
    override def toString: String = "Pursuer Velocity from Settings"
  }
  case object EvaderVelocity extends VelocityType {
    override def toString: String = "Pursuer Velocity * Gamma"
  }


  trait PilotType
  case object Evade extends PilotType
  case object Pursue extends PilotType

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

case class PilotSettings(pilotType: PilotType,
                         name: String,
                         color: PilotColor,
                         velocityType: VelocityType,
                         turningRadius: Double = 0)
