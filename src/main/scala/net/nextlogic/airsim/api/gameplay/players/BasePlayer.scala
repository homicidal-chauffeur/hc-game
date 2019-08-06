package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.simulators.settings.PilotSettings.Evade
import net.nextlogic.airsim.api.utils.{Constants, DriveTrainType, Vector3r, YawMode}

trait BasePlayer {
  def evade(moveInfo: MoveInfo): Double
  def pursue(moveInfo: MoveInfo): Double

  def evadeOrPursue(player: PlayerRouter.Player, moveInfo: MoveInfo): Double = {
    val newTheta = player.actionType match  {
      case Evade => evade(moveInfo)
      case _ => pursue(moveInfo)
    }
    move(player, newTheta)

    newTheta
  }

  def move(player: PlayerRouter.Player, theta: Double): Unit = {
    val velocity = Vector3r(
      (player.maxVelocity * Math.cos(theta)).toFloat,
      (player.maxVelocity * Math.sin(theta)).toFloat
    )
    player.vehicle.moveByVelocityZ(
      velocity, Vector3r(0, 0, Constants.altitude),
      Constants.moveDuration, // not sure we need this at all
      DriveTrainType.maxDegreesOfFreedom, YawMode()
    )
    //		moveByAngle(-0.1f, 0f, -5f, (float) theta, dt);
    // println(s"${player.vehicle.settings.name}: Moving by velocity $velocity and theta $theta")

  }
}
