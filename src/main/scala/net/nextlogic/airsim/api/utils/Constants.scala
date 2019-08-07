package net.nextlogic.airsim.api.utils

object Constants {
//  val IP = "10.10.0.82"
  val IP = "35.189.49.107"
  val PORT = 41451

  val eVehicle = "Evader"
  val pVehicle = "Pursuer"

  val moveDuration = 20f
  val altitude: Float = -20f


  val planeHeight: Float = -10f
  val resetHeight: Float = -7f
  val setupVelocity = 10f
  val setupWaitTime = 20
  val TIMEOUT = 60f

  val locationUpdateDelay = 100
  val pilotDelay = 100

  val defaultGamma = 0.5
  val defaultBeta = 0.8

  val gameTime = 60

  val pursuerVelocity = 10 // velocity of pursuer, m/s

  val turningRadius = 8 // turning radius of pursuer, m
  val timeStepForAngleChange = 0.1 //deltaT

  var gameType = 1 // game type

  var gamma = 1 // velocity ratio of pursuer-to-evader, unitless

  var beta = 1 // radius ratio of capture-to-turning, unitless


}
