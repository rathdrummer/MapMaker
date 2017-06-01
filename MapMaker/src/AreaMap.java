/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mrc16ake
 */
public class AreaMap {
    public int[][] grid;
    public final int numCols;
    public final int numRows;
    private final ShowMap showMap;
    private static final int MAX_VAL=15;
    
    private boolean showGUI = true;

    public AreaMap(int[][] grid, int numCols, int numRows) {
        this.grid = grid;
        this.numCols = numCols;
        this.numRows = numRows;
        this.showMap = new ShowMap(numRows, numCols, showGUI);
    }
    
    
    public void update(int robotRow, int robotCol){
        
        showMap.updateMap(grid,MAX_VAL,robotRow,robotCol);
    }

    
    
    public void empty(int xElement, int yElement) {
        grid[xElement][yElement]--;
        if (grid[xElement][yElement]<0) grid[xElement][yElement] = 0;

    }
    
    public void empty(double xElement, double yElement){
        this.empty((int)xElement, (int)yElement);
    }
    
    public void full(int xElement, int yElement) {
        grid[xElement][yElement]+=3;
        if (grid[xElement][yElement]>MAX_VAL) grid[xElement][yElement] = MAX_VAL;
    }
    
    public void full(double xElement, double yElement){
        this.full((int)xElement, (int)yElement);
    }
   
    
}
