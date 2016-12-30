/********************************************************************************
 * CASTLE TOWER GOAL TRACKER 
 * 
 * This subclass of TargetTracker implements the calls
 * to the GRIP-generated image processing pipeline for the FRC 2016 castle tower
 * goal and scores potential targets to see if they represent a tower goal.
 ********************************************************************************/

package org.usfirst.frc4579.vision;

import java.util.ArrayList;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.usfirst.frc4579.instrumentation.EventLogging;
import org.usfirst.frc4579.instrumentation.EventLogging.*;

public class CastleTowerGoalTracker extends TargetTracker {
	
	// 2016 Castle Tower Goal attributes.
	private final float TRUE_CASTLE_TOWER_GOAL_WIDTH            = 20.0f/12.0f;  // feet
	private final float TRUE_CASTLE_TOWER_GOAL_HEIGHT           = 14.0f/12.0f;  // feet
	private final int   TRUE_CASTLE_TOWER_GOAL_MIN_NUM_VERTICES = 8;
	private final float TRUE_AREA_RATIO                         = 160.0f/280.0f;
	private final int   MIN_VALID_COVERAGE_AREA_SCORE           = 70;
	//private final float TRUE_ASPECT_RATIO                       = 20.0f/14.0f;
	//private final int   MIN_VALID_ASPECT_RATIO_SCORE            = 75;
	
	// Instantiate the GRIP pipeline.
	GRIPCastleTowerGoalPipeline m_pipeline = new GRIPCastleTowerGoalPipeline();
	
	// Constructor:
	CastleTowerGoalTracker () {
		super("Castle Tower Goal"); // Invoke the parent constructor.
	}
	
	
	// Invokes the GRIP image processing pipeline and returns a list
	// of contours that may include the desired target.
	ArrayList<MatOfPoint> invokeImageProcessingPipeline (Mat targetImage) {
		
    	m_pipeline.setsource0(targetImage);
		m_pipeline.process();
		ArrayList<MatOfPoint> contours = m_pipeline.filterContoursOutput();
		
		return contours;
	}
	
	// This method scores a contour based on how accurately it represents the
	// desired target. 
	ArrayList<Integer> scoreContour(MatOfPoint contour, int contourID, Rect rect) {

		ArrayList<Integer> scores = new ArrayList<Integer>();
		scores.add(0); // Init the total score to 0 (not a desired target).
		
		/***********************************************************************************
		 * Perform a Coverage Area test.
		 ***********************************************************************************/
		
		int coverageScore = ContourTests.coverageAreaTest(contour, rect, TRUE_AREA_RATIO);
		scores.add(coverageScore);

		if (coverageScore < MIN_VALID_COVERAGE_AREA_SCORE) {

			EventLogging.logInterestingEvent(
							  INTERESTINGEVENTS.CONTOUR_FAILED_COVERAGE_AREA_TEST,
							  "ContourID= " + contourID + " Score= " + coverageScore);
			
			return scores;
		}
		
		EventLogging.logInterestingEvent(
				  		  INTERESTINGEVENTS.CONTOUR_PASSED_COVERAGE_AREA_TEST,
				  		  "ContourID= " + contourID + " Score= " + coverageScore);
		
		/***********************************************************************************
		 * Perform an Aspect Ratio test.
		 ***********************************************************************************/
		
		/* This test doesn't work.  The "equivalent rectangle" that is computed, while correct
		 * based on the formula, does not have an aspect ratio anywhere close to the true
		 * bounding rectangle.  Pending further analysis, this test is disabled.
		int aspectScore = ContourTests.aspectRatioTest(contour, rect, TRUE_ASPECT_RATIO);
		scores.add(aspectScore);
		//aspectScore = 100; // Fake a successful test until we can get it working.

		if (aspectScore < MIN_VALID_ASPECT_RATIO_SCORE) {
 
			EventLogging.logInterestingEvent( 
					  		INTERESTINGEVENTS.CONTOUR_FAILED_ASPECT_RATIO_TEST,
					  		"ContourID= " + contourID + " Score= " + aspectScore);
			return scores;
		}

		EventLogging.logInterestingEvent(
						  INTERESTINGEVENTS.CONTOUR_PASSED_ASPECT_RATIO_TEST,
				          "ContourID= " + contourID + " Score= " + aspectScore); */
		int aspectScore = 0;
		
		/***********************************************************************************
		 * Set the first score element to the total score, then return the test scores.
		 ***********************************************************************************/
		scores.set(0, coverageScore + aspectScore);

		return scores;
	}
	
	float trueTargetWidthFt () {
		return TRUE_CASTLE_TOWER_GOAL_WIDTH;
	}

	float trueTargetHeightFt () {
		return TRUE_CASTLE_TOWER_GOAL_HEIGHT;
	}
	
	int trueTargetMinNumberVertices() {
		return TRUE_CASTLE_TOWER_GOAL_MIN_NUM_VERTICES;
	}
}
