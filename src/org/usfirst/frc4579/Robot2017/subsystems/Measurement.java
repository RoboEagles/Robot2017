package org.usfirst.frc4579.Robot2017.subsystems;

import org.usfirst.frc4579.Robot2017.Robot;
import com.eagles.sensors.MPU6050_I2C;
import com.eagles.sensors.VL53LOX_I2C;
import org.usfirst.frc4579.instrumentation.DebugTextFile;
import org.usfirst.frc4579.instrumentation.Instrumentation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Measurement extends Subsystem {
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	private static final double MMtoInches = 0.0393701;      // Millimeters to inches
	
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    //private final AnalogInput ultrasonic = RobotMap.measurementultrasonic;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	
    private final MPU6050_I2C mpu   = new MPU6050_I2C(MPU6050_I2C.ACCELFULLSCALE.ACCEL2G, 
    												  MPU6050_I2C.GYROFULLSCALE.DEGSEC250);
    private final VL53LOX_I2C lidar = new VL53LOX_I2C(true, false, false); // long range mode, normal speed, normal accuracy.

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    private final double angleToAccelScale = 1.0;
    
    private double fieldPosX        = 0.0; // X distance in field frame.  Field origin is location of robot at beginning of match.
    private double fieldPosY        = 0.0; // Y distance in field frame.
    private double distanceX        = 0.0; // Robot frame.
    private double distanceY        = 0.0; // Robot frame.
    private double linealDistance   = 0.0;

    private double velocityX        = 0.0; // Robot frame.
    private double velocityY        = 0.0; // Robot frame.

    private double robotAngleRateZ  = 0.0;
    private double robotAngleZ      = 0.0;
    private double robotAngleY      = 0.0;
    private double robotAngleX      = 0.0;

    private double correctedRange   = 0.0;  // Range finder range.
    
    private double lastTime  = 0.0;
    
    private boolean firstCall       = true;
    private boolean mpuAvailable    = false;
    private boolean lidarAvailable  = false;
    private boolean lidarContinuous = true;  // True  => lidar makes continous back-to-back measurements.
    										 // False => use lidar in single-shot mode.
    
    // Create a debug file containing data for this class for post-run
    // analysis.
    DebugTextFile measData = new DebugTextFile(
    		"measData", 															// Base file name.
    		true, 																	// Add time stamp to data.
    		"AccelX\tAccelY\tAccelZ\tVelX\tVelY\tdistX\tdistY\tzAngleRate\tzAngle", // File header.
    		20000);																	// Max anticipated lines in file.

    // Initializes the accelerometer and distance ranging devices.
    public void initialize() {

    	mpuAvailable = mpu.init();
    	
    	lidarAvailable = lidar.init(false);
    	if (lidarAvailable) {
    		lidar.startContinuous(0); // Start back-to-back continuouse measurements.
    	}
    	
    	if (mpuAvailable) {
    		
        	System.out.println("***** MEASUREMENT INITIALIZED" + "\n");

    	}
    	else {
        	System.out.println("***** MPU INIT FAILED *********" + "\n");
    		measData.write("MPU INIT FAILED");
    	}
		
    	reset();
    	
    	SmartDashboard.putBoolean ("MPU Available:", mpuAvailable);
    	
    }
    
    public void stopSensors () {
    	
    	if (lidarAvailable) {
    		lidar.stopContinuous();
    	}
    }
    
    // Resets the robot's current kinematic data.  All new kinematic computations
    // will be relative to the position of the robot at the time of this call.
    public void reset() {
    	velocityX        = 0.0;
        velocityY        = 0.0;
        distanceX        = 0.0;
        distanceY        = 0.0;
        robotAngleZ      = 0.0;  
        robotAngleRateZ  = 0.0;
        robotAngleZ      = 0.0;
        robotAngleY      = 0.0;
        robotAngleX      = 0.0;
    }

    // Returns true if the distance measuring device is working.
    public boolean distanceAvailable() {
    	return lidarAvailable;
    }
    
    // Returns true if the accelerometer is working.
    public boolean positionAvailable() {
    	return mpuAvailable;
    }
    
    // Compute accelerometer and range data.
    public void measure() {
    	
    	if (mpuAvailable) {
    		
    		// Compute change in time since last computations.
    		double time   = Instrumentation.timeNow();
    		if (firstCall) {
    			lastTime = time;
    			firstCall = false;
    		}
    		double deltaT = time - lastTime;

    		lastTime = time;

    		// Get latest accelerometer data.
    		mpu.read(Robot.driveTrain.isNotMoving(), time);

    		double accelerationX  = mpu.getAccelX();
    		double accelerationY  = mpu.getAccelY();
    		double accelerationZ  = mpu.getAccelZ();
    		
    		velocityX += accelerationX * deltaT;
    		velocityY += accelerationY * deltaT;
    		
    		// Compute new distance vector.
    		double dX = velocityX * deltaT;
    		distanceX += dX;
    		double dY = velocityY * deltaT;
    		distanceY += dY;
    		
    		linealDistance += Math.sqrt((dX * dX) + (dY * dY));

    		// Compute new angular data.
    		robotAngleRateZ = mpu.getGyroRateZ();
    		robotAngleZ    += robotAngleRateZ * deltaT;
    		double angleRateY = mpu.getGyroRateY();
    		robotAngleY    += angleRateY * deltaT;
    		double angleRateX = mpu.getGyroRateX();
    		robotAngleX    += angleRateX * deltaT;

    		// Compute the relative motion to absolute field coordinates.
        	fieldPosX += dX * Math.sin(Math.toRadians(robotAngleZ));
        	fieldPosY += dY * Math.sin(Math.toRadians(robotAngleZ));

    		// Display the data.
    		SmartDashboard.putString ("Field Pos X:", String.format("%7.1f", distanceX));
    		SmartDashboard.putString ("Field Pos Y:", String.format("%7.1f", distanceY));
    		SmartDashboard.putString ("Distance X:" , String.format("%7.1f", distanceX));
    		SmartDashboard.putString ("Distance Y:" , String.format("%7.1f", distanceY));
    		SmartDashboard.putString ("Velocity X:" , String.format("%7.2f", velocityX));
    		SmartDashboard.putString ("Velocity Y:" , String.format("%7.2f", velocityY));
    		SmartDashboard.putString ("Angle Z:"    , String.format("%7.1f", robotAngleZ));
    		SmartDashboard.putString ("Angle Y:"    , String.format("%7.1f", robotAngleY));
    		SmartDashboard.putString ("Angle X:"    , String.format("%7.1f", robotAngleX));
    		SmartDashboard.putString ("Total Lineal Distance:", String.format("%7.1f", linealDistance));

    		measData.write( accelerationX + "\t" + accelerationY  + "\t" + accelerationZ + "\t" +
    						velocityX + "\t" + velocityY + "\t" + 
    						distanceX + "\t" + distanceY + "\t" + robotAngleRateZ + "\t" + robotAngleZ);
    		
    	}
    	
/*    	if (lidarAvailable) {
    		
    		double range;
    		
    		if (lidarContinuous) 
    			range = lidar.readRangeContinuousMillimeters();
    		else
    		    range = (double)lidar.readRangeSingleMillimeters();
    		range *= MMtoInches;
    	    correctedRange = 1.125 * range;
    		SmartDashboard.putString ("VL53LOX Rng:", String.format("%7.1f", range));
    		SmartDashboard.putString ("VL53LOX Corrected Rng:", String.format("%7.1f", correctedRange));
    	}
    	else
    		correctedRange = 0.0;*/
    	
    }
    
/*    // Returns the X displacement from starting location in inches.
    public double getFieldPositionX() {
    	return fieldPosX;
    }

    public double getDistanceX() {
    	return distanceX;
    }
    
    // Returns the Y displacement from starting location in inches.
    public double getFieldPositionY() {
    	return fieldPosY;
    }

    public double getDistanceY() {
    	return distanceY;
    }
    
    // X component of velocity vector in inches/sec.
    public double getVelocityX() {
    	return velocityX;
    }
    
    // Y component of velocity vector in inches/sec.
    public double getVelocityY() {
    	return velocityY;
    }
    */
    
    // Returns the robot angular displacement from its original placement (in degrees).
    public double getAngle() {
    	return robotAngleZ;
    }

    // Returns the robot angular rate (in degrees/sec).
    public double getAngleRate() {
    	return robotAngleRateZ;
    }
    
    // Returns the current range from a detected object (in inches).
    public double getRange() {
    	return correctedRange;
    }
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}
}
