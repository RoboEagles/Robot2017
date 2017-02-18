package com.eagles.sensors;


//import com.eagles.math.Vector3d;
//import com.eagles.math.Vector3s;

import edu.wpi.first.wpilibj.I2C;

public class MPU_6050_I2C {
	
	private byte deviceAddress;
	
	private int accelSensitivity = 0,
				gyroSensitivity = 0,
				sampleRateDivider = 18,
				digitalLPFConfig = 3;
	
	// Where the raw values from the registers will be stored
	public Vector3s rawAccelerometer = new Vector3s(),	// Accelerometer Raw
					 rawGyro = new Vector3s();			// Gyro Raw
	
	private Vector3d accelValues = new Vector3d(),		// Converted, unfiltered readings for accelerometer
					 gyroValues = new Vector3d();		// Converted, unfiltered readings for gyro
	
	private double temp = 0.; // Current temperature value in degrees C
	
	private double accelLSB_Sensitivity = ACCEL_SENSITIVITY[accelSensitivity][1], // Grab default LSB sensitivity for accelerometer
				   gyroLSB_Sensitivity = GYRO_SENSITIVITY[gyroSensitivity][1];	// Grab default LSB sensitivity for gyro
	
	//Define registers to be used
	@SuppressWarnings("unused")
	private static final int REGISTER_PWRMGMT_1 = 0x6B, 
							 REGISTER_PWRMGMT_2 = 0x6C,
							 REGISTER_ACCEL = 0x3B,
							 REGISTER_GYRO = 0x43,
							 REGISTER_CONFIG = 0x1A,
							 REGISTER_GYRO_CONFIG = 0x1B,
							 REGISTER_ACCEL_CONFIG = 0x1C,
							 REGISTER_TEMP = 0x41,
							 REGISTER_SAMPLE_RATE = 0x19,
							 REGISTER_INTERUPT_ENABLE = 0x38,
							 REGSITER_INTERUPT_STATUS = 0x3A;
	
	// Define data ready interrupt bit, for controlling readouts
	private static int DATA_READY_INT = 0;
	
	//Array found in MPU 6050 datasheet for LSB Sensitivity
	private static final double[][] ACCEL_SENSITIVITY = {
			{ 2. , 16384. },{ 4. , 8192. },{ 8. , 4096. },{ 16. , 2048. },
	};
	
	private static final double[][] GYRO_SENSITIVITY = {
			{ 250 , 131. },{ 500. , 65.5 },{ 1000. , 32.8 },{ 2000. , 16.4 },
	};

	// Define an enumeration for grabbing accel LSB Sensitivity from a constant
	// "Value" refers to position in the ACCEL_SENSITIVITY array
	public enum ACCEL_VALUES {
		
		k2g(0), k4g(1), k8g(2), k16g(3);
		
		private ACCEL_VALUES(int settingValue) {
			this.value = settingValue;
		}
		
		public int value;
		
	}
	
	// Define an enumeration for grabbing accel LSB Sensitivity from a constant
	// "Value" refers to position in the GYRO_SENSITIVITY array
	public enum GYRO_VALUES {
		
		k250(0), k500(1), k1000(2), k2000(3);
		
		private GYRO_VALUES(int settingValue) {
			this.value = settingValue;
		}
		
		public int value;
		
	}
	
	// I2C interface for the MPU
	private I2C MPU;
	
	private byte[] accelReads = new byte[6], // Raw Byte Readinds of the Accelerometer
					gyroReads = new byte[6], // Raw Byte Reading of the Gyro
					interruptStatus = new byte[1], // Interrupt Status byte. Bit 0 is INT_READY
					tempBuff = new byte[2], // Temperature raw byte read buffer
					READS = new byte[14]; // Primary buffer for reading values
	
	/*
	 * 
	 * @param byte deviceAddress I2C device address of the MPU6050
	 * @param ACCEL_VALUES accelSensitivity Acceleration sensitivity value k2g,k4g,k8g,k16g
	 * @param GYRO_VALUES gyroSensitivity  Gyro (deg/s) sensitivity value k250,k500,k1000,k2000 (deg/s)
	 * 
	 */
	public MPU_6050_I2C(byte deviceAddress, 
						ACCEL_VALUES accelSensitivity, 
						GYRO_VALUES gyroSensitivity) {
	
		this.accelSensitivity = accelSensitivity.value;
		this.gyroSensitivity = gyroSensitivity.value;
		this.deviceAddress = deviceAddress;
		
		this.accelLSB_Sensitivity = ACCEL_SENSITIVITY[this.accelSensitivity][1];
		this.gyroLSB_Sensitivity = GYRO_SENSITIVITY[this.gyroSensitivity][1];
		
		MPU = new I2C(I2C.Port.kOnboard, (int) this.deviceAddress);
		
		init();
		
	}

	/*
	 * 
	 * @param byte deviceAddress I2C device address of the MPU6050
	 * 
	 */
	public MPU_6050_I2C(byte deviceAddress) {
		
		this(deviceAddress, ACCEL_VALUES.k4g, GYRO_VALUES.k500);
		
		
	}

