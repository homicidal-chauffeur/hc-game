package net.nextlogic.airsim.gameplay.manual;

import net.nextlogic.airsim.gameplay.agile.AgileDronePlayer;

public class AgileXboxPlayer extends AgileDronePlayer {
    XboxController xbox;
    public AgileXboxPlayer(String ip, String vehicle, double v) throws Exception {
        super(ip, vehicle, v);

        xbox = new XboxController();
        if (!xbox.gamepadSet()) {
            throw new Exception("Could not find XBox controller.");
        }
    }

    @Override
    public void evade() {
        steer(xbox.pollLeftJoyStick());
        move();
    }

    @Override
    public void pursue() {
        steer(xbox.pollLeftJoyStick());
        move();
    }
}
