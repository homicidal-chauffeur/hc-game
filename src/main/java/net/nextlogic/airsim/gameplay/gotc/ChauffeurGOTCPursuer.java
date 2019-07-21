package net.nextlogic.airsim.gameplay.gotc;

import net.nextlogic.airsim.gameplay.DronePlayer;
import net.nextlogic.airsim.gameplay.Pursuer;
import net.nextlogic.airsim.gameplay.chauffeur.ChauffeurDronePlayer;

import java.awt.geom.Point2D;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChauffeurGOTCPursuer extends ChauffeurDronePlayer implements Pursuer {
    private double eVel;

    private List<Point2D> relativeTrajectory;

    /*
    public ChauffeurGOTCPursuer(String ip, int port, double v, double r, double l, DronePlayer e) throws UnknownHostException {
        super(ip, port, v, r);

        relativeTrajectory = new ArrayList<Point2D>();

        setCaptureL(l);
        setOpponent(e);
    }

    public ChauffeurGOTCPursuer(String ip, int port, double v, double r, double l) throws UnknownHostException {
        super(ip, port, v, r);

        relativeTrajectory = new ArrayList<Point2D>();

        setCaptureL(l);
    }

    public ChauffeurGOTCPursuer(String ip, int port, double v, double r, DronePlayer e) throws UnknownHostException {
        super(ip, port, v, r);

        relativeTrajectory = new ArrayList<Point2D>();

        setOpponent(e);
    }

    */

    public ChauffeurGOTCPursuer(String ip, String vehicle, double v, double r) throws UnknownHostException {
        super(ip, vehicle, v, r);
    }

    @Override
    public void setOpponent(DronePlayer e) {
        super.setOpponent(e);
        eVel = opponent.getMaxV();
    }

    @Override
    public void updatePositionData() {
        super.updatePositionData();
        relativeTrajectory = getRelativePath(opponent);
    }

    public List<Point2D> getRelativeTrajectory() {
        return relativeTrajectory;
    }

    public void pursue() {
        Point2D relativePos = getCurrentRelativePos();

        double x = relativePos.getX();
        double y = relativePos.getY();

        // relative angle
        double relativeTheta = Math.atan2(y, x);

        // control variable, which is effectively turning radius
        double phi = -Math.signum(eVel*Math.sin(relativeTheta) - maxV);

        steer(phi);
        super.move();
    }

    public void pursueOld() {
        Vector3r ePos = opponent.getPos();

        // differences in x, y coords in global frame
        double xDiff = (ePos.getX() - position.getX());
        double yDiff = (ePos.getY() - position.getY());

        // x, y coords of evader wrt pursuer
        double x = xDiff*Math.cos(theta) - yDiff*Math.sin(theta);
        double y = -xDiff*Math.sin(theta) + yDiff*Math.cos(theta);

        // relative angle
        double relativeTheta = Math.atan2(y, x);

        // control variable, which is effectively turning radius
        double phi = -Math.signum(eVel*Math.sin(relativeTheta) - maxV);

        System.out.println(relativeTheta);
        System.out.println(phi);

        steer(phi);
        move();
    }
}
