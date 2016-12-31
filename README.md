# Robot2017

This repository holds the 2017 RoboEagles robot code.  New this year are instrumentation and vision packages.   These are summarized in more detail, below.  
Here are some 2017 software tasks:  

* Use RobotBuilder to generate the software framework for the 2017 robot.  
  * Define and implement commands.  
  * Define and implement subsystems.  
  * Define and implement autonomous mode.  
    * Use vision system?  
    * If so, design and implement driving algorithms.  Drive straight to target?  Drive in an arc to end up perpendicular to the target?  Drive around the target?  
    * Use discrete inputs that are read by the software to identify pre-programmed autonomous actions?
* Vision coding and integration  
  * Build a true-size mock-up of each target to be tracked.  Place retro-reflective tape as in the real game.  
  * Take photos of the targets lit using the camera light.  
  * Use these photos as inputs to the GRIP tool to create an image processing pipeline java class for each target.  
  * Create a child to the TargetTrack class for each target to be tracked.  
  * Test tracking ability using the target mock-ups and the robot simulator.  


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

## Installing OpenCV and Setup of OpenCV Library in Eclipse

OpenCV provides a library of image processing routines that the vision package uses to identify objects on images taken by a camers.  OpenCV needs to be installed on the laptop and configured in Eclipse.  

* **Install Windows version of OpenCV 3.1:** http://opencv.org/downloads.html  Install to C:\OpenCV-3.1.0.  
* **Configure Eclipse Robot2017 For OpenCV:**  
  * Window->Preferences->Java –> Build Path –> User Libraries -> New…   
  * Enter the name of the new library, “OpenCV-3.1.0”, and press “OK”.  
  * Select “Add External JARs…”  
  * Browse through C:\\OpenCV-3.1.0\\build\\java\\ and select opencv-310.jar.   
  * Extend the opencv-246.jar and select Native library location and press Edit....  
  * Select External Folder... and browse to select the folder C:\\OpenCV-3.1.0\\build\\java\\x64. If you have a 32-bit system you need to select the x86 folder instead of x64.  

## Installing OpenCV on a RoboRIO  

The following process relies on two programs being installed on the laptop:  

* Python: https://www.python.org/downloads/  
* Putty SSH client:  http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html  

Then:  

* Copy installer.py, found in this repository, to the Scripts folder where python was installed.  
* Open a command window.  On Windows 10: Right-mouse-button on Windows icon -> Command Prompt  
* Type: cd "path to where python.exe is located”  
* Make sure the roboRIO is powered and the laptop is connected to the robot network (4579).  
* In the command window type:  
  * python installer.py download-opkg opencv3-java  
  * python installer.py install-opkg opencv3-java  
* Start Putty and connect to RoboRIO:  
  * Set the Host Name to: roboRIO-4579-FRC.local  
  * Set Connection Type to: SSH  
  * Select Open.  
  * Log in as: lvuser  
* Use Putty to move the OpenCv .jar and .so files into one of the java_library_path directories:  
  * cp /usr/local/share/OpenCV/java/libopencv_java310.so /usr/lib  
  * cp /usr/local/share/OpenCV/java/opencv-310.jar /usr/lib  
* If not already present, in the Robot Java code you need to load the OpenCV library.  In Robot.java, underneath the class definition **add**:  
  
  public class Robot extends IterativeRobot {  
  
     **static {**  
          **System.load("/usr/local/share/OpenCV/java/libopencv_java310.so");**  
          **}**  
             
* If not already present, add the following to the Eclipse build.xml file at the end but before "&lt;/project>":  
     &lt;var name="classpath" value="${classpath}:C:\OpenCV-3.1.0\opencv\build\java/opencv-310.jar" />  
    
## RobotBuilder  

Update Robot2017.yaml, in this repository, to design the 2017 robot and generate the software framework.

## Graphically Represented Image Processing (GRIP) Tool
The FRC GRIP tool is a program for rapidly prototyping and deploying computer vision algorithms.  It is used to generate an “image processing pipeline” that isolates features of an image that are believed to be a target of the type being searched for (e.g., the 2015 Castle Tower Goal).  These features are called “contours”.  The best contour is selected by applying tests to the contours to select the one that best matches the target being searched for.  

__*Robot2017.grip, in this repository, needs to be modified to find contours for objects of interest in the FRC 2017 game.*__  

Download the latest version of GRIP from: https://github.com/WPIRoboticsProjects/GRIP/releases
Select x64 or x86_32 version of the .exe, depending on your particular laptop.  

Introduction to GRIP: https://wpilib.screenstepslive.com/s/4485/m/24194/l/463566-introduction-to-grip?id=463566-introduction-to-grip

##Vision Processing Software Architecture  

A vision java package, composed of multiple java classes, has been created.  In a nutshell:  

* **TargetTracker class.**  This class is an abstract class, meaning that it defines abstract methods that a child class to this class must implement.  These methods define the physical attributes of the target being searched for, which GRIP-generated image processing pipeline to invoke for a particular target, and which contour tests to run.  TargetTracker’s trackTarget method can then be invoked to find the target.  It returns a range, bearing, and elevation to the target.  

* **CastleTowerGoalTracker class.**  This class is an example of a child class to the TargetTracker class.  It defines the attributes of the 2016 game castle tower goal.  It implements the abstract methods in the TargetTracker class.  __*This class needs to be renamed and updated to identify the attributes of a 2017 game target.*__  

* **GRIPCastleTowerGoalPipeline class.**  This is the GRIP-generated java code that implements the image processing pipeline for a particular target.  __*This class will need to be renamed and updated for a 2017 game target.*__  

##Instrumentation Package  

The instrumentation package saves data for debug purposes.  

* **Instrumentation class.**  This class sets up a “\lvuser\runs” directory on the RoboRIO to hold debug files.  Each time the software starts up a new directory is created in the “runs” directory.  The directory name contains a time reference (the time supplied by the system does not seem to account for our time zone).  Each child class can then write data files to this directory.  This data can be pulled over to a laptop for analysis using an FTP client like FileZilla: https://filezilla-project.org/download.php?type=client .  Log on to the RoboRIO as follows:

  * Host: roborio-4579-frc.local
  * Username: lvuser
  * Password: <leave blank>
  * Port: 22

* **EventLogging class.**  This child class of the Instrumentation class creates an Events.txt file.  This file contains a trail of time-tagged “breadcrumbs”, or events, of various logic paths in the code.  See EventLogging.java for more information.  

* **ImageLogging class.**  This child class of the Instrumentation class writes .jpg files processed by the vision software.  The .jpg file name contains the time at which the image was processed.   If one or more possible targets are detected, the image is annotated as follows:
  * An orange cross marks the center of the image.
  * Target range, bearing, and elevation are in the upper left.
  * Potential target contours are drawn in blue.
  * The contour that most likely represents the target is bounded by a green rectangle.  The contours of less-likely targets are bounded by a red rectangle.
  * Bounding rectangles are annotated on the upper left corner with a red contour ID number, and one or more contour test scores are shown in orange down the right side of the rectangle.  Using the time tag in the file name, and the contour ID, more information about the processing of the contours can be found in the Events.txt file.  

* **FRCSmartDashboard class.**  This class provides methods to write data to the SmartDashboard.  The format of the data that is displayed is saved in the SmartDashboard.xml file contained in this repository.  

* **FRCPCDashboard class.**  This class can be used to encapsulate the writing/reading of data to/from the PC Dashboard.
