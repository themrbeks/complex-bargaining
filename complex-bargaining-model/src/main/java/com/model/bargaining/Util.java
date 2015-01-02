package com.model.bargaining;

import java.io.*;

/**
 * Created by Stjepan on 26/12/14.
 */
public class Util {

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
    public static double betaC;
    public static int numberOfTradingDays;
    public static int numberOfIterationsPerDay;
    public static String betaFileName;
    public static double[] beta;


    public static int tradingDayCounter;
    public static int iterationCounter;
    public static double e = 1e-5; //calculation error margin

    public static void initialize(String fileName) throws Exception {

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
                        supplyNetworkStepDelta = Double.parseDouble(values[1].trim());
                        break;
                    case "demandNetworkStepDelta":
                        demandNetworkStepDelta = Double.parseDouble(values[1].trim());
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
                    case "betaC":
                        betaC = Double.parseDouble(values[1].trim());
                        break;
                    case "numberOfTradingDays":
                        numberOfTradingDays = Integer.parseInt(values[1].trim());
                        break;
                    case "numberOfIterationsPerDay":
                        numberOfIterationsPerDay = Integer.parseInt(values[1].trim());
                        break;
                    case "betaFileName":
                        betaFileName = values[1].trim();
                        break;
                }
            }
            line = in.readLine();
        }
        in.close();
        fStream.close();

        beta = new double[numberOfTradingDays];
        BufferedReader br = new BufferedReader(new FileReader(betaFileName));
        for (int i = 0; i < numberOfTradingDays; i++) {
            line = br.readLine();
            if (line == null) {
                throw new Exception("Size of beta does not fit the number of trading days.");
            }
            beta[i] = Double.parseDouble(line.trim());
            beta[i] = 1; //TODO
        }
        br.close();

    }

    public static double getBeta() {
        return beta[tradingDayCounter];
    }

    public static double getBetaC() {
        return betaC;
    }

}
