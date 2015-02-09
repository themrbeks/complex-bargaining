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
            this.price = this.calculateNextPrice(firstOppositeAgent);
            return false;
        }
    }

    private double calculateNextPrice (TradingAgent firstOppositeAgent){
        double deltaScalingFactor = 1-this.calculateLambda(firstOppositeAgent);
        return this.price * (1 + this.concessionStep*deltaScalingFactor);
    }

    /**
     * Returns lambda value for the current state of the market.
     * @param firstOppositeAgent
     * @return
     */
    public double calculateLambda(TradingAgent firstOppositeAgent){

        int sizeOfOppositeCluster = firstOppositeAgent.connections.size() + 1;
        int sizeOfOwnCluster = this.connections.size() + 1;
        double innerPart = (double) sizeOfOppositeCluster / (double) (sizeOfOppositeCluster + sizeOfOwnCluster);

        if (this instanceof SupplyAgent) {
            return Math.pow(innerPart,Util.getBeta());
        }
        else {
            return Math.pow(innerPart,(double)1/Util.getBeta());
        }

    }

    private boolean acceptsOffer (TradingAgent firstOppositeAgent, double referentPrice) {
        double expectedUtilityOfOfferedPrice = this.calculateExpectedUtility(firstOppositeAgent.price, firstOppositeAgent, referentPrice, 0);
        double expectedUtilityOfOwnPrice = this.calculateExpectedUtility(this.calculateNextPrice(firstOppositeAgent), firstOppositeAgent, referentPrice, 1);
        if ((expectedUtilityOfOfferedPrice - expectedUtilityOfOwnPrice > Util.e)) {
            return true;
        }
        return false;
    }
}
