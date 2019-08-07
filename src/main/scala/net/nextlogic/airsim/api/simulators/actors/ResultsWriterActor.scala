package net.nextlogic.airsim.api.simulators.actors

import java.io._
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.Path
import net.nextlogic.airsim.api.simulators.actors.RefereeActor.Start
import net.nextlogic.airsim.api.simulators.actors.ResultsWriterActor.{ResultsFile, WriteFile}
import net.nextlogic.airsim.api.simulators.actors.SimulationActor.StopSimulation
import net.nextlogic.airsim.api.simulators.settings.SimulatorSettings
import net.nextlogic.airsim.api.utils.MultirotorState
import play.api.libs.json.{Json, Writes}

import scala.collection.mutable



object ResultsWriterActor {
  def props(settings: SimulatorSettings): Props =
    Props(new ResultsWriterActor(settings))

  case object WriteFile

  case class ResultsFile(startDate: LocalDateTime,
                         startMillis: Long,
                         settings: SimulatorSettings,
                         moves: Seq[MoveInfo],
                         telemetry: mutable.Map[String, Seq[MultirotorState]])
  object ResultsFile {
    implicit val formats: Writes[ResultsFile] = Json.writes[ResultsFile]
  }
}

class ResultsWriterActor(settings: SimulatorSettings) extends Actor with ActorLogging {
  val logger = Logging(context.system, this)
  var telemetry: mutable.Map[String, Seq[MultirotorState]] = mutable.Map[String, Seq[MultirotorState]]()
  var moves: mutable.Queue[MoveInfo] = mutable.Queue[MoveInfo]()

  override def receive: Receive = stoppedReceive

  def stoppedReceive: Receive = {
    case Start(startTime) =>
      logger.info("Starting results writer...")
      context.become(startedReceive(startTime), discardOld = true)

    case e => logger.debug(s"Received unexpected $e in stopped state")
  }

  def startedReceive(startTime: Long): Receive = {
    case Path(path, vehicleSettings) =>
      logger.debug(s"Received path from ${vehicleSettings.name}")
      telemetry.update(vehicleSettings.name, path)

      if (receivedAllPaths) self ! WriteFile


    case WriteFile =>
      val results = ResultsFile(
        Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDateTime,
        startTime,
        settings,
        moves,
        telemetry
      )

      val t = LocalDateTime.now()
      val st = t.format(DateTimeFormatter.ISO_DATE_TIME).replace(":", "-")
      val pw = new PrintWriter(new File(s"results/json/$st.json" ))
      try {
        pw.write(Json.toJson(results).toString())
        pw.close()
      } catch {
        case e: Exception => logger.error(s"Couldn't save the results with error ${e.getMessage}")
      } finally {
        context.parent ! StopSimulation
      }

      logger.debug("Written results and stopping the simulation...")


    case moveInfo: MoveInfo =>
      moves.enqueue(moveInfo.copy(time = startTime - moveInfo.time))
  }

  def receivedAllPaths: Boolean = telemetry.keySet.size == moves.map(_.player).distinct.size
}
