package com.model.bargaining;

import com.jmatio.types.MLDouble;

import java.io.*;
import java.util.Random;

/**
 * Created by Stjepan on 26/12/14.
 */
public class Util {

    public static Random random;

    public static int numberOfSupplyNodes;
    public static int numberOfDemandNodes;
    public static double supplyNetworkProbabilityP;
    public static double demandNetworkProbabilityP;
    public static double supplyNetworkStepDelta;
    public static double demandNetworkStepDelta;
    public static double supplyNetworkInitialNodePrice;
    public static double demandNetworkInitialNodePrice;
    public static double supplyAgentConcessionStep;
    public static double demandAgentConcessionStep;
    public static int numberOfTradingDays;
    public static int numberOfIterationsPerDay;
    public static String realPriceFileName;
    public static double[] realPrice;
    public static int numberOfIterationsToDiscard;
    public static double betaExponent;
    public static double betaCs;
    public static double betaCd;
    public static double pConstant;


    public static double lastPrice;
    public static double lastlastPrice;
    public static int tradingDayCounter;
    public static int iterationCounter;
    public static double e = 1e-5; //calculation error margin

    public static void initialize(String fileName) throws Exception {

        random = new Random();

        FileReader fStream = new FileReader(fileName);
        BufferedReader in = new BufferedReader(fStream);

        String line = in.readLine();
        while (line != null){
            if (line.contains(":")) {
                String[] values = line.split(":");
                switch (values[0]) {
                    case "numberOfSupplyNodes":
                        numberOfSupplyNodes = Integer.parseInt(values[1].trim());
                        break;
                    case "numberOfDemandNodes":
                        numberOfDemandNodes = Integer.parseInt(values[1].trim());
                        break;
                    case "supplyNetworkProbabilityP":
                        supplyNetworkProbabilityP = Double.parseDouble(values[1].trim());
                        break;
                    case "demandNetworkProbabilityP":
                        demandNetworkProbabilityP = Double.parseDouble(values[1].trim());
                        break;
                    case "supplyNetworkStepDelta":
                        supplyNetworkStepDelta = 1 + Double.parseDouble(values[1].trim());
                        break;
                    case "demandNetworkStepDelta":
                        demandNetworkStepDelta = 1 + Double.parseDouble(values[1].trim());
                        break;
                    case "supplyNetworkInitialNodePrice":
                        supplyNetworkInitialNodePrice = Double.parseDouble(values[1].trim());
                        break;
                    case "demandNetworkInitialNodePrice":
                        demandNetworkInitialNodePrice = Double.parseDouble(values[1].trim());
                        break;
                    case "supplyAgentConcessionStep":
                        supplyAgentConcessionStep = 1 + Double.parseDouble(values[1].trim());
                        break;
                    case "demandAgentConcessionStep":
                        demandAgentConcessionStep = 1 + Double.parseDouble(values[1].trim());
                        break;
                    case "betaCs":
                        betaCs = Double.parseDouble(values[1].trim());
                        break;
                    case "betaCd":
                        betaCd = Double.parseDouble(values[1].trim());
                        break;
                    case "betaExponent":
                        betaExponent = Double.parseDouble(values[1].trim());
                        break;
                    case "numberOfTradingDays":
                        numberOfTradingDays = Integer.parseInt(values[1].trim());
                        break;
                    case "numberOfIterationsPerDay":
                        numberOfIterationsPerDay = Integer.parseInt(values[1].trim());
                        break;
                    case "realPriceFileName":
                        realPriceFileName = values[1].trim();
                        break;
                    case "numberOfIterationsToDiscard":
                        numberOfIterationsToDiscard = Integer.parseInt(values[1].trim());
                        break;
                    case "pConstant":
                        pConstant = Double.parseDouble(values[1].trim());
                        break;
                }
            }
            line = in.readLine();
        }
        in.close();
        fStream.close();

        realPrice = new double[numberOfTradingDays+1];
        BufferedReader br = new BufferedReader(new FileReader(realPriceFileName));
        for (int i = 1; i <= numberOfTradingDays; i++) {
            line = br.readLine();
            if (line == null) {
                throw new Exception("Size of realPrice does not fit the number of trading days.");
            }
            realPrice[i] = Double.parseDouble(line.trim());
        }
        br.close();
        realPrice[0] = realPrice[1];
    }

    public static double getBeta() {
        return 0.3*lastPrice/(realPrice[tradingDayCounter]); //TODO stavi one stvari
//        return Math.pow((1/lastlastPrice)*lastPrice/(realPrice[tradingDayCounter]),betaExponent);
    }

    public static double getSupplyNetworkProbabilityP() {
        double pExponent = Math.pow((Util.realPrice[Util.tradingDayCounter])/Util.lastPrice,1);
        return Math.pow(Util.pConstant,pExponent);
    }

    public static double getDemandNetworkProbabilityP() {
        double pExponent = Math.pow((Util.lastPrice/Util.realPrice[Util.tradingDayCounter]),1);
        return Math.pow(Util.pConstant, pExponent);
    }

    public static MLDouble exportRealPrice(String fileName) {
        double[] realRealPrice = new double[realPrice.length-1];
        for (int i = 1; i < realPrice.length; i++) {
            realRealPrice[i-1] = realPrice[i];
        }
        return new MLDouble(fileName,realRealPrice,1);
    }

    public static MLDouble exportBeta (String fileName, double[] averageDayPrices) {
        double[] beta = new double[realPrice.length-1];
        for (int i = 1; i < realPrice.length; i++) {
            beta[i-1] = averageDayPrices[i-1]/realPrice[i];
        }
        return new MLDouble(fileName,beta,1);
    }

}
