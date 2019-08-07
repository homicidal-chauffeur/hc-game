package net.nextlogic.airsim.api.utils

import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.ui.common.UiUtils
import org.junit.runner.RunWith
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class MultirotorStateUtilsSpec extends WordSpecLike with Matchers {
  "Multirotor status " should {
    "de-serialize from JSON (coming from AirSim)" in {
      noException should be thrownBy MultirotorStateUtils.getMultirotorState(stateJson)
    }
    "serialize back to JSON" in {
      val state = MultirotorStateUtils.getMultirotorState(stateJson)
      noException should be thrownBy Json.toJson(state)
    }
    "de-serialze from it's own serialization" in {
      val initialState = MultirotorStateUtils.getMultirotorState(stateJson)
      val json = Json.toJson(initialState).toString()
      // println(json)
      noException should be thrownBy MultirotorStateUtils.getMultirotorState(json)
      val deSerialized = MultirotorStateUtils.getMultirotorState(json)
      deSerialized shouldBe initialState
    }
  }

  val stateJson: String =
    """
      |{
      |  "collision": {
      |    "has_collided": false,
      |    "penetration_depth": 0.0,
      |    "time_stamp": 0,
      |    "normal": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.0
      |    },
      |    "impact_point": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.0
      |    },
      |    "position": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.0
      |    },
      |    "object_name": "",
      |    "object_id": -1
      |  },
      |  "kinematics_estimated": {
      |    "position": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.9382445
      |    },
      |    "orientation": {
      |      "w_val": 1.0,
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.0
      |    },
      |    "linear_velocity": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 2.100678
      |    },
      |    "angular_velocity": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.0
      |    },
      |    "linear_acceleration": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 8.125096
      |    },
      |    "angular_acceleration": {
      |      "x_val": 0.0,
      |      "y_val": 0.0,
      |      "z_val": 0.0
      |    }
      |  },
      |  "gps_location": {
      |    "latitude": 47.64201507396219,
      |    "longitude": -122.14017833285332,
      |    "altitude": 125.08802
      |  },
      |  "timestamp": 1563678930180905216,
      |  "landed_state": 0,
      |  "rc_data": {
      |    "timestamp": 0,
      |    "pitch": 0.0,
      |    "roll": 0.0,
      |    "throttle": 0.0,
      |    "yaw": 0.0,
      |    "left_z": 0.0,
      |    "right_z": 0.0,
      |    "switches": 0,
      |    "vendor_id": "",
      |    "is_initialized": false,
      |    "is_valid": false
      |  }
      |}
      |
      |""".stripMargin
}
