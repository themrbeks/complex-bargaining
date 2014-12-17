import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stjepan on 17/12/14.
 */
public abstract class ClusteredChainNetwork extends UndirectedSparseGraph {

    public List<Integer> listOfNodeIDs;
    public double probabilityP;
    public double stepDelta;

    public abstract Node getLastNode (); //TODO: implementirati u Supply i Demand klasama
    public abstract Node getFirstNode (); //TODO: implementirati u Supply i Demand klasama

    public Node getNode (int nodeID) {
        ArrayList<Node> listOfNodesInTheNetwork = new ArrayList(this.getVertices());
        for(Node nodeItem : listOfNodesInTheNetwork) {
            if (nodeItem.hasID(nodeID)){
                return nodeItem;
            }
        }
        return null;
    }

    public void addNode (Node node) {
        if (Util.random.nextDouble()<this.probabilityP) { //with probability probabilityP, connect to the existing structure
            Node originalNodeToConnectTo = this.getRandomNode();
            double priceOfNewNode = this.calculatePriceOfNeighborhood(originalNodeToConnectTo);
            ArrayList<Node> nodesToConnectTo = new ArrayList(this.getNeighbors(originalNodeToConnectTo));

            node.price = priceOfNewNode; //set both prices to new price as average of neighborhood
            node.initialBargainingPrice = priceOfNewNode;

            this.listOfNodeIDs.add(node.ID); //add this node's id to the list of the network nodes
            this.addVertex(node);
            this.connectNodeToNeighborhood(node, nodesToConnectTo);
        }
        else {  //with probability 1 - probabilityP, connect to the tail of the network
            Node lastNode = this.getLastNode();
            double priceOfNewNode = lastNode.price + stepDelta;

            ArrayList<Node> nodesToConnectTo = new ArrayList(); //connect only to the last node
            nodesToConnectTo.add(lastNode);

            this.listOfNodeIDs.add(node.ID); //add this node's id to the list of the network nodes
            this.addVertex(node);
            this.connectNodeToNeighborhood(node, nodesToConnectTo);
        }
    }

    public int size() {
        return this.listOfNodeIDs.size();
    }

    public Node getRandomNode () {
        int randomNodeIndex = Util.random.nextInt(this.size());
        return this.getNode(this.listOfNodeIDs.get(randomNodeIndex));
    }

    public double calculatePriceOfNeighborhood (Node node) {
        ArrayList<Node> listOfNeighborsOfNode = new ArrayList(this.getNeighbors(node));
        double sumOfPrices = 0;
        for (Node nodeItem : listOfNeighborsOfNode) {
            sumOfPrices += nodeItem.price;
        }
        return sumOfPrices / (double) listOfNeighborsOfNode.size();
    }

    public void connectNodeToNeighborhood (Node node, ArrayList<Node> neighborhood) {
        for (Node nodeItem : neighborhood) {
            this.addEdge(new Edge(), node, nodeItem );
        }
    }

}

