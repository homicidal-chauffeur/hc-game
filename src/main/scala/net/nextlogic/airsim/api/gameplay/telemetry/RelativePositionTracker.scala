package net.nextlogic.airsim.api.gameplay.telemetry

import java.awt.geom.Point2D

case class RelativePositionTracker(pursuer: PositionTracker, evader: PositionTracker,
                                   captureRadius: Double, var theta: Double = 0) {
  def getRelativePos(otherPos: Point2D): Point2D = {
    val position = pursuer.currentPosition.get
    val x_e = otherPos.getX
    val y_e = otherPos.getY
    val x_p = position.getX
    val y_p = position.getY
    val x = (x_e - x_p) * Math.cos(theta) + (y_e - y_p) * Math.sin(theta)
    val y = -(x_e - x_p) * Math.sin(theta) + (y_e - y_p) * Math.cos(theta)
    new Point2D.Double(x, y)
  }

  def getCurrentRelativePos: Point2D = getRelativePos(evader.get2DPos)

  def gameOver: Boolean = {
    val distance = pursuer.position.distance(evader.position)
    println(s"Distance: $distance < $captureRadius ? ${distance < captureRadius}")
    distance < captureRadius
  }

}
