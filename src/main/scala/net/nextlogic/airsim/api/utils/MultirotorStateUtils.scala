package net.nextlogic.airsim.api.utils

import play.api.libs.json.Reads
import play.api.libs.json._
import play.api.libs.functional.syntax._

object MultirotorStateUtils {
  def parseState(stateJson: String): MultirotorState =
    Json.parse(stateJson).validate[MultirotorState]
      .asOpt.get

  def isLanded(stateJson: String): Boolean = parseState(stateJson).landedState == 0

  def getPosition(stateJson: String): Vector3r = parseState(stateJson).kinematicsEstimated.position

  def getOrientation(stateJson: String): Quaternionr = parseState(stateJson).kinematicsEstimated.orientation
}

object KinematicsEstimated {

  implicit val reads: Reads[KinematicsEstimated] = (
    (__ \ "position").read[Vector3r] and
      (__ \ "orientation").read[Quaternionr]
  )(KinematicsEstimated.apply _)
}

object MultirotorState {
  implicit val reads: Reads[MultirotorState] = (
    (__ \ "timestamp").read[Long] and
      (__ \ "landed_state").read[Int] and
      (__ \ "kinematics_estimated").read[KinematicsEstimated]
  )(MultirotorState.apply _)
}

case class KinematicsEstimated(position: Vector3r, orientation: Quaternionr)
case class MultirotorState(timestamp: Long, landedState: Int, kinematicsEstimated: KinematicsEstimated)

