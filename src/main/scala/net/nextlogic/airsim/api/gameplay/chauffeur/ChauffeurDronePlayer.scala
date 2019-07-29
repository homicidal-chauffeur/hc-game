package net.nextlogic.airsim.api.gameplay.chauffeur

import net.nextlogic.airsim.api.gameplay.{AirSimBaseClient, DronePlayer}
import net.nextlogic.airsim.api.utils.{Constants, Vector3r}

case class ChauffeurDronePlayer(vehicle: AirSimBaseClient) extends DronePlayer {
  override def steer(phi: Double): Unit = {
    val dtheta = phi * (vehicle.settings.maxVelocity / Constants.turningRadius)
    theta += dtheta * Constants.timeStepForAngleChange
    println(s"${vehicle.settings.name}: Steering with theta $theta")
  }

  override def evade(relativePos: Vector3r, opponentTheta: Double): Unit = {
    val x = relativePos.x
    val y = relativePos.y

    // control variable, which is effectively turning radius
    val phi = y match {
      case 0 if x < 0 => 1
      case 0 => 0
      case _ => Math.signum(y)
    }

    //System.out.println(relativePos);
    steer(phi)
    move()
  }

  def pursue(relativePos: Vector3r, opponentTheta: Double): Unit = {
    val x = relativePos.x
    val y = relativePos.y
    // control variable, which is effectively turning radius
    println(s"${vehicle.settings.name}: relative distance ($x, $y)")

    val phi = y match {
      case 0 if x < 0 => 1
      case 0 => 0
      case _ => Math.signum(y)
    }

    steer(phi)

    move()
  }

}
