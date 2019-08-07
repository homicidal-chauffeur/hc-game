package net.nextlogic.airsim.api.simulators.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.{MoveInfo, Player}
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.Path
import net.nextlogic.airsim.api.simulators.actors.RefereeActor.Start
import net.nextlogic.airsim.api.simulators.settings.PilotSettings.{Evade, HCMerz}
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.utils.{MultirotorStateUtils, Vector3r, VehicleSettings}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ResultsWriterSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender with  WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A results writer" should {
    "write the results file" in {
      val writer = system.actorOf(ResultsWriterActor.props(SimulatorSettings()))
      //EventFilter.info(pattern = "Starting results writer...", occurrences = 1) intercept {
        writer ! Start()
      //}

      val player = Player(Evade, HCMerz, 25.3, 1.4, 1000, AirSimBaseClient("0.0.0.0", 8080, VehicleSettings("Testy", Evade)))
      val move = MoveInfo(player, 1.5, Vector3r(10, 20, 30), 2.5, Vector3r(5, 6, 7), Vector3r(7, 8, 9))

      writer ! move
      writer ! move

      val state = MultirotorStateUtils.getMultirotorState(stateJson)
      //EventFilter.debug(pattern = s"Received path from ${player.vehicle.settings.name}", occurrences = 1) intercept {
        writer ! Path(Seq(state), player.vehicle.settings)
      //}
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
