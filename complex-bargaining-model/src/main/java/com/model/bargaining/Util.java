package com.model.bargaining;

/**
 * Created by Stjepan on 26/12/14.
 */
public class Util {

    public static double[] beta;
    public static double betaC;
    public static int tradingDay;
    public static int iterationCounter;

    public static double getBeta() {
        return beta[tradingDay];
    }

    public static double getBetaC() {
        return betaC;
    }

}
