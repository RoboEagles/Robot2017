/**********************************************************************
 * EVENT LOG
 * This class allows the coder to leave a trail of time-tagged
 * "breadcrumbs" (events) for debug purposes.  The events are saved
 * in memory until saveEventLog is called, at which point the events
 * are dumped to a text file.
 **********************************************************************/

package org.usfirst.frc4579.instrumentation;

import java.util.*;
import java.text.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EventLogging extends Instrumentation {
	
	/*********************************************************************************
	 * NORMAL EVENTS document "normal" execution events, e.g, the beginning and end of
	 * major functionalflows.
	 *********************************************************************************/
	public enum NORMALEVENTS {
						START_INITIALIZE_COMMAND,   // Outer level command events (NORMAL)
						END_INITIALIZE_COMMAND,
						START_EXECUTE_COMMAND,
						END_EXECUTE_COMMAND,
						START_COMMAND_IS_FINNISHED,
						END_COMMAND_IS_FINNISHED,
						START_COMMAND_END,
						END_COMMAND_END,
						START_COMMAND_INTERRUPTED,
						END_COMMAND_INTERRUPTED,
						CAMERA_INITIALIZED,          // Vision processing (NORMAL)
						CAMERA_START_IMAGE_GRAB,
						CAMERA_END_IMAGE_GRAB,
						START_TRACK_TARGET, 
						END_TRACK_TARGET,
						START_TRACK_TARGET_GET_IMAGE,
						END_TRACK_TARGET_GET_IMAGE,
						START_TRACK_TARGET_GRIP_PROCESSING_PIPELINE,
						END_TRACK_TARGET_GRIP_PROCESSING_PIPELINE,
						START_TRACK_TARGET_PROCESSING_CONTOURS,
						END_TRACK_TARGET_PROCESSING_CONTOURS,
						START_TRACK_TARGET_DRAWING_CONTOURS,
						END_TRACK_TARGET_DRAWING_CONTOURS,
						START_TRACK_TARGET_WRITING_IMAGE,
						END_TRACK_TARGET_WRITING_IMAGE

	};
	
	/*********************************************************************************
	 * INTERESTING EVENTS document, as an example, the results of lower-level flows 
	 * of control.
	 *********************************************************************************/
	public enum INTERESTINGEVENTS {
						
						MPU6050_CAL,
						MPU6050_DATA,
						MEASUREMENT_DATA
	};

	/*********************************************************************************
 	 * BAD EVENTS should not happen and should be debugged.
 	 *********************************************************************************/
	public enum BADEVENTS {
						TARGET_TRACKER_UNHANDLED_EXCEPTION
	};

	private static boolean logAvailable = false;

	// Constructor
	public EventLogging () {

		// Create the directory to hold the events file.
		super();
		logAvailable = instrumentationAvailable();
	}

	// Invoke the constructor to set up the instrumentation file structure.
	static {
 	    new EventLogging();
	}
	
	// Define the event log.  Event are stored locally to save throughput.  The
	// saveEventLog can be invoked (say, after a match is over) to dump the log
	// to the Driver Station or storage device.
	private static List<String> eventLog = new ArrayList<String>(10000);		

	/***************************************************************************
	 * This method returns true if event logging is available.  It would not
	 * be available if the instrumentation data directory structure could not
	 * be created.
	 ***************************************************************************/
	public static boolean loggingAvailable() {
		return logAvailable;
	}

	/***************************************************************************
	 * This method logs the specified event onto the eventLog.
	 ***************************************************************************/
	private static synchronized void logEvent (String event) {

		String eventStr = String.format("%10.6f", timeNow()) + '\t' + event + '\t';
		
		eventLog.add(eventStr);

	}

	/***************************************************************************
	 * This method logs a NORMAL event.
	 ***************************************************************************/
	public static synchronized void logNormalEvent (NORMALEVENTS event, String auxData) {

		if (logAvailable) 
			logEvent ("NORMAL      \t" + event.name() + "\t" + auxData);

	}

	/***************************************************************************
	 * This method logs an INTERESTING event.
	 ***************************************************************************/
	public static synchronized void logInterestingEvent (INTERESTINGEVENTS event, String auxData) {

		if (logAvailable) 
			logEvent ("INTERESTING\t" + event.name() + "\t" + auxData);

	}

	/***************************************************************************
	 * This method logs a BAD event.
	 ***************************************************************************/
	public static synchronized void logBadEvent (BADEVENTS event, String auxData) {

		if (logAvailable) 
			logEvent ("BAD         \t" + event.name() + "\t" + auxData);

	}

	/***************************************************************************
	 * Log a string to the event log with no other information.
	 ***************************************************************************/
	public static void logString (String str) {
		eventLog.add(str);
	}
	
	/***************************************************************************
	 * This method dumps the event log to a file.
	 ***************************************************************************/
	public static synchronized void saveEventLog () {

		File             logFile;
		BufferedWriter   bw;
		FileWriter       fw;
		SimpleDateFormat hrMinFormat = new SimpleDateFormat ("_hh.mm.ss");		

		if (logAvailable) {

			// Create a File object with the event log file name.
			Date date = new Date();
			logFile   = new File(dataDirectoryName() + "/Events" + hrMinFormat.format(date) + ".txt");

			try {

				if (!logFile.exists())
					logFile.createNewFile();

				if (logFile.exists()) {
					
					// Try to dump it to the file.
					fw = new FileWriter(logFile);
					bw = new BufferedWriter(fw);

					// Write the event log someplace.
					for (String element : eventLog) {

						// On the roboRIO, users should store files to /home/lvuser or subfolders created there. Or,
						// write them to a USB thumbstick at /media/sda1 (or reference /U/ and /V/).  USB stick may have
						// to be FAT-formatted and not "too large".
						bw.write(element);
						bw.write("\r\n");
						//System.out.println(element); 
					}

					bw.close();
					fw.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}


		}

		// Free up memory.
		eventLog.clear();
	}
}

