package com.eagles.sensors;

import org.usfirst.frc4579.filters.AverageFilter;
import org.usfirst.frc4579.filters.FirstOrderLPF;
import org.usfirst.frc4579.instrumentation.EventLogging;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MPU6050_I2C {
	
	private static final byte   deviceAddress         = 0x68;
	private static final int    numCalibrationSamples = 100;
	
	private I2C MPU = new I2C(I2C.Port.kOnboard, (int)deviceAddress);
	
	// Acceleration configuration data.
	public  enum ACCELFULLSCALE { ACCEL2G  , ACCEL4G  , ACCEL8G   , ACCEL16G };
	public  enum GYROFULLSCALE  { DEGSEC250, DEGSEC500, DEGSEC1000, DEGSEC2000};

	// Constructor
	public MPU6050_I2C (ACCELFULLSCALE accelFullScale, GYROFULLSCALE gyroFullScale)
	{
		accelRegConfigValue = accelFullScaleRegSettings[accelFullScale.ordinal()];
		accelScaleFactor    = accelScaleFactors        [accelFullScale.ordinal()]; // / GsToAccel;
		gyroRegConfigValue  = gyroFullScaleRegSettings [gyroFullScale .ordinal()];
		gyroScaleFactor     = gyroScaleFactors         [gyroFullScale .ordinal()];
	}
    
	// Acceleration axes configuration data.
	private final int[]    accelFullScaleRegSettings = {0x00, 0x08, 0x10, 0x18}; // Bits 3 & 4 of REGISTER_ACCEL_CONFIG
	private final double[] accelScaleFactors         = {16384.0, 8192.0, 4096.0, 2048.0};
	private final int      accelRegConfigValue;
	private final double   accelScaleFactor;

	// Gyro axes configuration data.
	private final int[]    gyroFullScaleRegSettings = {0x00, 0x08, 0x10, 0x18}; // Bits 3 & 4 of REGISTER_GYRO_CONFIG
	private final double[] gyroScaleFactors         = {131.0, 65.5, 32.8, 16.4};
	private final int      gyroRegConfigValue;
	private final double   gyroScaleFactor;

	// Data maintained for each axis.
	AxisData xAccelData = new AxisData("xAccel", 0.5, numCalibrationSamples, 0.0); // Name, LPF time constant, num cal samples, nominal axis value.
	AxisData yAccelData = new AxisData("yAccel", 0.5, numCalibrationSamples, 0.0);
	AxisData zAccelData = new AxisData("zAccel", 0.5, numCalibrationSamples, 1.0);
	AxisData xGyroData  = new AxisData("xGyro" , 0.7, numCalibrationSamples, 0.0);
	AxisData yGyroData  = new AxisData("yGyro" , 0.7, numCalibrationSamples, 0.0);
	AxisData zGyroData  = new AxisData("zGyro" , 0.7, numCalibrationSamples, 0.0);
	
	double   tempF      = 0.0; // Temperature degrees F.
	
	private Timer readTimer   = new Timer();
	
	private boolean mpuAvailable = false;

	//Define registers to be used
	@SuppressWarnings("unused")
	private static final int REGISTER_SELF_TEST_X       = 0x0D,
							 REGISTER_SELF_TEST_Y       = 0x0E,
							 REGISTER_SELF_TEST_Z       = 0x0F,
							 REGISTER_SELF_TEST_A       = 0x10,
							 REGISTER_SAMPLE_RATE       = 0x19,
							 REGISTER_CONFIG            = 0x1A,
							 REGISTER_GYRO_CONFIG       = 0x1B,
							 REGISTER_ACCEL_CONFIG      = 0x1C,
							 REGISTER_FIFO_ENABLE       = 0x23,
							 REGISTER_INTERRUPT_ENABLE  = 0x38,
							 REGSITER_INTERRUPT_STATUS  = 0x3A,
							 REGISTER_ACCEL             = 0x3B,
							 REGISTER_TEMP              = 0x41,
							 REGISTER_GYRO              = 0x43,
							 REGISTER_SIGNAL_PATH_RESET = 0x68, // bit 0 =  Temp, 1 = accel, 2 = gyro
							 REGISTER_PWRMGMT_1         = 0x6B, 
							 REGISTER_PWRMGMT_2         = 0x6C;

	// Initializes the MPU with pre-defined settings
	public boolean init() {
		
		// Start the timer to determine how long init takes.
		readTimer.reset();
		readTimer.start();
		
		// Set up the chip.
		MPU.write(REGISTER_SAMPLE_RATE      , 7);  // Sample rate divider.
		MPU.write(REGISTER_CONFIG           , 6);  // No external sync, DLPF mode 6.
		MPU.write(REGISTER_GYRO_CONFIG , gyroRegConfigValue);
		MPU.write(REGISTER_ACCEL_CONFIG, accelRegConfigValue);
		MPU.write(REGISTER_PWRMGMT_1        , 1);  // PLL with X axis gyroscope reference
		MPU.write(REGISTER_FIFO_ENABLE      , 0);  // Disable FIFO.
		MPU.write(REGISTER_INTERRUPT_ENABLE , 1);  // Interrupt enable

		// Compute calibration data for each axis.

    	for(int i = 0; i < numCalibrationSamples; i++) {
    		
    		computeCalibrationData();
    		if (!mpuAvailable) break;

    	}

    	EventLogging.logInterestingEvent(
    			EventLogging.INTERESTINGEVENTS.MPU6050_DATA, 
    			"name\tscaledValue\tcorrectedValue\tfilteredValue\tcalAvg\tcalStdDev"
    			);
    	
		readTimer.stop();
    	
    	SmartDashboard.putString ("MPU6050 Init Time:", String.format("%7.4f", readTimer.get()));
    	
    	return mpuAvailable;
    	
	}
	
	public void computeCalibrationData () {
		
		if (readRawData()) {

			xAccelData.accumulateCalibrationData();
			yAccelData.accumulateCalibrationData();
			zAccelData.accumulateCalibrationData();
			xGyroData .accumulateCalibrationData();
			yGyroData .accumulateCalibrationData();
			zGyroData .accumulateCalibrationData();

		}

	}
	
	// Read the raw data for each axis.
	private boolean readRawData() {
		
		byte[]  READS           = new byte[14];
        byte[]  interruptStatus = new byte[1];
        int     dataReady;
        boolean timeout  = false;
        
        readTimer.reset();
        readTimer.start();
        
        // Read until a non-zero value is returned (sometimes all-zeros are turned).
        do {

        	// Wait for a "data ready" status.
        	do {

        		MPU.read(REGSITER_INTERRUPT_STATUS, 1, interruptStatus);
        		dataReady = interruptStatus[0] & 0x01;
        		timeout   = readTimer.get() >= 1.0;

        	} while ((dataReady == 0) & !timeout);

        	if (timeout) {
        		readTimer.stop();
        		System.out.println("MPU6050 read timeout!");
        		mpuAvailable = false;
        		return false;
        	}

		    // Read the data.
		    MPU.read(REGISTER_ACCEL, READS.length, READS);
		
        } while ((READS[0] == 0) && (READS[1] == 0));
		
        mpuAvailable = true;
	
		xAccelData.computeRawAndScaledValue( READS[0] , READS[1] , accelScaleFactor); //Convert the int's to scaled values.
		yAccelData.computeRawAndScaledValue( READS[2] , READS[3] , accelScaleFactor);
		zAccelData.computeRawAndScaledValue( READS[4] , READS[5] , accelScaleFactor);
		xGyroData .computeRawAndScaledValue( READS[8] , READS[9] , gyroScaleFactor);
		yGyroData .computeRawAndScaledValue( READS[10], READS[11], gyroScaleFactor);
		zGyroData .computeRawAndScaledValue( READS[12], READS[13], gyroScaleFactor);
		
		byte tempH = READS[6];
		int  tempL = toU(READS[7]);
		int temp   = (tempH << 8) | tempL;
		tempF      = (double) temp * 0.0052941 + 97.754;
		
		readTimer.stop();

		//System.out.println( "ah= " + String.format("0x%02X", READS[0]) + "  al= " + String.format("0x%02X", READS[1]));
		
		return true;
	}
	
	// Called iteratively to read the MPU axis values.  Once called the individual
	// public access methods can be called to return values.
	public void read( boolean isNotMoving, double timeNow) {
		
		// Read the raw data and convert to scaled values.
		if (readRawData()) {

			// Process the data for each axis.
			xAccelData.processAxisData(isNotMoving);
			yAccelData.processAxisData(isNotMoving);
			zAccelData.processAxisData(isNotMoving);
			xGyroData .processAxisData(isNotMoving);
			yGyroData .processAxisData(isNotMoving);
			zGyroData .processAxisData(isNotMoving);
			
			// Log the data for each axis.  Could have incorporated this in processAxisData but
			// this way allows you to disable the data for selected axes if you get overwhelmed by data.
			xAccelData.logAxisData();
			yAccelData.logAxisData();
			zAccelData.logAxisData();
			xGyroData .logAxisData();
			yGyroData .logAxisData();
			zGyroData .logAxisData(); 

			//SmartDashboard.putString ("MPU6050 Temp:"     , String.format("%5.1f", getTemp()));
			SmartDashboard.putString ("MPU6050 Read Time:", String.format("%7.4f", readTimer.get()));

		}

	}

	//Return the X acceleration in g's.
	public double getAccelX() {
		return xAccelData.axisFilteredValue();
	}
	
	//Return the Y acceleration in g's
	public double getAccelY() {
		return yAccelData.axisFilteredValue();
	}
	
	//Return the Z acceleration in g's
	public double getAccelZ() {
		return zAccelData.axisFilteredValue();		
	}
	
	//Return the X gyro value in deg/sec
	public double getGyroRateX() {
		return xGyroData.axisFilteredValue();
	}
	
	//Return the Y gyro value in deg/sec
	public double getGyroRateY() {
		return yGyroData.axisFilteredValue();
	}
	
	//	Return the Z gyro value in deg/sec, 
	public double getGyroRateZ() {
		return zGyroData.axisFilteredValue();
	}
	
	//	Return the temperature of the MPU in degrees f
	public double getTemp() { // degrees F
		return tempF;
	}
    
	// Java does not support unsigned values, so we convert Java's signed byte
    // to an unsigned value in a signed integer.
	private int toU(byte value) {
	    return value >= 0 ? value : 2 * (int) Byte.MAX_VALUE + 2 + value;
	}
	
	/***********************************************************************************
	 * Parent class that encapsulates the common data/processing of each IMU axis type.
     ***********************************************************************************/
	private class AxisData {
		
		private String        name;                 // Name of the axis for debug purposes.
		private int           rawValue       = 0;   // The raw value of the axis as returned by the MPU6050.
		private double        scaledValue    = 0.0; // The raw value scaled to the proper units.
		public  double        correctedValue = 0.0; // The scaled value with the average (offset) and trim applied.
		private double        filteredValue  = 0.0; // Result of running the correctedValue thru the low pass filter.
		private double		  desiredCalValue;      // The value to be returned under no motion/level orientation.
		private FirstOrderLPF lpf;                  // Low pass filter for the correctedValue, producing filteredValue.
		private AverageFilter avgStats;
		
		// Constructor
		private AxisData (String name, double lpfK, int numSamplesForAverage, double desiredCalValue) {

			this.name    		 = name;
			this.lpf             = new FirstOrderLPF(lpfK); // lpfK may need to be tuned for each axis.
			this.avgStats        = new AverageFilter(numSamplesForAverage);
			this.desiredCalValue = desiredCalValue;
		}
		
		// Compute intermediate stats during calibration.
		public void accumulateCalibrationData (){
			
			avgStats.accumulate(this.scaledValue);

		}
		
		// Computes the raw and scaled value as supplied by the MPU6050.
		public void computeRawAndScaledValue (byte highOrder, byte lowOrder, double scaleFactor) {

			this.rawValue = (highOrder << 8)   | toU(lowOrder);    //Convert all the bytes to int's.

			this.scaledValue = (double)this.rawValue / scaleFactor;
		}
		
		// Child class provides unique implementation based on axis type.
		public void processAxisData (boolean notMoving) {
	
			double average = this.avgStats.average();
			
			// Apply the offset (running average) to remove any bias.
			this.correctedValue = this.scaledValue - average + this.desiredCalValue;
			
			// Apply the LPF to the corrected value.
			this.filteredValue  = lpf.filter(this.correctedValue);
			
			// if we are not moving then recompute the offset to apply to measurements.  This is intended
			// to correct drift.  "Not moving" may be difficult to determine.  For instance, motors may be
			// commanded to zero but the robot is still coasting.  Here it is defined as the external "not moving"
			// indicator being "true" and the measurement being within one standard deviation of its current
			// "not moving" average.
			if (notMoving)
				if (Math.abs(this.scaledValue - average) < this.avgStats.stdDeviation())
				    accumulateCalibrationData();
			
		}
		
		public void logAxisData () {
			
	    	EventLogging.logInterestingEvent(
	    			EventLogging.INTERESTINGEVENTS.MPU6050_DATA, 
	    			this.name 				+ "\t" +
	    			this.scaledValue        + "\t" +
	    			this.correctedValue     + "\t" + 
	    			this.filteredValue      + "\t" + 
	    			this.avgStats.average() + "\t" + 
	    			this.avgStats.stdDeviation()
	    			);
		}
		
		// Access methods for internal data.
		public int    axisRawValue       () { return this.rawValue; }
		public double axisScaledValue    () { return this.scaledValue; }
		public double axisCorrectedValue () { return this.correctedValue; }
		public double axisFilteredValue  () { return this.filteredValue; }
		
	}
	
	
}