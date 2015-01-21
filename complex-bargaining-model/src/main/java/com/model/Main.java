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

        test.simulate(Util.numberOfIterationsPerDay, Util.numberOfTradingDays,Util.supplyAgentConcessionStep,Util.demandAgentConcessionStep);

        Collection<MLArray> c = new ArrayList<>();

        c.add(new MLDouble("realPrice",Util.realPrice, 1));
System.out.print("\nExporting data to mat file...");
//        c.add(test.exportIntradayPrices("intradayPrices"));
        c.add(test.exportAverageDailyPrices("averageDailyPrices"));
        c.add(test.exportDailyQuantities("dailyTradingQuantities"));
        c.add(test.exportDailyVolumes("dailyTradingVolumes"));
//        c.add(test.exportSupplyNetworkSize("supplySize"));
//        c.add(test.exportDemandNetworkSize("demandSize"));
        c.add(Util.exportRealPrice("externalRealPrice"));
        c.add(Util.exportBeta("beta",test.averageDayPrices));

        new MatFileWriter("output.mat", c);
System.out.print("\nDone.\n");
    }
}
