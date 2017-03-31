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
import org.usfirst.frc4579.Robot2017.Robot;
import org.usfirst.frc4579.Robot2017.commands.*;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;

import edu.wpi.first.wpilibj.command.Subsystem;


/**
 *
 */
public class Lifter extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final SpeedController liftMotor = RobotMap.lifterliftMotor;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS


    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void climb(){
    	/* WARNING:
    	 * Do NOT implement anything that sets the liftMotor to -1(backwards). Setting the liftMotor will break the ratchet.
    	 */
    	double pov = Robot.oi.getLiftJoystick().getPOV();
    	if (pov == 0) {
    		liftMotor.set(.75);
    	} else if (pov == 180) {
    		liftMotor.set(-.25);
    	} else {
    		liftMotor.set(0);
    	} 
    	
    }
    public void stop(){
    	liftMotor.set(0);
    }
    
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new Climb());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
}

