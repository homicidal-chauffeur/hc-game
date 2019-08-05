package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.utils.Constants

object ChauffeurPlayer extends BasePlayer {
  def steer(phi: Double, moveInfo: MoveInfo): Double = {
    val dtheta = phi * (moveInfo.maxVelocity / moveInfo.turningRadius)
    moveInfo.myTheta + dtheta * Constants.timeStepForAngleChange
  }

  def evade(moveInfo: MoveInfo): Double = {
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    val phi = y match {
      case 0 if x < 0 => 1
      case 0 => 0
      case _ => Math.signum(y)
    }

    steer(phi, moveInfo)
  }

  def pursue(moveInfo: MoveInfo): Double = {
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    val phi = y match {
      case 0 if x < 0 => 1
      case 0 => 0
      case _ => Math.signum(y)
    }

    steer(phi, moveInfo)
  }

}
