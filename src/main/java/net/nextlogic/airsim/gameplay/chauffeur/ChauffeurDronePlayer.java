package net.nextlogic.airsim.gameplay.chauffeur;

import net.nextlogic.airsim.Simulator;
import net.nextlogic.airsim.gameplay.DronePlayer;

import java.net.UnknownHostException;

public class ChauffeurDronePlayer extends DronePlayer {
    final static double deltaT = 0.08; // time step for angle change
    protected double minR; // minimum turning radius

    public ChauffeurDronePlayer(String ip, String vehicle, double v, double r) throws UnknownHostException {
        super(ip, vehicle, v);

        minR = r;
    }

    public double getMinR() {
        return minR;
    }

    public void steer(double phi) {
        double dtheta = phi*(maxV/minR);
        theta += dtheta*deltaT;
    }

    public void move() {
        Vector3r vel = new Vector3r((float) (maxV*Math.cos(theta)), (float) (maxV*Math.sin(theta)), 0f);
        moveByVelocityZ(vel, new Vector3r(0, 0, Simulator.planeHeight), dt, DrivetrainType.MaxDegreeOfFreedom,
                new YawMode());

//		moveByAngle(-0.1f, 0f, -5f, (float) theta, dt);

        updatePositionData();
    }

}
