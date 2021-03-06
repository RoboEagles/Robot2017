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
import edu.wpi.first.wpilibj.PowerDistributionPanel;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.lang.*;

/**
 *
 */
public class PDPCurrent extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final PowerDistributionPanel robotPDP = RobotMap.pDPCurrentrobotPDP;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	int count = 0;
	
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	/*
	This is the motor we are currently using for the lifter:
	RS775-5 Motor For PG71 and PG188 Gearbox (am-2161)
	
	*/
	
	public double getCurrent(int channel){
		return robotPDP.getCurrent(channel);
	}
	
	public double getSpeed(double maxSpeed, double stallCurrent, double minCurrent){
		double mSpeed = maxSpeed,
				sCurrent = stallCurrent,
				mCurrent = minCurrent;
				//current = robotPDP.getCurrent();
		
		// Don't think the formula below works			
		return 1337; //-1*(mSpeed / (sCurrent - mCurrent)) * current + ((mSpeed) / (sCurrent - mCurrent) * mCurrent); 
	}
	public double getTorque(int channel){
		double ratio = 22/35;
		return ratio * robotPDP.getCurrent(channel);
	}
	
	// A method of checking the current by checking if the current drawn is more than other cases
	public boolean checkSpike(int channel){

		double averageCurrent = 0;
		double current = robotPDP.getCurrent(channel);
		if (count > 100){
			averageCurrent = (averageCurrent + current) / 2;
			count += 1;
		}
		else{
			// Gets how far off the current is compared to the average
			double difference  = current - averageCurrent;
			if (true) {
				return true;
			}
		}
		return false;
	}
	
	// A method of detecting a spike by checking if it passes the expected current
	public boolean getSpike(int channel){
		double current = robotPDP.getCurrent(channel);

		// If the lift motor draws more than this amount of current that means that there was a spike
		double spikeLine = 22 * 0.75;
		
		if (current > spikeLine){
			return true;
		}
		else{
			return false;
		}
	}
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
}

