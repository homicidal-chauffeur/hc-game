package net.nextlogic.airsim.api.ui.settings

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.utils.Constants
import org.junit.runner.RunWith
import org.scalatest.WordSpecLike
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SettingsPanelSpec extends WordSpecLike {
  "A SettingsPanel" when {
    "created" should {
      "be able to generate settings in" in {
        val p = new SettingsPanel(SimulatorSettings(pilotSettings = UiUtils.defaultPlayers))
        val s = p.settings
        assertResult(Constants.IP){s.ip}
      }
    }
    "updated" should {
      "get updated settings" in {
        val p = new SettingsPanel(SimulatorSettings(pilotSettings = UiUtils.defaultPlayers))
        p.ipField.text = "1.2.3.4"
        val s = p.settings
        assertResult("1.2.3.4"){s.ip}

      }
    }
  }

}
