package net.nextlogic.airsim.api.simulators

case class SimulatorSettings(ip: String,
                             gameType: Int, gamma: Double, beta: Double,
                             maxVelocityEvader: Double, maxVelocityPursuer: Double,
                             captureDistance: Double) {

}
