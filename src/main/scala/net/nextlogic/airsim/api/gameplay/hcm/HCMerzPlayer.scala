package net.nextlogic.airsim.api.gameplay.hcm

import net.nextlogic.airsim.api.gameplay.{AirSimBaseClient, DronePlayer}
import net.nextlogic.airsim.api.utils.{Constants, Vector3r}

case class HCMerzPlayer(vehicle: AirSimBaseClient) extends DronePlayer {
  def steer(d: Double): Unit = {
    // player is agile, so angle can be changed abruptly
    println(s"${vehicle.settings.name}: Steering with theta $d")
    theta = d
  }

  def steerPursuer(phi: Double): Unit = {
    val dtheta = phi * (vehicle.settings.maxVelocity / Constants.turningRadius)
    theta += dtheta * Constants.timeStepForAngleChange
    println(s"${vehicle.settings.name}: Steering with theta $theta")
  }

  override def evade(relativePos: Vector3r, opponentTheta: Double): Unit = {
    // val o = opponent.asInstanceOf[ChauffeurDronePlayer]
    val minR = Constants.turningRadius // o.getMinR
    val x = relativePos.x
    val y = relativePos.y
    System.out.println(s"E rel: x=$x, y=$y")
    var phi = .0
    if ((x * x + (y - minR) * (y - minR)) < minR * minR) {
      phi = opponentTheta + Math.atan2(y + minR, x)
      System.out.println("In left turning circle")
    }
    else if ((x * x + (y + minR) * (y + minR)) < minR * minR) {
      phi = opponentTheta + Math.atan2(y - minR, x)

      System.out.println("In right turning circle")
    }
    else if (Math.hypot(x, y) < minR) phi = opponentTheta + Math.atan2(y, x) + Math.PI / 2
    else phi = opponentTheta + Math.atan2(y, x)

    steer(phi)
    move()
  }

  override def pursue(relativePosition: Vector3r, opponentTheta: Double): Unit = {
    val y = relativePosition.y
    val x = relativePosition.x

    val phi = y match {
      case 0 if (x < 0) => 1
      case 0 => 0
      case _ => Math.signum(y)
    }

    steerPursuer(phi)
    move()
  }


}
