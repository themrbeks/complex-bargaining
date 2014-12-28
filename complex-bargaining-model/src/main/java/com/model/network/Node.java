package com.model.network;

import java.util.ArrayList;

/**
 * Created by Stjepan on 17/12/14.
 */
public class Node {

    static int IDcounter = 0;

    public double price;
    public int ID;
    public ArrayList<Node> connections;

    public Node (double price) {
        this.price = price;
        this.ID = IDcounter++;
        this.connections = new ArrayList<>();
    }

    public Node () {
        this.ID = IDcounter++;
        this.connections = new ArrayList<>();
    }

    public void addToConnections(Node node) {
        this.connections.add(node);
    }

    public boolean hasID (int ID) {
        return this.ID == ID;
    }
}
