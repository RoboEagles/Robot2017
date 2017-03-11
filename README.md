# Robot2017

This repository holds the 2017 RoboEagles robot code.  New this year are some instrumentation packages.   These are summarized in more detail, below.  

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

