import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.model.bargaining.Market;
import com.model.bargaining.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Stjepan on 25/12/14.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Util.initialize("parameters.txt");

        Market test = new Market(Util.numberOfSupplyNodes,Util.numberOfDemandNodes,Util.supplyNetworkProbabilityP,Util.demandNetworkProbabilityP,Util.supplyNetworkStepDelta,Util.demandNetworkStepDelta,Util.supplyNetworkInitialNodePrice,Util.demandNetworkInitialNodePrice);
        test.supplyNetwork.exportToCSV("supply.csv");
        test.demandNetwork.exportToCSV("demand.csv");

        test.simulate(Util.numberOfIterationsPerDay, Util.numberOfTradingDays,Util.supplyAgentConcessionStep,Util.demandAgentConcessionStep);

        Collection<MLArray> c = new ArrayList<>();
        c.add(test.supplyNetwork.exportNodePrices("supplyPrices"));
        c.add(test.demandNetwork.exportNodePrices("demandPrices"));
        c.add(test.exportIntradayPrices("intradayPrices"));

        new MatFileWriter("output.mat", c);
    }
}