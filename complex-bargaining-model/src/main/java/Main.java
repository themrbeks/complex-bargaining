import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.model.bargaining.Market;
import com.model.network.ClusteredChainNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Stjepan on 25/12/14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        int numberOfNodes = 500;
        double probabilityP = 0.5;
        double demandStepDelta = 0.99;
        double supplyStepDelta = 1.01;
        double initialNodePrice = 100;

        Market test = new Market(numberOfNodes,numberOfNodes,probabilityP,probabilityP,supplyStepDelta,demandStepDelta,initialNodePrice,initialNodePrice);
        test.supplyNetwork.exportToCSV("supply.csv");
        test.demandNetwork.exportToCSV("demand.csv");

        test.simulate(1000,500,0.999,1.001);

        Collection<MLArray> c = new ArrayList<>();
        c.add(test.supplyNetwork.exportNodePrices("supplyPrices"));
        c.add(test.demandNetwork.exportNodePrices("demandPrices"));

        new MatFileWriter("output.mat", c);
    }
}
