/**
 * Stores the map for use by any parts of the robot architecture that need it
 * @author mrc16ake
 */
public class AreaMap {
    private static final int MAX_VAL=15;
    public static final float GRID_ELEMENT_SIZE = 0.1f;
    
    public int[][] grid;
    public final int numCols;
    public final int numRows;
    private final ShowMap showMap;
    
    
    private boolean showGUI = true;

    public AreaMap(int[][] grid, int numCols, int numRows) {
        this.grid = grid;
        this.numCols = numCols;
        this.numRows = numRows;
        this.showMap = new ShowMap(numRows, numCols, showGUI);
    }
    
    /**
     * Updates the ShowMap image
     * @param robotRow the row where the robot is
     * @param robotCol the column where the robot is
     */
    public void update(int robotRow, int robotCol){
        showMap.updateMap(grid,MAX_VAL,robotRow,robotCol);
    }

    
    /**
     * Decrements an element of the grid. To be used for empty zones
     * @param xElement the x grid coordinate (column)
     * @param yElement the y grid coordinate (row)
     */
    public void empty(int xElement, int yElement) {
        grid[xElement][yElement]--;
        if (grid[xElement][yElement]<0) grid[xElement][yElement] = 0;

    }
    
    public void empty(double xElement, double yElement){
        this.empty((int) Math.floor(xElement), (int) Math.floor(yElement));
    }
    
    /**
     * Increments an element of the grid. To be used for full zones.
     * @param xElement the x grid coordinate (column)
     * @param yElement the y grid coordinate (row)
     */
    public void full(int xElement, int yElement) {
        grid[xElement][yElement]+=3;
        if (grid[xElement][yElement]>MAX_VAL) grid[xElement][yElement] = MAX_VAL;
    }
    
    public void full(double xElement, double yElement){
        this.full((int)Math.floor(xElement), (int)Math.floor(yElement));
    }
    
    /**
     * Translates a position's coordinates into grid coordinates
     * @param p the position to send (x,y)
     * @return the (column,row) grid coordinates as elements 0 and 1
     */
    public int[] getGridIndex(Position p){
        int x = (int) Math.floor(p.getX()/GRID_ELEMENT_SIZE);
        int y = (int) Math.floor(p.getY()/GRID_ELEMENT_SIZE);
        return new int[]{x,y};
    }
   
    
}
