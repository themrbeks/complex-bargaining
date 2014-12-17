/**
 * Created by Stjepan on 17/12/14.
 */
public class Node {

    public double price;
    public double initialBargainingPrice;
    public int ID;

    public Node (double price) {
        this.price = price;
        this.initialBargainingPrice = price;
        this.ID = Util.nextNodeID();
    }

    public boolean hasID (int ID) {
        return this.ID == ID;
    }
}
