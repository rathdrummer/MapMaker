/**
 * Updates the Map with the data acquired from the lasers
 * 
 */
public class Cartographer {

    private final AreaMap map;
    private final float mapWidth; //x
    private final float mapLength; //y
    private static final float GRID_ELEMENT_SIZE = 0.1f; // To change. is this 10cm?
    

    public Cartographer(AreaMap map, float mapWidth, float mapLength) {
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapLength = mapLength;
    }
    
    public void updateLaserLine(){
        
    }
    
    
    
}
