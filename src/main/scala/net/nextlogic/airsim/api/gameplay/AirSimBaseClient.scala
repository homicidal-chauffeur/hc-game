package net.nextlogic.airsim.api.gameplay

import net.nextlogic.airsim.api.utils.Constants.{IP, PORT}
import net.nextlogic.airsim.api.utils.{Constants, DriveTrainType, GeoPoint, MultirotorState, MultirotorStateUtils, Quaternionr, Vector3r, VehicleSettings, YawMode}
import org.msgpack.MessagePack
import org.msgpack.`type`.Value
import org.msgpack.rpc.{Client, Future}
import org.msgpack.rpc.loop.EventLoop
import play.api.libs.json.Json

case class AirSimBaseClient(settings: VehicleSettings) {
  val client = new Client(settings.ip, PORT, EventLoop.start(new MessagePack))

  def ping: Boolean = {
    val response = this.client.callApply("ping", Array())
    println("Ping result: " + response.asBooleanValue().getBoolean)
    response.asBooleanValue().getBoolean
  }

  def getHomeGeoPoint: Option[GeoPoint] = {
    val result = this.client.callApply("getHomeGeoPoint", Array(settings.name))
    println(s"${settings.name}: Getting home point ${result.asMapValue().toString()}")
    Json.parse(result.asMapValue().toString()).validate[GeoPoint]
      .asOpt
  }

  def confirmConnection(): Unit = {
    println(s"${settings.name}: Waiting for connection: ")
    while (getHomeGeoPoint.isEmpty) {
      Thread.sleep(1000)
      System.out.print(".")
    }

    println(s"${settings.name} connected. ")
  }

  def enableApi(isEnabled: Boolean): Unit = {
    println(s"${settings.name}: Enabling api control...")
    this.client.callApply("enableApiControl", Array[AnyRef](isEnabled.asInstanceOf[AnyRef], settings.name))
  }

  def isApiControlEnabled: Boolean = {
    val response = this.client.callApply("isApiControlEnabled", Array(settings.name))
    response.asBooleanValue.getBoolean
  }

  def booleanCommand(command: String): Boolean = {
    val response = this.client.callApply(command, Array(settings.name))
    response.asBooleanValue.getBoolean
  }

  def intCommand(command: String): Int = {
    val response = this.client.callApply(command, Array(settings.name))
    response.asIntegerValue.getInt
  }

  def voidCommand(command: String): Unit = {
    this.client.callApply(command, Array[AnyRef](settings.name))
  }

  def takeoff(max_wait_seconds: Float): Future[Value] =
    this.client.callAsyncApply("takeoff", Array[AnyRef](max_wait_seconds.asInstanceOf[AnyRef], settings.name))

  def land(max_wait_seconds: Float): Future[Value] =
    this.client.callAsyncApply("land", Array[AnyRef](max_wait_seconds.asInstanceOf[AnyRef], settings.name))


  def goHome(): Unit = voidCommand("goHome")

  def hover(): Unit = voidCommand("hover")

  def armDisarm(arm: Boolean): Unit = {
    println(s"${settings.name}: Arming...")
    this.client.callApply("armDisarm", Array[AnyRef](arm.asInstanceOf[AnyRef], settings.name))
  }

  def reset(): Unit = {
    println(s"${settings.name}: Resetting...")
    this.client.callApply("reset", Array())
  }
  // -----------------------------------  Query Methods ---------------------------------------------

  def getMultirotorState: String = this.client.callApply("getMultirotorState", Array(settings.name)).toString

  def getPosition: Vector3r = MultirotorStateUtils.getPosition(getMultirotorState)

  def getOrientation: Quaternionr = MultirotorStateUtils.getOrientation(getMultirotorState)

  def isLanded: Boolean = MultirotorStateUtils.isLanded(getMultirotorState)


  // -----------------------------------  APIs for control ---------------------------------------------
  def moveByVelocityZ(vel: Vector3r, pos: Vector3r, duration: Float, drivetrain: Int, yaw_mode: YawMode): Unit = {
    val args = Array[AnyRef](
      vel.x.asInstanceOf[AnyRef], vel.y.asInstanceOf[AnyRef],
      pos.z.asInstanceOf[AnyRef], duration.asInstanceOf[AnyRef],
      drivetrain.asInstanceOf[AnyRef], yaw_mode.toMap, settings.name
    )
    this.client.callAsyncApply("moveByVelocityZ", args)
  }

  def moveToPosition(pos: Vector3r, velocity: Float,
                     maxWaitSeconds: Float = Constants.TIMEOUT,
                     driveTrain: Int = DriveTrainType.maxDegreesOfFreedom,
                     yawMode: YawMode = YawMode(),
                     lookAhead: Int = -1,
                     adaptiveLookAhead: Int = -1): Future[Value] = {
    println(s"${settings.name}: Sent to: $pos")
    val args = Array[AnyRef](
      pos.x.asInstanceOf[AnyRef], pos.y.asInstanceOf[AnyRef], pos.z.asInstanceOf[AnyRef],
      velocity.asInstanceOf[AnyRef], maxWaitSeconds.asInstanceOf[AnyRef],
      driveTrain.asInstanceOf[AnyRef], yawMode.toMap,
      lookAhead.asInstanceOf[AnyRef], adaptiveLookAhead.asInstanceOf[AnyRef],
      settings.name
    )
    this.client.callAsyncApply("moveToPosition", args)
  }

}
