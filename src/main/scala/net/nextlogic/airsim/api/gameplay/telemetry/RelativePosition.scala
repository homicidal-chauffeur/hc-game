package net.nextlogic.airsim.api.gameplay.telemetry

import net.nextlogic.airsim.api.utils.Vector3r

object RelativePosition {
  def relativePosTo2D(myPosition: Vector3r, otherPos: Vector3r, theta: Double): Vector3r = {
    val x = (otherPos.x - myPosition.x) * Math.cos(theta) + (otherPos.y - myPosition.y) * Math.sin(theta)
    val y = -(otherPos.x - myPosition.x) * Math.sin(theta) + (otherPos.y - myPosition.y) * Math.cos(theta)
    Vector3r(x, y)
  }

}
