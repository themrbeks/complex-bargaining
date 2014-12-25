import java.io.IOException;

/**
 * Created by Stjepan on 25/12/14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        int numberOfNodes = 1000;
        double probabilityP = 0.8;
        double stepDelta = 0.999;
        double initialNodePrice = 100;

        Util.initialize();

        ClusteredChainNetwork test = new ClusteredChainNetwork(numberOfNodes,probabilityP,stepDelta,initialNodePrice);
        test.exportToCSV("test.csv");
    }
}
