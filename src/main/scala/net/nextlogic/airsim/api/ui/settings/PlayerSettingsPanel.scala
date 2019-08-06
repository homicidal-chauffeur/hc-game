package net.nextlogic.airsim.api.ui.settings

import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.ui.common.UiUtils._

import scala.swing.{ComboBox, GridPanel, TextField}

class PlayerSettingsPanel(val initialSettings: PilotSettings) extends GridPanel(1, 5) {
  val actionTypeField: ComboBox[ActionType] = new ComboBox[ActionType](Seq(Evade, Pursue)) {
    selection.item = initialSettings.actionType
  }
  val strategyField: ComboBox[PilotStrategy] = new ComboBox[PilotStrategy](Seq(Agile, Chauffeur, HCMerz)) {
    selection.item = initialSettings.pilotStrategy
  }
  val playerNameField: TextField = newField(initialSettings.name)
  val colorField: ComboBox[PilotColor] = new ComboBox[PilotColor](Seq(Blue, Red)) {
    selection.item = initialSettings.color
  }
  val topVelocityField: ComboBox[VelocityType] = new ComboBox[VelocityType](Seq(EvaderVelocity, PursuerVelocity)) {
    selection.item = initialSettings.velocityType
  }
  val turningRadiusField: TextField = newNumberField(initialSettings.turningRadius.toString)
  val pilotDelayField: TextField = newNumberField(initialSettings.pilotDelay.toString)

  contents ++= Seq(
    actionTypeField, strategyField, playerNameField, colorField, topVelocityField,
    turningRadiusField, pilotDelayField
  )

  def settings: PilotSettings = PilotSettings(
    actionTypeField.selection.item,
    strategyField.selection.item,
    playerNameField.text,
    colorField.selection.item,
    topVelocityField.selection.item,
    UiUtils.parseOptDouble(turningRadiusField.text).getOrElse(initialSettings.turningRadius),
    UiUtils.parseOptInt(pilotDelayField.text).getOrElse(initialSettings.pilotDelay)
  )
}
