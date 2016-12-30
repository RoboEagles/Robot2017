/******************************************************************
 * TARGET TRACKER
 * 
 * This class receives image snapshots from a camera and searches the image
 * for a desired "target".  This may be a FRC game piece, obstacle,
 * or goal.  The abstract methods in this class must be implemented by
 * a subclass that is unique to the type of object being tracked.
 * These methods provide information about the object as well as
 * tests to be run that validate that a potential object is indeed
 * such an object.
 * 
 * In order to limit the amount of processing performed in each
 * 20ms frame, the trackTarget method is implemented in steps,
 * with one step performed each call.  It is assumed that only
 * one TargetTracker is active at any one time.
 *****************************************************************/

package org.usfirst.frc4579.vision;

import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.usfirst.frc4579.Robot2017.Robot;
import org.usfirst.frc4579.Robot2017.subsystems.Camera;
import org.usfirst.frc4579.instrumentation.EventLogging;
import org.usfirst.frc4579.instrumentation.ImageLogging;
import org.usfirst.frc4579.vision.Vision.TargetStatus;
import org.usfirst.frc4579.instrumentation.EventLogging.*;
import org.usfirst.frc4579.instrumentation.FRCSmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public abstract class TargetTracker {

	private final float tanHalfFOV = (float)Math.tan(Math.toRadians(Camera.CAMERA_HALF_HOR_FOV));


	TargetTracker(String tgtName) {
		
		// Make sure that the Vision processing rate is less than the main processing rate.
		assert (Vision.VISION_PROCESSING_EXECUTION_RATE_IN_HZ < Robot.MAIN_THREAD_EXECUTION_RATE_IN_HZ) : "Vision processing rate too high.";
		
		// Make sure that the Vision processing rate is less the camera frame grabber rate.
		assert (Vision.VISION_PROCESSING_EXECUTION_RATE_IN_HZ < Camera.FRAME_GRABBER_EXECUTION_RATE_IN_HZ) : "Vision rate higher than Frame Grabber rate.";
		
		m_tgtName = tgtName;
		
		// Start the timer used to see if it is time to run again.
		m_timer = new Timer();
		m_timer.start();
	}
	

	
	// The logic in the trackTarget method is performed over multiple calls to level
	// out the CPU loading.  Define the various processing states.
	private enum TRACKERSTATE {READIMAGE, PIPELINE, PROCESSCONTOURS, WRITEIMAGE};
	private Exception INVALID_TRACKER_STATE;
	
	private TRACKERSTATE m_trackerState        = TRACKERSTATE.READIMAGE;
	private TargetStatus m_targetStatus;
	private Mat          m_targetSearchImage   = new Mat();
	private int			 m_bestContourID       = 0;
	private Timer        m_timer;
	private final double m_executionPeriodSecs = 1.0 / (double)Vision.VISION_PROCESSING_EXECUTION_RATE_IN_HZ;
	private boolean      m_isFirstCheck        = true;
	private boolean		 m_thisTargetDisabled  = false; // Set to true when a software exception is detected.
	private String		 m_tgtName;
		
	// Declare a list of potential target contours.
	ArrayList<MatOfPoint> m_potTargetContours = new ArrayList<MatOfPoint>();
	
    // Declare a list of scores.  The first entry is the total score of all tests
	// run on a contour.  Subsequent entries are individual test scores.
	ArrayList<Integer>    m_scores            = new ArrayList<Integer>();

	// Provide a unique contour ID for each contour that is generated to aid in debug.
	// Make it "static" so that a unique contour is generated no matter how many instances
	// of this class exist.
	private static int uniqueContourID = 0;
	
	/***************************************************************************************
	 * This subclass-supplied method invokes the GRIP image pipeline to get image contours.
	 ***************************************************************************************/
	abstract ArrayList<MatOfPoint> invokeImageProcessingPipeline (Mat targetImage);
	
	/***************************************************************************************
	 * This subclass-supplied method returns a list of scores that represents
	 * how consistent the supplied contour is compared to the true target. The
	 * first score in the list is the total score.  Any other list entries represent 
	 * the scores of individual tests, and are returned for display/debug purposes.
	 * The higher the value of scores the more consistent it is with the true
	 * target. If a 0 total score is returned, the contour was not judged to be a
	 * potential target.  "rect" is the bounding rectangle for the contour. countourID uniquely
	 * identifies the contour for instrumentation purposes.
	 ***************************************************************************************/
	abstract ArrayList<Integer> scoreContour(MatOfPoint contour, int contourID, Rect rect);
	
	/***************************************************************************************
	 * This subclass-supplied method returns the width of the actual target being
	 * searched for.
	 ***************************************************************************************/
	abstract float trueTargetWidthFt ();
	
	/***************************************************************************************
	 * This subclass-supplied method returns the width of the actual target being
	 * searched for.
	 ***************************************************************************************/
	abstract float trueTargetHeightFt ();
	
	/***************************************************************************************
	 * This subclass-supplied method returns the minimum number of vertices to expect for
	 * the actual target being searched for.
	 ***************************************************************************************/
	abstract int trueTargetMinNumberVertices();
	
	/***************************************************************************************
	 * This method checks to see if it is time to start processing another camera image.
	 ***************************************************************************************/
	public boolean timeToExecute() {
		
		// if this is the first time that the tracker has been called, return "true".
		if (m_isFirstCheck) {
			m_isFirstCheck = false;
			return true;
		}
		
		// If an execution cycle is in progress, return "true" so that it continues to completion
		// (if an execution cycle is not in progress, m_trackerState will be set to the first state).
		if (m_trackerState != TRACKERSTATE.values()[0] )
			return true;
		
		// Otherwise, return "true" if the execution period has expired and a new cycle can be started.
		return (m_timer.hasPeriodPassed(m_executionPeriodSecs));
	}
	
	/***************************************************************************************
	 * This method searches the m_targetSearchImage for a target that satisfies the
	 * criteria as derived from the child class.  If such a target is found, its range and 
	 * bearing are returned.
	 ***************************************************************************************/
	public TargetStatus trackTarget () {
		
		// if this processing has crashed previously, report it as disabled.
		if (m_thisTargetDisabled) {
			FRCSmartDashboard.setTracking  ("FAULTED");
			FRCSmartDashboard.setRange     (0.0);
			FRCSmartDashboard.setBearing   (0.0);
			FRCSmartDashboard.setElevation (0.0);
			return Vision.FAULTEDSTATUS;
		}
		
		// if there are no camera images, don't bother;
		if (!Robot.camera.imageAvailable()) {
			FRCSmartDashboard.setTracking  ("NO IMAGE");
			FRCSmartDashboard.setRange     (0.0);
			FRCSmartDashboard.setBearing   (0.0);
			FRCSmartDashboard.setElevation (0.0);
			return Vision.NOTARGETSTATUS;
		}
		
		// Initialize the returned target status.
		m_targetStatus = Vision.NOTARGETSTATUS;
		
		// Catch any exception that occurs in the target processing.  If it occurs,
		// we'll disable this target processor so that the robot code does not terminate.
		try {

			// Process the current tracker state.
			switch (m_trackerState) {

			case READIMAGE: // Get an image from the camera.

				// Init the best contour ID to "none found".
				m_bestContourID = 0;

				// Clear the timer so that timeToExecute can determine when it is time to begin 
				// a new execution cycle.
				m_timer.reset();

				EventLogging.logNormalEvent(NORMALEVENTS.START_TRACK_TARGET, " " + m_tgtName);

				EventLogging.logNormalEvent(NORMALEVENTS.START_TRACK_TARGET_GET_IMAGE, "");

				m_targetSearchImage = Robot.camera.getImage();
				//m_targetSearchImage = Imgcodecs.imread("/home/lvuser/TestImages/0.jpg",Imgcodecs.CV_LOAD_IMAGE_COLOR);
				//m_targetSearchImage = Imgcodecs.imread("/home/lvuser/TestImages/205.jpg",Imgcodecs.CV_LOAD_IMAGE_COLOR);

				m_trackerState      = TRACKERSTATE.PIPELINE;

				EventLogging.logNormalEvent(NORMALEVENTS.END_TRACK_TARGET_GET_IMAGE, "");

				break;

			case PIPELINE: // Invoke the GRIP image processing pipeline.

				EventLogging.logNormalEvent(NORMALEVENTS.START_TRACK_TARGET_GRIP_PROCESSING_PIPELINE, "");

				// Get a list of contours from the provided image.
				m_potTargetContours.clear();
				m_potTargetContours = invokeImageProcessingPipeline(m_targetSearchImage);
				m_trackerState      = TRACKERSTATE.PROCESSCONTOURS;

				EventLogging.logNormalEvent(NORMALEVENTS.END_TRACK_TARGET_GRIP_PROCESSING_PIPELINE, "");

				break;

			case PROCESSCONTOURS:  // Search the contours for possible targets.

				EventLogging.logNormalEvent(NORMALEVENTS.START_TRACK_TARGET_PROCESSING_CONTOURS, 
						"Num Contours: " + m_potTargetContours.size());

				// Display all the contours on the received image.
				Imgproc.drawContours(m_targetSearchImage, m_potTargetContours, -1, Camera.BLUE);

				int	       bestScore         = 0;
				int        bestContourIndex  = 0;
				Rect       bestRect          = new Rect();
				MatOfPoint contouri;

				// for each contour
				for (int i = 0; i < m_potTargetContours.size(); i++) {

					// Generate a unique ID for this contour for instrumentation purposes.
					uniqueContourID++;

					// Get a contour
					contouri = m_potTargetContours.get(i);

					// Don't bother with small contours
					if (contouri.rows() >= trueTargetMinNumberVertices() ) {

						// Get the bounding rectangle and score the contour.
						Rect rect = Imgproc.boundingRect(contouri);

						// Clear out scores of any previous contour.
						m_scores.clear();

						// Run the tests for a valid contour and get the scores.
						m_scores = scoreContour(contouri, uniqueContourID, rect);

						// The first entry is the total score.
						int totalScore = m_scores.get(0);

						// Draw the rectangle on the debug image.
						Point upperLeft  = new Point((double)rect.x, (double)(rect.y));
						Point lowerRight = new Point((double)(rect.x + rect.width), (double)(rect.y + rect.height));
						Imgproc.rectangle(m_targetSearchImage, upperLeft, lowerRight, Camera.RED);

						// Annotate the contourID.
						Imgproc.putText(m_targetSearchImage, 
								Integer.toString(uniqueContourID), 
								new Point(upperLeft.x, upperLeft.y - 5), 
								Core.FONT_HERSHEY_SIMPLEX, 
								0.4, 
								Camera.RED);

						// Draw the individual scores down the right edge of the bounding rectangle.
						for (int j=1; j < m_scores.size(); j++)

							Imgproc.putText(m_targetSearchImage, 
									Integer.toString(m_scores.get(j)), 
									new Point(lowerRight.x + 5, (upperLeft.y + (j*20))), 
									Core.FONT_HERSHEY_SIMPLEX, 
									0.4, 
									Camera.ORANGE);

						// If it is a target candidate
						if (totalScore > 0) {

							// If it is the best match so far, save it for future processing.
							if (totalScore > bestScore) {

								bestRect         = rect.clone();
								bestScore        = m_scores.get(0);
								bestContourIndex = i;
								m_bestContourID    = uniqueContourID;

								EventLogging.logInterestingEvent
								(INTERESTINGEVENTS.CONTOUR_ACCEPTED_AND_IS_NEW_BEST, 
										"Contour ID = " + uniqueContourID + "  Score= " + totalScore + "  Num Points: " + contouri.rows());    				}
							else
								EventLogging.logInterestingEvent
								(INTERESTINGEVENTS.CONTOUR_ACCEPTED_BUT_NOT_BEST, 
										"Contour ID = " + uniqueContourID + "  Score= " + totalScore + "  Num Points: " + contouri.rows());


						}
					} else
						EventLogging.logInterestingEvent
						(INTERESTINGEVENTS.CONTOUR_REJECTED_TOO_FEW_POINTS, 
								"Contour ID = " + uniqueContourID + "  Num Points: " + contouri.rows());
				}


				// if we think we've found the target
				if (bestScore > 0) {

					//
					// Compute range/bearing/elevation.  
					//

					// First, get the width of the contour via a bounding rectangle.
					float tgtWidth = bestRect.width;

					// horizontal dist from center of image to center of target = TrueTgtWidth * TgtHorOffsetFromCenter / TgtPixelWidth 
					int rectCenterX = bestRect.x + (bestRect.width  / 2);
					int rectCenterY = bestRect.y + (bestRect.height / 2);
					float TgtHorDistFromImageCntrFt = 
							trueTargetWidthFt() * (float)(rectCenterX - Camera.HORIZONTAL_IMAGE_CENTER) / tgtWidth;

					// HalfFOVft = trueTgtWidthFt * HalfFOVPixels / tgtWidthPixels
					float halfFOVft = trueTargetWidthFt() * (float)Camera.HORIZONTAL_IMAGE_CENTER / tgtWidth;

					// Apparent distance to center of image = halfFOVft / tan(halfFOV)
					float d = halfFOVft / tanHalfFOV;

					float tgtRange = (float)Math.sqrt((halfFOVft * halfFOVft) + (d * d));

					// bearing = arctan(horizonatal_dist_from_center_of_image / distance)

					float tgtBearing = 
							(float)Math.toDegrees(Math.atan((double)(TgtHorDistFromImageCntrFt) / (double)tgtRange));

					float tgtHeight  = bestRect.height;
					float tgtVertDistFromImageCntrFt = 
							trueTargetHeightFt() * (float)(Camera.VERTICAL_IMAGE_CENTER - rectCenterY) / tgtHeight;
					float tgtElevation = 
							(float)Math.toDegrees(Math.atan((double)(tgtVertDistFromImageCntrFt) / (double)tgtRange));
					
					//
					// Annotate the input image with what we've learned.
					//

					// Display the best rectangle as GREEN.
					Point upperLeft  = new Point((double)bestRect.x, (double)(bestRect.y));
					Point lowerRight = new Point((double)(bestRect.x + bestRect.width), (double)(bestRect.y + bestRect.height));
					Imgproc.rectangle(m_targetSearchImage, upperLeft, lowerRight, Camera.GREEN);

					// Put a cross in the center of the best rect.
					Imgproc.drawMarker(m_targetSearchImage, new Point(rectCenterX,rectCenterY), Camera.GREEN);

					// Note the range and bearing
					Imgproc.putText(m_targetSearchImage, 
							"Range  : " + Integer.toString((int)tgtRange) + " ft", 
							new Point(0, 20), 
							Core.FONT_HERSHEY_SIMPLEX, 
							0.5, 
							Camera.GREEN);

					Imgproc.putText(m_targetSearchImage, 
							"Bearing: " + Integer.toString((int)tgtBearing) + " deg", 
							new Point(0, 40), 
							Core.FONT_HERSHEY_SIMPLEX, 
							0.5, 
							Camera.GREEN);

					Imgproc.putText(m_targetSearchImage, 
							"Elevation: " + Integer.toString((int)tgtElevation) + " deg", 
							new Point(0, 60), 
							Core.FONT_HERSHEY_SIMPLEX, 
							0.5, 
							Camera.GREEN);
					
					// Mark the center of the image.
					Imgproc.drawMarker(m_targetSearchImage, 
							new Point(Camera.HORIZONTAL_IMAGE_CENTER,
									  Camera.VERTICAL_IMAGE_CENTER), 
							Camera.ORANGE);

					m_targetStatus = new TargetStatus(Vision.TARGETVALIDITY.VALID, tgtRange, tgtBearing, tgtElevation);

				}

				m_trackerState = TRACKERSTATE.WRITEIMAGE;

				if (m_targetStatus.validity == Vision.TARGETVALIDITY.VALID) {

					EventLogging.logNormalEvent(NORMALEVENTS.END_TRACK_TARGET_PROCESSING_CONTOURS, 
							" Range: " + m_targetStatus.tgtRange + 
							" Bearing: " + m_targetStatus.tgtBearing + 
							" Elevation: " +m_targetStatus.tgtElevation);

					FRCSmartDashboard.setTracking  (m_tgtName);
					FRCSmartDashboard.setRange     (m_targetStatus.tgtRange);
					FRCSmartDashboard.setBearing   (m_targetStatus.tgtBearing);
					FRCSmartDashboard.setElevation (m_targetStatus.tgtElevation);
				}
				else
				{
					EventLogging.logNormalEvent(NORMALEVENTS.END_TRACK_TARGET_PROCESSING_CONTOURS, "");

					FRCSmartDashboard.setTracking  ("");
					FRCSmartDashboard.setRange     (0.0);
					FRCSmartDashboard.setBearing   (0.0);
					FRCSmartDashboard.setElevation (0.0);
				}

				break;

			case WRITEIMAGE: // Write the annotated image to disk for debug.

				EventLogging.logNormalEvent(NORMALEVENTS.START_TRACK_TARGET_WRITING_IMAGE, "");

				//Save the image for post-run analysis.
				ImageLogging.save (m_targetSearchImage, m_bestContourID);
				m_trackerState = TRACKERSTATE.READIMAGE;

				// Display the debug image (or send to driver station?)
				// im.showImage(targetSearchImage);

				EventLogging.logNormalEvent(NORMALEVENTS.END_TRACK_TARGET_WRITING_IMAGE, "");

				EventLogging.logNormalEvent(NORMALEVENTS.END_TRACK_TARGET, " " + m_tgtName);
				break;

			default:  // Should never get here.  Throw an exception.
				
				throw INVALID_TRACKER_STATE;
					
			}
			
		} catch(Exception ex){
			EventLogging.logBadEvent(BADEVENTS.TARGET_TRACKER_UNHANDLED_EXCEPTION, " " + ex.toString());
			m_thisTargetDisabled = true;
			m_targetStatus       = Vision.FAULTEDSTATUS;
		}
		return m_targetStatus;
	}

}
