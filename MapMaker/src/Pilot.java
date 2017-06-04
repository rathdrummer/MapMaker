
import java.util.LinkedList;

/**
 * The reactive part of the robot. The navigator provides an unobstructed point, the Pilot moves to it.
 * This is mostly done. Just needs testing and adjustment.
 * @author Adam
 */
public class Pilot {
    private RobotCommunication rCom;
    private Position destination;
    private Thread task;
    private LaserPropertiesResponse lpr;
    
    public static final double POINT_REACHED_THRESHOLD = 0.5; // How close to a point the robot has to be for it to have "arrived". 
    public static final double OBSTACLE_DANGER_DISTANCE = 1; // How close an obstacle has to be for it to be a danger.
    public static final double OBSTACLE_AVOIDANCE_GAIN = 0; // How intensely the robot turns from obstacles. Keep between 0 and 1. Only change from 0 once everything else works.    
    public static final double TOP_LINEAR_SPEED = 1;
    public static final double TOP_ANGULAR_SPEED = 3;
    public static final double TICK = 0.01;


    
    
    public Pilot(RobotCommunication rCom) {
        this.rCom = rCom;
        this.destination = getRobotPosition();
        this.lpr=new LaserPropertiesResponse();
        try{
            rCom.getResponse(lpr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the current position of the robot using rCom. TODO
     * @return the robot position
     */
    public Position getRobotPosition() { 
        LocalizationResponse lr = new LocalizationResponse();
        try{
            rCom.getResponse(lr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Position(lr.getPosition());
    }
    
    /**
     * Returns the heading of the robot in radians. TODO
     * @return the robot heading
     */
    private double getRobotHeading() {
        LocalizationResponse lr = new LocalizationResponse();
        try{
            rCom.getResponse(lr);
        }catch (Exception e){
            e.printStackTrace();
        }    
        return lr.getHeadingAngle();
    }
    
    /**
     * Sets the speed of the differential drive. TODO
     * @param linearSpeed
     * @param angularSpeed 
     */
    private void setSpeed(double linearSpeed, double angularSpeed) {
        DifferentialDriveRequest dr = new DifferentialDriveRequest();
        dr.setAngularSpeed(angularSpeed);
        dr.setLinearSpeed(linearSpeed);
        
        try{
            rCom.putRequest(dr);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Finds how much we need to adjust the angular speed to avoid obstacles
     * @return the amount to add to the angular speed
     */
    public double getObstacleAvoidanceSpeed(){
        // Only look at detected objects within 1 meter.
	LaserEchoesResponse ler = new LaserEchoesResponse();
        
        try{
            rCom.getResponse(ler);
        } catch (Exception e){
            e.printStackTrace();
        }
        double[] laserEchoes = ler.getEchoes();
        double[] laserAngles = getAngles(lpr);
        
        double adjustmentSpeed;
        
        // Find if we should turn left or right
        int leftDanger=0;
        double leftAdjustmentSpeed = 0;
        int rightDanger=0;
        double rightAdjustmentSpeed = 0;
                
        
        for (int i = 0; i<laserEchoes.length; i++){
            // Only look within 1 meter, in a range of 90 degrees ahead of the robot.
            if (laserEchoes[i]<OBSTACLE_DANGER_DISTANCE && laserAngles[i]<Math.PI/8 && laserAngles[i]>-Math.PI/8){
                // See if the danger is mainly on the left or the right
                if (laserAngles[i]>=0){
                    leftDanger++;
                    leftAdjustmentSpeed+=1d/(1+laserAngles[i]);
                } else if (laserAngles[i]<0) {
                    rightDanger++;
                    rightAdjustmentSpeed+=1d/(1+(-laserAngles[i]));
                }
            }
        }
        
        if (leftDanger<rightDanger){
            adjustmentSpeed = rightAdjustmentSpeed;
        } else {
            adjustmentSpeed = leftAdjustmentSpeed;
        }
        
        return adjustmentSpeed*OBSTACLE_AVOIDANCE_GAIN;
    }
    
    /**
     * Starts the robot moving towards the destination position.
     */
    public void go(){
        task = new Thread() {
            public void run() {
		while (true) {
                    Position robotPosition = getRobotPosition();
                    double robotDistance = robotPosition.getDistanceTo(destination);
                    
                    while (robotDistance > POINT_REACHED_THRESHOLD){
                        
                        double robotHeading = getRobotHeading();
                        double bearingToDestination = robotPosition.getBearingTo(destination);
                        double headingToDestination = bearingToDestination - robotHeading;

                        // Scaling down of angular speed based on heading difference
                        // Makes for less oscillations around the path
                        // Method by Adam Kavanagh Coyne and Shanmuganthan Sabarisathasivam
                        double angularSpeed = TOP_ANGULAR_SPEED*(1-Math.exp(-2*headingToDestination*headingToDestination));
                        angularSpeed += getObstacleAvoidanceSpeed();
                        setSpeed(TOP_LINEAR_SPEED,angularSpeed);
                        
                        // Speed is set, sleep
                        try{
                            Thread.sleep((long) (TICK*1000));
                        }catch(InterruptedException e){
                            System.out.println("Exception: Sleep interrupted in Pilot.go()");
                        }
                        
                        // Refresh data
                        robotPosition = getRobotPosition();
                        robotDistance = robotPosition.getDistanceTo(destination);
                        
                    }
		}
            }

            

            
	};
	task.start();
    }
    
    public void setDestination(Position p){
        this.destination = p;
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
