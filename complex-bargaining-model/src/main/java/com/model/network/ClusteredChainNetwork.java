package com.model.network;

import com.jmatio.types.MLDouble;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Stjepan on 17/12/14.
 */
public class ClusteredChainNetwork extends UndirectedSparseGraph {

    protected static Random random = new Random();

    public List<Integer> listOfNodeIDs;
    public double stepDelta;
    public double probabilityP;

    public ClusteredChainNetwork(double probabilityP, double stepDelta) {
        super();
        this.listOfNodeIDs = new ArrayList<>();
        this.stepDelta = stepDelta;
        this.probabilityP = probabilityP;
    }

    protected Node getLastNode() {
        if ((this.stepDelta - 1)>0){ //if the step is larger than 1, the network spreads towards growing prices
            return this.getMaxNode();
        }
        return this.getMinNode();
    }

    protected Node getFirstNode() {
        if ((this.stepDelta - 1)>0){ //if the step is larger than 1, the network spreads towards growing prices
            return this.getMinNode();
        }
        return this.getMaxNode();
    }

    public void initializeNetwork(int numberOfNodes, double initialNodePrice){
        this.addInitialNode(initialNodePrice);
        for (int i = 0; i < numberOfNodes-1; i++) {
            this.addNewNodeToNetwork();
        }
    }

    protected void addNewNodeToNetwork(){
        this.addNodeToNetwork(new Node());
    }

    protected Node getNode (int nodeID) {
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
    protected Node getRandomNode () {
        int randomNodeIndex = random.nextInt(this.size());
        return this.getNode(this.listOfNodeIDs.get(randomNodeIndex));
    }

    private Node getRandomNodeWhichIsNotInTheFirstCluster() {
        ArrayList<Integer> nodeIDsNotInFirst = new ArrayList<Integer>();
        Node firstNode = this.getFirstNode();
        for (int i = 0; i <listOfNodeIDs.size(); i++) {
            if (!this.getNode(listOfNodeIDs.get(i)).connections.contains(firstNode))
                nodeIDsNotInFirst.add(listOfNodeIDs.get(i));
        }
        int randomNodeIndex = random.nextInt(nodeIDsNotInFirst.size());
        return this.getNode(nodeIDsNotInFirst.get(randomNodeIndex));
    }


    /**
     * Adds the specified node to the network, following the clustered chain network algorithm, with the probability parameter p.
     * @param node - the node to add to the network
     */
    public void addNodeToNetwork(Node node) {
        ArrayList<Node> nodesToConnectTo;
        if (random.nextDouble()<this.probabilityP) { //with probability probabilityP, connect to the existing structure
            addNodeToExistingStructure(node);
        }
        else {  //with probability 1 - probabilityP, connect to the tail of the network
            addNodeToTail(node);
        }
    }

    public void addNodeToNetworkAvoidFirstCluster(Node node) {
        ArrayList<Node> nodesToConnectTo;
        if (random.nextDouble()<this.probabilityP) { //with probability probabilityP, connect to the existing structure
            addNodeToExistingStructureAvoidFirstCluster(node);
        }
        else {  //with probability 1 - probabilityP, connect to the tail of the network
            addNodeToTail(node);
        }
    }

    public void removeNodeFromNetworkAndReconnectNeighbors(Node node){
        for (int i = 0; i < node.connections.size(); i++) {
            for (int j = i+1; j < node.connections.size(); j++) {
                if(!this.isNeighbor(node.connections.get(i),node.connections.get(j))) {
                    this.connectNodes(node.connections.get(i), node.connections.get(j));
                }
            }
        }

        for (Node neighbor : node.connections) {
            neighbor.connections.remove(node);
        }
        node.connections.clear();
        this.listOfNodeIDs.remove((Integer)node.ID);
        this.removeVertex(node);
    }

    public void removeNodeFromNetwork(Node node) {
        for (Node neighbor : node.connections) {
            neighbor.connections.remove(node);
        }
        node.connections.clear();
        this.listOfNodeIDs.remove((Integer)node.ID);
        this.removeVertex(node);
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

    public MLDouble exportNodePrices(String variableName) {
        double [] nodePrices = new double [this.size()];
        int i = 0;
        ArrayList<Node> listOfNodesInNetwork = new ArrayList<Node>(this.getVertices());
        for (Node nodeItem : listOfNodesInNetwork){
            nodePrices[i++] = nodeItem.price;
        }
        return new MLDouble(variableName,nodePrices,1);
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

    public void addInitialNode(double initialNodePrice) {
        this.addNode(new Node(initialNodePrice));
    }

    /**
     * Just add the given node to the list of IDs and to the structure. No connections, nothing fancy.
     * @param node
     */
    public void addNode (Node node) {
        this.listOfNodeIDs.add(node.ID); //add this node's id to the list of the network nodes
        this.addVertex(node);
    }

    private void addNodeToTail(Node node) {
        Node lastNode = this.getLastNode();

        double priceOfNewNode = lastNode.price * stepDelta; //the price of new node is relative to the last tail price
        node.price = priceOfNewNode; //set both prices

        ArrayList<Node> nodesToConnectTo = new ArrayList(); //connect only to the last node
        nodesToConnectTo.add(lastNode);

        this.addNode(node);
        this.connectNodeToNeighborhood(node, nodesToConnectTo);
    }

    public void addNodeToExistingStructure(Node node) {
        Node originalNodeToConnectTo = this.getRandomNode(); //get first uniformly random node to connect to

        double priceOfNewNode = this.calculatePriceOfNeighborhood(originalNodeToConnectTo); //new node price is the neighborhood avg
        node.price = priceOfNewNode; //set both prices to new price as average of neighborhood

        ArrayList<Node> nodesToConnectTo = new ArrayList(this.getNeighbors(originalNodeToConnectTo)); //connect to all the neighbors of the first node
        nodesToConnectTo.add(originalNodeToConnectTo);

        this.addNode(node);
        this.connectNodeToNeighborhood(node, nodesToConnectTo);
    }

    public void addNodeToExistingStructureAvoidFirstCluster(Node node) {
        Node originalNodeToConnectTo = this.getRandomNodeWhichIsNotInTheFirstCluster(); //get first uniformly random node to connect to

        double priceOfNewNode = this.calculatePriceOfNeighborhood(originalNodeToConnectTo); //new node price is the neighborhood avg
        node.price = priceOfNewNode; //set both prices to new price as average of neighborhood

        ArrayList<Node> nodesToConnectTo = new ArrayList(this.getNeighbors(originalNodeToConnectTo)); //connect to all the neighbors of the first node
        nodesToConnectTo.add(originalNodeToConnectTo);

        this.addNode(node);
        this.connectNodeToNeighborhood(node, nodesToConnectTo);
    }

    public int size() {
        return this.listOfNodeIDs.size();
    }

    private double calculatePriceOfNeighborhood (Node node) {
        ArrayList<Node> listOfNeighborsOfNode = new ArrayList(this.getNeighbors(node));
        listOfNeighborsOfNode.add(node);
        double sumOfPrices = 0;
        for (Node nodeItem : listOfNeighborsOfNode) {
            sumOfPrices += nodeItem.price;
        }
        return sumOfPrices / (double) listOfNeighborsOfNode.size();
    }

    private void connectNodeToNeighborhood (Node node, ArrayList<Node> neighborhood) {
        for (Node nodeItem : neighborhood) {
            this.connectNodes(node,nodeItem);
        }
    }

    private void connectNodes (Node node1, Node node2) {
        this.addEdge(new Edge(), node1, node2);
        node1.addToConnections(node2);
        node2.addToConnections(node1);
    }

}

