package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.Player
import net.nextlogic.airsim.api.simulators.settings.PilotSettings.{Evade, HCMerz}
import net.nextlogic.airsim.api.utils.VehicleSettings
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner
import play.api.libs.json.Json

trait MyInterface {
  def someMethod : String
}

@RunWith(classOf[JUnitRunner])
class PlayerRouterSpec extends WordSpecLike with Matchers with MockFactory  {

  "Player " should {
      "serialize to JSON using the vehicle name" in {
        val settings = VehicleSettings("Testy", Evade)
        val p = Player(Evade, HCMerz, 25.3, 1.4, 1000, AirSimBaseClient("0.0.0.0", 8080, settings))
        noException should be thrownBy Json.toJson(p)
      }
  }
}
