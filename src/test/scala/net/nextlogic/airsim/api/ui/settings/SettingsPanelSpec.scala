package net.nextlogic.airsim.api.ui.settings

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.ui.visualizer.SimulationPanel
import net.nextlogic.airsim.api.utils.Constants
import org.junit.runner.RunWith
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SettingsPanelSpec extends WordSpecLike with Matchers {
  "A SettingsPanel" when {
    "created" should {
      "be able to generate settings in" in {
        val p = new SettingsPanel(SimulatorSettings(pilotSettings = UiUtils.defaultPlayers), new SimulationPanel())
        val s = p.settings
        s.ip shouldBe Constants.IP
      }
    }
    "updated" should {
      "get updated settings" in {
        val p = new SettingsPanel(SimulatorSettings(pilotSettings = UiUtils.defaultPlayers), new SimulationPanel())
        p.ipField.text = "1.2.3.4"
        val s = p.settings
        s.ip shouldBe "1.2.3.4"
      }
    }
  }

}
