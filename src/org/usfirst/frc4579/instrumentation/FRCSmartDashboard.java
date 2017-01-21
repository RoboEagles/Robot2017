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
	
	public static void setGrabbingImages (boolean value) {
		SmartDashboard.putBoolean ("Grabbing Images:", value);
	}
	
	public static void setTracking ( String value) {
		SmartDashboard.putString ("Tracking:", value);
	}
	
	public static void setRange (double value) {
		SmartDashboard.putNumber ("Range:", value);
	}
	
	public static void setBearing (double value) {
		SmartDashboard.putNumber ("Bearing:", value);
	}
	
	public static void setElevation (double value) {
		SmartDashboard.putNumber ("Elevation:", value);
	}
}
