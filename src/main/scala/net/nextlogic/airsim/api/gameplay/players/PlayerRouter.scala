package net.nextlogic.airsim.api.gameplay.players

import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.{Quaternionr, Vector3r}
import play.api.libs.json.{JsValue, Json, Writes}


object PlayerRouter {
  case class Player(actionType: ActionType, pilotStrategy: PilotStrategy,
                    maxVelocity: Double, turningRadius: Double, pilotDelay: Int,
                    vehicle: AirSimBaseClient)
  implicit object PlayerWrites extends Writes[Player] {
    def writes(p: Player): JsValue = Json.toJson(p.vehicle.settings.name)
  }

  case class MoveInfo(player: Player,
                      myTheta: Double, relPosition: Vector3r, oppTheta: Double,
                      myPosition: Vector3r, oppPosition: Vector3r,
                      myOrientation: Quaternionr, oppOrientation: Quaternionr,
                      time: Long = System.currentTimeMillis())

  object MoveInfo {
    implicit val writes: Writes[MoveInfo] = Json.writes[MoveInfo]
  }

  def moveWithTheta(moveInfo: MoveInfo): Double = {
    moveInfo.player.pilotStrategy match {
      case Agile => AgilePlayer.evadeOrPursue(moveInfo)
      case Chauffeur => ChauffeurPlayer.evadeOrPursue(moveInfo)
      case HCMerz => HCMerzPlayer.evadeOrPursue(moveInfo)
    }
  }



}
