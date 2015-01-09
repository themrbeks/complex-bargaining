package com.model.bargaining;

import com.jmatio.types.MLDouble;

import java.util.ArrayList;

/**
 * Created by Stjepan on 27/12/14.
 */
public class Market {

    public DemandNetwork demandNetwork;
    public SupplyNetwork supplyNetwork;

    int numberOfSupplyNodes;
    int numberOfDemandNodes;
    double supplyP;
    double demandP;
    double supplyNetworkStepDelta;
    double demandNetworkStepDelta;
    double initialSupplyNodePrice;
    double initialDemandNodePrice;
    double supplyConcessionStep;
    double demandConcessionStep;
    int numberOfIterationsPerDay;
    int numberOfDays;

    public double[] intraDayPrices;
    public double[] averageDayPrices;
    public double[] dailyQuantities;
    public double[] dailyVolumes;
    public double[] supplyNetworkSize;
    public double[] demandNetworkSize;

    double supplyReferentPrice;
    double demandReferentPrice;

    public Market(int numberOfSupplyNodes, int numberOfDemandNodes, double supplyP, double demandP, double supplyNetworkStepDelta, double demandNetworkStepDelta, double initialSupplyNodePrice, double initialDemandNodePrice) {
        this.numberOfSupplyNodes = numberOfSupplyNodes;
        this.numberOfDemandNodes = numberOfDemandNodes;
        this.supplyP = supplyP;
        this.demandP = demandP;
        this.supplyNetworkStepDelta = supplyNetworkStepDelta;
        this.demandNetworkStepDelta = demandNetworkStepDelta;
        this.initialSupplyNodePrice = initialSupplyNodePrice;
        this.initialDemandNodePrice = initialDemandNodePrice;

        this.demandNetwork = new DemandNetwork(demandP,demandNetworkStepDelta);
        this.supplyNetwork = new SupplyNetwork(supplyP,supplyNetworkStepDelta);

        demandNetwork.initializeNetwork(numberOfDemandNodes,initialDemandNodePrice);
        supplyNetwork.initializeNetwork(numberOfSupplyNodes,initialSupplyNodePrice);

    }

    public void simulate(int numberOfIterationsPerDay, int numberOfDays, double supplyConcessionStep, double demandConcessionStep){
        this.numberOfIterationsPerDay = numberOfIterationsPerDay;
        this.numberOfDays = numberOfDays;
        this.supplyConcessionStep = supplyConcessionStep;
        this.demandConcessionStep = demandConcessionStep;

        this.intraDayPrices = new double[numberOfDays*numberOfIterationsPerDay*2];
        this.supplyNetworkSize = new double[numberOfDays*numberOfIterationsPerDay*2];
        this.demandNetworkSize = new double[numberOfDays*numberOfIterationsPerDay*2];
        this.averageDayPrices = new double[numberOfDays];
        this.dailyQuantities = new double[numberOfDays];
        this.dailyVolumes = new double[numberOfDays];


        //referent prices are just the initial prices of the first nodes in the network before the star of the next bargaining phase (after each trade occurs)
        this.supplyReferentPrice = this.supplyNetwork.getFirstNode().price;
        this.demandReferentPrice = this.demandNetwork.getFirstNode().price;
        this.setAllInitialAgentPrices();

        Util.iterationCounter = 0;
        Util.tradingDayCounter = 0;

        this.discardFirstIterations();

        for (int i = 0; i < numberOfDays; i++) {
            for (int j = 0; j < this.numberOfIterationsPerDay; j++) {

                this.supplyNetworkSize[Util.iterationCounter] = this.supplyNetwork.size();
                this.intraDayPrices[Util.iterationCounter++] = this.moveSupply(demandReferentPrice);
                if (!Double.isNaN(this.intraDayPrices[Util.iterationCounter-1])){
                    this.dailyQuantities[Util.tradingDayCounter] += 1;
                    this.dailyVolumes[Util.tradingDayCounter] += this.intraDayPrices[Util.iterationCounter-1];
                }
                this.demandNetworkSize[Util.iterationCounter] = this.demandNetwork.size();
                this.intraDayPrices[Util.iterationCounter++] = this.moveDemand(supplyReferentPrice);
                if (!Double.isNaN(this.intraDayPrices[Util.iterationCounter-1])){
                    this.dailyQuantities[Util.tradingDayCounter] += 1;
                    this.dailyVolumes[Util.tradingDayCounter] += this.intraDayPrices[Util.iterationCounter-1];
                }
            }
            this.averageDayPrices[Util.tradingDayCounter] = (double)this.dailyVolumes[Util.tradingDayCounter]/(double)this.dailyQuantities[Util.tradingDayCounter];
            Util.tradingDayCounter++;
        }
    }

