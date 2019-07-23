package net.nextlogic.airsim.api.gameplay.agile

import net.nextlogic.airsim.api.gameplay.telemetry.{PositionTracker, RelativePositionTracker}
import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.utils.{Constants, DriveTrainType, Vector3r, YawMode}

case class AgileDronePlayer(vehicle: AirSimBaseClient) {
  val tracker = PositionTracker(vehicle)
  var theta: Double = 0.0

  def move(): Unit = {
    val vel = Vector3r(
      (vehicle.settings.maxVelocity * Math.cos(theta)).toFloat,
      (vehicle.settings.maxVelocity * Math.sin(theta)).toFloat, 0f
    )
    vehicle.moveByVelocityZ(
      vel, Vector3r(0, 0, Constants.planeHeight),
      Constants.moveDuration, DriveTrainType.maxDegreesOfFreedom, YawMode()
    )
    //		moveByAngle(-0.1f, 0f, -5f, (float) theta, dt);
    tracker.updatePositionData()
  }

  def steer(d: Double): Unit = {
    // player is agile, so angle can be changed abruptly
    theta = d
  }

  def evade(relativePosition: RelativePositionTracker): Unit = {
    val relativePos = relativePosition.getCurrentRelativePos
    val x = relativePos.getX
    val y = relativePos.getY

    steer(theta + Math.atan2(y, x) + Math.PI)
    move()
  }

  def pursue(relativePosition: RelativePositionTracker): Unit = {
    val relativePos = relativePosition.getCurrentRelativePos
    val x = relativePos.getX
    val y = relativePos.getY

    steer(theta + Math.atan2(y, x))
    move()
  }

}
