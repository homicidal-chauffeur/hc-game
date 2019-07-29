//package net.nextlogic.airsim.gameplay
//
//import com.beust.klaxon.Klaxon
//
//object MultirotorStateUtils2 {
//    fun isLanded(stateJson: String): Boolean {
//        return Klaxon().parse<State>(stateJson)!!.landed_state == 0
//    }
//
//    fun getPosition(stateJson: String): AirSimStructures.Vector3r {
//        val point = Klaxon().parse<State>(stateJson)!!.kinematics_estimated.position
//
//        return AirSimStructures.Vector3r(point)
//    }
//
//    fun getOrientation(stateJson: String): AirSimStructures.Quaternionr {
//        val q = Klaxon().parse<State>(stateJson)!!.kinematics_estimated.orientation
//
//        return AirSimStructures.Quaternionr(q)
//    }
//}
//
//class Point(val x_val: Float, val y_val: Float, val z_val: Float)
//class Quaternion(val w_val: Float, val x_val: Float, val y_val: Float, val z_val: Float)
//class KinematicsEstimated(val position: Point, val orientation: Quaternion)
//class State(val timestamp: Long, val landed_state: Int, val kinematics_estimated: KinematicsEstimated)
