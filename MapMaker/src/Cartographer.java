/**
 * Updates the Map with the data acquired from the lasers
 * 
 */
public class Cartographer {

    private final AreaMap map;
    private final float mapWidth; //x
    private final float mapLength; //y
        

    public Cartographer(AreaMap map, float mapWidth, float mapLength) {
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapLength = mapLength;
    }
    
    /**
     * Updates elements of the map along a laser line.
     * @param robotPosition the position of the robot
     * @param laserAngle the angle of the laser TO THE X AXIS, not the robot's orientation. In radians.
     * @param echoLength the distance along the laser to the obstacle.
     */
    public void updateLaserLine(Position robotPosition, double laserAngle, double echoLength){
        double currentX = robotPosition.getX();
        double currentY = robotPosition.getY();
        double currentDistance = 0; // the distance along the laser
        
        do{
            currentDistance += map.GRID_ELEMENT_SIZE;
            
            currentX = currentDistance * Math.cos(laserAngle);
            currentY = currentDistance * Math.sin(laserAngle);
            
            map.empty(currentX/map.GRID_ELEMENT_SIZE, currentY/map.GRID_ELEMENT_SIZE);
            
        }while (currentDistance <= echoLength);
        
        map.full(currentX/map.GRID_ELEMENT_SIZE, currentY/map.GRID_ELEMENT_SIZE);
        
    }
    
    
    /**
     * Updates the map with a data load. CALL THIS ONE with data from TestRobot2 :)
     * @param robotPosition the x,y coordinates of the robot
     * @param robotHeading the angle of the robot from the x axis.
     * @param angles the angles for the laser set from the last function of TestRobot2. Considers that the angle straight ahead is 0.
     * @param echoes the distance for each laser range from RobotCommunications
     */
    public void updateWithLaserData(Position robotPosition, double robotHeading, double[] angles, double[] echoes){
        double correctedAngle;
        
        for (int i=0; i<angles.length; i++){    
            correctedAngle=angles[i]+robotHeading;
            updateLaserLine(robotPosition, correctedAngle, echoes[i]);
        }
    }
    
    
}
