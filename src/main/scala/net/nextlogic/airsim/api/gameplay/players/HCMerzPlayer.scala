package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.utils.Constants

object HCMerzPlayer extends BasePlayer {
  def steerEvade(theta: Double): Double =
  // player is agile, so angle can be changed abruptly
    theta

  def steerPursue(phi: Double, moveInfo: MoveInfo): Double = {
    val dtheta = phi * (moveInfo.maxVelocity / moveInfo.turningRadius)
    moveInfo.myTheta + dtheta * Constants.timeStepForAngleChange
  }


  def evade(moveInfo: MoveInfo): Double = {
    val minR = moveInfo.turningRadius
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    var phi = 0.0
    if ((x * x + (y - minR) * (y - minR)) < minR * minR) {
      phi = moveInfo.opponentsTheta + Math.atan2(y + minR, x)
      System.out.println("In left turning circle")
    }
    else if ((x * x + (y + minR) * (y + minR)) < minR * minR) {
      phi = moveInfo.opponentsTheta + Math.atan2(y - minR, x)

      System.out.println("In right turning circle")
    }
    else if (Math.hypot(x, y) < minR) phi = moveInfo.opponentsTheta + Math.atan2(y, x) + Math.PI / 2
    else phi = moveInfo.opponentsTheta + Math.atan2(y, x)

    steerEvade(phi)
  }

  def pursue(moveInfo: MoveInfo): Double = {
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    val phi = y match {
      case 0 if (x < 0) => 1
      case 0 => 0
      case _ => Math.signum(y)
    }

    steerPursue(phi, moveInfo)

  }
}
