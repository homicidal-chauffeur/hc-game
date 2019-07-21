package net.nextlogic.airsim.gameplay;

import net.nextlogic.airsim.Simulator;
import org.msgpack.MessagePack;
import org.msgpack.rpc.Client;
import org.msgpack.rpc.Future;
import org.msgpack.rpc.loop.EventLoop;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.net.UnknownHostException;
import java.util.List;

public class AirSimBase{

}

class AirSimClientBase extends AirSimStructures {
    protected Client client;
    protected String vehicle = "";
    protected static int PORT = 41451;

    public AirSimClientBase(String ip, String vehicle) throws UnknownHostException {
        MessagePack messagePack = new MessagePack();
        EventLoop loop = EventLoop.start(messagePack);

        if (ip.equals("")) {
            ip = "10.10.0.82";
        }

        this.client = new Client(ip, PORT, loop);
        this.vehicle = vehicle;
    }

    public String ping() {
        Value response = this.client.callApply("ping", new Object[]{});
        System.out.println("Ping result: " + response.toString());
        return response.toString();
    }

    public void confirmConnection() throws InterruptedException {
        System.out.print("Waiting for connection: ");
        GeoPoint home = this.getHomeGeoPoint();
        while (home.isZero()) {
            Thread.sleep(1000);
            home = this.getHomeGeoPoint();
            System.out.print(".");
        }

        System.out.print("connected. ");
        System.out.println("Home of " + vehicle + ": " + home.toString());
    }

    public GeoPoint getHomeGeoPoint() {
        Value result = this.client.callApply("getHomeGeoPoint", new Object[]{vehicle});

        return new GeoPoint(result.asMapValue());
    }

    // Basic flight control
    public void enableApiControl(boolean is_enabled) {
        System.out.println("Enabling api control for " + vehicle + "...");
        Value response = this.client.callApply("enableApiControl", new Object[]{true, vehicle});
//        this.client.callApply("enableApiControl", new Object[] {true, vehicle});
    }

    public boolean isApiControlEnabled() {
        Value response = this.client.callApply("isApiControlEnabled", new Object[] {vehicle});
        return response.asBooleanValue().getBoolean();
    }
}

// -----------------------------------  Multirotor APIs ---------------------------------------------
class MultirotorClient extends AirSimClientBase {
    public MultirotorClient(String ip, String vehicle) throws UnknownHostException {
        super(ip, vehicle);
    }

    public void armDisarm(boolean arm) {
        System.out.println("Arming " + vehicle + "...");
        this.client.callApply("armDisarm", new Object[] {arm, vehicle});
    }

    /* this is deprecated and replaced by applyAsync
    public boolean timeoutCommand(String command, float max_wait_seconds) {
        Value response = this.client.callApply(command, new Object[] {max_wait_seconds});
        return response.asBooleanValue().getBoolean();
    }
    */

    public boolean booleanCommand(String command) {
        Value response = this.client.callApply(command, new Object[] {vehicle});
        return response.asBooleanValue().getBoolean();
    }

    public int intCommand(String command) {
        Value response = this.client.callApply(command, new Object[] {vehicle});
        return response.asIntegerValue().getInt();
    }

    // used to be used for getPosition, getOrientation, getVelocity, etc.
    // cannot be used anymore - replaced by getMultirotorState json
    @Deprecated
    public MapValue mapCommand(String command) {
        Value response = this.client.callApply(command, new Object[] {vehicle});
        return response.asMapValue();
    }

    public void voidCommand(String command) {
        this.client.callApply(command, new Object[] {vehicle});
    }

    public Future<Value> takeoff(float max_wait_seconds) {
        // return timeoutCommand("takeoff", max_wait_seconds);
        return this.client.callAsyncApply(
                "takeoff",
                new Object[]{max_wait_seconds, vehicle}
                );
    }

    public Future<Value> land(float max_wait_seconds) {
        // return timeoutCommand("land", max_wait_seconds);
        return this.client.callAsyncApply(
                "land",
                new Object[]{max_wait_seconds, vehicle}
        );
    }

    public void goHome() {
        voidCommand("goHome");
    }

    public void hover() {
        voidCommand("hover");
    }


    // -----------------------------------  Query Methods ---------------------------------------------

    public String getMultirotorState() {
        return this.client.callApply("getMultirotorState", new Object[] {vehicle}).toString();
    }

    public Vector3r getPosition() {
        return MultirotorStateUtils.INSTANCE.getPosition(getMultirotorState());
    }

    public Vector3r getVelocity() {
        return new Vector3r(mapCommand("getVelocity"));
    }

    public Quaternionr getOrientation() {
        return MultirotorStateUtils.INSTANCE.getOrientation(getMultirotorState());
    }

    // @deprecated this doesn't work - use isLanded() instead
    @Deprecated
    public int getLandedState() {
        return intCommand("getLandedState");
    }

    public boolean isLanded() {
        return MultirotorStateUtils.INSTANCE.isLanded(getMultirotorState());
    }

    public GeoPoint getGpsLocation() {
        return new GeoPoint(mapCommand("getGpsLocation"));
    }

    public Triplet getRollPitchYaw() {
        return AirSimStructures.toEulerianAngle(this.getOrientation());
    }

//	    public CollisionInfo getCollisionInfo() {
//	        return CollisionInfo.from_msgpack(this.client.call('getCollisionInfo'))
//	    }
//
//	     getRCData(self) {
//	    #    return this.client.call('getRCData')
//	    }
//
//	     timestampNow(self) {
//	        return this.client.call('timestampNow')
//	    }
//
//	     getServerDebugInfo(self) {
//	         return this.client.call('getServerDebugInfo')

    public boolean isSimulationMode() {
        return booleanCommand("isSimulationMode");
    }


