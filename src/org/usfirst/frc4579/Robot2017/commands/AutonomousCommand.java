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

import org.usfirst.frc4579.Robot2017.Robot;
import org.usfirst.frc4579.instrumentation.EventLogging;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 */
public class AutonomousCommand extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
	public Timer timer = new Timer();
	public String robotPosition;
	public double distanceTime = 0;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public AutonomousCommand() {
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    	requires(Robot.switches);
    	requires(Robot.driveTrain);
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	robotPosition = Robot.switches.getMode();
    	System.out.println("AUTOCOMM IS: "+ robotPosition);
    	timer.start();
    	if (robotPosition == "center") {
    		
    		distanceTime = 2.8;
    	} else {
    		distanceTime = 2.8;
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (robotPosition != "null") {
    		EventLogging.logNormalEvent(EventLogging.NORMALEVENTS.START_EXECUTE_COMMAND, "Autonomous");
        	
        	// Update IMU data.
        	Robot.meas.measure();

        	// Add Autonoumous code here.
        	Robot.driveTrain.driveStraight(0.4);

        	EventLogging.logNormalEvent(EventLogging.NORMALEVENTS.END_EXECUTE_COMMAND, "Autonomous");
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (timer.get() >= distanceTime || robotPosition == "null") {
    		return true;
    	}
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrain.stop();
    	timer.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
