package net.nextlogic.airsim.api.ui.settings

import javax.swing.BorderFactory
import net.nextlogic.airsim.api.simulators.SimulationRunner
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings.{AgileSimulation, GameType, HCMerzSimulation, HomicidalChauffeurSimulation}
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.ui.visualizer.SimulationPanel

import scala.collection.mutable
import scala.swing.BorderPanel.Position.{Center, North, South}
import scala.swing.event.ButtonClicked
import scala.swing.{BorderPanel, Button, ComboBox, GridPanel, Label, ScrollPane, TextField}


class SettingsPanel(initialSettings: SimulatorSettings, val visualizer: SimulationPanel) extends BorderPanel {
  val ipField: TextField = UiUtils.newField(initialSettings.ip)
  val portField: TextField = UiUtils.newIntField(initialSettings.port.toString)

  val gammaField: TextField = UiUtils.newNumberField(initialSettings.gamma.toString)
  val betaField: TextField = UiUtils.newNumberField(initialSettings.beta.toString)
  val gameTypeField: ComboBox[GameType] =
    new ComboBox[GameType](List(AgileSimulation, HomicidalChauffeurSimulation, HCMerzSimulation)) {
      selection.item = initialSettings.gameType
    }

  val gameTimeField: TextField = UiUtils.newIntField(initialSettings.gameTime.toString)
  val locationUpdateDelayField: TextField = UiUtils.newIntField(initialSettings.locationUpdateDelay.toString)
  val pursuerVelocityField: TextField = UiUtils.newNumberField(initialSettings.maxVelocityPursuer.toString)

  val startSim = new Button("Start Simulation")
  val pauseSim = new Button("Pause Simulation")

  val simSettings: GridPanel = new GridPanel(3, 6) {
    contents ++= Seq(
      new Label("Simulator IP: "), ipField,
      new Label("Port: "), portField,
      new Label(""), new Label(""),

      new Label("Game Presets: "), gameTypeField,
      new Label("Gamma: "), gammaField,
      new Label("Beta: "), betaField,


      new Label("Game Time (s): "), gameTimeField,
      new Label("Location Update Delay (ms): "), locationUpdateDelayField,
      new Label("Pursuer Velocity: "), pursuerVelocityField,
    )
    border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
  }
  val buttons: GridPanel = new GridPanel(1, 6) {
    contents ++= Seq(
      new Label(), new Label(), new Label(), new Label(),
      startSim, pauseSim
    )
    border = BorderFactory.createEmptyBorder(16, 8, 8, 8)
  }

  val playersTable = new PlayersPanel(mutable.Seq(initialSettings.pilotSettings:_*))

  layout(simSettings) = North
  layout(new ScrollPane(playersTable)) = Center
  layout(buttons) = South

  listenTo(startSim, pauseSim)

  reactions += {
    case ButtonClicked(`startSim`) =>
      simSettings.contents.foreach(c => c.enabled = false)
      playersTable.enabled = false
      buttons.contents.filter(_ != pauseSim).foreach(c => c.enabled = false)

      val currentSettings = settings
      visualizer.clear()
      visualizer.pilotSettings = currentSettings.pilotSettings

      SimulationRunner.run(settings, Some(visualizer))

    case ButtonClicked(`pauseSim`) =>
      simSettings.contents.foreach(c => c.enabled = true)
      playersTable.enabled = true
      buttons.contents.foreach(c => c.enabled = true)
  }

  border = BorderFactory.createEmptyBorder(16, 8, 8, 8)

  def settings: SimulatorSettings = {
    val g = UiUtils.parseOptDouble(gammaField.text)
    val b = UiUtils.parseOptDouble(betaField.text)

    SimulatorSettings(
      ipField.text,
      UiUtils.parseOptInt(portField.text).get,
      gameTypeField.selection.item,
      g.get, b.get,

      UiUtils.parseOptDouble(pursuerVelocityField.text).get,
      UiUtils.parseOptInt(locationUpdateDelayField.text).get,

      UiUtils.parseOptInt(gameTimeField.text).get,

      playersTable.settings
    )
  }


}
