
import java.util.LinkedList;

/**
 *
 * @author Adam
 */
public class Navigator {

    private AreaMap map;
    private Pilot pilot;
    private LinkedList<Position> path;
    
    public static final int OBSTACLE_THRESHOLD = 2; // Above = obstacle. Below = empty
    
    public Navigator(AreaMap map, Pilot pilot) {
        this.map=map;
        this.pilot=pilot;
    }
    
    public void run(){
        throw new UnsupportedOperationException("This functionality is not yet implemented.");
    }
    
    public Position findClosestBorder(){
        // Is it cool to loop over all the map elements? will it take too long?
        LinkedList<int[]> borderElements = new LinkedList<>();
        for (int i=1;i<map.numCols-1;i++){
            for (int j=1;j<map.numRows-1;j++){
                if (map.grid[i][j]>0 && map.grid[i][j]<OBSTACLE_THRESHOLD){
                    int[] adjacentElements = {
                        map.grid[i][j-1],
                        map.grid[i][j+1],
                        map.grid[i-1][j],
                        map.grid[i+1][j],
                        map.grid[i+1][j-1],
                        map.grid[i+1][j+1],
                        map.grid[i-1][j-1],
                        map.grid[i-1][j+1]
                    };
                    
                    for (int element : adjacentElements){
                        if (element == -1){
                            borderElements.add(new int[]{i,j});
                            break;
                        }
                    }
                }
            }
        }
        
        if (borderElements.isEmpty()){
            return new Position(-1,-1);
        }
        
        Position robotPosition = pilot.getRobotPosition();
        Position destination = map.getWorldCoordinates(borderElements.peek());
        while (!borderElements.isEmpty()){
            Position currentDest = map.getWorldCoordinates(borderElements.pop());
            if (robotPosition.getDistanceTo(currentDest)<robotPosition.getDistanceTo(destination)){
                destination = currentDest;
            }
        }
        return destination;
    }
    
    public void calculatePathTo(Position dest){
        throw new UnsupportedOperationException("Functionality not implemented yet"); // Here is where we calculate the path, and store the list of positions
    }
    
}
