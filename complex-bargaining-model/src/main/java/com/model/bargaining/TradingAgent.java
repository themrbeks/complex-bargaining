package com.model.bargaining;

import com.model.network.Node;

/**
 * Created by Stjepan on 26/12/14.
 */
public abstract class TradingAgent extends Node {

    public double concessionStep;
    public double initialBargainingPrice;

    protected abstract double calculateExpectedUtility (double offer, TradingAgent firstOppositeAgent, double referentPrice, int time);

    protected abstract double calculateUtility (double offer, double referentPrice);

    public TradingAgent(double concessionStep){
        super();
        this.concessionStep = concessionStep;
    }

    public TradingAgent (double price, double concessionStep) {
        super(price);
        initialBargainingPrice = price;
        this.concessionStep = concessionStep;
    }

    /**
     * The agent makes a move, with respect to the offer by the first opposite agent.
     * If it accepts the offer, returns true, otherwise returns false and corrects price.
     * @param firstOppositeAgent
     * @return
     */
    public boolean move (TradingAgent firstOppositeAgent, double referentPrice) {
        if (this.acceptsOffer(firstOppositeAgent, referentPrice)){
            return true;
        }
        else {
            double deltaScalingFactor;
            if (this instanceof DemandAgent) {
                deltaScalingFactor = 1-Math.pow(this.calculateLambda(firstOppositeAgent),1);//Util.betac/Util.getBeta());
            }
            else {
                deltaScalingFactor = 1-Math.pow(this.calculateLambda(firstOppositeAgent),1);//Util.getBeta()/Util.betac);
            }
            this.price *= 1+((this.concessionStep-1) * deltaScalingFactor);
            return false;
        }
    }

    /**
     * Returns lambda value for the current state of the market.
     * @param firstOppositeAgent
     * @return
     */
    public double calculateLambda(TradingAgent firstOppositeAgent){

        int sizeOfOppositeCluster = firstOppositeAgent.connections.size() + 1;
        int sizeOfOwnCluster = this.connections.size() + 1;

//      double sumOfOwnProximities = 0;
//		for (int i = 0; i < referentNode.connections.size(); i++) {
//			sumOfOwnProximities += (double) 1 / (double)this.calculateDistance(referentNode.connections.get(i).activationPrice);
//		}
//		double sumOfNeighborProximities = sumOfOwnProximities;
//		for (int i = 1; i < this.connections.size(); i++) {
//			sumOfNeighborProximities += (double) 1 / (double)referentNode.calculateDistance(this.connections.get(i).activationPrice);
//		}
//		double innerPart = sumOfOwnProximities / sumOfNeighborProximities;

        double innerPart = (double) sizeOfOppositeCluster / (double) (sizeOfOppositeCluster + sizeOfOwnCluster);

        if (this instanceof SupplyAgent) {
            return Math.pow(innerPart, Util.getBeta() / Util.getBetaC());
        } else {
            return Math.pow(innerPart, Util.getBetaC() / Util.getBeta());
        }
    }

    private boolean acceptsOffer (TradingAgent firstOppositeAgent, double referentPrice) {
        double expectedUtilityOfOfferedPrice = this.calculateExpectedUtility(firstOppositeAgent.price, firstOppositeAgent, referentPrice, 0);
        double expectedUtilityOfOwnPrice = this.calculateExpectedUtility(this.price, firstOppositeAgent, referentPrice, 1);
        if ((expectedUtilityOfOfferedPrice - expectedUtilityOfOwnPrice > Util.e)) {
            return true;
        }
        return false;
    }
}
