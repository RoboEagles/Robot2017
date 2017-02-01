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
import org.usfirst.frc4579.math.*;
import org.usfirst.frc4579.Robot2017.commands.*;
import org.usfirst.frc4579.instrumentation.FRCSmartDashboard;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class UltrasonicSensor extends Subsystem {

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final Ultrasonic ultraLeft = RobotMap.ultrasonicSensorultraLeft;
    private final Ultrasonic ultraRight = RobotMap.ultrasonicSensorultraRight;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	public void initUltras() {
		ultraLeft.setAutomaticMode(true);
		ultraRight.setAutomaticMode(true);
		// removed setAutomaticMode methods
	}

	public void getDistances() {
		/*
		 * double leftValue = ultraLeft.getValue(); double rightValue =
		 * ultraRight.getValue();
		 * 
		 * double leftVoltage = ultraLeft.getVoltage(); double rightVoltage =
		 * ultraRight.getVoltage();
		 */
		System.out.println("Getting distances!");
		double leftDist = ultraLeft.getRangeInches();
		double rightDist = ultraRight.getRangeInches();

		// Vector2d testVector = new Vector2d(leftDist, rightDist);

		// FRCSmartDashboard.setDistances(leftValue, rightValue);
		// FRCSmartDashboard.setVoltages(leftVoltage, rightVoltage);
		FRCSmartDashboard.setRealDistances(leftDist, rightDist);
		// System.out.println("Left distance: "+leftDist);
		// System.out.println("Right distance: "+rightDist);
	}

	
	public void initDefaultCommand() {
	
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new AlignToShip());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}
}
