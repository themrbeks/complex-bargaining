package com.model.bargaining;

import com.model.network.Node;

/**
 * Created by Stjepan on 26/12/14.
 */
public abstract class TradingAgent extends Node {

    public double startPrice;
    public double concessionStep;

    public TradingAgent(){
        super();
    }

    public TradingAgent (double price) {
        super(price);
        this.startPrice = price;
    }

    /**
     * The agent makes a move, with respect to the offer by the first opposite agent.
     * If it accepts the offer, returns true, otherwise returns false and corrects price.
     * @param referentAgent
     * @return
     */
    public boolean move (TradingAgent referentAgent) {
        if (this.acceptsOffer(referentAgent.price)){
            return true;
        }
        else {
            this.price *= this.concessionStep;
            return false;
        }
    }

    /**
     * Returns lambda value for the current state of the market.
     * @param firstOppositeAgent
     * @return
     */
    public double getLambda (TradingAgent firstOppositeAgent){

        int sizeOfOppositeCluster = firstOppositeAgent.connections.size() + 1;
        int sizeOfOwnCluster = this.connections.size() + 1;

        double innerPart = (double) sizeOfOppositeCluster / (double) (sizeOfOppositeCluster + sizeOfOwnCluster);

        if (this instanceof SupplyAgent) {
            return Math.pow(innerPart, Util.getBeta() / Util.getBetaC());
        } else {
            return Math.pow(innerPart, Util.getBetaC() / Util.getBeta());
        }
    }

    private boolean acceptsOffer (double offer) {
        return true;

    }
}
