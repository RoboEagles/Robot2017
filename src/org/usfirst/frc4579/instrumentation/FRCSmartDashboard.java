/******************************************************************************
 * This class encapsulates the functionality of the SmartDashboard (launched
 * with the FRC Driver Station.
 ******************************************************************************/

package org.usfirst.frc4579.instrumentation;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FRCSmartDashboard {

	public static void setCameraInitialized (boolean value) {
		SmartDashboard.putBoolean ("Camera Initialized:", value);
	}
	
	public static void setDistances(double leftDist, double rightDist) {
		SmartDashboard.putNumber("Left distance:", leftDist);
		SmartDashboard.putNumber("Right distance:", rightDist);
	}
}
