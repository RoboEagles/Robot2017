// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4579.Robot2017;

import org.usfirst.frc4579.Robot2017.commands.*;
import org.usfirst.frc4579.Robot2017.subsystems.*;
import org.usfirst.frc4579.instrumentation.EventLogging;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	// Flag used to detect the first call to disabledInit.
	boolean firstDICall = true;
	
    Command autonomousCommand;

    public static OI oi;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static Cam cam;
    public static DriveTrain driveTrain;
    public static Switches switches;
    public static Lifter lifter;
    public static LaserDistanceSensor laserDistanceSensor;
    public static PDPCurrent pDPCurrent;
    public static ServoTestSystem servoTestSystem;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static UsbCamera dashcam;
	public static Measurement meas;

    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    RobotMap.init();
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        cam = new Cam();
        driveTrain = new DriveTrain();
        switches = new Switches();
        lifter = new Lifter();
        laserDistanceSensor = new LaserDistanceSensor();
        pDPCurrent = new PDPCurrent();
        servoTestSystem = new ServoTestSystem();

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS 
        cam.initCamera();
        meas = new Measurement();
        meas.initialize();
        /*This is where the IMU should be initialized.
         *   Instantiate
         *   Get a small sample of readings from the accelerometer, x, y, and z, 
         *   and take the average of each.  Instantiate a Kalman filter 
         *   
         */
        // OI must be constructed after subsystems. If the OI creates Commands
        //(which it very likely will), subsystems are not guaranteed to be
        // constructed yet. Thus, their requires() statements may grab null
        // pointers. Bad news. Don't move it.
        oi = new OI();

        // instantiate the command used for the autonomous period
        
        //Note: I removed the AutonomousCommand flags for robotbuilder, you may need to re-add them in the future.
        String state = switches.getMode();
        String direction;
        if (state != "null") {
        	autonomousCommand = new Auto_PlaceGear(state);
        } else {
        	System.out.println("State was null...");
        }
    }

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    public void disabledInit(){
    	// This method can be called at startup when the DS connects and is in Disable state.
        if (!firstDICall) {
    	    EventLogging.saveEventLog();
    	}
    	firstDICall = false;
    	cam.lightOff();
    }

    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
        cam.changeCameraToTeleop();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
