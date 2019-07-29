package net.nextlogic.airsim.api.gameplay.agile

import net.nextlogic.airsim.api.gameplay.{AirSimBaseClient, DronePlayer}
import net.nextlogic.airsim.api.utils.Vector3r

case class AgileDronePlayer(vehicle: AirSimBaseClient) extends DronePlayer {
  def steer(d: Double): Unit = {
    // player is agile, so angle can be changed abruptly
    println(s"${vehicle.settings.name}: Steering with theta $d")
    theta = d
  }

  def evade(relativePos: Vector3r, opponentTheta: Double): Unit = {
    val x = relativePos.x
    val y = relativePos.y

    println(s"${vehicle.settings.name}: relative distance ($x, $y)")

    steer(theta + Math.atan2(y, x) + Math.PI)
    move()
  }

  def pursue(relativePos: Vector3r, opponentTheta: Double): Unit = {
    val x = relativePos.x
    val y = relativePos.y

    steer(theta + Math.atan2(y, x))
    move()
  }

}