    public MLDouble exportIntradayPrices(String variableName) {
        return new MLDouble(variableName,this.intraDayPrices,1);
    }

    public MLDouble exportDailyVolumes(String variableName) {
        return new MLDouble(variableName,this.dailyVolumes,1);
    }

    public MLDouble exportDailyQuantities(String variableName) {
        return new MLDouble(variableName,this.dailyQuantities,1);
    }

    public MLDouble exportAverageDailyPrices(String variableName) {
        return new MLDouble(variableName,this.averageDayPrices,1);
    }

    public MLDouble exportSupplyNetworkSize(String variableName) {
        return new MLDouble(variableName,this.supplyNetworkSize,1);
    }

    public MLDouble exportDemandNetworkSize(String variableName) {
        return new MLDouble(variableName,this.demandNetworkSize,1);
    }

    private double moveSupply(double demandReferentPrice) {
        SupplyAgent activeSupplyAgent = (SupplyAgent) this.supplyNetwork.getRandomNode();
        DemandAgent firstDemandAgent = (DemandAgent) this.demandNetwork.getFirstNode();

        if (activeSupplyAgent.move(firstDemandAgent,demandReferentPrice)) {
            double tradePrice = firstDemandAgent.price;
            this.trade(activeSupplyAgent, firstDemandAgent);
System.out.println(Util.tradingDayCounter + ": " + tradePrice);
            return tradePrice;
        }
        return Double.NaN;
    }

    private double moveDemand(double supplyReferentPrice) {
        DemandAgent activeDemandAgent = (DemandAgent) this.demandNetwork.getRandomNode();
        SupplyAgent firstSupplyAgent = (SupplyAgent) this.supplyNetwork.getFirstNode();

        if (activeDemandAgent.move(firstSupplyAgent,supplyReferentPrice)) {
            double tradePrice = firstSupplyAgent.price;
            this.trade(firstSupplyAgent, activeDemandAgent);
System.out.println(Util.tradingDayCounter + ": " + tradePrice);
            return tradePrice;
        }
        return Double.NaN;
    }

    private void trade (SupplyAgent tradingSupplyAgent, DemandAgent tradingDemandAgent) {
System.out.println(this.supplyNetwork.size());
System.out.println(this.demandNetwork.size());
        this.demandNetwork.removeNodeFromNetwork(tradingDemandAgent);
        this.supplyNetwork.removeNodeFromNetwork(tradingSupplyAgent);
        this.supplyReferentPrice = this.supplyNetwork.getFirstNode().price;
        this.demandReferentPrice = this.demandNetwork.getFirstNode().price;
        this.supplyNetwork.addNewNodeToNetwork();
        this.demandNetwork.addNewNodeToNetwork();
        this.setAllInitialAgentPrices();
    }

    private void setAllInitialAgentPrices() {
        ArrayList<DemandAgent> listOfDemandNodes = new ArrayList<DemandAgent>(this.demandNetwork.getVertices());
        ArrayList<SupplyAgent> listOfSupplyNodes = new ArrayList<SupplyAgent>(this.supplyNetwork.getVertices());

        for (DemandAgent agent : listOfDemandNodes) {
            agent.initialBargainingPrice = agent.price;
        }

        for (SupplyAgent agent : listOfSupplyNodes) {
            agent.initialBargainingPrice = agent.price;
        }
    }


    private void discardFirstIterations () {
        for (int i = 0; i < Util.numberOfIterationsToDiscard; i++) {
            this.moveSupply(demandReferentPrice);
            this.moveDemand(supplyReferentPrice);
        }
    }

}
