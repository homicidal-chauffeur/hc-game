package net.nextlogic.airsim.api.utils

import java.util

import org.msgpack.`type`.MapValue
import play.api.libs.json.{Reads, __}
import play.api.libs.functional.syntax._

import scala.collection.JavaConverters._

case class VehicleSettings(ip: String, name: String, maxVelocity: Double, turningRadius: Double = 0)

case class YawMode(isRate: Boolean = true, yawOrRate: Float = 0f) {

  override def toString: String = s"{'is_rate': $isRate, 'yaw_or_rate': $yawOrRate}"

  def toMap: util.Map[String, AnyRef] = Map(
    "is_rate" -> isRate.asInstanceOf[AnyRef], "yaw_or_rate" -> yawOrRate.asInstanceOf[AnyRef]
  ).asJava
}

case object DriveTrainType {
  val maxDegreesOfFreedom: Int = 0
  val forwardOnly: Int = 1
}

case class Vector3r(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
  override def toString: String = s"[x, y, z] = [$x, $y, $z]"

  def toMap: Map[String, Float] = Map("x_val" -> x, "y_val" -> y, "z_val" -> z)

  def distance(other: Vector3r): Double = {
    val xDiff = other.x.toDouble - this.x
    val yDiff = other.y.toDouble - this.y
    val zDiff = other.z.toDouble - this.z

    Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff)
  }
}

object Vector3r {
  implicit val reads: Reads[Vector3r] = (
    (__ \ "x_val").read[Float] and
      (__ \ "y_val").read[Float] and
      (__ \ "z_val").read[Float]
    )(Vector3r.apply _)
}

case class Quaternionr(x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 1f) {
  override def toString: String = s"[x, y, z, w] = [$x, $y, $z, $w]"
}

object Quaternionr {
  implicit val reads: Reads[Quaternionr] = (
    (__ \ "x_val").read[Float] and
      (__ \ "y_val").read[Float] and
      (__ \ "z_val").read[Float] and
      (__ \ "w_val").read[Float]
    )(Quaternionr.apply _)

}

case class GeoPoint(lat: Float = 0f, long: Float = 0f, alt: Float = 0) {
  def isZero: Boolean = lat == 0 && long == 0 && alt == 0

  override def toString: String = s"Lat=$lat, Long=$long, Alt=$alt"
}

object GeoPoint {
  def fromMap(map: MapValue): GeoPoint = GeoPoint(
    map.get("latitude").asFloatValue().getFloat,
    map.get("longitude").asFloatValue().getFloat,
    map.get("altitude").asFloatValue().getFloat
  )

  implicit val reads: Reads[GeoPoint] = (
    (__ \ "latitude").read[Float] and
      (__ \ "longitude").read[Float] and
      (__ \ "altitude").read[Float]
    )(GeoPoint.apply _)

}
