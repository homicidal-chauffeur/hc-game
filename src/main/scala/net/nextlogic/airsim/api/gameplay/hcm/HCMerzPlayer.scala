//package net.nextlogic.airsim.api.gameplay.hcm
//
//import java.awt.geom.Point2D
//
//import net.nextlogic.airsim.api.gameplay.AirSimBaseClient
//import net.nextlogic.airsim.api.gameplay.telemetry.{PositionTracker, RelativePositionTracker}
//
//case class HCMerzPlayer(vehicle: AirSimBaseClient) {
//  val tracker = PositionTracker(vehicle)
//  var theta: Double = 0.0
//
//  override def evade(relativePosition: RelativePositionTracker): Unit = {
//    val relativePos = relativePosition.getCurrentRelativePos
//    val o = opponent.asInstanceOf[ChauffeurDronePlayer]
//    val minR = o.getMinR
//    val pTheta = opponent.getTheta
//    val x = relativePos.getX
//    val y = relativePos.getY
//    System.out.printf("E rel: x=%.2f, y=%.2f\n", x, y)
//    var phi = .0
//    if ((x * x + (y - minR) * (y - minR)) < minR * minR) {
//      phi = pTheta + Math.atan2(y + minR, x)
//      System.out.println("In left turning circle")
//    }
//    else if ((x * x + (y + minR) * (y + minR)) < minR * minR) {
//      phi = pTheta + Math.atan2(y - minR, x)
//
//      System.out.println("In right turning circle")
//    }
//    else if (Math.hypot(x, y) < minR) phi = pTheta + Math.atan2(y, x) + Math.PI / 2
//    else phi = pTheta + Math.atan2(y, x)
//    steer(phi)
//    super.move()
//  }
//
//
//}
