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
    public static double initialSupplyNetworkProbabilityP;
    public static double initialDemandNetworkProbabilityP;
    public static double supplyNetworkStepDelta;
    public static double demandNetworkStepDelta;
    public static double supplyNetworkInitialNodePrice;
    public static double demandNetworkInitialNodePrice;
    public static double supplyAgentConcessionStep;
    public static double demandAgentConcessionStep;
    public static int numberOfTradingDays;
    public static int numberOfIterationsPerDay;
    public static double[] realPrice;
    public static double[] exponent;
    public static int numberOfIterationsToDiscard;
    public static double pConstant;
    public static double a;
    public static double probabilityOfReconnect;
    public static double epsilon;

    public static double kolikoPuta;


    public static double lastPrice;
    public static double lastlastPrice;
    public static int tradingDayCounter;
    public static int iterationCounter;
    public static double e = 1e-5; //calculation error margin

    public static void initialize(String fileName) throws Exception {

        kolikoPuta = 0;

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
                    case "initialSupplyNetworkProbabilityP":
                        initialSupplyNetworkProbabilityP = Double.parseDouble(values[1].trim());
                        break;
                    case "initialDemandNetworkProbabilityP":
                        initialDemandNetworkProbabilityP = Double.parseDouble(values[1].trim());
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
//                    case "supplyNetworkInitialNodePrice":
//                        supplyNetworkInitialNodePrice = Double.parseDouble(values[1].trim());
//                        break;
//                    case "demandNetworkInitialNodePrice":
//                        demandNetworkInitialNodePrice = Double.parseDouble(values[1].trim());
//                        break;
                    case "supplyAgentConcessionStep":
                        supplyAgentConcessionStep = Double.parseDouble(values[1].trim());
                        break;
                    case "demandAgentConcessionStep":
                        demandAgentConcessionStep = Double.parseDouble(values[1].trim());
                        break;
                    case "numberOfTradingDays":
                        numberOfTradingDays = Integer.parseInt(values[1].trim());
                        break;
                    case "numberOfIterationsPerDay":
                        numberOfIterationsPerDay = Integer.parseInt(values[1].trim());
                        break;
                    case "numberOfIterationsToDiscard":
                        numberOfIterationsToDiscard = Integer.parseInt(values[1].trim());
                        break;
                    case "realPriceFileName":
                        realPrice = parseCSVFile(values[1].trim(),numberOfTradingDays);
                        supplyNetworkInitialNodePrice = realPrice[0]*1.01;
                        demandNetworkInitialNodePrice = realPrice[0]*0.99;
                        break;
                    case "a":
                        a = Double.parseDouble(values[1].trim());
                        exponent = calculateExponent(realPrice);
                        break;
                    case "probabilityOfReconnect":
                        probabilityOfReconnect = Double.parseDouble(values[1].trim());
                        break;
                    case "epsilon":
                        epsilon = Double.parseDouble(values[1].trim());
                        break;
                }
            }
            line = in.readLine();
        }
        in.close();
        fStream.close();
    }

    public static double getBeta() {
        return (double) exponent[tradingDayCounter];//*lastlastPrice/lastPrice;
    }

    public static double getSupplyNetworkProbabilityP() {
        double ratio = Math.pow(Util.lastPrice/Util.realPrice[Util.tradingDayCounter],1);
        return 1 - Math.exp(-Util.supplyNetworkProbabilityP*ratio);
    }

    public static double getDemandNetworkProbabilityP() {
        double ratio = Math.pow(Util.realPrice[Util.tradingDayCounter]/Util.lastPrice,1);
        return 1 - Math.exp(-Util.demandNetworkProbabilityP*ratio);
    }

    public static MLDouble exportRealPrice(String fileName) {
        return new MLDouble(fileName,realPrice,1);
    }


    private static double[] parseCSVFile(String fileName, int maxSize) throws Exception { /* Assumes enter-delimited file!!!*/
        String line;
        double[] parsedDouble = new double[maxSize];
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        for (int i = 0; i < maxSize; i++) {
            line = br.readLine();
            if (line == null) {
                throw new Exception("Size of realPrice does not fit the number of trading days.");
            }
            parsedDouble[i] = Double.parseDouble(line.trim());
        }
        br.close();
        return parsedDouble;
    }

    private static double[] calculateExponent (double[] intrinsic) {
        double[] exponent = new double[intrinsic.length];
        double thisMonth, lastMonth;
        thisMonth = intrinsic[0];
        lastMonth = intrinsic[0];
        for (int i = 0; i < intrinsic.length; i++) {
            if (intrinsic[i]!=thisMonth) {
                lastMonth = thisMonth;
                thisMonth = intrinsic[i];
            }
            exponent[i] = (Util.a * (thisMonth-lastMonth)/lastMonth);
//            exponent[i] = 1/(thisMonth/lastMonth*25-24);
        }
        return exponent;
    }


}
