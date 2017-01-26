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

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;
import org.usfirst.frc4579.Robot2017.RobotMap;
import org.usfirst.frc4579.instrumentation.EventLogging;
import org.usfirst.frc4579.instrumentation.EventLogging.*;
import org.usfirst.frc4579.instrumentation.FRCSmartDashboard;

/**
 *
 */
public class Camera extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final Relay light = RobotMap.cameraLight;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS


    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    private static boolean initComplete = false;
	private FrameGrabber   frameGrabber = new FrameGrabber();
	private VideoCapture   vCap;
	
	// Camera settings
	public static final int   HORIZONTAL_IMAGE_WIDTH  = 320;                // Pixels
	public static final int   HORIZONTAL_IMAGE_CENTER = 			  	    // Pixels
						  	     HORIZONTAL_IMAGE_WIDTH / 2;
	public static final int   VERTICAL_IMAGE_WIDTH    = 240;                // Pixels
	public static final int   VERTICAL_IMAGE_CENTER   =					    // Pixels
							     VERTICAL_IMAGE_WIDTH / 2;
	public static final float CAMERA_HORIZONTAL_FOV   = 46.0f;				// Degrees
	public static final float CAMERA_HALF_HOR_FOV     =
			                     CAMERA_HORIZONTAL_FOV / 2.0f;
	
	// LifeCam 3000 property IDs
	static final int   ID_HORIZONTAL_IMAGE_WIDTH = 3;  // pixels
	static final int   ID_VERTICAL_IMAGE_WIDTH   = 4;  // pixels
	static final int   ID_BACKLIGHT_COMP         = 5;  // value range 0..10
	static final int   ID_BRIGHTNESS             = 10; // value range 30..255
	static final int   ID_CONTRAST               = 11; // value range 0..10
	static final int   ID_SATURATION             = 12; // value range 0..200
	static final int   ID_EXPOSURE               = 15; // value range -11..1
	static final int   ID_WHITEBALANCE           = 17; // value range 2800..10000;

	// Some colors that can be used wherever.
	public static final Scalar RED    = new Scalar(0, 0, 255);
	public static final Scalar BLUE   = new Scalar(255, 0, 0);
	public static final Scalar GREEN  = new Scalar(0, 255, 0);
	public static final Scalar ORANGE = new Scalar(0, 128, 255);
	public static final Scalar YELLOW = new Scalar(0, 255, 255);
	public static final Scalar PINK   = new Scalar(255, 0,255);
	public static final Scalar WHITE  = new Scalar(255, 255, 255);
	
	public static final int FRAME_GRABBER_EXECUTION_RATE_IN_HZ = 5;
	
	/*******************************************************************
	 * FRAMEGRABBER
	 * This class implements a thread that initializes the camera
	 * and grabs an image at a rate specifiec by 
	 * FRAME_GRABBER_EXECUTION_RATE_IN_HZ.  Any thread using this image
	 * should run at a slower rate.
	 *******************************************************************/
	class FrameGrabber implements Runnable {
		
		public  Boolean imageAvailable = false;
		private Thread  t;
		private Mat     image          = new Mat();
		private Timer   timer          = new Timer();
		private Boolean grabImages     = false;
		private long    sleepTime      = 0;
		private long    periodMS;
		private double  periodSecs;
		
		FrameGrabber () {
			
			// Create a new thread.
			t          = new Thread(this, "FrameGrabber Thread");
	        vCap       = new VideoCapture(0);
	        periodSecs = 1.0 / (double)FRAME_GRABBER_EXECUTION_RATE_IN_HZ;
	        periodMS   = (long)((periodSecs) * 1000.0);
	        
	        // Start the new thread.
			t.start();
		}
		
		// Entry point for the FrameGrabber thread.
		public void run() {
			
			timer.start();
			
			// Establish comm with the camera.
						
			while (!initComplete) {
				
				vCap.open(0);
				
				if (vCap.isOpened()) {
					// Confirm than we can grab a frame.
					initComplete = vCap.read(image);
				}
				
				// if no joy, wait for 1 second and try again.
				if (!initComplete) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		    FRCSmartDashboard.setCameraInitialized(initComplete);

			// Initialize camera parameters.
			vCap.set(ID_HORIZONTAL_IMAGE_WIDTH, HORIZONTAL_IMAGE_WIDTH);
			vCap.set(ID_VERTICAL_IMAGE_WIDTH, VERTICAL_IMAGE_WIDTH);
			//vCap.set(ID_BACKLIGHT_COMP, ?);         // value range 0..10
			//vCap.set(ID_BRIGHTNESS, ?);             // value range 30..255
			//vCap.set(ID_CONTRAST, ?);               // value range 0..10
			//vCap.set(ID_SATURATION, ?);             // value range 0..200
			//vCap.set(ID_EXPOSURE, ?);               // value range -11..1
			//vCap.set(ID_WHITEBALANCE, ?);           // value range 2800..10000;
			
			//for (int i=0; i<=30; i++) {
	    	//    System.out.println("Prop ID " + i + " is " + vCap.get(i));
			//}
			
			timer.stop();
			EventLogging.logNormalEvent(NORMALEVENTS.CAMERA_INITIALIZED, " in " + timer.get() + " secs.  Res: " + 
											vCap.get(ID_HORIZONTAL_IMAGE_WIDTH) + " x " + vCap.get(ID_VERTICAL_IMAGE_WIDTH));
			System.out.println("Camera initialized in " + timer.get() + " secs.");

			// Signal init complete.
			initComplete = true;

			// Grab frames from the camera.
			while (true) {
				// COMMENTED OUT BECAUSE BELOW DOESN'T EXIST ANYMORE
			    // FRCSmartDashboard.setGrabbingImages(grabImages);

			    if (grabImages) {

					timer.reset();
					timer.start();
					
					synchronized (image) {
						EventLogging.logNormalEvent(NORMALEVENTS.CAMERA_START_IMAGE_GRAB, ""); 
						vCap.read(image);
						imageAvailable = true;
						EventLogging.logNormalEvent(NORMALEVENTS.CAMERA_END_IMAGE_GRAB, ""); 
					}

					timer.stop();

					// Compute how long to sleep in ms to achieve 5Hz rate.
					sleepTime = (long)((periodSecs - timer.get()) * 1000.0);
					
					if ((sleepTime < 0) || (sleepTime > periodMS))
						sleepTime = periodMS;
					
				}
				else
				{
					sleepTime      = periodMS;
					imageAvailable = false;
				}
				
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		/***********************************************************
		 * This method returns a cloned image of the most recently
		 * grabbed image.
		 ***********************************************************/
		public Mat getImage () {
			synchronized (image) {
				return image.clone();
			}
		}
		
		/***********************************************************
		 * This method tells the FrameGrabber to being grabbing
		 * images.
		 ***********************************************************/
		public void enableImageGrabbing () { 
			grabImages = true;
		}
		
		/***********************************************************
		 * This method tells the FrameGrabber to stop grabbing
		 * images.
		 ***********************************************************/
		public void disableImageGrabbing () { 
			grabImages = false;
		}
		
		/***********************************************************
		 * This method indicates if an image is available to be
		 * read.
		 ***********************************************************/
		public boolean imageAvailable() {
			return imageAvailable;
		}
		
	}

	/***************************************************************
	 * This method returns true if the camera has been initialized.
	 ***************************************************************/
	public static boolean initialized() {
		return initComplete;
	}

	/***************************************************************
	 * This method returns true if the camera has grabbed an image
	 * and it is available to be read.
	 ***************************************************************/
	public boolean imageAvailable () {
		return frameGrabber.imageAvailable();
	}
    
	/***************************************************************
	 * This method returns a grabbed image.
	 ***************************************************************/
    public Mat getImage () {
		return frameGrabber.getImage();
    }

	/***************************************************************
	 * This method tells the FrameGrabber to begin grabbing images
	 * (frames).
	 ***************************************************************/
    public void enableImageGrabbing () { 
    	lightOn();
    	frameGrabber.enableImageGrabbing(); 
    }
    
	/***************************************************************
	 * This method tells the FrameGrabber to cease grabbing images
	 * (frames).
	 ***************************************************************/
    public void disableImageGrabbing () {
    	frameGrabber.disableImageGrabbing(); 
    	lightOff();
    }
    
	/***************************************************************
	 * This method toggles the state of the camera light.
	 ***************************************************************/
    public void lightToggle() {
        if (RobotMap.cameraLight.get() == Relay.Value.kOff) {
            RobotMap.cameraLight.set(Relay.Value.kForward);
            return;
        }
        if (RobotMap.cameraLight.get() == Relay.Value.kForward) {
            RobotMap.cameraLight.set(Relay.Value.kOff);
        } 
    }

	/***************************************************************
	 * This method turns the camera light on.
	 ***************************************************************/
    public void lightOn() {
         RobotMap.cameraLight.set(Relay.Value.kForward);
    }

	/***************************************************************
	 * This method turns the camera light off.
	 ***************************************************************/
    public void lightOff() {
    	RobotMap.cameraLight.set(Relay.Value.kOff);
    }    
    
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
}

