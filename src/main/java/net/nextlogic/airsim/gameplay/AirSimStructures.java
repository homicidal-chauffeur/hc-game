package net.nextlogic.airsim.gameplay;


import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirSimStructures {
    public static class YawMode {
        public boolean is_rate;
        public float yaw_or_rate;

        public YawMode() {
            this.is_rate = true;
            this.yaw_or_rate = 0;
        }

        public String toString() {
            return String.format("{'is_rate': %s, 'yaw_or_rate': %f}", this.is_rate, this.yaw_or_rate);
        }

        public YawMode(boolean is_rate, float yaw_or_rate) {
            this.is_rate = is_rate;
            this.yaw_or_rate = yaw_or_rate;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("is_rate", this.is_rate);
            map.put("yaw_or_rate", this.yaw_or_rate);

            return map;
        }
    }

    // static method
    public static byte[]  stringToUint8Array(String bstr) {
        return bstr.getBytes();
    }


    // helper method for converting getOrientation to roll/pitch/yaw
    // https{#en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
    // static method
    public static Triplet toEulerianAngle(Quaternionr q) {
        List<Float> xyzw = q.getXYZW();
        float x = xyzw.get(0);
        float y = xyzw.get(1);
        float z = xyzw.get(2);
        float w = xyzw.get(3);
        float ysqr = y*y;

        // roll (x-axis rotation)
        float t0 = 2.0f * (w*x + y*z);
        float t1 = 1.0f - 2.0f*(x*x + ysqr);
        float roll = (float) Math.atan2(t0, t1);

        // pitch (y-axis rotation)
        float t2 = 2.0f * (w*y - z*x);

        if (t2 > 1.0f) {
            t2 = 1.0f;
        }

        if (t2 < -1.0f) {
            t2 = -1.0f;
        }

        float pitch = (float) Math.asin(t2);

        // yaw (z-axis rotation)
        float t3 = 2.0f * (w*z + x*y);
        float t4 = 1.0f - 2.0f * (ysqr + z*z);
        float yaw = (float) Math.atan2(t3, t4);

        return new Triplet(pitch, roll, yaw);
    }

    // static method
    public static Quaternionr toQuaternion(Triplet pry) {
        float pitch = pry.get(0);
        float roll = pry.get(1);
        float yaw = pry.get(2);

        float t0 = (float) Math.cos(yaw * 0.5);
        float t1 = (float) Math.sin(yaw * 0.5);
        float t2 = (float) Math.cos(roll * 0.5);
        float t3 = (float) Math.sin(roll * 0.5);
        float t4 = (float) Math.cos(pitch * 0.5);
        float t5 = (float) Math.sin(pitch * 0.5);

        float x_val = t0 * t3 * t4 - t1 * t2 * t5;
        float y_val = t0 * t2 * t5 + t1 * t3 * t4;
        float z_val = t1 * t2 * t4 - t0 * t3 * t5;
        float w_val = t0 * t2 * t4 + t1 * t3 * t5;

        return new Quaternionr(x_val, y_val, z_val, w_val);

    }

    public static class AirSimImageType {
        public static int Scene = 0;
        public static int DepthPlanner = 1;
        public static int DepthPerspective = 2;
        public static int DepthVis = 3;
        public static int DisparityNormalized = 4;
        public static int Segmentation = 5;
        public static int SurfaceNormals = 6;
    }

    public static class DrivetrainType {
        public static int MaxDegreeOfFreedom = 0;
        public static int ForwardOnly = 1;
    }

    public static class LandedState {
        public static int Landed = 0;
        public static int Flying = 1;
    }

    public static class Vector3r {
        protected Map<String, Float> pos = new HashMap<String, Float>();

        public Vector3r(float x_val, float y_val, float z_val) {
            pos.put("x_val", x_val);
            pos.put("y_val", y_val);
            pos.put("z_val", z_val);
        }

        public Vector3r(Point point) {
            pos.put("x_val", point.getX_val());
            pos.put("y_val", point.getY_val());
            pos.put("z_val", point.getZ_val());
        }

        public Vector3r() {
            this(0f, 0f, 0f);
        }

        public float get(String key) {
            return pos.get(key);
        }

        public float getX() {
            return get("x_val");
        }

        public float getY() {
            return get("y_val");
        }

        public float getZ() {
            return get("z_val");
        }

        public Vector3r(MapValue response) {
            for (Value key : response.keySet()) {
                String k = key.asRawValue().getString();
                float val = response.get(key).asFloatValue().getFloat();
                pos.put(k, val);
            }
        }

        public List<Float> getXYZ() {
            List<Float> xyz = new ArrayList<Float>();

            xyz.add(pos.get("x_val"));
            xyz.add(pos.get("y_val"));
            xyz.add(pos.get("z_val"));

            return xyz;
        }

        public String toString() {
            return String.format("[x, y, z] = %s", getXYZ().toString());
        }

        public Map<String, Float> toMap() {
            return new HashMap<String, Float>(this.pos);
        }

        public double distance(Vector3r other) {
            double xDiff = (double) other.getX() - this.getX();
            double yDiff = (double) other.getY() - this.getY();
            double zDiff = (double) other.getZ() - this.getZ();

            return Math.sqrt(xDiff*xDiff + yDiff*yDiff + zDiff*zDiff);
        }
    }

    public static class Quaternionr extends Vector3r {

        public Quaternionr (float x_val, float y_val, float z_val, float w_val) {
            super(x_val, y_val, z_val);
            pos.put("w_val", w_val);
        }

        public Quaternionr () {
            super();
            pos.put("w_val", 1f);
        }

        public Quaternionr(MapValue response) {
            super(response);
        }

        public Quaternionr(Quaternion q) {
            super(q.getX_val(), q.getY_val(), q.getZ_val());
            pos.put("w_val", q.getW_val());
        }

        public List<Float> getXYZW() {
            List<Float> xyzw = getXYZ();
            xyzw.add(pos.get("w_val"));
            return xyzw;
        }

        public String toString() {
            return String.format("[x, y, z, w] = %s", getXYZW().toString());
        }
    }

    public static class Pose {
        public Vector3r position = new Vector3r();
        public Quaternionr orientation = new Quaternionr();

        public Pose(Vector3r position_val, Quaternionr orientation_val) {
            this.position = position_val;
            this.orientation = orientation_val;
        }
    }

    public static class GeoPoint {

        protected HashMap<String, Float> gp = new HashMap<String, Float>();

        public GeoPoint(float latitude, float longitude, float altitude) {
            gp.put("latitude", latitude);
            gp.put("longitude", longitude);
            gp.put("altitude", altitude);
        }

        public GeoPoint() {
            this(0f, 0f, 0f);
        }

        public GeoPoint(MapValue response) {
            for (Value key : response.keySet()) {
                String k = key.asRawValue().getString();
                float val = response.get(key).asFloatValue().getFloat();

                gp.put(k, val);
            }
        }

        public List<Float> getGlobalPos() {
            List<Float> result = new ArrayList<Float>();

            result.add(gp.get("latitude"));
            result.add(gp.get("longitude"));
            result.add(gp.get("altitude"));

            return result;
        }

        public boolean isZero() {
            List<Float> zero = new ArrayList<Float>();
            zero.add(0f);
            zero.add(0f);
            zero.add(0f);

            return this.getGlobalPos().equals(zero);
        }

        public String toString() {
            return String.format("Lat=%f, Long=%f, Alt=%f", gp.get("latitude"),
                    gp.get("longitude"), gp.get("altitude"));
        }
    }


    public static class CollisionInfo {
        public boolean has_collided = false;
        public Vector3r normal = new Vector3r();
        public Vector3r impact_point = new Vector3r();
        public Vector3r position = new Vector3r();
        public float penetration_depth = 0;
        public float time_stamp = 0;
    }

    public static class ImageRequest {
        public int camera_id = 0;
        public int image_type = AirSimImageType.Scene;
        public boolean pixels_as_float = false;
        public boolean compress = false;

        public ImageRequest(int camera_id, int image_type, boolean pixels_as_float, boolean compress) {
            this.camera_id = camera_id;
            this.image_type = image_type;
            this.pixels_as_float = pixels_as_float;
            this.compress = compress;
        }

        public ImageRequest(int camera_id, int image_type) {
            this.camera_id = camera_id;
            this.image_type = image_type;
            this.pixels_as_float = false;
            this.compress = true;
        }

        public ImageRequest(int camera_id, int image_type, boolean pixels_as_float) {
            this.camera_id = camera_id;
            this.image_type = image_type;
            this.pixels_as_float = pixels_as_float;
            this.compress = true;
        }


    }

    public static class ImageResponse {
        public int image_data_uint8 = 0;
        public float image_data_float = 0;
        public Vector3r camera_position = new Vector3r();
        public Quaternionr camera_orientation = new Quaternionr();
        public int time_stamp = 0;
        public String message = "";
        public float pixels_as_float = 0;
        public boolean compress = true;
        public int width = 0;
        public int height = 0;
        public int image_type = AirSimImageType.Scene;
    }

    public static class Triplet {
        ArrayList<Float> data;

        public Triplet(float a, float b, float c) {
            data = new ArrayList<Float>();

            data.add(a);
            data.add(b);
            data.add(c);
        }

        public float get(int index) {
            return data.get(index);
        }
    }
}
