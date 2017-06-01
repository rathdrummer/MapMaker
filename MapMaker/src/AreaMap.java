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
    public float[][] grid;
    public final int numCols;
    public final int numRows;
    private ShowMap showMap;
    
    private boolean showGUI = true;

    public AreaMap(float[][] grid, int numCols, int numRows) {
        this.grid = grid;
        this.numCols = numCols;
        this.numRows = numRows;
        this.showMap = new ShowMap(numRows, numCols, showGUI);
    }
    
    public void updateMap(){
        showMap.updateMap(grid,numRows,numCols);
    }
    
}
