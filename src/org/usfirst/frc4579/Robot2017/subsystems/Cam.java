// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4579.Robot2017.subsystems;

import org.usfirst.frc4579.Robot2017.RobotMap;
import org.usfirst.frc4579.Vision.GripPipeline;
import org.usfirst.frc4579.Robot2017.commands.*;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.List;
import java.util.ArrayList; //I DONT KNOW WHY WE NEED TWO PACKAGES TO GET ONE THING BUT OOOOOOK
import java.util.Date;
import java.lang.Math;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSink;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.CameraServer;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;

//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;

/**
 *
 */
public class Cam extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final Relay light = RobotMap.camLight;
    private final SpeedController lightController = RobotMap.camLightController;
    private final Relay lightRelay = RobotMap.camLightRelay;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private UsbCamera camObject;
    private CvSink cvSink;
    private CvSource cvSource;
    private GripPipeline myGripPipeline = new GripPipeline();
    private ArrayList<MatOfPoint> contourList = new ArrayList<>();
    private ArrayList<MatOfPoint> primaryContourList = new ArrayList<>();
	
    private Thread t;
    
    private boolean isStarted = false;
    private boolean isProcessing = false;
    private int RESOLUTION_X = 640;
    private int RESOLUTION_Y = 360;
    private double PIX_TO_DEG = (RESOLUTION_X / 51);
    private Point point1 = new Point(RESOLUTION_X/2,0);
    private Point point2 = new Point(RESOLUTION_X/2,RESOLUTION_Y);
    
    
    private Mat rawImage = new Mat();
	private Mat input = new Mat();
	private Mat output = new Mat();
	private Timer timer = new Timer();
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public boolean takeSnapshot() {
		cvSink.grabFrame(rawImage);
    	if (!rawImage.empty()) {
	    	rawImage.copyTo(input);
	    	myGripPipeline.process(input);
	    	output = myGripPipeline.hsvThresholdOutputPure();
	    	contourList = myGripPipeline.filterContoursOutput();
	    	Imgproc.drawContours(rawImage, contourList, -1, new Scalar(0,0,255,255), 2);
	    	Imgproc.line(rawImage, point1, point2, new Scalar(255,255,0,255));
	    	System.out.println("There are "+contourList.size()+" contours.");
	    	cvSource.putFrame(rawImage);
    	} else {
    		System.out.println("Mat image is empty!");
    	}
    	return true;
	}
    public void startProcessing(){
    	contourList.clear();
    	System.out.println("Starting processing thread");
    	t = new Thread(() -> {
    		timer.start();
    		while (!Thread.interrupted()) {
				double start = timer.get();
	        	takeSnapshot();
	        	double end = timer.get();
	        	double elapsed = (end-start);
	        	//System.out.println("Elapsed time: "+elapsed);
	        	try {
	        		Thread.sleep(20);
	        	} catch (InterruptedException e) {
	        	}
        	}
    	});
    	t.start();
    	//Only process a frame if there are no frames being processed?
    }
    public void endProcessing () {
    	if (t.getState() == Thread.State.RUNNABLE) {
    		t.interrupt();
    	}
    	System.out.println("Removing camera and server");
    	//For some reason, removing the server and camera doesn't seem to do anything.
    	//EDIT: It seems that calling removeServer actually only removes the server from a table, not destroy them.
    	//Maybe calling free() on the VideoSource/sink may remove it?
    	//Note, Mjpegserver is a subclass of Videosink.
    	
    	//Check the Mjpegserverimpl.cpp in the cscore repository. Error occurs at line 504.
    	/*
    	CameraServer.getInstance().removeServer("serve_ContourVideo");
    	CameraServer.getInstance().removeServer("serve_Usb Camera 0");
    	CameraServer.getInstance().removeCamera("ContourVideo");
    	CameraServer.getInstance().removeCamera("Usb Camera 0"); 
    	*/
    }
    public void lightOn(double brightness) {
    	//Since the light controller is technically a motor controller, there is no lightOff() method.
    	lightController.set(brightness);
    }
    public void changeFPS(int framerate) {
    	//The framerate could be lowered when doing image processing, and raised while under driver control.
    	if(framerate > 30 || framerate < 1) {
    		framerate = 30;
    	}
    	camObject.setFPS(framerate);
    }
    public double getDistanceFromCenter(Rect leftRect, Rect rightRect) {
    	double leftBotRight = leftRect.br().x;
    	double rectGap = rightRect.tl().x - leftBotRight;
    	double pegPosition = leftBotRight + (rectGap/2);
    	
    	//if the pegPosition is left, then num is positive, if right then negative
    	double errorInPixels = (RESOLUTION_X/2) - pegPosition;
    	double errorInDegrees = errorInPixels / PIX_TO_DEG;
    	return errorInDegrees;
    }
    public void initCamera() {
    	if (!isStarted) {
    		System.out.println("Starting the camera!");
    		isStarted = true;
    		//Starts the camera. THIS SHOULD BE CALLED EVEN IN AUTONOMOUS. getVideo() will NOT work without startAutomaticCapture or addServer().
        	camObject = CameraServer.getInstance().startAutomaticCapture();
        	camObject.setResolution(RESOLUTION_X, RESOLUTION_Y);
        	camObject.setFPS(24); // just because you set it at a fps doesn't mean it will run at that fps
        	camObject.setBrightness(-10);
        	camObject.setWhiteBalanceManual(0);
        	camObject.setExposureManual(-10);
        	cvSink = CameraServer.getInstance().getVideo();
        	//cvSource = CameraServer.getInstance().putVideo("ContourVideo", 320, 240); //Only the streaming res is low
        	cvSource = new CvSource("ContourVideo", VideoMode.PixelFormat.kMJPEG, 320, 240, 30);
        	CameraServer.getInstance().addCamera(cvSource);
            VideoSink server = CameraServer.getInstance().addServer("serve_" + cvSource.getName());
            server.setSource(cvSource);
    	}
    	
    }
    public void setPrimaryContours(ArrayList<MatOfPoint> list) {
    	primaryContourList = list;
    }
    public ArrayList<MatOfPoint> getPrimaryContours() {
    	return primaryContourList;
    }
    public ArrayList<MatOfPoint> getContours() {
    	return contourList;
    }
    /*
     * Returns the degree between the robot center and the peg in degrees
     */
    public double getErrorFromContours(MatOfPoint contour1, MatOfPoint contour2) {
    	Rect rect1 = Imgproc.boundingRect(contour1);
    	Rect rect2 = Imgproc.boundingRect(contour2);
    	double errorInDegrees;
    	if (rect1.tl().x < rect2.tl().x) { //If rect1 is 
    		// If rect1.x is smaller than rect2.x then rect1 is the left rect
    		errorInDegrees = getDistanceFromCenter(rect1,rect2);
    	} else if (rect1.tl().x > rect2.tl().x) {
    		//Rect 2 is smaller, and is the left one 
    		errorInDegrees = getDistanceFromCenter(rect2, rect1);
    	} else {
    		//Rects are the same or some other error, do nothing...?
    		errorInDegrees = 99999;
    	}
    	//left is positive, right is negative
    	return errorInDegrees;
    }
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND
    	//setDefaultCommand(new Auto_PlaceGear("LEFT"));
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
}

