package net.nextlogic.airsim.api.simulators.settings

import net.nextlogic.airsim.api.ui.common.UiUtils
import org.junit.runner.RunWith
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class PilotSettingsSpec extends WordSpecLike with Matchers {
  "Pilot Settings " when {
    "Created" should {
      "serialize to JSON" in {
        val s: PilotSettings = UiUtils.defaultPlayers.head
        noException should be thrownBy Json.toJson(s)
        // println(Json.toJson(s))
        // {"actionType":"Evade","pilotStrategy":"HCMerz","name":"Evader","color":"Blue","velocityType":"Pursuer Velocity * Gamma","turningRadius":8,"pilotDelay":100}
      }
    }
  }
}
