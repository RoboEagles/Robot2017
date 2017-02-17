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

/**
 *
 */
public class AutonomousCommand extends Command {

    // BEGIN AUTOGENERATECODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public AutonomousCommand() {
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    	requires(Robot.switches);
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	int mode = Robot.switches.getMode();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    	EventLogging.logNormalEvent(EventLogging.NORMALEVENTS.START_EXECUTE_COMMAND, "Autonomous");

    	// Add Autonoumous code here.
    	if(mode == 1){
    		//System.out.println("Running the middle drive program");
    		Robot.switches.mAutoDrive();
    	}
    	else if(mode == 2){
    		//System.out.println("Running the left drive program");
    		Robot.switches.lAutoDrive();
    	}
    	else if(mode == 3){
    		//System.out.println("Running the right drive program");
    		Robot.switches.rAutoDrive();
    	}
    	else{
    		//System.out.println("Didn't recieve a valid input from switches");
    		Robot.driveTrain.stop();
    	}
    	Robot.switches.printSwitches();
    	
    	EventLogging.logNormalEvent(EventLogging.NORMALEVENTS.END_EXECUTE_COMMAND, "Autonomous");

    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrain.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
