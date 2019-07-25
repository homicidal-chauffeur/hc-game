package net.nextlogic.airsim.api.gameplay.telemetry

import java.awt.geom.Point2D

case class RelativePositionTracker(evader: PositionTracker, pursuer: PositionTracker,
                                   captureRadius: Double) {
  def relativePosTo2D(myPosition: Point2D, otherPos: Point2D, theta: Double): Point2D = {
    val x_e = otherPos.getX
    val y_e = otherPos.getY
    val x_p = myPosition.getX
    val y_p = myPosition.getY
    val x = (x_e - x_p) * Math.cos(theta) + (y_e - y_p) * Math.sin(theta)
    val y = -(x_e - x_p) * Math.sin(theta) + (y_e - y_p) * Math.cos(theta)
    new Point2D.Double(x, y)
  }

  def relativePosForPursuer(theta: Double): Point2D = relativePosTo2D(pursuer.get2DPos, evader.get2DPos, theta)
  def relativePosForEvader(theta: Double): Point2D = relativePosTo2D(evader.get2DPos, pursuer.get2DPos, theta)

  def gameOver: Boolean = {
    val distance = evader.position.distance(pursuer.position)
    println(s"Distance: $distance < $captureRadius ? ${distance < captureRadius}")
    distance < captureRadius
  }

}
