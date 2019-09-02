package net.nextlogic.airsim.api.persistance.dao

import java.sql.Timestamp

import net.nextlogic.airsim.api.gameplay.players.PlayerRouter.MoveInfo
import net.nextlogic.airsim.api.simulators.settings.Capture
import net.nextlogic.airsim.api.simulators.settings.{PilotSettings, SimulatorSettings}
import net.nextlogic.airsim.api.utils.MultirotorState


case class SimulationDb(startTime: Long, date: Timestamp, tags: Option[String], id: Long = 0)

case class SimulationSettingsDb(simulationId: Long,
                                ip: String, port: Int, gameType: String,
                                gamma: Double, beta: Double, maxVelocityPursuer: Double,
                                locationUpdateDelay: Int, gameTime: Int, id: Long = 0)

case class PilotSettingsDb(simulationSettingsId: Long,
                           name: String,
                           actionType: String,
                           pilotStrategy: String,
                           color: String,
                           velocityType: String,
                           turningRadius: Double,
                           pilotDelay: Int,
                           id: Long = 0
                          )

case class CaptureDb(simulationId: Long,
                     distance: Double,
                     time: Long,
                     id: Long = 0)

case class MoveDb(simulationId: Long,
                  time: Long,
                  pilotSettingsId: Long,
                  myTheta: Double, oppTheta: Double,
                  myPositionX: Double, myPositionY: Double, myPositionZ: Double,
                  oppPositionX: Double, oppPositionY: Double, oppPositionZ: Double,
                  myOrientationX: Double, myOrientationY: Double, myOrientationZ: Double, myOrientationW: Double,
                  oppOrientationX: Double, oppOrientationY: Double, oppOrientationZ: Double, oppOrientationW: Double,
                  id: Long = 0
                 )

case class KinematicsPositionDb(simulationId: Long,
                                 time: Long,
                                 pilotSettingsId: Long,
                                 positionX: Double, positionY: Double, positionZ: Double,
                                 orientationX: Double, orientationY: Double, orientationZ: Double, orientationW: Double,
                                 id: Long = 0
                                )
case class KinematicsVelocityDb(
                                 simulationId: Long,
                                 time: Long,
                                 pilotSettingsId: Long,
                                 linearVelocityX: Double, linearVelocityY: Double, linearVelocityZ: Double,
                                 angularVelocityX: Double, angularVelocityY: Double, angularVelocityZ: Double,
                                 linearAccelerationX: Double, linearAccelerationY: Double, linearAccelerationZ: Double,
                                 angularAccelerationX: Double, angularAccelerationY: Double, angularAccelerationZ: Double,
                                 id: Long = 0
                               )

object ResultsModels {
  def fromSimulatorSettings(simulationId: Long, s: SimulatorSettings): SimulationSettingsDb =
    SimulationSettingsDb(
      simulationId, s.ip, s.port, s.gameType.toString,
      s.gamma, s.beta, s.maxVelocityPursuer, s.locationUpdateDelay, s.gameTime
    )

  def fromPilotSettings(simulatorSettingsId: Long, s: PilotSettings): PilotSettingsDb =
    PilotSettingsDb(
      simulatorSettingsId,
      s.name,
      s.actionType.toString, s.pilotStrategy.toString, s.color.toString,
      s.velocityType.toString, s.turningRadius, s.pilotDelay
    )

  def fromCapture(simulationId: Long, c: Capture): CaptureDb =
    CaptureDb(simulationId, c.distance, c.time)

  def fromMoveInfo(simulationId: Long, playerConfigId: Long, m: MoveInfo): MoveDb =
    MoveDb(
      simulationId,
      m.time, playerConfigId,
      m.myTheta, m.oppTheta,
      m.myPosition.x, m.myPosition.y, m.myPosition.z,
      m.oppPosition.x, m.oppPosition.y, m.oppPosition.z,
      m.myOrientation.x, m.myOrientation.y, m.myOrientation.z, m.myOrientation.w,
      m.oppOrientation.x, m.oppOrientation.y, m.oppOrientation.z, m.oppOrientation.w
    )

  def positionFromKinematics(simulationId: Long, playerConfigId: Long, m: MultirotorState): KinematicsPositionDb =
    KinematicsPositionDb(
      simulationId,
      m.timestamp, playerConfigId,
      m.kinematicsEstimated.position.x, m.kinematicsEstimated.position.y, m.kinematicsEstimated.position.z,
      m.kinematicsEstimated.orientation.x, m.kinematicsEstimated.orientation.y, m.kinematicsEstimated.orientation.z, m.kinematicsEstimated.orientation.w
    )

  def velocityFromKinematics(simulationId: Long, playerConfigId: Long, m: MultirotorState): KinematicsVelocityDb =
    KinematicsVelocityDb(
      simulationId,
      m.timestamp, playerConfigId,
      m.kinematicsEstimated.linearVelocity.x, m.kinematicsEstimated.linearVelocity.y, m.kinematicsEstimated.linearVelocity.z,
      m.kinematicsEstimated.angularVelocity.x, m.kinematicsEstimated.angularVelocity.y, m.kinematicsEstimated.angularVelocity.z,
      m.kinematicsEstimated.linearAcceleration.x, m.kinematicsEstimated.linearAcceleration.y, m.kinematicsEstimated.linearAcceleration.z,
      m.kinematicsEstimated.angularAcceleration.x, m.kinematicsEstimated.angularAcceleration.y, m.kinematicsEstimated.angularAcceleration.z,
    )

}
