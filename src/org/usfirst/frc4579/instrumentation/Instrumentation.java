/*****************************************************************************
 * INSTRUMENTATION
 * 
 * This class creates a "runs" data directory into which debug data can be
 * saved for a particular run.  A new folder is created in the "runs"
 * directory to hold the data for a particular run, with the name of
 * the folder containing the date and time that the run began.  The inheriting 
 * subclass responsible for writing a particular kind of debug data has access 
 * to this folder path via the dataDirectoryName method.
 *****************************************************************************/

package org.usfirst.frc4579.instrumentation;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.wpi.first.wpilibj.Timer;

import java.io.File;

public abstract class Instrumentation {

	private static String  runDataDir = new String();
	private static boolean instrAvailable;
	
	// Constructor
	Instrumentation () {
		
		// Has this run's instrumentation directory already been created?
		instrAvailable = !runDataDir.isEmpty();
		
		// if not
		if (!instrAvailable) {

			// Try to create a "runs" directory file object.
			String mainPath = System.getProperty("user.home");
			
			instrAvailable  = true;

			File fileDir1 = new File(mainPath + "/runs");

			// if the directory doesn't already exist try to create it
			if (!fileDir1.exists()) 
				instrAvailable = fileDir1.mkdirs();

			// if the "runs" directory exists
			if (instrAvailable) {

				// Try to create the unique directory for this run
				SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy.MM.dd__hh.mm");
				Date date                   = new Date();
				runDataDir                  = fileDir1.getAbsolutePath() + "/" + dateFormat.format(date);
				File fileDir2               = new File(runDataDir);

				instrAvailable = fileDir2.mkdir();
			}
		}

	}

	// Returns true if runDataDir was successfully created.
	public static boolean instrumentationAvailable () {return instrAvailable;}
	
	// Returns the path to where debug data should be saved.
	public static String dataDirectoryName () {return runDataDir;}
	
	// Provides a common time source for child classes to time-tag debug data.
	// The returned time is in seconds.
	public static double timeNow () {
		return Timer.getFPGATimestamp();
	}

}
