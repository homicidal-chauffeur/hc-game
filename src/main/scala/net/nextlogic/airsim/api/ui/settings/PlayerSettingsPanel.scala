package net.nextlogic.airsim.api.ui.settings

import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.ui.common.UiUtils._

import scala.swing.{ComboBox, GridPanel}

class PlayerSettingsPanel(val initialSettings: PilotSettings) extends GridPanel(1, 5) {
  val playerTypeField = new ComboBox[PilotType](Seq(Evade, Pursue)) {
    selection.item = initialSettings.pilotType
  }
  val playerNameField = newField(initialSettings.name)
  val colorField = new ComboBox[PilotColor](Seq(Blue, Red)) {
    selection.item = initialSettings.color
  }
  val topVelocityField = new ComboBox[VelocityType](Seq(EvaderVelocity, PursuerVelocity)) {
    selection.item = initialSettings.velocityType
  }
  val turningRadiusField = newNumberField(initialSettings.turningRadius.toString)

  contents ++= Seq(
    playerTypeField, playerNameField, colorField, topVelocityField,
    turningRadiusField
  )

  def settings: PilotSettings = PilotSettings(
    playerTypeField.selection.item,
    playerNameField.text,
    colorField.selection.item,
    topVelocityField.selection.item,
    UiUtils.parseOptDouble(turningRadiusField.text).get
  )
}
