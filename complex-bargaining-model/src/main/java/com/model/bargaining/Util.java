package com.model.bargaining;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
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
    public static double betaC;
    public static int numberOfTradingDays;
    public static int numberOfIterationsPerDay;
    public static String betaFileName;
    public static double[] beta;
    public static int numberOfIterationsToDiscard;


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
                    case "numberOfIterationsToDiscard":
                        numberOfIterationsToDiscard = Integer.parseInt(values[1].trim());
                        break;
                }
            }
            line = in.readLine();
        }
        in.close();
        fStream.close();

        beta = new double[numberOfTradingDays+1];
        beta[0] = betaC;
        BufferedReader br = new BufferedReader(new FileReader(betaFileName));
        for (int i = 1; i < numberOfTradingDays; i++) {
            line = br.readLine();
            if (line == null) {
                throw new Exception("Size of beta does not fit the number of trading days.");
            }
            beta[i] = Double.parseDouble(line.trim());
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
