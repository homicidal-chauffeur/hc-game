package net.nextlogic.airsim.api.ui

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.ui.settings.SettingsPanel
import net.nextlogic.airsim.api.ui.visualizer.SimulationPanel

import scala.swing.BorderPanel.Position.{Center, North, South}
import swing._

object MainWindow  extends SimpleSwingApplication {
  val visualizer = new SimulationPanel()

  override def top: Frame = new MainFrame {
    title = "Simulator & Game Settings"

    val settings = new SettingsPanel(SimulatorSettings(pilotSettings = UiUtils.defaultPlayers), visualizer)

    val panel = new BorderPanel {
      layout(settings) = North
      layout(visualizer) = Center
    }

    contents = panel

  }


}
