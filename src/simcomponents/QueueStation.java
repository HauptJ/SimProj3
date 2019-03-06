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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;
import randomgenr.ExponentialGenr;
import randomgenr.UniformGenr;

/**
 *
 * @author Gary R. Mayer
 */
public class QueueStation implements Simulatable {
    private static UniformGenr outSelectGenr = new UniformGenr();   // pseudorandom uniform variate generator
    
    private final String name;
    private final LinkedList<Job> jobQueue;            // linked list implements a FIFO queue
    private final int numServers;                      // number of servers
    private final ArrayList<Job> activeServers;        // active job servers for this station
    private final TreeSet<OutputPair> outputStations;  // set of output stations
    private final ArrayList<EventObserver> observers;

    private final ExponentialGenr serviceTimeGenr;
    public int RC_Stock;
    
    protected QueueStation(String name, int numServers, double serviceRate) {
        this.name = name;
        this.jobQueue = new LinkedList<>();
        this.numServers = numServers;
        this.activeServers = new ArrayList<>();
        this.outputStations = new TreeSet<>();
        this.observers = new ArrayList<>(2);
        this.serviceTimeGenr = new ExponentialGenr();
        this.serviceTimeGenr.setEventRate(serviceRate);
        this.RC_Stock = 2;
    }

    /**
     * Adds a job to the queue station system. If a server is free, it immediately
     * begins work on the job. If no server is free, the job is queued.
     * 
     * @param job the job to add to the queue station system
     */
    public void addJob(Job job) {
        this.jobQueue.add(job);
        
        // if a server is not used, start job immediately
        if (this.activeServers.size() < this.numServers) {
            startNextJob();
        }
        
        // Repair Center stock update
        if ("Repair Center".equals(this.getName())) {
            this.RC_Stock = this.RC_Stock -1;
             System.out.println("  " + this.name + " Stock: " + this.RC_Stock);
        }
    }
    
    /**
     * Adds a QueueStation as output from this station's completed execution
     * with specified probability.
     * 
     * @param station queue station to receive output of execution
     * @param probability probability that this station gets the output
     */
    public void addOutputStation(QueueStation station, double probability) {
        this.outputStations.add(new OutputPair(station, probability));
    }
    
    @Override
    public void execute() {
        // remove job from server; as there is no distinction between jobs in
        //  this example, just take the first
        Job finishedJob = this.activeServers.remove(0);
        
        // send job to selected output station
        QueueStation outputStation = selectOutputStation();
        outputStation.addJob(finishedJob);
        System.out.println("  " + this.name + " sending job to " + outputStation.getName());

        // get next job from queue
        startNextJob();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void register(EventObserver observer) {
        this.observers.add(observer);
    }
    
    public void setRandomSeed(long seed) {
        QueueStation.outSelectGenr.setSeed(seed);
        this.serviceTimeGenr.setSeed(seed);
    }

    @Override
    public void unregister(EventObserver observer) {
        this.observers.remove(observer);
    }
    
    private QueueStation selectOutputStation() {
        if (this.outputStations.isEmpty())
            return null;
        
        double outputSelect = outSelectGenr.nextVariate();
        double currentSelect = 0.0;
        OutputPair outPair;
        QueueStation outputStation = null;
        Iterator<OutputPair> osIter = this.outputStations.descendingIterator();

        while (outputSelect > currentSelect) {
            // update outputStation to next station in list
            if (!osIter.hasNext()) {
                throw new IllegalStateException("Invalid output station. Unable to send output."
                        + " Ensure output probabilities sum to unity.");
            }

            outPair = osIter.next();
            outputStation = outPair.getStation();

            // add next item probability to currentSelect
            currentSelect += outPair.getProbability();
        }
        
        return outputStation;
    }
    
    private void startNextJob() {
        // get pending job from queue
        Job nextJob = this.jobQueue.poll();
        
        if (nextJob != null) {
            // determine the delta time from now to complete the job 
            // and notify observers of the pending simulation event
            double serviceTime = this.serviceTimeGenr.nextVariate();
            
            notifyObservers(serviceTime);

            // process the job until event completion time
            this.activeServers.add(nextJob);
            
            System.out.printf("  " + this.name + ": Started job. Done in %.3f. " 
                + this.activeServers.size() + " of " + this.numServers
                + " server(s) busy.%n", serviceTime);
        }
    }
    
    private void notifyObservers(double eventTime) {
        SimEvent simEvent = new SimEvent(this, eventTime);
        
        for (EventObserver observer : this.observers) {
            observer.notify(simEvent);
        }
    }
    
    /**
     * Internal data class to capture probability when output may nondeterministically
     * go to a different next place.
     */
    private class OutputPair implements Comparable<OutputPair> {
        private QueueStation station;
        private Double probability;
        
        public OutputPair(QueueStation station, double probability) {
            this.station = station;
            this.probability = probability;
        }

        @Override
        public int compareTo(OutputPair op) {
            if (this.probability >= op.getProbability())
                return 1;
            
            else
                return -1;
        }
        
        public Double getProbability() {
            return this.probability;
        }
        
        public QueueStation getStation() {
            return this.station;
        }
            
    }
}
