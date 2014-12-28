package com.model.bargaining;

import com.model.network.ClusteredChainNetwork;

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



}
