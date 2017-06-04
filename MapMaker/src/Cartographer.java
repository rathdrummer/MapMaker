/**
 * Updates the AreaMap with the data acquired from the lasers.
 * 
 */
public class Cartographer {

    private final AreaMap map;
    private final float mapWidth; //x
    private final float mapLength; //y
    private Thread task;
    private LaserPropertiesResponse lpr;
    private RobotCommunication rCom;
    
    public static final double TICK = 0.1;
        

    public Cartographer(AreaMap map, float mapWidth, float mapLength, RobotCommunication rCom) {
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapLength = mapLength;
        this.lpr = new LaserPropertiesResponse();
        this.rCom = rCom;
        try{
            this.rCom.getResponse(lpr);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void run(){
        task = new Thread() {
            public void run() {
		try{
                    while (true) {
                        LocalizationResponse lr = new LocalizationResponse();
                        LaserEchoesResponse ler = new LaserEchoesResponse();
                    
                        rCom.getResponse(lr);
                        rCom.getResponse(ler);
                        
                        updateWithLaserData(lr,ler,lpr);
                        
                        Thread.sleep((int)(TICK*1000));
                    }
                }catch (Exception e){
                    System.out.print("Cartographer exception - "+e.getLocalizedMessage());                    
                }
            }
        };
        task.start();
            
        
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
     * Updates the map with a data load. CALL THIS ONE with data from TestRobot2.
     * Ensure the responses are valid (have been used with getResponse).
     * Assumes echoes are the distance from the robot position to the detected obstacle.
     * @param lr
     * @param ler
     * @param lpr 
     */
    public void updateWithLaserData(LocalizationResponse lr, LaserEchoesResponse ler, LaserPropertiesResponse lpr){
        // First, let's get the data we need from the responses.
        double robotHeading = lr.getHeadingAngle();
        Position robotPosition = new Position(lr.getPosition());
        
        double[] laserEchoes = ler.getEchoes();
        double[] laserAngles = getAngles(lpr);
        
        double correctedAngle;
        for (int i=0; i<laserAngles.length; i++){    
            correctedAngle=laserAngles[i]+robotHeading;
            updateLaserLine(robotPosition, correctedAngle, laserEchoes[i]);
        }
        
    }   

    public double[] getAngles(LaserPropertiesResponse lpr) {
        // create a table of the right size
        int beamCount = (int) ((lpr.getEndAngle()-lpr.getStartAngle())/lpr.getAngleIncrement())+1;
        double[] angles = new double[beamCount];
        
        // Based on example code, it is wiser to use 1 degree in radians as an 
        // increment to avoid rounding errors.
        double increment = 1 * (Math.PI/180);
        angles[0] = lpr.getStartAngle();
        
        for (int i = 1; i<beamCount; i++){
            angles[i] = angles[i-1]+increment;
        }
        
        return angles;
    }
}
