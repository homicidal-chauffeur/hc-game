package net.nextlogic.airsim.api.gameplay

import net.nextlogic.airsim.api.utils.{Constants, DriveTrainType, Vector3r, YawMode}

trait DronePlayer {
  val vehicle: AirSimBaseClient
  var theta: Double = 0

  def move(): Unit = {
    val velocity = Vector3r(
      (vehicle.settings.maxVelocity * Math.cos(theta)).toFloat,
      (vehicle.settings.maxVelocity * Math.sin(theta)).toFloat, 0f
    )
    vehicle.moveByVelocityZ(
      velocity, Vector3r(0, 0, Constants.altitude),
      Constants.moveDuration, DriveTrainType.maxDegreesOfFreedom, YawMode()
    )
    //		moveByAngle(-0.1f, 0f, -5f, (float) theta, dt);
    println(s"${vehicle.settings.name}: Moving by velocity $velocity and theta $theta")
  }

  // this need to be implemented in subclasses because it depends on agility
  def steer(d: Double)

  def evade(relativePosition: Vector3r, opponentTheta: Double)

  def pursue(relativePosition: Vector3r, opponentTheta: Double)
}
