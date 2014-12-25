/**
 * Created by Stjepan on 17/12/14.
 */
public class Node {

    static int IDcounter = 0;

    public double price;
    public double initialBargainingPrice;
    public int ID;

    public Node (double price) {
        this.price = price;
        this.initialBargainingPrice = price;
        this.ID = IDcounter++;
    }

    public Node () {
        this.ID = IDcounter++;
    }

    public boolean hasID (int ID) {
        return this.ID == ID;
    }
}
