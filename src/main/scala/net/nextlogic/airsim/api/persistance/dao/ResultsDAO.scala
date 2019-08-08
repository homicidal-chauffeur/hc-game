package net.nextlogic.airsim.api.persistance.dao

import java.sql.Timestamp

import net.nextlogic.airsim.api.simulators.actors.ResultsWriterActor.ResultsFile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object ResultsDAO {
  val db = Database.forConfig("database")
//  val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")

  def saveResults(results: ResultsFile): Future[Long] = {
    val action = for {
      simId <- simulations returning simulations.map(_.id) += SimulationDb(results.startMillis, new Timestamp(results.startMillis), None)
      setId <- simulationSettings returning simulationSettings.map(_.id) += ResultsModels.fromSimulatorSettings(simId, results.settings)
      pilots <- pilotsReturningRow ++= results.settings.pilotSettings.map(s => ResultsModels.fromPilotSettings(setId, s))
      _ <- captures ++= results.captures.map(c => ResultsModels.fromCapture(simId, c))
      _ <- moves ++= results.moves.map(
        m => ResultsModels.fromMoveInfo(simId, pilots.filter(p => p.name == m.player.vehicle.settings.name).head.id , m)
      )
    } yield simId

    db.run(action)
  }


  class Simulations(tag: Tag) extends Table[SimulationDb](tag, "simulations") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def startTime = column[Long]("start_time")
    def date = column[Timestamp]("date_at")
    def tags = column[Option[String]]("tags")

    def * = (startTime, date, tags, id) <> (SimulationDb.tupled, SimulationDb.unapply)
  }
  val simulations = TableQuery[Simulations]

  case class SimulationSettings(tag: Tag) extends Table[SimulationSettingsDb](tag, "simulation_settings") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def simulationId = column[Long]("simulation_id")
    def ip = column[String]("ip")
    def port = column[Int]("port")
    def gameType = column[String]("game_type")
    def gamma = column[Double]("gamma")
    def beta = column[Double]("beta")
    def maxVelocityPursuer = column[Double]("max_velocity_pursuer")
    def locationUpdateDelay = column[Int]("location_update_delay")
    def gameTime = column[Int]("game_time")

    def * = (simulationId, ip, port, gameType, gamma, beta, maxVelocityPursuer,
      locationUpdateDelay, gameTime, id) <> (SimulationSettingsDb.tupled, SimulationSettingsDb.unapply)
  }
  val simulationSettings = TableQuery[SimulationSettings]

  case class PilotSettings(tag: Tag) extends Table[PilotSettingsDb](tag, "pilot_settings") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def simulationSettingsId = column[Long]("simulation_settings_id")
    def name = column[String]("name")
    def actionType = column[String]("action_type")
    def pilotStrategy = column[String]("pilot_strategy")
    def color = column[String]("color")
    def velocityType = column[String]("velocity_type")
    def turningRadius = column[Double]("turning_radius")
    def pilotDelay = column[Int]("pilot_delay")

    def * = (simulationSettingsId, name,
      actionType, pilotStrategy, color, velocityType, turningRadius,
      pilotDelay, id) <> (PilotSettingsDb.tupled, PilotSettingsDb.unapply)
  }
  val pilotSettings = TableQuery[PilotSettings]
  val pilotsReturningRow =
    pilotSettings returning pilotSettings.map(_.id) into { (pilot, id) => pilot.copy(id = id)}



  case class Captures(tag: Tag) extends Table[CaptureDb](tag, "captures") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def simulationId = column[Long]("simulation_id")
    def time = column[Long]("time")
    def distance = column[Double]("distance")

    def * = (simulationId, distance, time, id) <> (CaptureDb.tupled, CaptureDb.unapply)
  }
  val captures = TableQuery[Captures]

  case class Moves(tag: Tag) extends Table[MoveDb](tag, "moves") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def simulationId = column[Long]("simulation_id")
    def time = column[Long]("time")
    def playerConfigId = column[Long]("pilot_settings_id")
    def myTheta = column[Double]("my_theta")
    def oppTheta = column[Double]("opp_theta")

    def myPositionX = column[Double]("my_position_x")
    def myPositionY = column[Double]("my_position_y")
    def myPositionZ = column[Double]("my_position_z")

    def oppPositionX = column[Double]("opp_position_x")
    def oppPositionY = column[Double]("opp_position_y")
    def oppPositionZ = column[Double]("opp_position_z")

    def myOrientationX = column[Double]("my_orientation_x")
    def myOrientationY = column[Double]("my_orientation_y")
    def myOrientationZ = column[Double]("my_orientation_z")
    def myOrientationW = column[Double]("my_orientation_w")

    def oppOrientationX = column[Double]("opp_orientation_x")
    def oppOrientationY = column[Double]("opp_orientation_y")
    def oppOrientationZ = column[Double]("opp_orientation_z")
    def oppOrientationW = column[Double]("opp_orientation_w")

    def * = (simulationId, time, playerConfigId,
      myTheta, oppTheta,
      myPositionX, myPositionY, myPositionZ,
      oppPositionX, oppPositionY, oppPositionZ,
      myOrientationX, myOrientationY, myOrientationZ, myOrientationW,
      oppOrientationX, oppOrientationY, oppOrientationZ, oppOrientationW,
      id
    ) <> (MoveDb.tupled, MoveDb.unapply)
  }
  def moves = TableQuery[Moves]
}
