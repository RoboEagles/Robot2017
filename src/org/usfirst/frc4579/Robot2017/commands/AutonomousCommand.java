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
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
<<<<<<< HEAD
    	EventLogging.logNormalEvent(EventLogging.NORMALEVENTS.START_EXECUTE_COMMAND, "Autonomous");

    	// Showing the state of the Switches 
    	SmartDashboard.putBoolean("Middle Switch", Robot.Switches.middle);
    	SmartDashboard.putBoolean("Right Switch", Robot.Switches.rightSwitch);
    	SmartDashboard.putBoolean("Left Switch", Robot.Switches.leftSwitch);
    	
    	// Add Autonoumous code here.
    	
    	// Runs a method according to what button is switched
    	if(Robot.Switches.getMode() == 1){
    		System.out.Println("Running the middle drive program");
    		Robot.Switches.mAutoDrive();
    	}
    	else if(Robot.Switches.getMode() == 2){
    		System.out.Println("Running the left drive program");
    		Robot.Switches.lAutoDrive();
    	}
    	else if(Robot.Switches.getMode() == 3){
    		System.out.Println("Running the right drive program");
    		Robot.Switches.rAutoDrive();
    	}
    	else(){
    		System.out.Println("Didn't recieve a valid input from switches");
    		Robot.DriveTrain.stop();
=======
    	// Add Autonoumous code here.
    	if(Robot.switches.getMode() == 1){
    		System.out.println("Running the middle drive program");
    		Robot.switches.mAutoDrive();
    	}
    	else if(Robot.switches.getMode() == 2){
    		System.out.println("Running the left drive program");
    		Robot.switches.lAutoDrive();
    	}
    	else if(Robot.switches.getMode() == 3){
    		System.out.println("Running the right drive program");
    		Robot.switches.rAutoDrive();
    	}
    	else{
    		System.out.println("Didn't recieve a valid input from switches");
    		Robot.driveTrain.stop();
>>>>>>> origin/master
    	}
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
