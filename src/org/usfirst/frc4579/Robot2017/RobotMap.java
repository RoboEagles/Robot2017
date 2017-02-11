// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4579.Robot2017;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static Relay camLight;
    public static SpeedController camLightController;
    public static SpeedController driveTraincontrollerLeft;
    public static SpeedController driveTraincontrollerRight;
    public static RobotDrive driveTrainRobotDrive;
    public static Ultrasonic ultrasonicSensorultraLeft;
    public static Ultrasonic ultrasonicSensorultraRight;
    public static DigitalInput switchesmiddle;
    public static DigitalInput switchesrightSwitch;
    public static DigitalInput switchesleftSwitch;
    public static SpeedController lifterliftMotor;
    public static PowerDistributionPanel pDPCurrentrobotPDP;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public static void init() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        camLight = new Relay(0);
        LiveWindow.addActuator("Cam", "Light", camLight);
        
        camLightController = new Victor(1);
        LiveWindow.addActuator("Cam", "LightController", (Victor) camLightController);
        
        driveTraincontrollerLeft = new VictorSP(9);
        LiveWindow.addActuator("DriveTrain", "controllerLeft", (VictorSP) driveTraincontrollerLeft);
        
        driveTraincontrollerRight = new VictorSP(8);
        LiveWindow.addActuator("DriveTrain", "controllerRight", (VictorSP) driveTraincontrollerRight);
        
        driveTrainRobotDrive = new RobotDrive(driveTraincontrollerLeft, driveTraincontrollerRight);
        
        driveTrainRobotDrive.setSafetyEnabled(true);
        driveTrainRobotDrive.setExpiration(0.1);
        driveTrainRobotDrive.setSensitivity(0.5);
        driveTrainRobotDrive.setMaxOutput(1.0);

        driveTrainRobotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        ultrasonicSensorultraLeft = new Ultrasonic(8, 9);
        LiveWindow.addSensor("UltrasonicSensor", "ultraLeft", ultrasonicSensorultraLeft);
        
        ultrasonicSensorultraRight = new Ultrasonic(6, 7);
        LiveWindow.addSensor("UltrasonicSensor", "ultraRight", ultrasonicSensorultraRight);
        
        switchesmiddle = new DigitalInput(0);
        LiveWindow.addSensor("Switches", "middle", switchesmiddle);
        
        switchesrightSwitch = new DigitalInput(1);
        LiveWindow.addSensor("Switches", "rightSwitch", switchesrightSwitch);
        
        switchesleftSwitch = new DigitalInput(2);
        LiveWindow.addSensor("Switches", "leftSwitch", switchesleftSwitch);
        
        lifterliftMotor = new Talon(0);
        LiveWindow.addActuator("Lifter", "liftMotor", (Talon) lifterliftMotor);
        
        pDPCurrentrobotPDP = new PowerDistributionPanel(0);
        LiveWindow.addSensor("PDPCurrent", "robotPDP", pDPCurrentrobotPDP);
        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    }
}
