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
import org.usfirst.frc4579.Robot2017.commands.*;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Switches extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final DigitalInput middle = RobotMap.switchesmiddle;
    private final DigitalInput rightSwitch = RobotMap.switchesrightSwitch;
    private final DigitalInput leftSwitch = RobotMap.switchesleftSwitch;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void printSwitches(){
	    boolean mState = middle.get();
	    boolean lState = leftSwitch.get();
	    boolean rState = rightSwitch.get();
	    SmartDashboard.putBoolean("Middle Switch",mState);
	    SmartDashboard.putBoolean("Left Switch",lState);
	    SmartDashboard.putBoolean("Right Switch",rState);
    }
    public int getMode(){
    	
    	if(middle.get() || leftSwitch.get() && rightSwitch.get()){
    		return 1;
    	}
    	else if(leftSwitch.get()){
    		return 2;
    	}
    	else if(rightSwitch.get()){
    		return 3;
    	} else {
    		return 0;
    	}
    	else{
    		System.out.Println("Got back something weird")
    	}
    }
    
    public void mAutoDrive(){
    	Robot.LaserDistanceSensor.readLaser();
    }
    
    public void lAutoDrive(){
    	
    }
    
    public void rAutoDrive(){
    	
    }
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        setDefaultCommand(new AutonomousCommand());
    }
}

