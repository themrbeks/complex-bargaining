package com.model.bargaining;

import com.model.network.Node;

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

        Util.iterationCounter = 0;
        for (int i = 0; i < numberOfDays; i++) {
            for (int j = 0; j < this.numberOfIterationsPerDay; j++) {
                this.intraDayPrices[Util.iterationCounter++] = this.moveSupply();
                this.intraDayPrices[Util.iterationCounter++] = this.moveDemand();
            }
        }
    }

    private double moveSupply() {
        SupplyAgent activeSupplyAgent = (SupplyAgent) this.supplyNetwork.getRandomNode();
        DemandAgent firstDemandAgent = (DemandAgent) this.demandNetwork.getFirstNode();

        if (activeSupplyAgent.move(firstDemandAgent)) {
            this.trade(activeSupplyAgent, firstDemandAgent);
            return this.demandNetwork.getFirstNode().price;
        }
        return Double.NaN;
    }

    private double moveDemand() {
        DemandAgent activeDemandAgent = (DemandAgent) this.demandNetwork.getRandomNode();
        SupplyAgent firstSupplyAgent = (SupplyAgent) this.supplyNetwork.getFirstNode();

        if (activeDemandAgent.move(firstSupplyAgent)) {
            this.trade(firstSupplyAgent, activeDemandAgent);
            return this.supplyNetwork.getFirstNode().price;
        }
        return Double.NaN;
    }

    private void trade (SupplyAgent tradingSupplyAgent, DemandAgent tradingDemandAgent) {
        this.demandNetwork.removeNodeFromNetwork(tradingDemandAgent);
        this.supplyNetwork.removeNodeFromNetwork(tradingSupplyAgent);
        this.demandNetwork.addNodeToNetwork(new Node());
        this.supplyNetwork.addNodeToNetwork(new Node());
    }




}
