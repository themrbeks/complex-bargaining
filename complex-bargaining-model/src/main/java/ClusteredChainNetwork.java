import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Stjepan on 17/12/14.
 */
public class ClusteredChainNetwork extends UndirectedSparseGraph {

    public List<Integer> listOfNodeIDs;
    public double stepDelta;
    public double probabilityP;

    public ClusteredChainNetwork(int numberOfNodes, double probabilityP, double stepDelta, double initialNodePrice) {
        super();
        this.initializeNetwork(numberOfNodes, probabilityP, stepDelta, initialNodePrice);
    }

    public Node getLastNode() {
        if ((this.stepDelta - 1)>0){ //if the step is larger than 1, the network spreads towards growing prices
            return this.getMaxNode();
        }
        return this.getMinNode();
    }

    public void initializeNetwork(int numberOfNodes, double probabilityP, double stepDelta, double initialNodePrice){
        this.listOfNodeIDs = new ArrayList<>();
        this.stepDelta = stepDelta;
        this.probabilityP = probabilityP;

        this.addInitialNode(initialNodePrice);
        for (int i = 0; i < numberOfNodes-1; i++) {
            this.addNodeToNetwork(new Node(),this.probabilityP);
System.out.println(i);
        }
    }

    public Node getNode (int nodeID) {
        ArrayList<Node> listOfNodesInTheNetwork = new ArrayList(this.getVertices());
        for(Node nodeItem : listOfNodesInTheNetwork) {
            if (nodeItem.hasID(nodeID)){
                return nodeItem;
            }
        }
        return null;
    }

    /**
     * Returns a randomly chosen node from the network, with uniform probability.
     * @return
     */
    public Node getRandomNode () {
        int randomNodeIndex = Util.random.nextInt(this.size());
        return this.getNode(this.listOfNodeIDs.get(randomNodeIndex));
    }

    /**
     * Adds the specified node to the network, following the clustered chain network algorithm, with the probability parameter p.
     * @param node - the node to add to the network
     */
    public void addNodeToNetwork(Node node, double p) {
//System.out.println("\nDodajem cvor " + node.ID );
        ArrayList<Node> nodesToConnectTo;
        if (Util.random.nextDouble()<p) { //with probability probabilityP, connect to the existing structure
//System.out.println(" i to u postojecu strukturu.");
            addNodeToExistingStructure(node);
        }
        else {  //with probability 1 - probabilityP, connect to the tail of the network
            addNodeToTail(node);
//System.out.println(" i to na kraj.");
        }
    }

    public void exportToCSV (String fileName) throws IOException {

        String stringToWrite = "Source,Destination \n";

        FileWriter fStream = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(fStream);

        List<Edge> listOfEdges = new ArrayList<Edge>(this.getEdges());

        for (Edge edgeItem : listOfEdges) {
            List<Node> incidentNodes = new ArrayList<Node>(this.getIncidentVertices(edgeItem));
            stringToWrite = stringToWrite + "Node" + incidentNodes.get(0).ID + "," + "Node" + incidentNodes.get(1).ID + "\n";
        }

        out.write(stringToWrite);
        out.close();
        fStream.close();
    }

    private Node getMaxNode () {
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

    private Node getMinNode () {
        ArrayList<Node> listOfNodesInNetwork = new ArrayList<Node>(this.getVertices());
        double minPrice = 10e8;
        Node lastNode = null;
        for (Node nodeItem : listOfNodesInNetwork) {
            if (nodeItem.price < minPrice) {
                lastNode = nodeItem;
                minPrice = lastNode.price;
            }
        }
        return lastNode;
    }

    private void addInitialNode(double initialNodePrice) {
        this.addNode(new Node(initialNodePrice));
    }

    /**
     * Just add the given node to the list of IDs and to the structure. No connections, nothing fancy.
     * @param node
     */
    private void addNode (Node node) {
        this.listOfNodeIDs.add(node.ID); //add this node's id to the list of the network nodes
        this.addVertex(node);
    }

    private void addNodeToTail(Node node) {
        Node lastNode = this.getLastNode();

        double priceOfNewNode = lastNode.price * stepDelta; //the price of new node is relative to the last tail price
        node.price = priceOfNewNode; //set both prices
        node.initialBargainingPrice = priceOfNewNode;

        ArrayList<Node> nodesToConnectTo = new ArrayList(); //connect only to the last node
        nodesToConnectTo.add(lastNode);

        this.addNode(node);
        this.connectNodeToNeighborhood(node, nodesToConnectTo);
    }

    private void addNodeToExistingStructure(Node node) {
        Node originalNodeToConnectTo = this.getRandomNode(); //get first uniformly random node to connect to
//System.out.println("Spajam cvor " + node.ID + " s cvorom " + originalNodeToConnectTo.ID);
        double priceOfNewNode = this.calculatePriceOfNeighborhood(originalNodeToConnectTo); //new node price is the neighborhood avg
        node.price = priceOfNewNode; //set both prices to new price as average of neighborhood
        node.initialBargainingPrice = priceOfNewNode;

//Collection test = this.getNeighbors(originalNodeToConnectTo);
//System.out.println(test.size());
        ArrayList<Node> nodesToConnectTo = new ArrayList(this.getNeighbors(originalNodeToConnectTo)); //connect to all the neighbors of the first node
        nodesToConnectTo.add(originalNodeToConnectTo);
//System.out.println("Susjedi cvora " + originalNodeToConnectTo.ID + " su: ");
//for (Node neighbor : nodesToConnectTo){
//    System.out.print(neighbor.ID + " ");
//}
        this.addNode(node);
        this.connectNodeToNeighborhood(node, nodesToConnectTo);
    }

    public int size() {
        return this.listOfNodeIDs.size();
    }

    private double calculatePriceOfNeighborhood (Node node) {
        ArrayList<Node> listOfNeighborsOfNode = new ArrayList(this.getNeighbors(node));
        double sumOfPrices = 0;
        for (Node nodeItem : listOfNeighborsOfNode) {
            sumOfPrices += nodeItem.price;
        }
        return sumOfPrices / (double) listOfNeighborsOfNode.size();
    }

    private void connectNodeToNeighborhood (Node node, ArrayList<Node> neighborhood) {
        for (Node nodeItem : neighborhood) {
            this.addEdge(new Edge(), node, nodeItem );
//System.out.println("Dodajem vezu izmedu cvorova " + node.ID + " i " + nodeItem.ID);
        }

    }

}

