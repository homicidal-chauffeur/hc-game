package net.nextlogic.airsim.gameplay.hcm;

import net.nextlogic.airsim.gameplay.DronePlayer;
import net.nextlogic.airsim.gameplay.Evader;
import net.nextlogic.airsim.gameplay.chauffeur.ChauffeurDronePlayer;

import java.awt.geom.Point2D;
import java.net.UnknownHostException;

public class HCMerzPursuer extends ChauffeurDronePlayer implements Evader {

    /* never used
    HCMerzPursuer(String ip, int port, double v, double r, double l, DronePlayer e) throws UnknownHostException {
        super(ip, port, v, r);

        setCaptureL(l);
        setOpponent(e);
    }

    HCMerzPursuer(String ip, int port, double v, double r, double l) throws UnknownHostException {
        super(ip, port, v, r);

        setCaptureL(l);
    }

    HCMerzPursuer(String ip, int port, double v, double r, DronePlayer e) throws UnknownHostException {
        super(ip, port, v, r);

        setOpponent(e);
    }

    */

    public HCMerzPursuer(String ip, String vehicle, double v, double r) throws UnknownHostException {
        super(ip, vehicle, v, r);
    }

    public void pursue() {
        Point2D relativePos = getCurrentRelativePos();

        double x = relativePos.getX();
        double y = relativePos.getY();

        // control variable, which is effectively turning radius
        double phi;
        if (y == 0) {
            if (x < 0) {
                phi = 1;
            } else {
                phi = 0;
            }
        } else {
            phi = Math.signum(y);
        }

        steeringDecisions.add(new SteeringDecision(relativePos, phi, opponent.getTheta()));

        steer(phi);
        super.move();
    }
}

