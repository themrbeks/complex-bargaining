import java.util.Random;

/**
 * Created by Stjepan on 17/12/14.
 */
public class Util {
    public static Random random;
    public static int edgeIDCounter, nodeIDCounter;

    public void initializeAll () {
        random = new Random();
        edgeIDCounter = 0;
        nodeIDCounter = 0;
    }

    public static int nextEdgeID() {
        return edgeIDCounter++;
    }

    public static int nextNodeID() {
        return nodeIDCounter++;
    }
}
