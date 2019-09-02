package net.nextlogic.airsim.api.ui

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.ui.settings.SettingsPanel
import net.nextlogic.airsim.api.ui.visualizer.SimulationPanel

import scala.swing.BorderPanel.Position.{Center, North, South}
import swing._

object MainWindow  extends SimpleSwingApplication {
  val visualizer = new SimulationPanel()
  val settings = new SettingsPanel(SimulatorSettings(pilotSettings = UiUtils.defaultPlayers), visualizer)

  override def top: Frame = new MainFrame {
    title = "Simulator & Game Settings"

    val panel = new BorderPanel {
      layout(settings) = North
      layout(visualizer) = Center
    }

    contents = panel

  }


}
