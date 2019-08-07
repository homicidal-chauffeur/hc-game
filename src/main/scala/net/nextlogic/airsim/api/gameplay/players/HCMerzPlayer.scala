package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.utils.Constants

object HCMerzPlayer extends BasePlayer {
  def steerEvade(theta: Double): Double =
  // player is agile, so angle can be changed abruptly
    theta

  def steerPursue(phi: Double, moveInfo: MoveInfo): Double = {
    val dtheta = phi * (moveInfo.player.maxVelocity / moveInfo.player.turningRadius)
    moveInfo.myTheta + dtheta * Constants.timeStepForAngleChange
  }


  def evade(moveInfo: MoveInfo): Double = {
    val minR = moveInfo.player.turningRadius
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    val phi =
      if ((x * x + (y - minR) * (y - minR)) < minR * minR) {
        System.out.println("In left turning circle")
        moveInfo.opponentsTheta + Math.atan2(y + minR, x)
      }
      else if ((x * x + (y + minR) * (y + minR)) < minR * minR) {
        System.out.println("In right turning circle")
        moveInfo.opponentsTheta + Math.atan2(y - minR, x)
      }
      else if (Math.hypot(x, y) < minR) {
        moveInfo.opponentsTheta + Math.atan2(y, x) + Math.PI / 2
      }
      else moveInfo.opponentsTheta + Math.atan2(y, x)

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
