package net.nextlogic.airsim.api.gameplay.telemetry

import java.awt.geom.Point2D

case class RelativePositionTracker(evader: PositionTracker, pursuer: PositionTracker,
                                   captureRadius: Double) {

  def relativePosForPursuer(theta: Double): Point2D =
    RelativePosition.relativePosTo2D(pursuer.get2DPos, evader.get2DPos, theta)

  def relativePosForEvader(theta: Double): Point2D =
    RelativePosition.relativePosTo2D(evader.get2DPos, pursuer.get2DPos, theta)

  def gameOver: Boolean = {
    val distance = evader.position.distance2D(pursuer.position)
    println(s"Distance: $distance < $captureRadius ? ${distance < captureRadius}")
    distance < captureRadius
  }

}
