package com.model.bargaining;

import com.model.network.ClusteredChainNetwork;

import java.util.ArrayList;

/**
 * Created by Stjepan on 25/12/14.
 */
public class DemandNetwork extends ClusteredChainNetwork {

    public DemandNetwork(double probabilityP, double stepDelta){
        super(probabilityP, stepDelta);
    }

    @Override
    public void addInitialNode(double initialNodePrice) {
        DemandAgent initialNode = new DemandAgent(initialNodePrice);
        this.addNode(initialNode);
    }

    @Override
    protected void addNewNodeToNetwork(){
        this.addNodeToNetwork(new DemandAgent());
    }

    @Override
    protected DemandAgent getNode (int nodeID) {
        ArrayList<DemandAgent> listOfNodesInTheNetwork = new ArrayList(this.getVertices());
        for(DemandAgent nodeItem : listOfNodesInTheNetwork) {
            if (nodeItem.hasID(nodeID)){
                return nodeItem;
            }
        }
        return null;
    }

    @Override
    public DemandAgent getRandomNode() {
        int randomNodeIndex = random.nextInt(this.size());
        return this.getNode(this.listOfNodeIDs.get(randomNodeIndex));
    }

    @Override
    public DemandAgent getFirstNode() {
        ArrayList<DemandAgent> listOfNodesInNetwork = new ArrayList<DemandAgent>(this.getVertices());
        double maxPrice = 0;
        DemandAgent lastNode = null;
        for (DemandAgent nodeItem : listOfNodesInNetwork) {
            if (nodeItem.price > maxPrice) {
                lastNode = nodeItem;
                maxPrice = lastNode.price;
            }
        }
        return lastNode;
    }

    public boolean reconnectLastAgentIntoNetwork () {
        double temp = Util.random.nextDouble();
        if (temp < Util.probabilityOfReconnect) {
            this.removeNodeFromNetworkAndReconnectNeighbors(this.getLastNode());
            this.addNodeToExistingStructure(new DemandAgent());
            return true;
        }
        return false;
    }

}
