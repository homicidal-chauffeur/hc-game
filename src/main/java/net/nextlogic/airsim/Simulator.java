package net.nextlogic.airsim;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import net.nextlogic.airsim.gameplay.AirSimStructures.*;
import net.nextlogic.airsim.gameplay.DronePlayer;
import net.nextlogic.airsim.gameplay.agile.AgileEvader;
import net.nextlogic.airsim.gameplay.agile.AgilePursuer;
import net.nextlogic.airsim.gameplay.chauffeur.ChauffeurEvader;
import net.nextlogic.airsim.gameplay.chauffeur.ChauffeurPursuer;
import net.nextlogic.airsim.gameplay.gotc.ChauffeurGOTCEvader;
import net.nextlogic.airsim.gameplay.gotc.ChauffeurGOTCPursuer;
import net.nextlogic.airsim.gameplay.hcm.HCMerzEvader;
import net.nextlogic.airsim.gameplay.hcm.HCMerzPursuer;
import net.nextlogic.airsim.gameplay.manual.AgileXboxPlayer;
import net.nextlogic.airsim.visualizer.GlobalField;

public class Simulator {
    public final static float planeHeight = -6f;
    public final static float setupVelocity = 10f;
    public final static float setupWaitTime = 10f;
    public final static float TIMEOUT = 60f;

    public final static float altitude = -30;


    public final static double baseV = 3; // velocity of pursuer, m/s
    public final static double baseR = 8; // turning radius of pursuer, m

    public int gameType = 1; // game type
    public double gamma = 1; // velocity ratio of pursuer-to-evader, unitless
    public double beta = 1; // radius ratio of capture-to-turning, unitless



    public static Vector3r eInitPos = new Vector3r(0, 0, altitude);
    private double captureL;
    private DronePlayer pursuer;
    private DronePlayer evader;

