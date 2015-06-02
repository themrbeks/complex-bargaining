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
    int numberOfIterationsPerDay;

    public double[] intraDayPrices;
    public double[] averageDayPrices;
    public double[] dailyQuantities;
    public double[] dailyVolumes;

    public double[] dailySupplyClusteringCoefficient;
    public double[] dailyDemandClusteringCoefficient;


    double[] firstSupplyClusterSize;
    double[] firstDemandClusterSize;
    double[] averageSupplyLambda;
    double[] averageDemandLambda;

    double supplyReferentPrice;
    double demandReferentPrice;

    public Market(int numberOfSupplyNodes, int numberOfDemandNodes, double supplyP, double demandP, double supplyPSim, double demandPSim, double supplyNetworkStepDelta, double demandNetworkStepDelta, double initialSupplyNodePrice, double initialDemandNodePrice) {
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

        demandNetwork.probabilityP = demandPSim;
        supplyNetwork.probabilityP = supplyPSim;

    }

    public void simulateWithoutIntradayPrices(){

        this.averageDayPrices = new double[Util.numberOfTradingDays];
        this.dailyQuantities = new double[Util.numberOfTradingDays];
        this.dailyVolumes = new double[Util.numberOfTradingDays];

        this.firstSupplyClusterSize = new double[Util.numberOfTradingDays];
        this.firstDemandClusterSize = new double[Util.numberOfTradingDays];
        this.averageSupplyLambda = new double[Util.numberOfTradingDays];
        this.averageDemandLambda = new double[Util.numberOfTradingDays];

        dailySupplyClusteringCoefficient = new double[Util.numberOfTradingDays];
        dailyDemandClusteringCoefficient = new double[Util.numberOfTradingDays];

        double lastTradedPriceOrNaN;

        //referent prices are just the initial prices of the first nodes in the network before the star of the next bargaining phase (after each trade occurs)
        this.setAllInitialAgentPrices();

        Util.tradingDayCounter = 0;
        Util.lastPrice = Util.realPrice[0];
        Util.lastlastPrice = Util.lastPrice;

System.out.print("\nDiscarding first " + Util.numberOfIterationsToDiscard + " iterations: \n[");

        this.discardFirstIterations();

System.out.print("] Done.");

System.out.print("\nSimulating " + Util.numberOfTradingDays + " trading days: \n[");

int segment = Util.numberOfTradingDays/10-1;

        while (Util.tradingDayCounter < Util.numberOfTradingDays) {

if (Util.tradingDayCounter%segment == 0) {
    System.out.print("-");
}
            while (Util.iterationCounter < 2*Util.numberOfIterationsPerDay) {

                lastTradedPriceOrNaN = this.moveSupply(demandReferentPrice);
                if (!Double.isNaN(lastTradedPriceOrNaN)){
                    this.dailyQuantities[Util.tradingDayCounter] += 1;
                    this.dailyVolumes[Util.tradingDayCounter] += lastTradedPriceOrNaN;

                    Util.lastlastPrice = Util.lastPrice;
                    Util.lastPrice = lastTradedPriceOrNaN;

                    demandNetwork.probabilityP = Util.getDemandNetworkProbabilityP();
                    supplyNetwork.probabilityP = Util.getSupplyNetworkProbabilityP();
                }
                Util.iterationCounter++;

                lastTradedPriceOrNaN = this.moveDemand(supplyReferentPrice);
                if (!Double.isNaN(lastTradedPriceOrNaN)){
                    this.dailyQuantities[Util.tradingDayCounter] += 1;
                    this.dailyVolumes[Util.tradingDayCounter] += lastTradedPriceOrNaN;

                    Util.lastlastPrice = Util.lastPrice;
                    Util.lastPrice = lastTradedPriceOrNaN;

                    demandNetwork.probabilityP = Util.getDemandNetworkProbabilityP();
                    supplyNetwork.probabilityP = Util.getSupplyNetworkProbabilityP();
                }
                Util.iterationCounter++;
            }

            this.firstSupplyClusterSize[Util.tradingDayCounter] = supplyNetwork.getFirstNode().connections.size();
            this.firstDemandClusterSize[Util.tradingDayCounter] = demandNetwork.getFirstNode().connections.size();
            this.dailyDemandClusteringCoefficient[Util.tradingDayCounter] = demandNetwork.calculateCC();
            this.dailySupplyClusteringCoefficient[Util.tradingDayCounter] = supplyNetwork.calculateCC();

            double sumOfLambda = 0;
            DemandAgent firstDemandAgent = demandNetwork.getFirstNode();
            ArrayList<SupplyAgent> supplyNodeList = new ArrayList(supplyNetwork.getVertices());
            for (int i = 0; i < supplyNodeList.size(); i++) {
                sumOfLambda += supplyNodeList.get(i).calculateLambda(firstDemandAgent);
            }
            this.averageSupplyLambda[Util.tradingDayCounter] = sumOfLambda/(double)supplyNodeList.size();

            sumOfLambda = 0;
            SupplyAgent firstSupplyAgent = supplyNetwork.getFirstNode();
            ArrayList<DemandAgent> demandNodeList = new ArrayList(demandNetwork.getVertices());
            for (int i = 0; i < demandNodeList.size(); i++) {
                sumOfLambda += demandNodeList.get(i).calculateLambda(firstSupplyAgent);
            }
            this.averageDemandLambda[Util.tradingDayCounter] = sumOfLambda/(double)demandNodeList.size();


            double dayPrice = this.dailyVolumes[Util.tradingDayCounter]/this.dailyQuantities[Util.tradingDayCounter];
            if (Double.isNaN(dayPrice)){
                if (Util.tradingDayCounter == 0) {
                    this.averageDayPrices[Util.tradingDayCounter] = (Util.demandNetworkInitialNodePrice + Util.supplyNetworkInitialNodePrice)/(double)2;
                }
                else {
                    this.averageDayPrices[Util.tradingDayCounter] = this.averageDayPrices[Util.tradingDayCounter-1];
                }
            }
            else {
                this.averageDayPrices[Util.tradingDayCounter] = dayPrice;
            }
            Util.tradingDayCounter++;
            Util.iterationCounter = 0;
        }
        System.out.print("] Done.");
    }


    public void simulate(int numberOfIterationsPerDay, int numberOfDays, double supplyConcessionStep, double demandConcessionStep){
        this.intraDayPrices = new double[numberOfDays*numberOfIterationsPerDay*2];
        this.averageDayPrices = new double[numberOfDays];
        this.dailyQuantities = new double[numberOfDays];
        this.dailyVolumes = new double[numberOfDays];


        //referent prices are just the initial prices of the first nodes in the network before the star of the next bargaining phase (after each trade occurs)
        this.setAllInitialAgentPrices();

        Util.iterationCounter = 0;
        Util.tradingDayCounter = 0;
        Util.lastPrice = Util.realPrice[0];
        Util.lastlastPrice = 1;

System.out.print("\nDiscarding first " + Util.numberOfIterationsToDiscard + " iterations: \n[");
        this.discardFirstIterations();
System.out.print("] Done.");

System.out.print("\nSimulating " + Util.numberOfTradingDays + " trading days: \n[");

int segment = Util.numberOfTradingDays/10-1;

        for (int i = 0; i < numberOfDays; i++) {

            this.dailyDemandClusteringCoefficient[Util.tradingDayCounter] = demandNetwork.calculateCC();
            this.dailySupplyClusteringCoefficient[Util.tradingDayCounter] = supplyNetwork.calculateCC();

if (i%segment == 0) {
    System.out.print("-");
}
            for (int j = 0; j < this.numberOfIterationsPerDay; j++) {

                this.intraDayPrices[Util.iterationCounter++] = this.moveSupply(demandReferentPrice);
                if (!Double.isNaN(this.intraDayPrices[Util.iterationCounter-1])){
                    this.dailyQuantities[Util.tradingDayCounter] += 1;
                    this.dailyVolumes[Util.tradingDayCounter] += this.intraDayPrices[Util.iterationCounter-1];

                    Util.lastlastPrice = Util.lastPrice;
                    Util.lastPrice = this.intraDayPrices[Util.iterationCounter-1];

                    demandNetwork.probabilityP = Util.getDemandNetworkProbabilityP();
                    supplyNetwork.probabilityP = Util.getSupplyNetworkProbabilityP();

//                    demandNetwork.probabilityP = Math.pow (Util.pConstant,Math.pow((Util.lastPrice/Util.realPrice[Util.tradingDayCounter]),0.2));
//                    supplyNetwork.probabilityP = Math.pow (Util.pConstant,Math.pow((Util.realPrice[Util.tradingDayCounter])/Util.lastPrice,0.2));
                }
                this.intraDayPrices[Util.iterationCounter++] = this.moveDemand(supplyReferentPrice);
                if (!Double.isNaN(this.intraDayPrices[Util.iterationCounter-1])){
                    this.dailyQuantities[Util.tradingDayCounter] += 1;
                    this.dailyVolumes[Util.tradingDayCounter] += this.intraDayPrices[Util.iterationCounter-1];

                    Util.lastlastPrice = Util.lastPrice;
                    Util.lastPrice = this.intraDayPrices[Util.iterationCounter-1];

                    demandNetwork.probabilityP = Util.getDemandNetworkProbabilityP();
                    supplyNetwork.probabilityP = Util.getSupplyNetworkProbabilityP();

//                    demandNetwork.probabilityP = Math.pow (Util.pConstant,Math.pow((Util.lastPrice/Util.realPrice[Util.tradingDayCounter]),0.2));
//                    supplyNetwork.probabilityP = Math.pow (Util.pConstant,Math.pow((Util.realPrice[Util.tradingDayCounter])/Util.lastPrice,0.2));


                }
            }
            double dayPrice = (double)this.dailyVolumes[Util.tradingDayCounter]/(double)this.dailyQuantities[Util.tradingDayCounter];
            if (Double.isNaN(dayPrice)){
                if (Util.tradingDayCounter == 0) {
                    this.averageDayPrices[Util.tradingDayCounter] = 100;
                }
                else {
                    this.averageDayPrices[Util.tradingDayCounter] = this.averageDayPrices[Util.tradingDayCounter-1];
                }
            }
            else {
                this.averageDayPrices[Util.tradingDayCounter] = dayPrice;
            }


            Util.tradingDayCounter++;
        }
        System.out.print("] Done.");
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

    public MLDouble exportAverageSupplyLambda(String variableName) {
        return new MLDouble(variableName,this.averageSupplyLambda,1);
    }

    public MLDouble exportAverageDemandLambda(String variableName) {
        return new MLDouble(variableName,this.averageDemandLambda,1);
    }

    public MLDouble exportFirstSupplyClusterSize(String variableName) {
        return new MLDouble(variableName,this.firstSupplyClusterSize,1);
    }

    public MLDouble exportFirstDemandClusterSize(String variableName) {
        return new MLDouble(variableName,this.firstDemandClusterSize,1);
    }

    public MLDouble exportExponent(String variableName) {
        return new MLDouble(variableName,Util.exponent,1);
    }

    public MLDouble exportSupplyPrices(String variableName) {
        double[] prices = new double[supplyNetwork.size()];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = supplyNetwork.getNode(supplyNetwork.listOfNodeIDs.get(i)).price;
        }
        return new MLDouble(variableName,prices,1);
    }

    public MLDouble exportDemandPrices(String variableName) {
        double[] prices = new double[demandNetwork.size()];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = demandNetwork.getNode(demandNetwork.listOfNodeIDs.get(i)).price;
        }
        return new MLDouble(variableName,prices,1);
    }

    public MLDouble exportDailySupplyClusteringCoefficients(String variableName) {
        return new MLDouble(variableName,this.dailySupplyClusteringCoefficient,1);
    }

    public MLDouble exportDailyDemandClusteringCoefficients(String variableName) {
        return new MLDouble(variableName,this.dailyDemandClusteringCoefficient,1);
    }


    private double moveSupply(double demandReferentPrice) {

        SupplyAgent activeSupplyAgent = (SupplyAgent) this.supplyNetwork.getRandomNode();
        DemandAgent firstDemandAgent = (DemandAgent) this.demandNetwork.getFirstNode();

        if (activeSupplyAgent.move(firstDemandAgent,demandReferentPrice)) {
            double tradePrice = firstDemandAgent.price;
            this.trade(activeSupplyAgent, firstDemandAgent);
//System.out.println(Util.tradingDayCounter + ": " + tradePrice);
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
//System.out.println(Util.tradingDayCounter + ": " + tradePrice);
            return tradePrice;
        }
        return Double.NaN;
    }

    private void trade (SupplyAgent tradingSupplyAgent, DemandAgent tradingDemandAgent) {
        this.demandNetwork.removeNodeFromNetworkAndReconnectNeighbors(tradingDemandAgent);
        this.supplyNetwork.removeNodeFromNetworkAndReconnectNeighbors(tradingSupplyAgent);
        this.supplyReferentPrice = this.supplyNetwork.getFirstNode().price;
        this.demandReferentPrice = this.demandNetwork.getFirstNode().price;
        this.supplyNetwork.addNewNodeToNetworkAvoidFirstCluster();
        this.demandNetwork.addNewNodeToNetworkAvoidFirstCluster();
        if (supplyNetwork.reconnectLastAgentIntoNetwork())
            Util.kolikoPuta++;
        if (demandNetwork.reconnectLastAgentIntoNetwork())
            Util.kolikoPuta++;
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

        this.supplyReferentPrice = this.supplyNetwork.getFirstNode().price;
        this.demandReferentPrice = this.demandNetwork.getFirstNode().price;
    }

    private void discardFirstIterations () {
int segment = Util.numberOfIterationsToDiscard/10-1;
        for (int i = 0; i < Util.numberOfIterationsToDiscard; i++) {
if (i%segment == 0) {
    System.out.print("-");
}
            this.moveSupply(demandReferentPrice);
            this.moveDemand(supplyReferentPrice);
        }
    }


}
