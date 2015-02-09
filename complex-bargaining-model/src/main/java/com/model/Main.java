package com.model;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.model.bargaining.Market;
import com.model.bargaining.Util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Stjepan on 25/12/14.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Util.initialize("parameters.txt");

        Market test = new Market(Util.numberOfSupplyNodes,Util.numberOfDemandNodes,Util.supplyNetworkProbabilityP,Util.demandNetworkProbabilityP,Util.supplyNetworkStepDelta,Util.demandNetworkStepDelta,Util.supplyNetworkInitialNodePrice,Util.demandNetworkInitialNodePrice);

//        test.simulate(Util.numberOfIterationsPerDay, Util.numberOfTradingDays,Util.supplyAgentConcessionStep,Util.demandAgentConcessionStep);

        test.simulateWithoutIntradayPrices();

        Collection<MLArray> c = new ArrayList<>();


System.out.print("\nExporting data to mat file...");

        c.add(test.exportAverageDailyPrices("averageDailyPrices"));
        c.add(test.exportDailyQuantities("dailyTradingQuantities"));
        c.add(test.exportDailyVolumes("dailyTradingVolumes"));
        c.add(Util.exportRealPrice("externalRealPrice"));
        c.add(test.exportAverageDemandLambda("averageDemandLambda"));
        c.add(test.exportAverageSupplyLambda("averageSupplyLambda"));
        c.add(test.exportFirstSupplyClusterSize("firstSupplyClusterSize"));
        c.add(test.exportFirstDemandClusterSize("firstDemandClusterSize"));

        new MatFileWriter("output.mat", c);
System.out.print("\nDone.\n");
    }
}
