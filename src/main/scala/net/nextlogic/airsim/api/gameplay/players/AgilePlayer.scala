package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo


object AgilePlayer extends BasePlayer {
  def steer(theta: Double): Double =
  // player is agile, so angle can be changed abruptly
  theta

  def evade(moveInfo: MoveInfo): Double = {
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    steer(moveInfo.myTheta + Math.atan2(y, x) + Math.PI)
  }

  def pursue(moveInfo: MoveInfo): Double = {
    val x = moveInfo.relPosition.x
    val y = moveInfo.relPosition.y

    steer(moveInfo.myTheta + Math.atan2(y, x))
  }
}
