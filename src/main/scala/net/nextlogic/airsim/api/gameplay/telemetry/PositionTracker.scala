package net.nextlogic.airsim.api.gameplay.telemetry

import java.awt.geom.{Line2D, Point2D}

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.utils.Vector3r

import scala.collection.mutable

case class PositionTracker(vehicle: AirSimBaseClient) {
  val path: mutable.Queue[Point2D.Double] = scala.collection.mutable.Queue[Point2D.Double]()
  var lastPosition: Option[Point2D.Double] = None
  var currentPosition: Option[Point2D.Double] = None
  var position = Vector3r()

  def updatePositionData(): Unit = {
    val pos = get2DPos
    lastPosition = currentPosition
    currentPosition = Some(pos)
    println(s"${vehicle.settings.name}: Position $pos")
    this.path.enqueue(pos)
  }

  def get2DPos: Point2D.Double = {
    position = vehicle.getPosition
    new Point2D.Double(position.x, position.x)
  }

  def getLastMovement: Option[Line2D] = lastPosition.map(lp => new Line2D.Float(lp, currentPosition.get))

}