    public static void main(String[] args)
    {
        if (args.length != 3) {
            System.out.println("Please specify game-type, speed ratio (gamma), and radius ratio (beta).");
            System.exit(1);
        }

        try {
            int type = Integer.parseInt(args[0]);
            double g = Double.parseDouble(args[1]);
            double b = Double.parseDouble(args[2]);

            Simulator sim = new Simulator(type, g, b);
            sim.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.exit(0);
    }

    public Simulator(int type, double g, double b) throws Exception {
        this.gameType = type;
        this.gamma = g;
        this.beta = b;

        String ipAddress = "35.189.49.107"; // localhost
        String eVehicle = "Evader";
        String pVehicle = "Pursuer";

        // player maximum velocities
        double eMaxV = gamma * baseV;
        double pMaxV = baseV;

        switch(type) {
            case 0:
                // Pedestrian Tag
                evader = new AgileEvader(ipAddress, eVehicle, eMaxV);
                pursuer = new AgilePursuer(ipAddress, pVehicle, pMaxV);
                break;
            case 1:
                // Homicidal Chauffeur
                evader = new AgileEvader(ipAddress, eVehicle, eMaxV);
                pursuer = new ChauffeurPursuer(ipAddress, pVehicle, pMaxV, baseR);
                break;
            case 2:
                // Suicidal Pedestrian
                evader = new ChauffeurEvader(ipAddress, eVehicle, eMaxV, baseR);
                pursuer = new AgilePursuer(ipAddress, pVehicle, pMaxV);
                break;
            case 3:
                // Game of Two Cars, naively combined
                evader = new ChauffeurEvader(ipAddress, eVehicle, eMaxV, baseR);
                pursuer = new ChauffeurPursuer(ipAddress, pVehicle, pMaxV, baseR);
                break;
            case 4:
                // Game of Two Cars
                evader = new ChauffeurGOTCEvader(ipAddress, eVehicle, eMaxV, baseR);
                pursuer = new ChauffeurGOTCPursuer(ipAddress, pVehicle, pMaxV, baseR);
                break;
            case 5:
                // Homicidal Chauffeur, xBox controlled
                evader = new AgileXboxPlayer(ipAddress, eVehicle, eMaxV);
                pursuer = new ChauffeurPursuer(ipAddress, pVehicle, pMaxV, baseR);
                break;
            case 6:
                // HC Merz Barrier Solution
                evader = new HCMerzEvader(ipAddress, eVehicle, eMaxV, baseR);
                pursuer = new HCMerzPursuer(ipAddress, pVehicle, pMaxV, baseR);
                break;
        }


        captureL = beta * baseR;

        System.out.println("CaptureL: " + captureL);

        evader.setCaptureL(captureL);
        pursuer.setCaptureL(captureL);

        System.out.println("CaptureL set");

        evader.setOpponent(pursuer);
        pursuer.setOpponent(evader);
        System.out.println("Opponents set");
    }

    public void run() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException {
        int rNum = 2; // minimum number of capture radii as initial position

        double r_init = rNum*captureL;
        int count = 0;
        double theta_init = 0;
        // eInitPos = new Vector3r((float) (r_init*Math.cos(theta_init)), (float) (r_init*Math.sin(theta_init)), planeHeight);
        eInitPos = new Vector3r((float) (r_init*Math.cos(theta_init)), 0.0f, altitude);

        String folderName = String.format("results/type-%d_g-%.3f_b-%.3f", gameType, gamma, beta);
        new File(folderName).mkdir();

        String folderPrefix = folderName+"/";

        GlobalField vis = new GlobalField(1600, 20, captureL);
//    	RelativeField relativeVis = new RelativeField(800, 20, captureL);
//		relativeVis.setPursuerState(new Point2D.Float(0, 0),  0);

        setupAPIControl();
        double t;

        Vector3r evaderSimInit, pursuerSimInit;

        setupPositions(eInitPos);

        //int count = 0;
        double start;
        PrintWriter resultFile = new PrintWriter(folderPrefix+"data.txt", "UTF-8");
        while (count < 1) { // was 50
            evader.setTheta(Math.acos(gamma));
            pursuer.setTheta(0);

//			System.out.println("Press any key to begin the chase:");
//			vis.waitKey();

            evader.updatePositionData();
            pursuer.updatePositionData();

            evaderSimInit = evader.getPos();
            pursuerSimInit = pursuer.getPos();
            System.out.println("Evader's initial position: " + evaderSimInit);
            System.out.println("Pursuer's initial position: " + pursuerSimInit);




            start = System.currentTimeMillis();
            t = 0;
            while (!pursuer.gameOver() && !evader.gameOver() && (t < 60)) {
                System.out.println("Time " + t);
                evader.evade();
                pursuer.pursue();

                updatePlot(vis);

                // Thread.sleep(100);
                t += 0.1;
            }

            double totalTime = (System.currentTimeMillis() - start)/1000;
            System.out.println("Time is: "+totalTime+" seconds");

            evader.hover();
            pursuer.hover();

            setPlot(vis);

            Thread.sleep((long) setupWaitTime*1000);
//			System.out.println("Press any key to save the image");
//			vis.waitKey();

            String filename = folderPrefix+String.format("test", count);
            vis.saveImage(filename+count+".png");

            boolean caught = (pursuer.gameOver() || evader.gameOver());

            String testResult = String.format("%d %.3f %.3f %.5f %.5f %s %.5f P:%s E:%s\n",
                    gameType, gamma, beta, r_init, theta_init, caught, totalTime, pursuerSimInit, evaderSimInit);
            System.out.print(testResult);

            resultFile.println(testResult);
            evader.printPath("evader", resultFile);
            pursuer.printPath("pursuer", resultFile);
            evader.printSteeringDecisions("Evader", resultFile);
            pursuer.printSteeringDecisions("Pursuer", resultFile);


            resultFile.flush();

            vis.clearAll();

            evader.clearPath();
            pursuer.clearPath();


            count++;

            r_init = (Math.floorDiv(count, 5) + rNum) * captureL;
            theta_init = Math.floorDiv(count, 5)*2*Math.PI + Math.floorMod(count,  5)*Math.PI/4;
            eInitPos = new Vector3r((float) (r_init*Math.cos(theta_init)), (float) (r_init*Math.sin(theta_init)), altitude);
            // reset(eInitPos);

            //Thread.sleep((long) TIMEOUT*1000);
        }

        evader.reset();

        resultFile.close();
    }

    public void updatePlot(GlobalField g) {
        g.setPursuerState(pursuer.get2DPos(), pursuer.getTheta());
        g.setEvaderState(evader.get2DPos(), evader.getTheta());
//		relativeVis.setEvaderState(pursuer.getCurrentRelativePos(), evader.getTheta() - pursuer.getTheta());

        g.addPursuerSegment(pursuer.getLastMovement());
        g.addEvaderSegment(evader.getLastMovement());
//		relativeVis.setEvaderPath(pursuer.getRelativeTrajectory());

        g.resetBoundaryForMax();
        g.repaint();
//		relativeVis.repaint();
    }

    public void setPlot(GlobalField g) {
        g.setPursuerPath(pursuer.getPath());
        g.setEvaderPath(evader.getPath());

        g.resetBoundaryForMax();
        g.repaint();

//		relativeVis.resetBoundaryForMax();
//		relativeVis.repaint();
    }

    public void setupAPIControl() throws InterruptedException {
        System.out.println("Setting API control");
        pursuer.confirmConnection();
        pursuer.enableApiControl(true);

        evader.confirmConnection();
        evader.enableApiControl(true);
    }

    public void setupPositions(Vector3r evaderInitPos) throws InterruptedException {
        System.out.println("Setting up positions...");
        setupPosition(pursuer, new Vector3r(0, 0, altitude));
        setupPosition(evader, evaderInitPos);
        // wait for the drone to travel so the initial position is updated correctly
        System.out.println("Waiting for the drones to reach the initial position...");
        Thread.sleep((long) (setupWaitTime * 1000));
    }

    private void setupPosition(DronePlayer drone, Vector3r initPos) throws InterruptedException {
        // Arm the drone and take off if not flying already
        System.out.println("Checking state and taking off...");
        if (drone.isLanded()) {
            // drone.armDisarm(true); they are armed from config file
            drone.takeoff(setupWaitTime);
        }

        // move to the initial position
        System.out.println("Moving to initial position...");
        drone.moveToPosition(initPos, setupVelocity);

        drone.updatePositionData();
    }
//
//    private void reset(Vector3r evaderInitPos) {
//    	double eDist = evader.get2DPos().distance(0,  0);
//    	double pDist = pursuer.get2DPos().distance(0, 0);
//    	if (eDist < pDist) {
//    		evader.moveToPosition(evaderInitPos, setupVelocity);
//    		pursuer.moveToPosition(new Vector3r(0, 0, planeHeight), setupVelocity);
//    	} else {
//    		pursuer.moveToPosition(new Vector3r(0, 0, planeHeight), setupVelocity);
//    		evader.moveToPosition(evaderInitPos, setupVelocity);
//    	}
//    }

    private void reset(Vector3r evaderInitPos) throws InterruptedException {
        Vector3r pStartPoint = new Vector3r(0, 0, altitude);

//    	Point2D eDiff = evader.getRelativePos(new Point2D.Double(evaderInitPos.getX(),
//    			evaderInitPos.getY()));
//    	double eDist = eDiff.distance(0, 0);
//
//    	double pDist = pursuer.get2DPos().distance(0, 0);

        Point2D pPos = pursuer.get2DPos();
        pursuer.moveToPosition(new Vector3r((float) pPos.getX(), (float) pPos.getY(), altitude), setupVelocity);

//    	if (eDist > pDist) {
        evader.moveToPosition(evaderInitPos, setupVelocity);

        //setupPosition(pursuer, pStartPoint);
        pursuer.moveToPosition(pStartPoint, setupVelocity);
//    	} else {
//        	pursuer.moveToPosition(pStartPoint, setupVelocity);
//        	evader.moveToPosition(evaderInitPos, setupVelocity);
//    	}
    }

}
