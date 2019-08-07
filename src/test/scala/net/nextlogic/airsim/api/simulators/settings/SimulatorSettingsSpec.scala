package net.nextlogic.airsim.api.simulators.settings

import net.nextlogic.airsim.api.ui.common.UiUtils
import org.junit.runner.RunWith
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class SimulatorSettingsSpec extends WordSpecLike with Matchers {
  "Simulator Settings " when {
    "Created" should {
      "serialize to JSON" in {
        val s = SimulatorSettings(pilotSettings = UiUtils.defaultPlayers)
        noException should be thrownBy Json.toJson(s)
        // println(Json.toJson(s))
        // {"ip":"35.189.49.107","port":41451,"gameType":"HCMerzSimulation","gamma":0.5,"beta":0.8,"maxVelocityPursuer":10,"locationUpdateDelay":100,"pilotSettings":[{"actionType":"Evade","pilotStrategy":"HCMerz","name":"Evader","color":"Blue","velocityType":"Pursuer Velocity * Gamma","turningRadius":8,"pilotDelay":100},{"actionType":"Pursue","pilotStrategy":"HCMerz","name":"Pursuer","color":"Red","velocityType":"Pursuer Velocity from Settings","turningRadius":8,"pilotDelay":100}]}
      }
    }
  }

}
