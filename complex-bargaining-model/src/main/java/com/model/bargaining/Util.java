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
    public static double[] realPrice;
    public static double[] exponent;
    public static int numberOfIterationsToDiscard;
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
                    case "initialSupplyNetworkProbabilityP":
                        supplyNetworkProbabilityP = Double.parseDouble(values[1].trim());
                        break;
                    case "initialDemandNetworkProbabilityP":
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
                    case "pConstant":
                        pConstant = Double.parseDouble(values[1].trim());
                        break;
                    case "realPriceFileName":
                        realPrice = parseCSVFile(values[1].trim(),numberOfTradingDays);
                        break;
                    case "exponentFileName":
                        exponent = parseCSVFile(values[1].trim(),numberOfTradingDays);
                        break;
                }
            }
            line = in.readLine();
        }
        in.close();
        fStream.close();
    }

    public static double getBeta() {
        if (tradingDayCounter<30)
            return 1;
        else
            return 1./(25*(0.7*exponent[tradingDayCounter]+0.3*(Util.lastPrice/Util.realPrice[Util.tradingDayCounter])/(Util.lastlastPrice/Util.realPrice[Util.tradingDayCounter-30]))-24);//*lastlastPrice/lastPrice;
    }

    public static double getSupplyNetworkProbabilityP() {
        double ratio = Math.pow(Util.lastPrice/Util.realPrice[Util.tradingDayCounter],1);
        return 1 - Math.exp(-Util.pConstant*ratio);
    }

    public static double getDemandNetworkProbabilityP() {
        double ratio = Math.pow(Util.realPrice[Util.tradingDayCounter]/Util.lastPrice,1);
        return 1 - Math.exp(-Util.pConstant*ratio);
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


}
