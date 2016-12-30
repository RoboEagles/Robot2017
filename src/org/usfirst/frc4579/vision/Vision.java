/******************************************************************************
 * VISION
 * 
 * This class is the top level class for vision processing.  It declares an
 * instance of a TargetTracker class for each kind of object to be tracked.
 * Methods are provided so that a tracker can be started when desired (e.g.,
 * in Autonomous mode or operator command).
 ******************************************************************************/

package org.usfirst.frc4579.vision;

public class Vision {

	// Define the rate at which vision processing should execute.
	public final static int VISION_PROCESSING_EXECUTION_RATE_IN_HZ = 4;

	// Define the results returned by the trackTarget method.
	public enum TARGETVALIDITY {SWFAULT, INVALID, VALID};
	
	public static class TargetStatus {
		
		public final TARGETVALIDITY validity;
		public final float          tgtRange;     // Feet
		public final float          tgtBearing;   // Degrees
		public final float			tgtElevation; // Degrees
		
		TargetStatus (TARGETVALIDITY targetValidity, float rng, float bearing, float elevation)  {
			this.validity     = targetValidity;
			this.tgtRange     = rng;
			this.tgtBearing   = bearing;
			this.tgtElevation = elevation;
		}
	}
	public static final TargetStatus NOTARGETSTATUS = new TargetStatus(TARGETVALIDITY.INVALID, 0.0f, 0.0f, 0.0f);
	public static final TargetStatus FAULTEDSTATUS  = new TargetStatus(TARGETVALIDITY.SWFAULT, 0.0f, 0.0f, 0.0f);
	
	// Instantiate a tracker for the Castle Tower Goal.
	private static CastleTowerGoalTracker m_castleTowerTgt = 
			new CastleTowerGoalTracker();
	
	// This method invokes the tracker for the Castle Tower Goal.
	public static TargetStatus findCastleTowerGoal() {

		TargetStatus castleTowerTgtData = NOTARGETSTATUS;

		// if it is time to execute the tracker
		if (m_castleTowerTgt.timeToExecute()) {

			// Call the tracker.
			castleTowerTgtData = m_castleTowerTgt.trackTarget();

		}

		return castleTowerTgtData;

	}
			
}

