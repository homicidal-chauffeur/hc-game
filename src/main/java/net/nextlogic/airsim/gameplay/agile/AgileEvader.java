package net.nextlogic.airsim.gameplay.agile;

import net.nextlogic.airsim.gameplay.DronePlayer;
import net.nextlogic.airsim.gameplay.Evader;
import net.nextlogic.airsim.gameplay.chauffeur.ChauffeurDronePlayer;

import java.awt.geom.Point2D;
import java.net.UnknownHostException;

public class AgileEvader extends AgileDronePlayer implements Evader {

    /* never used
    public AgileEvader(String ip, int port, double v, double l, DronePlayer p) throws UnknownHostException {
        super(ip, port, v);

        setCaptureL(l);
        setOpponent(p);
    }

    public AgileEvader(String ip, int port, double v, double l) throws UnknownHostException {
        super(ip, port, v);

        setCaptureL(l);
    }

    public AgileEvader(String ip, int port, double v, DronePlayer p) throws UnknownHostException {
        super(ip, port, v);

        setOpponent(p);
    }

    */

    public AgileEvader(String ip, String vehicle, double v) throws UnknownHostException {
        super(ip, vehicle, v);
    }




    public void evade() {
        Point2D relativePos = getCurrentRelativePos();
        System.out.println("Evader relative position: " + relativePos);
        double x = relativePos.getX();
        double y = relativePos.getY();

        steer(theta + Math.atan2(y, x) + Math.PI);
        super.move();
    }

    public void evadeJerk() {
        Point2D relativePos = getCurrentRelativePos();

        double x = relativePos.getX();
        double y = relativePos.getY();

        double relTheta = theta + Math.atan2(y, x) + Math.PI;
        if (opponent instanceof ChauffeurDronePlayer) {
            ChauffeurDronePlayer h = (ChauffeurDronePlayer) opponent;
            if (Math.hypot(x, y) <= (h.getMinR())) {
//				Random signGenerator = new Random();
//				relTheta += ( signGenerator.nextBoolean() ? 1 : -1 )*Math.PI/2;
                relTheta += Math.PI/2;
                steer(relTheta);
                super.move();
                return;
            }
        }

        steer(relTheta);
        super.move();
    }
}

