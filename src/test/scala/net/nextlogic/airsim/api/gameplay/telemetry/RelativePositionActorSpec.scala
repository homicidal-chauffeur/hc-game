package net.nextlogic.airsim.api.gameplay.telemetry

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.NewPosition
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor.{ForVehicle, NewTheta, RelativePositionWithThetas, Start}
import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.utils.{Quaternionr, Vector3r, VehicleSettings}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RelativePositionActorSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A RelativePositionActor" when {
    "Doesn't have vehicle position" should {
      "Return None" in {
        val relPos = system.actorOf(Props[RelativePositionActor])
        relPos ! Start(Seq())
        val v1 = VehicleSettings("Testy", PilotSettings.Pursue)

        relPos ! ForVehicle(v1)
        val reply = expectMsgType[Option[RelativePositionWithThetas]]
        assert(reply.isEmpty)
      }
    }

    "Has both vehicle position and opponent" should {
      "Return relative position" in {
        val relPos = system.actorOf(Props[RelativePositionActor])
        relPos ! Start(Seq())
        val v1 = VehicleSettings("Pursuer", PilotSettings.Pursue)
        val v2 = VehicleSettings("Evader", PilotSettings.Evade)

        val r = scala.util.Random

        (1 to 5).foreach {_ =>
          val p1 = Vector3r(r.nextInt(100), r.nextInt(100))
          val p2 = Vector3r(r.nextInt(200), r.nextInt(200))
          val o1 = Quaternionr(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))
          val o2 = Quaternionr(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))
          val theta = r.nextDouble
          val theta2 = r.nextDouble
          relPos ! NewPosition(p1, o1, v1)
          relPos ! NewPosition(p2, o2, v2)
          relPos ! NewTheta(theta, v1)
          relPos ! NewTheta(theta2, v2)

          relPos ! ForVehicle(v1)
          val reply = expectMsgType[Option[RelativePositionWithThetas]]
          assert(reply.isDefined)
          reply.get.oppTheta shouldBe theta2
          reply.get.relativePosition shouldBe RelativePosition.relativePosTo2D(p1, p2, theta)

          relPos ! ForVehicle(v2)
          val reply2 = expectMsgType[Option[RelativePositionWithThetas]]
          println(reply2)
          assert(reply2.isDefined)
          reply2.get.oppTheta shouldBe theta
          reply2.get.relativePosition shouldBe RelativePosition.relativePosTo2D(p2, p1, theta2)
        }


      }
    }

  }
}
