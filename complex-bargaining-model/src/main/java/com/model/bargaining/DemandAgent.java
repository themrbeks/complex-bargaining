package com.model.bargaining;

/**
 * Created by Stjepan on 25/12/14.
 */
public class DemandAgent extends TradingAgent {

    public DemandAgent(double price) {
        super(price,Util.demandAgentConcessionStep);
    }

    public DemandAgent(){
        super(Util.demandAgentConcessionStep);
    }

    @Override
    protected double calculateExpectedUtility (double offer, TradingAgent firstOppositeAgent, double supplyInit, int time) {
        double lambda = this.calculateLambda(firstOppositeAgent);
        double expectedUtility = this.calculateUtility(offer, supplyInit) * Math.pow(lambda, time);
        return expectedUtility;
    }

    @Override
    public double calculateUtility(double offer, double supplyInit){
        return (double)(supplyInit-offer)/(supplyInit-this.initialBargainingPrice);
    }

}
