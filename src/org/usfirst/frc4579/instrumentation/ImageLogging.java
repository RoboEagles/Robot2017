/**********************************************************************
 * IMAGE LOGGING
 * 
 * This class allows camera images to be saved for future debug. The
 * Instrumentation class provides the path to where the images should
 * be written.
 **********************************************************************/

package org.usfirst.frc4579.instrumentation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageLogging extends Instrumentation {

	private static boolean   logAvailable     = false;
	private static final int MAX_LIST_ENTRIES = 10000;
	private static int       imageCount       = 0;
	
	// Constructor
	public ImageLogging (){
		super();
		logAvailable = instrumentationAvailable();
	}
	
	// Set up the Instrumentation data file structure.
	static {
 	    new ImageLogging();
	}
	
	// Define the list of images to save.  Images are stored locally to save throughput.  The
	// saveImages can be invoked (say, after a match is over) to dump the images
	// to a storage device.
	private static List<Mat>     imageList     = new ArrayList<Mat>(MAX_LIST_ENTRIES);
	
	// Define a list of times at which the image was created.  These are used in the creation
	// of the file name when the image is written.  This time can assist in correlating the
	// image to events in the event log.
	private static List<String>  timeList      = new ArrayList<String>(MAX_LIST_ENTRIES);
	
	// Define a list of contour IDs.  These are used in the creation
	// of the file name when the image is written.  This contour ID can assist in correlating the
	// image to events in the event log.
	private static List<Integer> contourIDList = new ArrayList<Integer>(MAX_LIST_ENTRIES);
	
	public static boolean loggingAvailable() {
		return logAvailable;
	}
	
	/***************************************************************************
	 * Save an image to the memory, along with the contour ID and time stamp.
	 **************************************************************************/
	public static void save (Mat image, int contourID) {
		
		//imageList.add(image.clone());
		//contourIDList.add(contourID);
		SimpleDateFormat dateFormat = new SimpleDateFormat ("hh.mm.ss.SSS");
		Date date                   = new Date();
		//timeList.add(dateFormat.format(date));
		Imgcodecs.imwrite(dataDirectoryName() + "/" + ++imageCount + "_" + dateFormat.format(date) + "_" + "C" + contourID + ".jpg",
				image);
	}
	
	/***************************************************************************
	 * This method dumps the images to individual files.
	 ***************************************************************************/ 
	public static void saveImageLog () {
		
		// On the roboRIO, users should store files to /home/lvuser or subfolders created there. Or,
		// write them to a USB thumbstick at /media/sda1 (or reference /U/ and /V/).  USB stick may have
		// to be FAT-formatted and not "too large".

		if (loggingAvailable()) {

			// Write the event log someplace.
			for (int i = 0; i < imageList.size(); i++) {
				Imgcodecs.imwrite(dataDirectoryName() + "/" + i + "_" + timeList.get(i) + "_" + "C" + contourIDList.get(i) + ".jpg",
						imageList.get(i));
			}

			// Free up memory.
			imageList.clear();
			contourIDList.clear();
			timeList.clear();

		}

	}
	
}
