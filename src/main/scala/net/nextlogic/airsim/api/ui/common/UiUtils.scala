package net.nextlogic.airsim.api.ui.common

import net.nextlogic.airsim.api.simulators.settings.{PilotSettings, SimulatorSettings}
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Constants

import scala.collection.mutable
import scala.swing.{Alignment, ComboBox, TextField}

object UiUtils {
  def parseOptDouble(str: String): Option[Double] =
    try {
      Some(str.toDouble)
    } catch {
      case e: NumberFormatException => None
    }

  def parseOptInt(str: String): Option[Int] =
    try {
      Some(str.toInt)
    } catch {
      case e: NumberFormatException => None
    }

  def newField(defaultText: String = "", w: Int = 5): TextField = new TextField {
    text = defaultText
    columns = w

    verifier = (x => x == "pass")
  }

  def newIntField(defaultText: String = "0", w: Int = 5): TextField = new TextField {
    text = defaultText
    columns = w
    horizontalAlignment = Alignment.Right
    verifier = (x => parseOptInt(x).isDefined)
  }

  def newNumberField(defaultText: String = "0.0", w: Int = 5): TextField = new TextField {
    text = defaultText
    columns = w
    horizontalAlignment = Alignment.Right
    verifier = (x => parseOptDouble(x).isDefined)
  }

  lazy val defaultPlayers: mutable.Seq[PilotSettings] = mutable.Seq(
    PilotSettings(Evade, HCMerz, Constants.eVehicle , Blue, EvaderVelocity, Constants.turningRadius  ),
    PilotSettings(Pursue, HCMerz, Constants.pVehicle, Red, PursuerVelocity, Constants.turningRadius )
  )

  lazy val defaultSettings: SimulatorSettings = SimulatorSettings(
    pilotSettings = defaultPlayers
  )
}
