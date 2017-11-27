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
import java.util.LinkedList;
import randomgenr.ExponentialGenr;
import randomgenr.PoissonGenr;

/**
 * Produces the external events (job arrivals) into the system.
 * @author Gary R. Mayer
 */
public class Generator implements Simulatable {
    private final String name;
    private final PoissonGenr arrivalGenr;
    private final ExponentialGenr arrivalTimeGenr;
    private final LinkedList<SimEvent> arrivalEvents;
    private final ArrayList<EventObserver> observers;
    private QueueStation queueStation;
    
    /**
     * Constructor. Calculates all arrivals *in one unit time* using a Poisson
     * distribution for a given rate. Then calculates the times between all arrivals
     * using an exponential distribution with the same rate.
     * 
     * @param name name of the generator instance
     * @param rate the rate of events per unit time
     */
    public Generator(String name, double rate) {
        this.name = name;
        this.arrivalGenr = new PoissonGenr();
        this.arrivalGenr.setEventRate(rate);
        this.arrivalTimeGenr = new ExponentialGenr();
        this.arrivalTimeGenr.setEventRate(rate);
        this.arrivalEvents = new LinkedList<>();
        this.observers = new ArrayList<>();
        this.queueStation = null;
    }

    /**
     * Sends the first event in the queue to the attached queue station.
     */
    @Override
    public void execute() {
        // provide the queue station with its input
        if (this.queueStation == null || this.arrivalEvents.isEmpty()) {
            System.out.printf("  Generator: No jobs to send.%n");
        } 
        else {
            this.queueStation.addJob(new Job());
            System.out.printf("  Generator: Sent job to " + this.queueStation.getName() + ".%n");
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * Establish arrivals and time for each arrival.
     */
    public void initialize() {
        // calculate the number of arrivals in the unit time
        int numArrivalsWithinUnitTime = (int) (this.arrivalGenr.nextVariate());
        
        // calculate the times of the arrivals
        double currentTime = 0.0;
        double arrivalTime;
        for (int a=0; a < numArrivalsWithinUnitTime; a++) {
            arrivalTime = this.arrivalTimeGenr.nextVariate();
            currentTime += arrivalTime;
            SimEvent newEvent = new SimEvent(this, currentTime);
            this.arrivalEvents.add(newEvent);
            notifyObservers(newEvent);
        }
        
        System.out.printf("Generator: %d job(s) created.\n",
                numArrivalsWithinUnitTime);
    }

    @Override
    public void register(EventObserver observer) {
        this.observers.add(observer);
    }

    public void setQueueStation(QueueStation station) {
        this.queueStation = station;
    }
    
    public void setRandomSeed(long seed) {
        this.arrivalGenr.setSeed(seed);
        this.arrivalTimeGenr.setSeed(seed);
    }
    
    @Override
    public void unregister(EventObserver observer) {
        this.observers.remove(observer);
    }
    
    private void notifyObservers(SimEvent event) {
        for (EventObserver observer : this.observers) {
            observer.notify(event);
        }
    }
    
}
