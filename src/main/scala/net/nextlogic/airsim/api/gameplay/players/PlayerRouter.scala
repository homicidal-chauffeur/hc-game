package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Vector3r


object PlayerRouter {
  case class Player(actionType: ActionType, pilotStrategy: PilotStrategy,
                    maxVelocity: Double, turningRadius: Double,
                    vehicle: AirSimBaseClient)
  case class MoveInfo(myTheta: Double, relPosition: Vector3r, opponentsTheta: Double,
                      myPosition: Vector3r, opponentsPosition: Vector3r,
                      maxVelocity: Double, turningRadius: Double = 0)

  def moveWithTheta(player: Player, moveInfo: MoveInfo): Double = {
    player.pilotStrategy match {
      case Agile => AgilePlayer.evadeOrPursue(player, moveInfo)
      case Chauffeur => ChauffeurPlayer.evadeOrPursue(player, moveInfo)
      case HCMerz => HCMerzPlayer.evadeOrPursue(player, moveInfo)
    }
  }



}
