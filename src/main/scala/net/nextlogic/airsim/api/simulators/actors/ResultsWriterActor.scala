package net.nextlogic.airsim.api.simulators.actors

import akka.actor.{Actor, ActorLogging}
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.{MoveInfo, Player}
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.Path
import net.nextlogic.airsim.api.simulators.actors.ResultsWriterActor.MoveDetails
import net.nextlogic.airsim.api.simulators.actors.SimulationActor.StopSimulation

import scala.collection.mutable
import java.io.{File, FileWriter}
import java.time.{LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import akka.event.Logging

import com.opencsv.CSVWriter



object ResultsWriterActor {
  case class MoveDetails(player: Player, moveInfo: MoveInfo)
}

class ResultsWriterActor extends Actor with ActorLogging {
  val logger = Logging(context.system, this)
//  var moveDetails: mutable.Map[Player, mutable.Queue[MoveInfo]] =
//    mutable.Map[Player, mutable.Queue[MoveInfo]]()
  var moves = mutable.Queue[(Long, Player, MoveInfo)]()

  override def receive: Receive = {
    case Path(path, vehicleSettings) =>
      logger.debug(s"Received path from ${vehicleSettings.name}")
      val t = LocalDateTime.now()
      val st = t.format(DateTimeFormatter.ISO_DATE_TIME)
      val writer = new CSVWriter(
        new FileWriter(s"results/csv/$st.csv")
      )
      writer.writeNext(
        Array("Time", "Type", "Theta", "Opponents Theta", "Rel X", "Rel Y", "My X", "My Y", "Opp X", "Opp Y",
          "Max Velocity", "Turning Radius")
      )
//      moveDetails.keys.foreach { player =>
//        val queue = moveDetails(player)
//        logger.debug(s"${player.vehicle.settings.name}: ${queue.size} log entries found")
//        val lines: java.util.List[Array[String]] = queue.map(md => Array(
//          player.vehicle.settings.name, md.myTheta, md.opponentsTheta,
//          md.relPosition.x, md.relPosition.y,
//          md.myPosition.x, md.myPosition.y,
//          md.opponentsPosition.x, md.opponentsPosition.y,
//          md.maxVelocity, md.turningRadius
//        ).map(_.toString)
//        ).asJava
//        writer.writeAll(lines)
//      }

      val startMillis = moves.headOption.map(md => md._1).getOrElse(System.currentTimeMillis)
      moves.foreach{ playerWithMove =>
        val millis = playerWithMove._1
        val md = playerWithMove._3
        val player = playerWithMove._2

        val line = Array(
          millis - startMillis,
          player.vehicle.settings.name, md.myTheta, md.opponentsTheta,
          md.relPosition.x, md.relPosition.y,
          md.myPosition.x, md.myPosition.y,
          md.opponentsPosition.x, md.opponentsPosition.y,
          md.maxVelocity, md.turningRadius
        ).map(_.toString)

        writer.writeNext(line)
      }

      writer.close()
      logger.debug(s"Written path from ${vehicleSettings.name}")
      context.parent ! StopSimulation

    case MoveDetails(player, moveInfo) =>
//      val moves = moveDetails.getOrElse(player, mutable.Queue[MoveInfo]())
//      moves.enqueue(moveInfo)
//      moveDetails.update(player, moves)
      moves.enqueue((System.currentTimeMillis(), player, moveInfo))
  }
}
