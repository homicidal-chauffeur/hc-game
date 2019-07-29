package net.nextlogic.airsim.gameplay;

import com.google.gson.Gson;
import scala.Int;

public class MultirotorStateUtils {
    class KinematicsEstimated{
        public AirSimStructures.Vector3r position;
        public AirSimStructures.Quaternionr orientation;
    }

    class State {
        public Long timestamp;
        public Integer landed_state;
        public KinematicsEstimated kinematics_stimated;
    }

    public static AirSimStructures.Vector3r getPosition(String stateJson) {
        return parseState(stateJson).kinematics_stimated.position;
    }

    public static AirSimStructures.Quaternionr getOrientation(String stateJson) {
        return parseState(stateJson).kinematics_stimated.orientation;
    }

    public static boolean isLanded(String stateJson) {
        return parseState(stateJson).landed_state != 0;
    }

    private static State parseState(String stateJson) {
        Gson g = new Gson();
        System.out.println("Parsing state: " + stateJson);
        return g.fromJson(stateJson, State.class);
    }
}