	/*
	 * 
	 * Uses default device address (0x68), Accel Sensitivity (k4g), and Gyro Sensitivity (k500) 
	 * 
	 */
	public MPU_6050_I2C() {
		
		this((byte) 0x68);
		
	}
	
	// Initializes the MPU with pre-defined settings
	private void init() {
		
		byte[] registerConfig = new byte[1],
				registerAccelConfig = new byte[1],
				registerGyroConfig = new byte[1];
		
		MPU.read(REGISTER_CONFIG, registerConfig.length, registerConfig);
		MPU.read(REGISTER_ACCEL_CONFIG, registerAccelConfig.length, registerAccelConfig);
		MPU.read(REGISTER_GYRO_CONFIG, registerGyroConfig.length, registerGyroConfig);
		
		registerConfig[0] = (byte) ((registerConfig[0] & (byte) 248) | (byte) digitalLPFConfig);
		registerAccelConfig[0] = (byte) ((registerAccelConfig[0] & (byte) 99 | (byte) this.accelSensitivity << 3));
		registerGyroConfig[0] = (byte) ((registerGyroConfig[0] & (byte) 99 | (byte) this.gyroSensitivity << 3));
		
		MPU.write(REGISTER_SAMPLE_RATE, (byte) sampleRateDivider);
		MPU.write(REGISTER_CONFIG, registerConfig[0]);
		MPU.write(REGISTER_ACCEL_CONFIG, registerAccelConfig[0]);
		MPU.write(REGISTER_GYRO_CONFIG, registerGyroConfig[0]);
		MPU.write(REGISTER_PWRMGMT_1, (byte) 0x00);
		MPU.write(REGISTER_PWRMGMT_2, (byte) 0x00);
		MPU.write(REGISTER_INTERUPT_ENABLE, (byte) 0x01);
		
		
	}
	
	// Called iteratively to read the MPU, 
	// read() returns this object for object feedback (you can call .getAccel() right from the read() method 
	public MPU_6050_I2C read() {
				
		do {

			MPU.read(REGSITER_INTERUPT_STATUS, 1, interruptStatus);
			DATA_READY_INT = ((byte) interruptStatus[0]) & 0x01;
			
		} while (DATA_READY_INT == 0);
			
			MPU.read(REGISTER_ACCEL, READS.length, READS);
			
			int n = 0;
			for(int i = 0; i < accelReads.length; i++) {
				
				accelReads[i] = READS[n];
				n++;
				
			}
			
			for(int i = accelReads.length; i < (tempBuff.length + accelReads.length); i++) {
				
				tempBuff[i] = READS[n];
				n++;
				
			}
			
			for(int i = (accelReads.length + tempBuff.length); i < (tempBuff.length + accelReads.length + gyroReads.length); i++) {
				
				gyroReads[i] = READS[n];
				n++;
				
			}
			
			rawAccelerometer.X = (short) ((accelReads[0] << 8) | accelReads[1]);
			rawAccelerometer.Y = (short) ((accelReads[2] << 8) | accelReads[3]);
			rawAccelerometer.Z = (short) ((accelReads[4] << 8) | accelReads[5]);
			
			rawGyro.X = (short) ((gyroReads[0] << 8) | gyroReads[1]);
			rawGyro.Y = (short) ((gyroReads[2] << 8) | gyroReads[3]);
			rawGyro.Z = (short) ((gyroReads[4] << 8) | gyroReads[5]);

			short tempTmp = (short) ((tempBuff[0] << 8) | tempBuff[1]);
			
			temp = (tempTmp / 340) + 36.53;
		
		return this;
		
	}
	
	//Returns the acceleration as a vector of double data types in Gs
	public Vector3d getAccel() {
		return accelValues.set(rawAccelerometer).divide(this.accelLSB_Sensitivity);
	}
	
	//Returns the gyro as a vector of double data types in deg/s
	public Vector3d getGyro() {
		return gyroValues.set(rawGyro).divide(this.gyroLSB_Sensitivity);
	}
	
	//Return the X acceleration in Gs
	public double getAccelX() {
		return getAccel().X;
	}
	
	//Return the Y acceleration in Gs
	public double getAccelY() {
		return getAccel().Y;
	}
	
	//Return the Z acceleration in Gs
	public double getAccelZ() {
		return getAccel().Z;		
	}
	
	//Return the X gyro value in deg/s
	public double getGyroX() {
		return getGyro().X;
	}
	
	//Return the Y gyro value in deg/s
	public double getGyroY() {
		return getGyro().Y;
	}
	
	//	Return the Z gyro value in deg/s, 
	//	this will probably be the most used axis of the three
	public double getGyroZ() {
		return getGyro().Z;
	}
	
	//	Return the temperature of the MPU in degrees C
	public double getTemp() { // degrees Centigrade
		
		return temp;
		
	}

}