// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4579.Robot2017.commands;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc4579.Robot2017.Robot;

import org.opencv.core.*;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import org.opencv.imgproc.Imgproc;

/**
 *
 */
public class FacePeg extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
	private ArrayList<MatOfPoint> contours = new ArrayList<>();
	private double centerX = -1;
	private double allowedError = 1;
	private boolean takingShot = false;
	private boolean snapshotReady = false;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public FacePeg() {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.cam);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	contours = Robot.cam.getPrimaryContours();
    	double errorInDegrees = Robot.cam.getErrorFromContours(contours.get(0),contours.get(1));
    	//left is positive, right is negative
    	if (Math.abs(errorInDegrees) < allowedError) {
    		end();
    	}
    	System.out.println("Degrees needed to rotate: "+ errorInDegrees);
    	/*
    	 * targetangle = robotAngle + errorInDegrees
    	 * difference = targetAngle + 90
    	 * horizDist = Distance * Math.sin(difference)
    	 * angledist = horiz*Math.sqrt(2)
    	 * 
    	 */
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//robot.rotate(degrees)
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean atAngle = true;
    	if (atAngle) {
    		//takeSnapshot may interfere with robot
    		//Maybe start a new thread and check if it's ready on a later iteration?
    		if (snapshotReady) {
    			snapshotReady = false;
    			takingShot = false;
    			ArrayList<MatOfPoint> contours = Robot.cam.getContours();
        		if (contours.size() == 2) {
        			double error = Robot.cam.getErrorFromContours(contours.get(0),contours.get(1));
        			System.out.println("Error is: "+error);
        			if (error == 99999) {
        				//the rectangles are in the same place, do nothing(But how do you do nothing?)
        			} else if (error <= allowedError) {
        				System.out.println("The robot is facing the peg.");
        				return true;
        			} else {
        				//rotate the robot to the peg again(atAngle will be false)
        			}
        		}
        		//If there is one contour, then get whether if it's on the left or right, then rotate the robot in that direction(atAngle will be false)
        		
    		} else if (!takingShot) {
    			takingShot = true;
    			Thread t = new Thread(() -> {
        			snapshotReady = Robot.cam.takeSnapshot();
            	});
            	t.start();
    		}
    		//else do nothing and wait for the snapshot to be ready.
    	}
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
