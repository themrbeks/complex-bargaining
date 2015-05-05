package com.model;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;
import com.model.bargaining.Market;
import com.model.bargaining.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Stjepan on 25/12/14.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Util.initialize("parameters.txt");

        Market test = new Market(Util.numberOfSupplyNodes,Util.numberOfDemandNodes,Util.initialSupplyNetworkProbabilityP,Util.initialDemandNetworkProbabilityP, Util.supplyNetworkProbabilityP,Util.demandNetworkProbabilityP,Util.supplyNetworkStepDelta,Util.demandNetworkStepDelta,Util.supplyNetworkInitialNodePrice,Util.demandNetworkInitialNodePrice);

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
        c.add(test.exportExponent("exponent"));
        c.add(test.exportDemandPrices("demandPrices"));
        c.add(test.exportSupplyPrices("supplyPrices"));


        c.add(exportParametersToStruct());


        new MatFileWriter("output.mat", c);
System.out.print("\nDone.\n");
        System.out.println(Util.kolikoPuta);
    }

    public static MLStructure exportParametersToStruct () throws IOException {
        MLStructure mlStruct = new MLStructure("parameters", new int[] {1,1} );

        FileReader fStream = new FileReader("parameters.txt");
        BufferedReader in = new BufferedReader(fStream);

        String line = in.readLine();
        while (line != null){
            if (!line.split(":")[0].equals("realPriceFileName")) {
                double[] vals = new double[1];
                vals[0] = Double.parseDouble(line.split(":")[1]);
                MLDouble MLvals = new MLDouble(null,vals,1);
                mlStruct.setField(line.split(":")[0], MLvals);
            }
            line = in.readLine();
        }
        fStream.close();
        in.close();
        return mlStruct;
    }
}
