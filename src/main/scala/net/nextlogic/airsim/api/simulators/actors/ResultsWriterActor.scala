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

import scala.collection.JavaConverters._
import com.opencsv.CSVWriter
import net.nextlogic.airsim.api.utils.Vector3r



object ResultsWriterActor {
  case class MoveDetails(player: Player, moveInfo: MoveInfo)
}

class ResultsWriterActor extends Actor with ActorLogging {
  val logger = Logging(context.system, this)
  var moveDetails: mutable.Map[Player, mutable.Queue[MoveInfo]] =
    mutable.Map[Player, mutable.Queue[MoveInfo]]()

  override def receive: Receive = {
    case Path(path, vehicleSettings) =>
      logger.debug(s"Received path from ${vehicleSettings.name}")
      val t = LocalDateTime.now()
      val st = t.format(DateTimeFormatter.ISO_DATE_TIME)
      val writer = new CSVWriter(
        new FileWriter(s"results/csv/$st.csv")
      )
      writer.writeNext(
        Array("Type", "Theta", "Opponents Theta", "Rel X", "Rel Y", "My X", "My Y", "Opp X", "Opp Y",
          "Max Velocity", "Turning Radius")
      )
      moveDetails.keys.foreach { player =>
        val queue = moveDetails(player)
        logger.debug(s"${player.vehicle.settings.name}: ${queue.size} log entries found")
        val lines: java.util.List[Array[String]] = queue.map(md => Array(
          player.vehicle.settings.name, md.myTheta, md.opponentsTheta,
          md.relPosition.x, md.relPosition.y,
          md.myPosition.x, md.myPosition.y,
          md.opponentsPosition.x, md.opponentsPosition.y,
          md.maxVelocity, md.turningRadius
        ).map(_.toString)
        ).asJava
        writer.writeAll(lines)
      }
      writer.close()
      logger.debug(s"Written path from ${vehicleSettings.name}")
      context.parent ! StopSimulation

    case MoveDetails(player, moveInfo) =>
      val moves = moveDetails.getOrElse(player, mutable.Queue[MoveInfo]())
      moves.enqueue(moveInfo)
      moveDetails.update(player, moves)
    // logger.debug(s"${player.vehicle.settings.name}: Received moveInfo $moveInfo (updated to ${moves.size} steps)")
  }
}
