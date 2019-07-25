package net.nextlogic.airsim.api.utils

object Constants {
  val IP = "10.10.0.82"
  val PORT = 41451

  val eVehicle = "Evader"
  val pVehicle = "Pursuer"

  val moveDuration = 20f
  val altitude: Float = -30f


  val planeHeight: Float = -10f
  val resetHeight: Float = -7f
  val setupVelocity = 10f
  val setupWaitTime = 20
  val TIMEOUT = 60f


  val pursuerVelocity = 3 // velocity of pursuer, m/s

  val turningRadius = 8 // turning radius of pursuer, m
  val timeStepForAngleChange = 0.1 //deltaT

  var gameType = 1 // game type

  var gamma = 1 // velocity ratio of pursuer-to-evader, unitless

  var beta = 1 // radius ratio of capture-to-turning, unitless


}
