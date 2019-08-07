package net.nextlogic.airsim.api.utils

import play.api.libs.json.Reads
import play.api.libs.json._
import play.api.libs.functional.syntax._

object MultirotorStateUtils {
  def parseState(stateJson: String): MultirotorState =
    Json.parse(stateJson).validate[MultirotorState]
      .asOpt.get

  def isLanded(stateJson: String): Boolean = parseState(stateJson).landedState == 0

  def getPosition(stateJson: String): Vector3r = parseState(stateJson).kinematicsEstimated.position // Timer.time()

  def getMultirotorState(stateJson: String): MultirotorState = parseState(stateJson)

  def getOrientation(stateJson: String): Quaternionr = parseState(stateJson).kinematicsEstimated.orientation
}

object KinematicsEstimated {
  implicit val reads: Reads[KinematicsEstimated] = (
    (__ \ "position").read[Vector3r] and
      (__ \ "orientation").read[Quaternionr] and
      (__ \ "linear_velocity").read[Vector3r] and
      (__ \ "angular_velocity").read[Vector3r] and
      (__ \ "linear_acceleration").read[Vector3r] and
      (__ \ "angular_acceleration").read[Vector3r]
  )(KinematicsEstimated.apply _)

  implicit val writes: Writes[KinematicsEstimated] = (
    (__ \ "position").write[Vector3r] and
    (__ \ "orientation").write[Quaternionr] and
    (__ \ "linear_velocity").write[Vector3r] and
    (__ \ "angular_velocity").write[Vector3r] and
    (__ \ "linear_acceleration").write[Vector3r] and
    (__ \ "angular_acceleration").write[Vector3r]
  )(unlift(KinematicsEstimated.unapply))
}

object MultirotorState {
  implicit val reads: Reads[MultirotorState] = (
    (__ \ "timestamp").read[Long] and
      (__ \ "landed_state").read[Int] and
      (__ \ "kinematics_estimated").read[KinematicsEstimated]
  )(MultirotorState.apply _)
  implicit val writes: Writes[MultirotorState] = (
    (__ \ "timestamp").write[Long] and
      (__ \ "landed_state").write[Int] and
      (__ \ "kinematics_estimated").write[KinematicsEstimated]
  )(unlift(MultirotorState.unapply))
}

case class KinematicsEstimated(position: Vector3r, orientation: Quaternionr,
                               linearVelocity: Vector3r, angularVelocity: Vector3r,
                               linearAcceleration: Vector3r, angularAcceleration: Vector3r)
case class MultirotorState(timestamp: Long, landedState: Int, kinematicsEstimated: KinematicsEstimated)

