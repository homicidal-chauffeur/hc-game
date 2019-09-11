package net.nextlogic.airsim.api.persistance.dao
import java.sql.Timestamp
import java.util

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.alpakka.slick.javadsl.SlickSession
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import net.nextlogic.airsim.api.simulators.actors.ResultsWriterActor.ResultsFile
import net.nextlogic.airsim.gameplay.SteeringDecision

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._

object SteeringDecisionsDAO {
  implicit val system = ActorSystem("airsim-location-tracker-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  implicit val session = SlickSession.forConfig("slick-postgres")
  system.registerOnTermination(() => session.close())

  import session.profile.api._

  def save(d: util.ArrayList[SteeringDecision]): Unit = {
    println("Starting saving stream...")
    val run = new Timestamp(System.currentTimeMillis() / 1000 * 1000)
    val source = Source(d.asScala.toList)
    val logger = Flow[SteeringDecision]
      .addAttributes(
        Attributes.logLevels(
          onElement = Attributes.LogLevels.Off,
          onFailure = Attributes.LogLevels.Error,
          onFinish = Attributes.LogLevels.Info)
      )

    source
        .via(logger)
      .via(Slick.flow(p =>
        sqlu"""INSERT INTO steering_decisions (label, run, name, time, rel_pos_x, rel_pos_y, my_pos_x, my_pos_y, my_pos_time, opp_pos_x, opp_pos_y, opp_pos_time, my_theta, opp_theta, phi) VALUES
                ('Pause AirSim before each move to run calc',
                  $run, ${p.name}, ${p.time}, ${p.relativePosition.getX}, ${p.relativePosition.getY},
                  ${p.myPosition.getX}, ${p.myPosition.getY}, ${p.myPositionTime},
                  ${p.opponentPosition.getX}, ${p.opponentPosition.getY}, ${p.oppPositionTime},
                  ${p.myTheta}, ${p.opponentTheta}, ${p.phi}
                  )""")
      )
      .runWith(Sink.foreach(println))

  }

}
