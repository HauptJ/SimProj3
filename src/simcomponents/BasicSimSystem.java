/*
 * Copyright (c) 2017, Gary R. Mayer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package simcomponents;

/**
 * A simulation system providing an experimental frame for a simulation engine and
 * simulatable components. The domain of this experimental frame is a queueing network
 * of servers that processes a generic job.
 * 
 * @author Gary R. Mayer
 */
public class BasicSimSystem {

    /**
     * Takes up to two (optional) arguments. The first must be a double value that
     * represents the end time of the simulation. If a second argument is given,
     * it must be a long value representing the random number generator seed.
     * 
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        //double endSimTime = 10.0;       // max hours to simulate
        double endSimTime = 8.0;
        
        Long testSeed;                // default random seed
        //testSeed = 543210L;      
        //testSeed = 697L;
        testSeed = 200L;
        //testSeed = Long.MIN_VALUE;
        
        
        // the defined unit time here is an hour
        double jobArrivalRate = 1.31;    // average jobs per hour
        //int stationServers1 = 3;
        int CC_Servers = 3;
        //int stationServers2 = 1;
        int ST_Servers = 2;
        int SM_Servers = 1;
        int HT_Servers = 3;
        int HM_Servers = 1;
        int RC_Servers = 2;
        
        
        //double serviceRate1 = 0.5;      // average jobs per hour
        double CC_ServiceRate = 1.31;
        //double serviceRate2 = 1.0;      // average jobs per hour
        double ST_ServiceRate = 0.776;
        double SM_ServiceRate = 0.228;
        double HT_ServiceRate = 0.552;
        //double HM_ServiceRate = 0.326;
        double RC_ServiceRate = 0.101;
        
        double RC_ServiceRate_REPAIR = 0.0758;
        double RC_ServiceRate_REPLACE = 0.0253;
        
        if (args.length > 2) {
            throw new IllegalArgumentException("A maximum of two arguments is allowed.");
        }
        
        if (args.length == 2) {
            try {
                endSimTime = Double.valueOf(args[0]);
                testSeed = Long.valueOf(args[1]);
            } catch (NumberFormatException nfe) {
                System.err.println("Invalid arguments. When providing two"
                        + " arguments, the first must be a double value and the"
                        + " second must be a long value.");
            }            
        }
        
        else if (args.length == 1) {
            try {
                endSimTime = Double.valueOf(args[0]);
            } catch (NumberFormatException nfe) {
                System.err.println("Invalid argument. When providing only one"
                        + " argument, the value must be a double value.");
            }            
        }
        
        // instantiate simulation components
        SimEngine engine = SimEngine.getInstance();
        engine.setEndTime(endSimTime);
        Generator genr = new Generator("Field Tech Calls", jobArrivalRate);
        //QueueStation station1 = new QueueStation("Station_1", stationServers1, serviceRate1);
        QueueStation CC_Station = new QueueStation("Call Center", CC_Servers, CC_ServiceRate);
       // QueueStation station2 = new QueueStation("Station_2", stationServers2, serviceRate2);
        QueueStation ST_Station = new QueueStation("Software Tech", ST_Servers, ST_ServiceRate);
        QueueStation SM_Station = new QueueStation("Software Manager", SM_Servers, SM_ServiceRate);
        QueueStation HT_Station = new QueueStation("Hardware Tech", HT_Servers, HT_ServiceRate);
        //QueueStation HM_Station = new QueueStation("Hardware Manager", HM_Servers, HM_ServiceRate);
        QueueStation RC_Station = new QueueStation("Repair Center", RC_Servers, RC_ServiceRate);
        
        QueueStation RC_Station_REPAIR = new QueueStation("Repair Center REPAIR", RC_Servers, RC_ServiceRate_REPAIR);
        QueueStation RC_Station_REPLACE = new QueueStation("Repair Center REPLACE", RC_Servers, RC_ServiceRate_REPLACE);
        
        Transducer transd = new Transducer();
        
        if (testSeed != Long.MIN_VALUE) {
            genr.setRandomSeed(testSeed);
            //station1.setRandomSeed(testSeed);
            CC_Station.setRandomSeed(testSeed);
            //station2.setRandomSeed(testSeed);
            ST_Station.setRandomSeed(testSeed);
            SM_Station.setRandomSeed(testSeed);
            HT_Station.setRandomSeed(testSeed);
            //HM_Station.setRandomSeed(testSeed);
            RC_Station.setRandomSeed(testSeed);
            
            RC_Station_REPAIR.setRandomSeed(testSeed);
            RC_Station_REPLACE.setRandomSeed(testSeed);
            
        }
        
        // register the simulation engine to monitor component events and connect the components
        genr.register(engine);
        //station1.register(engine);
        CC_Station.register(engine);
        //station2.register(engine);
        ST_Station.register(engine);
        SM_Station.register(engine);
        HT_Station.register(engine);
        //HM_Station.register(engine);
        RC_Station.register(engine);
        
        
        RC_Station_REPAIR.register(engine);
        RC_Station_REPLACE.register(engine);
        
        // send generator output to station 1
        // 100% of station 1 output goes to station 2
        // 20% of station 2 output goes back to station 1; the rest goes to the transducer
        //genr.setQueueStation(station1);
        genr.setQueueStation(CC_Station);
        //station1.addOutputStation(station2, 1.0);
        CC_Station.addOutputStation(ST_Station, 0.58);
        CC_Station.addOutputStation(ST_Station, 0.27);
        CC_Station.addOutputStation(transd, 0.15);
        //station2.addOutputStation(station1, 0.2);
        ST_Station.addOutputStation(SM_Station, 0.30);
        ST_Station.addOutputStation(HT_Station, 0.20);
        ST_Station.addOutputStation(transd, 0.50);
        //station2.addOutputStation(transd, 0.8);
        
        SM_Station.addOutputStation(HT_Station, 0.20);
        SM_Station.addOutputStation(transd, 0.80);
        
        //HT_Station.addOutputStation(HM_Station, 0.59);
        HT_Station.addOutputStation(RC_Station, 0.59);
        HT_Station.addOutputStation(transd, 0.41);
        
        //HM_Station.addOutputStation(ST_Station, 0.05);
        //HM_Station.addOutputStation(RC_Station, 0.31);
        //HM_Station.addOutputStation(transd, 0.64);
        
        RC_Station.addOutputStation(RC_Station_REPAIR, 0.75);
        RC_Station.addOutputStation(RC_Station_REPLACE, 0.25);
        RC_Station.addOutputStation(transd, 1.0);
        
        // run the simulation
        System.out.println("Beginning simulation...\n");
        genr.initialize();
        engine.simulate();
        System.out.println("\nSIMULATION COMPLETE");
        
        
    }
    
}
