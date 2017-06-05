
import java.util.ArrayList;

/**
 * Search tree element.
 * @author Adam
 */
public class Node {
    public Position position;
    public Position destination;
    public double g; // g(n) - cost from origin to node
    public double h; // h(n) - cost from node to destination in straight line (heuristic)
    public ArrayList<Node> children;
    
    
    public Node(Position position){
        this.position = position;
    }
    
    public double findG(){
        // Maybe not necessary, can be done in findChildren
        return -1;
    }
    
    public double findH(){
        return h = position.getDistanceTo(destination);
    }
    
    public ArrayList<Node> findChildren(AreaMap map){
        // Find reachable children from this node and put them in children
        // Set child destination the same as parent destination
        
        for (Node child : children){
            child.g=this.g + child.position.getDistanceTo(this.position);
            child.findH();
        }
        return null;
    }
    
    public double getF(){
        return g+h;
    }
}
