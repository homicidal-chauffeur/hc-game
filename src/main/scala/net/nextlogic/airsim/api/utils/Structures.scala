package net.nextlogic.airsim.api.utils

import java.util

import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import org.msgpack.`type`.MapValue
import play.api.libs.json.{Json, OWrites, Reads, Writes, __}
import play.api.libs.functional.syntax._

import scala.collection.JavaConverters._

case class VehicleSettings(name: String, actionType: ActionType)

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

case class Vector3r(x: Double = 0f, y: Double = 0f, z: Double = 0f) {
  override def toString: String = s"[x, y, z] = [$x, $y, $z]"

  def toMap: Map[String, Double] = Map("x_val" -> x, "y_val" -> y, "z_val" -> z)

  def distance(other: Vector3r): Double = {
    val xDiff = other.x - this.x
    val yDiff = other.y - this.y
    val zDiff = other.z - this.z

    Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff)
  }

  def distance2D(other: Vector3r): Double = {
    val xDiff = other.x - this.x
    val yDiff = other.y - this.y

    Math.sqrt(xDiff * xDiff + yDiff * yDiff)
  }
}

object Vector3r {
  implicit val reads: Reads[Vector3r] = (
    (__ \ "x_val").read[Double] and
      (__ \ "y_val").read[Double] and
      (__ \ "z_val").read[Double]
    )(Vector3r.apply _)

  implicit val writes: Writes[Vector3r] = (
    (__ \ "x_val").write[Double] and
      (__ \ "y_val").write[Double] and
      (__ \ "z_val").write[Double]
    )(unlift(Vector3r.unapply))

}

case class Quaternionr(x: Double = 0f, y: Double = 0f, z: Double = 0f, w: Double = 1f) {
  override def toString: String = s"[x, y, z, w] = [$x, $y, $z, $w]"
}

object Quaternionr {
  implicit val reads: Reads[Quaternionr] = (
    (__ \ "x_val").read[Double] and
      (__ \ "y_val").read[Double] and
      (__ \ "z_val").read[Double] and
      (__ \ "w_val").read[Double]
    )(Quaternionr.apply _)

  implicit val writes: Writes[Quaternionr] = (
    (__ \ "x_val").write[Double] and
      (__ \ "y_val").write[Double] and
      (__ \ "z_val").write[Double] and
      (__ \ "w_val").write[Double]
    )(unlift(Quaternionr.unapply))

}

case class GeoPoint(lat: Double = 0f, long: Double = 0f, alt: Double = 0) {
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
    (__ \ "latitude").read[Double] and
      (__ \ "longitude").read[Double] and
      (__ \ "altitude").read[Double]
    )(GeoPoint.apply _)
  implicit val writes: Writes[GeoPoint] = (
    (__ \ "latitude").write[Double] and
      (__ \ "longitude").write[Double] and
      (__ \ "altitude").write[Double]
    )(unlift(GeoPoint.unapply))
}
