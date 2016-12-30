/*****************************************************************************
 * INSTRUMENTATION
 * 
 * This class creates a "runs" data directory into which debug data can be
 * saved for a particular run.  A new folder is created in the "runs"
 * directory to hold the data for a particular run, with the name of
 * the folder containing the date and time that the run began.  The inheriting 
 * subclass responsible for writing a particular kind of debug data has access 
 * to this folder path via the dataDirectoryName method.
 * 
 * The directory in which the "runs" directory is created is derived by
 * concatenating the user.home system property with the BaseDataDir system
 * property.  The BaseDataDir system properties is specified in Eclipse as
 * follows:
 * 		1. Select Run -> Run Configurations...
 * 		2. Select the desired program and the "(x) = Arguements" tab.
 * 		3. Under "VM Arguments" add: 
 * 				-DBaseDataDir="<path to append to the user.home value>"
 * 		4. Click apply.
 *****************************************************************************/

package org.usfirst.frc4579.instrumentation;

import java.text.SimpleDateFormat;
import java.util.Date;
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
			String mainPath = System.getProperty("user.home"); // + System.getProperty("BaseDataDir");
			//String mainPath = "/media/sa1";
			//String mainPath = "/V";
			
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

}
