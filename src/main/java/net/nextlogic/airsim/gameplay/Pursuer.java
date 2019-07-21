package net.nextlogic.airsim.gameplay;

import java.awt.geom.Point2D;
import java.util.List;

public interface Pursuer {
    public void pursue();
    public void updatePositionData();
    public List<Point2D> getRelativePath(DronePlayer e);
}
