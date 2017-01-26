# Robot2017

This repository holds the 2017 RoboEagles robot code.  New this year are some instrumentation packages.   These are summarized in more detail, below.  

Here are some 2017 software tasks:  

* Java Resources  
  * http://docs.oracle.com/javase/tutorial/java/  Read through Classes and Objects.  
  * https://github.com/RoboEagles/Resources/tree/master/Java%20Lesson
* Software Updates  
  * Install the FRC 2017 update suite (all laptops).  https://wpilib.screenstepslive.com/s/4485/m/13810/l/599669-installing-the-frc-2017-update-suite-all-languages  
  * Update Eclipse FRC plug-ins.  See "Installing the development plugins" in https://wpilib.screenstepslive.com/s/4485/m/13503/l/599747-installing-java-8-on-the-roborio-using-the-frc-roborio-java-installer-java-only  
* Use RobotBuilder to generate the software framework for the 2017 robot.  
  * Define and implement subsystems:  
    * Drive Train  
      * Two motors for now.  
      * Methods to do arcade drive and drive in a specific direction.   
    * Measurement __(Jonathan)__  
      * TBD distance sensor.  
      * MPU6060 accelerometer.  
    * Lift
      * Single motor controller.  
      * TBD limit switch to stop climb.  
    * Autonomous Switches  
      * Three toggle switches that specify robot placement along wall (Left, Center, Right).  
      * Need a method to determine placement position.  
      * Need a method to determine alliance (to be able to tell direction to drive to retrieval zone after placing gear).  
      * All three switches off means a default autonomous program (drive over baseline)?  
    * Camera __(Jonathan)__  
      * Stream camera images to Dashboard to allow pilot to better align to gear pegs.    
  * Define and implement commands:  
    * Place Gear: Stop robot motion, use distance sensor to find gear post, navigate to it, place gear, back robot off.  Tie this command to a joystick button.
    * Climb Rope: The lift motor is run as long as the joystick trigger is depressed.
    * Autonomous mode - read switches to determine which autonomous program to run:
      * Left: Drive to a predetermined location in front of the left gear post and execute Place Gear command.  After gear is placed drive towards gear retrieval zone.  
      * Center: Drive to a predetermined location in front of the center gear post and execute Place Gear command.  After gear is placed drive towards gear retrieval zone.  
      * Right: Drive to a predetermined location in front of the right gear post and execute Place Gear command.  After gear is placed drive towards gear retrieval zone.  
      * No position set (all switches off): Drive straight across baseline TBD feet.  
      * Two or more positions set (error): Drive straight across baseline TBD feet.  
* Instrumentation Package and Camera Class   
  * ** Per FIRST rule R14, we can't use the new Instrumentation package and Camera subsytem as-is since it was work performed before kick-off.  As such, the programmers will re-code the classes as follows:**    
     * Update the Instrumentation class as follows:  
       * Delete the "abstract" keyword from the class declaration.  
       * Create a "public static void initialize()" method that does the work of the constructor.  
       * Delete the constructor.  
       * Delete the processing related to the BaseDataDir feature and update the class header comments accordingly.  
     * Update the EventLogging class as follow:  
       * Delete "extends Instrumentation" from the class declaration.  
       * Delete the constructor and static block.  
     * Update the FRCSmartDashboard class as follows:  
       * Delete all but the setCameraIntialized method.  
       * Add other methods to send debug data to the Dashboard as needed.  
     * Update the Camera class as follows:  
       * Create an initialize method that initializes the camera in the same way that the FrameGrabber class does.   
       * Delete the FrameGrabber class and any methods or variables that reference it.  
       * Delete methods related to getting images.  
       * Delete methods related to controlling a light.  
       * Add processing to send camera images to the Dashboard.  
         * https://wpilib.screenstepslive.com/s/4485/m/24194/l/669166-using-the-camera-server-on-the-roborio-2017  
         * https://wpilib.screenstepslive.com/s/4485/m/24194/l/682778-read-and-process-video-cameraserver-class  

## Installing This Repository and Initial Eclipse Setup  

* **Clone this repository to your local machine.**  
* **Import into Eclipse:**  
  * File->Import->Existing Projects Into Workspace  
  * Set “Select root directory” to the location of the cloned repository and select Finish.  
* **Set up networktables and wpilib libraries, if not already present:**  
  * Right Mouse Button on project name -> Build Path -> Configure Build Path -> Libraries tab  
  * Select networktables -> Edit… -> Variable -> New..  
    Name: NET_LIB  
    Path (file): c:\\users\\bstevens\\java\\current\\lib\\networktables.jar  
  * Select wpilib -> Edit… -> Variable -> New...  
    Name: WPI_LIB  
    Path (file): c:\\users\\bstevens\\java\\current\\lib\\wpilib.jar  
    
## RobotBuilder  

Update Robot2017.yaml, in this repository, to design the 2017 robot and generate the software framework.  

##Instrumentation Package  

The instrumentation package saves data for debug purposes.  

* **Instrumentation class.**  This class sets up a “\lvuser\runs” directory on the RoboRIO to hold debug files.  Each time the software starts up a new directory is created in the “runs” directory.  The directory name contains a time reference (the time supplied by the system does not seem to account for our time zone).  Other instrumentation classes can then write data files to this directory.  This data can be pulled over to a laptop for analysis using an FTP client like FileZilla: https://filezilla-project.org/download.php?type=client .  Log on to the RoboRIO as follows:

  * Host: roborio-4579-frc.local
  * Username: lvuser
  * Password: <leave blank>
  * Port: 22

* **EventLogging class.**  This child class of the Instrumentation class creates an Events.txt file.  This file contains a trail of time-tagged “breadcrumbs”, or events, of various logic paths in the code.  See EventLogging.java for more information.  

* **FRCSmartDashboard class.**  This class provides methods to write data to the SmartDashboard.  The format of the data that is displayed is saved in the SmartDashboard.xml file contained in this repository.  

* **FRCPCDashboard class.**  This class can be used to encapsulate the writing/reading of data to/from the PC Dashboard.
