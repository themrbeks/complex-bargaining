import java.util.ArrayList;

/**
 * Created by Stjepan on 17/12/14.
 */
public class SupplyNetwork extends ClusteredChainNetwork {

    public SupplyNetwork(int numberOfNodes, double probabilityP, double stepDelta, double initialNodePrice) {
        this.initializeNetwork(numberOfNodes, probabilityP, stepDelta, initialNodePrice);
    }

    public Node getLastNode () { //looking for the node with the highest price
        ArrayList<Node> listOfNodesInNetwork = new ArrayList<Node>(this.getVertices());
        double maxPrice = 0;
        Node lastNode = null;
        for (Node nodeItem : listOfNodesInNetwork) {
            if (nodeItem.price > maxPrice) {
                lastNode = nodeItem;
                maxPrice = lastNode.price;
            }
        }
        return lastNode;
    }
}
