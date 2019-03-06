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

import java.util.concurrent.ThreadLocalRandom;

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
        
        // Generate random seed
        long RandMin = 2500L;
        long RandMax = 3000L;
        long RandLong = ThreadLocalRandom.current().nextLong(RandMin, RandMax);
        
       
        // the defined unit time here is an hour
        double jobArrivalRate = 1.31;    // average jobs per hour
        int CC_Servers = 3;
        int ST_Servers = 2;
        int SM_Servers = 1;
        int HT_Servers = 3;
        int HM_Servers = 1;
        int RC_Servers = 2;
        
        // average jobs per hour
        double CC_ServiceRate = 1.31;
        double ST_ServiceRate = 0.776;
        double SM_ServiceRate = 0.228;
        double HT_ServiceRate = 0.552;
        double HM_ServiceRate = 0.326;
        
        double RC_ServiceRate = 9999;
        double RC_ServiceRate_REPAIR = 9999;
        double RC_ServiceRate_REPLACE = 9999;
        

        // instantiate simulation components
        SimEngine engine = SimEngine.getInstance();
        engine.setEndTime(endSimTime);
        Generator genr = new Generator("Field Tech Calls", jobArrivalRate);
        QueueStation CC_Station = new QueueStation("Call Center", CC_Servers, CC_ServiceRate);
        QueueStation ST_Station = new QueueStation("Software Tech", ST_Servers, ST_ServiceRate);
        QueueStation SM_Station = new QueueStation("Software Manager", SM_Servers, SM_ServiceRate);
        QueueStation HT_Station = new QueueStation("Hardware Tech", HT_Servers, HT_ServiceRate);
        QueueStation HM_Station = new QueueStation("Hardware Manager", HM_Servers, HM_ServiceRate);
        QueueStation RC_Station = new QueueStation("Repair Center", RC_Servers, RC_ServiceRate);
        
        QueueStation RC_Station_REPAIR = new QueueStation("Repair Center REPAIR", RC_Servers, RC_ServiceRate_REPAIR);
        QueueStation RC_Station_REPLACE = new QueueStation("Repair Center REPLACE", RC_Servers, RC_ServiceRate_REPLACE);
        
        Transducer transd = new Transducer();
        
        
        genr.setRandomSeed(RandLong);
        CC_Station.setRandomSeed(RandLong);
        ST_Station.setRandomSeed(RandLong);
        SM_Station.setRandomSeed(RandLong);
        HT_Station.setRandomSeed(RandLong);
        HM_Station.setRandomSeed(RandLong);
        RC_Station.setRandomSeed(RandLong);
            
        RC_Station_REPAIR.setRandomSeed(RandLong);
        RC_Station_REPLACE.setRandomSeed(RandLong);
            
        
        
        // register the simulation engine to monitor component events and connect the components
        genr.register(engine);
        CC_Station.register(engine);
        ST_Station.register(engine);
        SM_Station.register(engine);
        HT_Station.register(engine);
        HM_Station.register(engine);
        RC_Station.register(engine);
        
        
        RC_Station_REPAIR.register(engine);
        RC_Station_REPLACE.register(engine);
        
        // send generator output to station 1
        // 100% of station 1 output goes to station 2
        // 20% of station 2 output goes back to station 1; the rest goes to the transducer
        genr.setQueueStation(CC_Station);
        
        CC_Station.addOutputStation(ST_Station, 0.58);
        CC_Station.addOutputStation(ST_Station, 0.27);
        CC_Station.addOutputStation(transd, 0.15);
        
        ST_Station.addOutputStation(SM_Station, 0.30);
        ST_Station.addOutputStation(HT_Station, 0.20);
        ST_Station.addOutputStation(transd, 0.50);
        
        SM_Station.addOutputStation(HT_Station, 0.20);
        SM_Station.addOutputStation(transd, 0.80);
        
        HT_Station.addOutputStation(HM_Station, 0.59);
        //HT_Station.addOutputStation(RC_Station, 0.59);
        HT_Station.addOutputStation(transd, 0.41);
        
        HM_Station.addOutputStation(ST_Station, 0.05);
        HM_Station.addOutputStation(RC_Station, 0.31);
        HM_Station.addOutputStation(transd, 0.64);
        
        RC_Station.addOutputStation(RC_Station_REPAIR, 0.75);
        RC_Station.addOutputStation(RC_Station_REPLACE, 0.25);
        
        RC_Station_REPAIR.addOutputStation(transd, 1.0);
        RC_Station_REPLACE.addOutputStation(transd, 1.0);
        
        // run the simulation
        System.out.println("Beginning simulation...\n");
        genr.initialize();
        engine.simulate();
        System.out.println("\nSIMULATION COMPLETE");
        
        
    }
    
}
