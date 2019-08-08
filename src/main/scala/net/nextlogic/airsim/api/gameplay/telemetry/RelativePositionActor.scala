package net.nextlogic.airsim.api.gameplay.telemetry

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import akka.event.Logging
import net.nextlogic.airsim.api.gameplay.telemetry.PositionTrackerActor.NewPosition
import net.nextlogic.airsim.api.gameplay.telemetry.RelativePositionActor._
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.{Quaternionr, Vector3r, VehicleSettings}

import scala.collection.mutable


object RelativePositionActor {
  case class ForVehicle(vehicleSettings: VehicleSettings)
  case object Distance
  case object Stop
  case class Start(trackers: Seq[ActorRef])
  case class NewTheta(theta: Double, vehicleSettings: VehicleSettings)
  case class RelativePositionWithThetas(relativePosition: Vector3r,
                                        myTheta: Double,
                                        oppTheta: Double,
                                        myPosition: Vector3r, oppPosition: Vector3r,
                                        myOrientation: Quaternionr, oppOrientation: Quaternionr)
}

class RelativePositionActor() extends Actor with ActorLogging with Timers {
  val logger = Logging(context.system, this)

  var positions: mutable.Map[VehicleSettings, Vector3r] = mutable.Map[VehicleSettings, Vector3r]()
  var orientations: mutable.Map[VehicleSettings, Quaternionr] = mutable.Map[VehicleSettings, Quaternionr]()
  var thetas: mutable.Map[VehicleSettings, Double] = mutable.Map[VehicleSettings, Double]()

  override def receive: Receive = stoppedReceive

  def startedReceive: Receive = {
    case ForVehicle(myVehicleSettings) =>
      val myPosition = positions.get(myVehicleSettings)
      val myOrientation = orientations.getOrElse(myVehicleSettings, Quaternionr())
      val myTheta = thetas.getOrElse(myVehicleSettings, 0.0d)

      val opponents = positions.keys.filter(vs => vs.actionType != myVehicleSettings.actionType)
      logger.debug(s"Getting relative position for $myVehicleSettings - opponents: ${opponents}")

      val relPosition = if (myPosition.isDefined && opponents.nonEmpty) {
        val relPositions = opponents.map { opponent =>
          val oppOrientation = orientations.getOrElse(opponent, Quaternionr())
          RelativePositionWithThetas(
            calculateRelPosition(myPosition.get, myTheta, positions(opponent)),
            myTheta,
            thetas.getOrElse(opponent, 0.0d),
            myPosition.get, positions(opponent),
            myOrientation, oppOrientation
          )
        }
        // TODO how to determine the shortest relative position???
        relPositions.headOption
      } else None
      logger.debug(s"Relative position for $myVehicleSettings: $relPosition")

      sender() ! relPosition

    case Distance =>
      // TODO refactor this as each side can have it's evaders and pursuers
      val evaderPositions = positions.keys.filter(vs => vs.actionType == Evade)
        .map(evader => positions(evader))
      val pursuerPositions = positions.keys.filter(vs => vs.actionType == Pursue)
        .map(pursuer => positions(pursuer))

      // find distances between each pursuer and each evader and return the shortest distance
      val distances = for {
        evader <- evaderPositions
        pursuer <- pursuerPositions
      } yield evader.distance2D(pursuer)

      // TODO this actually needs to traverse the distances and find the minimum
      sender() ! distances.headOption


    case NewPosition(position, orientation, vehicleSettings) =>
      positions.update(vehicleSettings, position)
      orientations.update(vehicleSettings, orientation)

    case NewTheta(theta, vehicleSettings) =>
      thetas.update(vehicleSettings, theta)

    case Stop =>
      logger.debug("Stopping relative position...")
      context.unbecome()

  }

  def stoppedReceive: Receive = {
    case _: NewPosition => sender() ! PositionTrackerActor.Stop

    case ForVehicle(_) => None
    case Distance => None
    case Start(trackers) =>
      context.become(startedReceive)
      trackers.foreach(t => t ! PositionTrackerActor.Start)
  }

  def calculateRelPosition(myPosition: Vector3r, theta: Double, opponentsPosition: Vector3r): Vector3r =
    RelativePosition.relativePosTo2D(myPosition, opponentsPosition, theta)

}
