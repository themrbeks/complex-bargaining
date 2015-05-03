package com.model.bargaining;

import com.model.network.ClusteredChainNetwork;
import com.model.network.Node;

import java.util.ArrayList;

/**
 * Created by Stjepan on 25/12/14.
 */
public class SupplyNetwork extends ClusteredChainNetwork {

    public SupplyNetwork(double probabilityP, double stepDelta){
        super(probabilityP, stepDelta);
    }

    @Override
    public void addInitialNode(double initialNodePrice) {
        SupplyAgent initialNode = new SupplyAgent(initialNodePrice);
        this.addNode(initialNode);
    }

    @Override
    protected void addNewNodeToNetwork(){
        this.addNodeToNetwork(new SupplyAgent());
    }

    protected void addNewNodeToNetworkAvoidFirstCluster(){
        this.addNodeToNetworkAvoidFirstCluster(new SupplyAgent());
    }

    @Override
    protected SupplyAgent getNode (int nodeID) {
        ArrayList<SupplyAgent> listOfNodesInTheNetwork = new ArrayList(this.getVertices());
        for(SupplyAgent nodeItem : listOfNodesInTheNetwork) {
            if (nodeItem.hasID(nodeID)){
                return nodeItem;
            }
        }
        return null;
    }

    @Override
    public SupplyAgent getRandomNode() {
        int randomNodeIndex = random.nextInt(this.size());
        return this.getNode(this.listOfNodeIDs.get(randomNodeIndex));
    }

    @Override
    public SupplyAgent getFirstNode() {
        ArrayList<SupplyAgent> listOfNodesInNetwork = new ArrayList<SupplyAgent>(this.getVertices());
        double minPrice = 10e8;
        SupplyAgent lastNode = null;
        for (SupplyAgent nodeItem : listOfNodesInNetwork) {
            if (nodeItem.price < minPrice) {
                lastNode = nodeItem;
                minPrice = lastNode.price;
            }
        }
        return lastNode;
    }

    public boolean reconnectLastAgentIntoNetwork () {
        double temp = Util.random.nextDouble();
        if (temp < Util.probabilityOfReconnect) {
            this.removeNodeFromNetworkAndReconnectNeighbors(this.getLastNode());
            this.addNodeToExistingStructure(new SupplyAgent());
            return true;
        }
        return false;
    }
}
