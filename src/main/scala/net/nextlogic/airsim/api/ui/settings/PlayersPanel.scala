package net.nextlogic.airsim.api.ui.settings

import javax.swing.BorderFactory
import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.utils.VehicleSettings

import scala.collection.mutable
import scala.swing.{BoxPanel, GridPanel, Label, Orientation}

class PlayersPanel(var players: mutable.Seq[PilotSettings]) extends BoxPanel(Orientation.Vertical) {
  val header: GridPanel = new GridPanel(1, 5) {
    contents ++= Seq(
      new Label("Player Type"),
      new Label("Strategy"),
      new Label("AirSim Name"),
      new Label("Color"),
      new Label("Top Velocity"),
      new Label("Turning Radius")
    )
  }

  contents += header
  contents ++= players.map(p => new PlayerSettingsPanel(p))

  border = BorderFactory.createEmptyBorder(16, 8, 8, 8)

  def addPlayer(): Unit = {

  }

  def settings: Seq[PilotSettings] =
    contents
      .filter(panel => panel.isInstanceOf[PlayerSettingsPanel])
      .map(_.asInstanceOf[PlayerSettingsPanel].settings)
}