    // -----------------------------------  APIs for control ---------------------------------------------
    public Value moveByAngle(float pitch, float roll, float z,
                             float yaw, float duration) {
        Object[] args = new Object[] {pitch, roll, z, yaw, duration, vehicle};

        return this.client.callApply("moveByAngle", args);
    }

    public Value moveByVelocity(Vector3r vel, float duration,
                                int drivetrain, YawMode yaw_mode) {
        Object[] args = new Object[] {vel.getX(), vel.getY(), vel.getZ(),
                duration, drivetrain, yaw_mode.toMap(), vehicle};

        return this.client.callApply("moveByVelocity", args);
    }

    public Value moveByVelocity(Vector3r vel, float duration) {
        Object[] args = new Object[] {vel.getX(), vel.getY(), vel.getZ(), duration,
                DrivetrainType.ForwardOnly, new YawMode(), vehicle};
        return this.client.callApply("moveByVelocity", args);
    }

    public Future<Value> moveByVelocityZ(Vector3r vel, Vector3r pos, float duration,
                                         int drivetrain, YawMode yaw_mode) {
        Object[] args = new Object[] {vel.getX(), vel.getY(), pos.getZ(), duration,
                drivetrain, yaw_mode.toMap(), vehicle};
        return this.client.callAsyncApply("moveByVelocityZ", args);
    }

    public Future<Value> moveByVelocityZ(Vector3r vel, Vector3r pos, float duration) {
        int drivetrain = DrivetrainType.ForwardOnly;
        YawMode yaw_mode = new YawMode();

        return moveByVelocityZ(vel, pos, duration, drivetrain, yaw_mode);
    }

    public Future<Value> moveOnPath(List<Vector3r> path, float velocity, float max_wait_seconds, int drivetrain, YawMode yaw_mode,
                            float lookahead, float adaptive_lookahead) {
        Object[] args = new Object[] {path, velocity, max_wait_seconds,
                yaw_mode.toMap(), lookahead, adaptive_lookahead, vehicle};
        return this.client.callAsyncApply("moveOnPath", args);
    }

    public Future<Value> moveOnPath(List<Vector3r> path, float velocity) {
        float max_wait_seconds = 60;
        int drivetrain = DrivetrainType.ForwardOnly;
        YawMode yaw_mode = new YawMode();
        float lookahead = -1;
        float adaptive_lookahead = 1;
        return moveOnPath(path, velocity, max_wait_seconds, drivetrain, yaw_mode, lookahead, adaptive_lookahead);
    }

    public Future<Value> moveToZ(float z, float velocity, float max_wait_seconds,
                                 YawMode yaw_mode, float lookahead, float adaptive_lookahead) {
        Object[] args = new Object[] {z, velocity, max_wait_seconds,
                yaw_mode.toMap(), lookahead, adaptive_lookahead, vehicle};

        return this.client.callAsyncApply("moveToZ", args);
    }

    public Future<Value> moveToZ(float z, float velocity, float max_wait_seconds) {
        YawMode yaw_mode = new YawMode();
        float lookahead = -1;
        float adaptive_lookahead = 1;
        return moveToZ(z, velocity, max_wait_seconds, yaw_mode,
                lookahead, adaptive_lookahead);
    }

    public Future<Value> moveToZ(float z, float velocity) {
        float max_wait_seconds = 60;
        YawMode yaw_mode = new YawMode();
        float lookahead = -1;
        float adaptive_lookahead = 1;
        return moveToZ(z, velocity, max_wait_seconds, yaw_mode,
                lookahead, adaptive_lookahead);
    }

    public Future<Value> moveToPosition(Vector3r pos, float velocity,
                                        float max_wait_seconds, int drivetrain, YawMode yaw_mode,
                                        float lookahead, float adaptive_lookahead) {
        Object[] args = new Object[] {pos.getX(), pos.getY(), pos.getZ(), velocity, max_wait_seconds,
                drivetrain, yaw_mode.toMap(), lookahead, adaptive_lookahead, vehicle};

        return this.client.callAsyncApply("moveToPosition", args);

    }

    public Future<Value> moveToPosition(Vector3r pos, float velocity) {
        System.out.println("Sent " + vehicle + " to: " + pos);
        float max_wait_seconds = Simulator.TIMEOUT;
        int drivetrain = DrivetrainType.MaxDegreeOfFreedom;
        YawMode yaw_mode = new YawMode();
        float lookahead = -1;
        float adaptive_lookahead = 1;

        return moveToPosition(pos, velocity, max_wait_seconds, drivetrain,
                yaw_mode, lookahead, adaptive_lookahead);
    }

//	    def moveByManual(self, vx_max, vy_max, z_min, duration, drivetrain = DrivetrainType.ForwardOnly, yaw_mode = YawMode()) {
//	        return this.client.call('moveByManual', vx_max, vy_max, z_min, duration, drivetrain, yaw_mode)
//
//	    }

    public Future<Value> rotateToYaw(float yaw, float max_wait_seconds, float margin) {
        Object[] args = new Object[] {yaw, max_wait_seconds, margin, vehicle};

        return this.client.callAsyncApply("rotateToYaw", args);
    }

    public Future<Value> rotateToYaw(float yaw) {
        float max_wait_seconds = 60;
        float margin = 5;
        return rotateToYaw(yaw, max_wait_seconds, margin);
    }

    public Future<Value> rotateByYawRate(float yaw_rate, float duration) {
        Object[] args = new Object[] {yaw_rate,duration, vehicle};

        return this.client.callAsyncApply("rotateByYawRate", args);
    }

    public void shutdown() {
        this.client.close();
        this.client.getEventLoop().shutdown();
    }
}
