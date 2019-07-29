package net.nextlogic.airsim.gameplay.hcm;

import net.nextlogic.airsim.gameplay.DronePlayer;
import net.nextlogic.airsim.gameplay.Evader;
import net.nextlogic.airsim.gameplay.agile.AgileDronePlayer;
import net.nextlogic.airsim.gameplay.chauffeur.ChauffeurDronePlayer;

import java.awt.geom.Point2D;
import java.net.UnknownHostException;

public class HCMerzEvader extends AgileDronePlayer implements Evader {

    /* never used
    HCMerzEvader(String ip, int port, double v, double l, DronePlayer e) throws UnknownHostException {
        super(ip, port, v);

        setCaptureL(l);
        setOpponent(e);
    }

    HCMerzEvader(String ip, int port, double v, DronePlayer e) throws UnknownHostException {
        super(ip, port, v);

        setOpponent(e);
    }

    public HCMerzEvader(String ip, int port, double v) throws UnknownHostException {
        super(ip, port, v);
    }

    */

    public HCMerzEvader(String ip, String vehicle, double v, double l) throws UnknownHostException {
        super(ip, vehicle, v);

        setCaptureL(l);
    }

    public void evade() {
        Point2D relativePos = opponent.getRelativePos(this.get2DPos());
        ChauffeurDronePlayer o = (ChauffeurDronePlayer) opponent;
        double minR = o.getMinR();
        double pTheta = opponent.getTheta();

        double x = relativePos.getX();
        double y = relativePos.getY();

        System.out.printf("E rel: x=%.2f, y=%.2f\n", x, y);

        double phi;
        if ((x*x + (y - minR)*(y - minR)) < minR*minR) {
            phi = pTheta + Math.atan2(y+minR, x);
            System.out.println("In left turning circle");
        } else if ((x*x + (y + minR)*(y + minR)) < minR*minR) {
            phi = pTheta + Math.atan2(y-minR, x);;
            System.out.println("In right turning circle");
        } else if (Math.hypot(x, y) < minR) {
            phi = pTheta + Math.atan2(y, x) + Math.PI/2;
        } else {
            phi = pTheta + Math.atan2(y, x);
        }

        steeringDecisions.add(new SteeringDecision(relativePos, phi, pTheta));

        steer(phi);
        super.move();
    }
}

