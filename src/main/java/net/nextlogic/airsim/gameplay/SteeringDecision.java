package net.nextlogic.airsim.gameplay;

import java.awt.geom.Point2D;

public class SteeringDecision {
    public String name;
    public Point2D myPosition;
    public long myPositionTime;
    public Point2D opponentPosition;
    public long oppPositionTime;
    public Point2D relativePosition;
    public double myTheta;
    public double opponentTheta;
    public double phi;
    public long time = System.currentTimeMillis();

    public SteeringDecision(String name, Point2D relativePosition,
                            Point2D myPosition, long myPositionTime,
                            Point2D opponentPosition, long oppPositionTime,
                            double myTheta, double opponentTheta,
                            double phi) {
        this.myPosition = myPosition;
        this.opponentPosition = opponentPosition;
        this.relativePosition = relativePosition;
        this.phi = phi;
        this.myTheta = myTheta;
        this.opponentTheta = opponentTheta;
        this.name = name;
        this.myPositionTime = myPositionTime;
        this.oppPositionTime = oppPositionTime;
    }

    @Override
    public String toString() {
        return this.name + "," + this.time + "," + relativePosition.getX() + "," + relativePosition.getY() + ","
                + myPosition.getY() + "," + myPosition.getY() + "," + myPositionTime + ","
                + opponentPosition.getY() + "," + opponentPosition.getY() + "," + oppPositionTime + ","
                + myTheta + "," + opponentTheta + "," + phi;
    }
}

