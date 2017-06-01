How to run the Java demonstration program in Eclipse:
-------------------------------------------------------
- Create a new Java project and select the src folder as the location of the project files
- Go to Project -> Properties -> Java Build Path and click on the Libraries tab
- Click on Add External Jars and select and add the three jar files in the lib folder:
  - Jackson-annotations-2.0.5.jar
  - Jackson-core-2.0.5.jar
  - Jackson-databind-2.0.5.jar
- Now it should be possible to run 'TestRobot2'. Remember to start MRDS4 before you run your program. This class contains communication with the robot and also an example on how to draw a map from a grid.

How to test your final code in Linux
-------------------------------------------------------
- Start MRDS on a windows machine
- Use putty or similar to log into itchy.cs.umu.se
- cd ~/edu/5DV122/lab2
- Run your code by: ./mapper http://hydra.cs.umu.se:50000 -30 -20 40 45 (change hydra to the actual computer you are running MRDS on). The simulated robot should now move around.

