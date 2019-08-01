package net.nextlogic.airsim.api.ui

import net.nextlogic.airsim.api.simulators.settings.PilotSettings._

import org.junit.runner.RunWith
import org.scalatest.{WordSpec, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner

import scala.swing.ComboBox

@RunWith(classOf[JUnitRunner])
class ComboSpec extends WordSpecLike {
  "A Combo" when {
    "Created" should {
      "Set and retrieve the value" in {
        val combo = new ComboBox[PilotColor](List(Blue, Red))
        combo.selection.item = Red
        assert(combo.selection.item == Red)
      }
      "Use the default value if nothing selected" in {
        val combo = new ComboBox[PilotColor](List(Blue, Red))
        assert(combo.selection.item == Blue)
      }
      "Be able to set default value" in {
        val combo = new ComboBox[PilotColor](List(Blue, Red)){
          selection.item = Red
        }
        assert(combo.selection.item == Red)
      }
    }
  }
}
