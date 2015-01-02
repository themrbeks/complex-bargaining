package com.model.bargaining;

/**
 * Created by Stjepan on 25/12/14.
 */
public class SupplyAgent extends TradingAgent {

    public SupplyAgent(double price) {
        super(price, Util.supplyAgentConcessionStep);
    }

    public SupplyAgent(){
        super(Util.supplyAgentConcessionStep);
    }

    @Override
    protected double calculateExpectedUtility (double offer, TradingAgent firstOppositeAgent, double demandInit, int time) {
        double lambda = this.calculateLambda(firstOppositeAgent);
        double expectedUtility = this.calculateUtility(offer, demandInit) * Math.pow(lambda, time);
        return expectedUtility;
    }

    @Override
    public double calculateUtility(double offer, double demandInit){
        return (double)(offer-demandInit)/(this.initialBargainingPrice-demandInit);
    }

}
