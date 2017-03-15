/*
 * CLASS: DebugTextFile
 * 
 * This class allows text debug data to be written to a file on the RoboRIO. A "runs" directory
 * is created in the directory designated by the user.home property (typically the "lvuser" directory).
 * Within the "runs" directory, a date and time-tagged directory is created to hold one or more debug
 * text files (one for each instantiation of this class).
 * 
 * Constructor Parameters:
 * 
 * 		baseFileName: The file name to which ".txt" will be appended.  Additionally, since hitting the Disable
 *                    and Enable buttons results in a new run, a time tag is added before the ".txt".  For example,
 *                    if baseFileName is "myfile", the resulting file name(s) will be of the form "myfile_hh.mm.ss.txt".
 * 
 * 		addTimeStamp: If true, will time tag each line of text written to the file.
 * 
 * 		header      : If non-null, this string is written as the first line in the file.  This can be useful if the file
 * 					  is imported into a spreadsheet for plotting.  If addTimeStamp is true, "Time\t" will be prepended 
 * 					  to the header.
 * 
 * 		anticipatedMaxLinesInFile:
 * 
 * 					  For speed reasons this class uses an ArrayList to hold the file data until it is
 * 					  written to a file via a call to saveDataFiles().  An ArrayList that exceeds its
 * 					  initial size allocation can grow automatically, but is inefficient.  Try to set
 * 					  this parameter to the max number of lines that you anticipate will be written to 
 * 					  the file.  When the data is written, and data was larger than the initial allocation,
 * 					  a warning is sent to the console noting the number of lines written to the file.
 * 					  This can be used to update the anticipatedMaxLinesInFile parameter.
 * 
 * The saveDebugFiles method should be called from Robot.disabledInit().  Robot.disabledInit is called when
 * the driver station Disable button is pressed.
 * 
 * Debug files can be transferred to the Driver Station with a program like FileZilla.  Be sure to clean
 * out old debug files by deleting them individually or deleting the lvuser\runs directory (using FileZilla).
 * 
 */

package org.usfirst.frc4579.instrumentation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DebugTextFile extends Instrumentation {

	private static final int 	   ALLOC_INCREMENT = 10000;
	private              int       initialAlloc    = ALLOC_INCREMENT;
	private				 int	   currentAlloc    = initialAlloc;
	private static       boolean   instrAvailable; // True => "runs" directory exists.
	private static       boolean   isFirstSave;    // First call to saveDebugData?
	private				 String    fileName;	   // Base file name for this file.
	private 			 String    header;		   // Header to be written to this file.
	private 			 boolean   addTimeStamp;   // True => time tag each line that is written to the file.

	// File data is stored locally to save throughput.  The saveFileData method is invoked to
	// dump the data to a file.
	private ArrayList<String> fileData;
	
	// List of all DebugTextFile objects that have been created.
	private static List<DebugTextFile> fileList = new ArrayList<DebugTextFile>(20);
	
	// Constructor
	public DebugTextFile (String baseFileName, boolean addTimeStamp, String header, int anticipatedMaxLinesInFile) {

		// Create the directory to hold the events file.
		super();
		instrAvailable = instrumentationAvailable();
		
		// if successful
		if (instrAvailable) {

			// Save the file name and header.
			this.fileName     = baseFileName;
			this.header       = header;
			this.addTimeStamp = addTimeStamp;
			this.initialAlloc = anticipatedMaxLinesInFile;
			
			// Allocate the file data (number of lines in file) based on initial worst-case estimate.
			this.fileData     = new ArrayList<String>(this.initialAlloc);

			// Add this object to the list of file objects.
			fileList.add(this);
		}
		
		isFirstSave = true;
	}
	
	// Writes a line to the debug file.
	public void write (String line) {
		
		if (!instrAvailable) return;
		
		if (addTimeStamp)
			fileData.add(timeNow() + "\t" + line);
		else
 		    fileData.add(line);
		
		// if the number of lines in the file has reached our allocation, increase the allocation.
		if (fileData.size() == currentAlloc)
		{
			currentAlloc += ALLOC_INCREMENT;
			fileData.ensureCapacity(currentAlloc);
		}
	}
	
	// Saves debug data to a file for each DebugTextFile object.
	public static void saveDataFiles() {

		// Robot.disabledInit() is called at startup (when no data has been written).
		// So don't try to dump data on the first call.
		if (isFirstSave) {
			isFirstSave = false;
			return;
		}

		// for each debug file
		for (DebugTextFile aDebugFile : fileList) {

			SimpleDateFormat hrMinFormat = new SimpleDateFormat ("_hh.mm.ss");

			if (instrAvailable) {

				// Create a File object with the event log file name.
				Date date = new Date();
				BasicTextFileOps fileWriter = new BasicTextFileOps
						(dataDirectoryName() + "/" + aDebugFile.fileName + hrMinFormat.format(date) + ".txt");

				fileWriter.openForWrite();

				// If a header was supplied, write it to the file.
				if (aDebugFile.header.length() != 0) {
					if (aDebugFile.addTimeStamp)
						fileWriter.writeLine("Time\t" + aDebugFile.header + "\r\n");
					else
						fileWriter.writeLine(aDebugFile.header + "\r\n");
				}

				// If final size is larger than initial allocation, print a warning.
				if (aDebugFile.fileData.size() > aDebugFile.initialAlloc)
					System.out.println("WARNING: File " + aDebugFile.fileName + " is " + aDebugFile.fileData.size() +
							" lines long.  Initial allocation was " + aDebugFile.initialAlloc + 
							".  Please update initial allocation in the DebugTextFile instantiation for this file.");

				// Write out each line of the file.
				for (String line : aDebugFile.fileData) {
					fileWriter.writeLine(line + "\r\n");
				}

				fileWriter.close();
			}

			// Free up memory.
			aDebugFile.fileData.clear();
		}

	}

}