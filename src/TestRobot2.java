/**
 * TestRobot interfaces to the (real or virtual) robot over a network
 * connection. It uses Java -> JSON -> HttpRequest -> Network -> DssHost32 ->
 * Lokarria(Robulab) -> Core -> MRDS4
 * 
 * @author Thomas Johansson, dept. of Computing Science, Umeå University, Umeå,
 *         Sweden Mail: thomasj@cs.umu.se
 * 
 *         Updated by Ola Ringdahl 2015-03-13, 2015-12-16
 */
public class TestRobot2 {
    private RobotCommunication robotcomm; // communication drivers

    /**
     * Create a robot connected to host "host" at port "port"
     * 
     * @param host
     *            normally http://127.0.0.1
     * @param port
     *            normally 50000
     */
    public TestRobot2(String host, int port) {
	robotcomm = new RobotCommunication(host, port);
    }

    /**
     * This simple main program creates a robot, sets up some speed and turning
     * rate and then displays angle and position for 16 seconds.
     * 
     * @param args
     *            not used
     * @throws Exception
     *             not caught
     */
    public static void main(String[] args) throws Exception {
	System.out.println("Creating Robot");
	// TestRobot2 robot = new TestRobot2("http://127.0.0.1", 50000);
	TestRobot2 robot = new TestRobot2("http://bratwurst.cs.umu.se", 50000);

	robot.run();
    }

    private void run() throws Exception {
	System.out.println("Creating response");
	LocalizationResponse lr = new LocalizationResponse();
	LaserEchoesResponse ler = new LaserEchoesResponse();
	LaserPropertiesResponse lpr = new LaserPropertiesResponse();

	System.out.println("Creating request");
	DifferentialDriveRequest dr = new DifferentialDriveRequest();
	// set up the request to move in a circle
	dr.setAngularSpeed(Math.PI * 0.2);
	dr.setLinearSpeed(0.3);

	System.out.println("Start to move robot");
	int rc = robotcomm.putRequest(dr);
	System.out.println("Response code " + rc);

	createMap(); // create an example map
	// Ask for the laser beam angles
	robotcomm.getResponse(lpr);
	double[] angles = getLaserAngles(lpr);
	for (int i = 0; i < 10; i++) {
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException ex) {
	    }

	    // ask the robot about its position and angle
	    robotcomm.getResponse(lr);

	    double angle = lr.getHeadingAngle();
	    System.out.println("heading = " + angle);

	    double[] position = getPosition(lr);

	    System.out.println("position = " + position[0] + ", " + position[1]);

	    // Ask the robot for laser echoes
	    robotcomm.getResponse(ler);
	    double[] echoes = ler.getEchoes();
	    System.out.println("Object at " + echoes[56] + "m in " + angles[56] * 180.0 / Math.PI + " degrees");
	}
	System.out.println("Anlgle at 0: " + angles[0] * 180.0 / Math.PI + " at 45: " + angles[45] * 180.0 / Math.PI
		+ " at 90: " + angles[90] * 180.0 / Math.PI + " at 225: " + angles[225] * 180.0 / Math.PI
		+ "\nAngle at 268: " + angles[268] * 180.0 / Math.PI + " at 270: " + angles[270] * 180.0 / Math.PI
		+ " at 270: " + angles[269] * 180.0 / Math.PI);

	// This is where the laser is mounted on the robot (15cm in front of center)
	double[] lpos = lpr.getPosition();
	System.out.println("Laser position (x,y,z): (" + lpos[0] + ", " + lpos[1] + ", " + lpos[2] + ")");

	// set up request to stop the robot
	dr.setLinearSpeed(0);
	dr.setAngularSpeed(0);

	System.out.println("Stop robot");
	rc = robotcomm.putRequest(dr);
	System.out.println("Response code " + rc);
    }

    /**
     * A simple example of how to use the ShowMap class that creates a map from
     * your grid, update it and save it to file
     */
    private void createMap() {
	/* use the same no. of rows and cols in map and grid */
	int nRows = 60;
	int nCols = 65;
	boolean showGUI = true; // set this to false if you run in putty
	ShowMap map = new ShowMap(nRows, nCols, showGUI);
	/* Creating a grid with 0.5 */
	int[][] grid = new int[nRows][nCols];
	for (int i = 0; i < nRows; i++) {
	    for (int j = 0; j < nCols; j++) {
		grid[i][j] = 7;
	    }
	}
	/* create some obstacles (black/grey) */
	// Upper left side:
	grid[0][0] = 15;
	grid[0][1] = 15;
	grid[0][2] = 15;
	grid[0][3] = 15;
	grid[0][4] = 15;
	grid[0][5] = 15;
	grid[0][6] = 15;
	grid[0][7] = 15;

	// Lower right side:
	grid[59][64] = 15;
	grid[58][64] = 15;
	grid[57][64] = 15;
	grid[56][64] = 15;
	grid[55][64] = 15;

	// Lower left side:
	grid[59][0] = 12;
	grid[59][1] = 11;
	grid[59][2] = 10;
	grid[59][3] = 9;
	grid[59][4] = 8;

	// An explored area (white)
	for (int rw = 35; rw < 50; rw++) {
	    for (int cl = 32; cl < 55; cl++) {
		grid[rw][cl] = 0;
	    }
	}
	// Max grid value
	int maxVal = 15;

	// Position of the robot in the grid (red dot)
	int robotRow = 40;
	int robotCol = 42;

	// Update the grid
	map.updateMap(grid, maxVal, robotRow, robotCol);
    }

    /**
     * Extract the robot bearing from the response
     * 
     * @param lr
     * @return angle in degrees
     */
    double getBearingAngle(LocalizationResponse lr) {
	double angle = lr.getHeadingAngle();
	return angle * 180 / Math.PI;
    }

    /**
     * Extract the position
     * 
     * @param lr
     * @return coordinates
     */
    double[] getPosition(LocalizationResponse lr) {
	return lr.getPosition();
    }

    /**
     * Get corresponding angles to each laser beam
     * 
     * @param lpr
     * @return laser angles in radians
     */
    double[] getLaserAngles(LaserPropertiesResponse lpr) {
	int beamCount = (int) ((lpr.getEndAngle() - lpr.getStartAngle()) / lpr.getAngleIncrement()) + 1;
	double[] angles = new double[beamCount];
	double a = lpr.getStartAngle();
	for (int i = 0; i < beamCount; i++) {
	    angles[i] = a;
	    // We get roundoff errors if we use AngleIncrement. Use 1 degree in
	    // radians instead
	    a += 1 * Math.PI / 180;// lpr.getAngleIncrement();
	}
	return angles;
    }
}
