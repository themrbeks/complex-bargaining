import java.io.IOException;

/**
 * Created by Stjepan on 25/12/14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        int numberOfNodes = 50;
        double probabilityP = 0.6;
        double stepDelta = 1.001;
        double initialNodePrice = 100;

        Util.initialize();

        SupplyNetwork test = new SupplyNetwork(numberOfNodes,probabilityP,stepDelta,initialNodePrice);
        test.exportToCSV("test.csv");
    }
}
