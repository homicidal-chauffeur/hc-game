package net.nextlogic.airsim.api.gameplay.agile

import java.awt.geom.Point2D

import net.nextlogic.airsim.api.gameplay.{AirSimBaseClient, DronePlayer}

case class AgileDronePlayer(vehicle: AirSimBaseClient) extends DronePlayer {
  def steer(d: Double): Unit = {
    // player is agile, so angle can be changed abruptly
    println(s"${vehicle.settings.name}: Steering with theta $d")
    theta = d
  }

  def evade(relativePos: Point2D, opponentTheta: Double): Unit = {
    val x = relativePos.getX
    val y = relativePos.getY

    println(s"${vehicle.settings.name}: relative distance ($x, $y)")

    steer(theta + Math.atan2(y, x) + Math.PI)
    move()
  }

  def pursue(relativePos: Point2D, opponentTheta: Double): Unit = {
    val x = relativePos.getX
    val y = relativePos.getY

    steer(theta + Math.atan2(y, x))
    move()
  }

}
