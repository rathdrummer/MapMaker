/**
 * Main method and mission planner - decides where next to move.
 * @author Adam
 */
public class MissionPlanner {
    public static final String HOST = "http://127.0.0.1";
    public static final int PORT = 50000;
    public static final int COLS = 50;
    public static final int ROWS = 50;


    
    public static void main(String[] args){
        System.out.println("Starting RobotCommunication...");
        RobotCommunication rCom = new RobotCommunication(HOST, PORT);
        System.out.println("Starting Map...");
        AreaMap map = new AreaMap(COLS, ROWS);
        System.out.println("Starting Cartographer...");
        Cartographer cartographer = new Cartographer(map,-1,-1,rCom); 
        System.out.println("Starting Pilot...");
        Pilot pilot = new Pilot(rCom);
        System.out.println("Starting Navigator...");
        Navigator navigator = new Navigator(map,pilot);
    }
}
