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

import java.util.PriorityQueue;

/**
 * SimEngine class to execute queue station models.
 * Implements the Singleton design pattern...because.
 * 
 * @author Gary R. Mayer
 */
public class SimEngine implements EventObserver {
    private static SimEngine instance;
    private double endTime;
    private double simTime;
    private final PriorityQueue<SimEvent> eventQueue;
    
    public static SimEngine getInstance() {
        
        if (SimEngine.instance == null) {
            SimEngine.instance = new SimEngine();
        }
        
        return SimEngine.instance;
    }
    
    @Override
    public void notify(SimEvent simEvent) {
        // change event delta time to simulation time
        double eventTime = this.simTime + simEvent.getEventTime();
        simEvent.setEventTime(eventTime);
        
        // put the simulation event in the event queue
        this.eventQueue.add(simEvent);
        System.out.printf("  Engine: Rec'vd event from " + simEvent.getSimulatable().getName()
                + " to occur at %.3f%n", eventTime);
        //System.out.printf("Event Job time: %.3f%n", simEvent.getEventTime()- this.simTime);
    }
    
    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }
    
    public void simulate() {
        SimEvent nextEvent;
        Simulatable simulatable;
        System.out.printf("Simulation time: %.3f. Running until: %.1f%n", this.simTime, this.endTime);
        
        while ((this.endTime > this.simTime) && (!this.eventQueue.isEmpty())) {
            // get next event
            nextEvent = this.eventQueue.poll();
            
            if (nextEvent != null) {
                // update sim time; assumes event time is sim time
                this.simTime = nextEvent.getEventTime();
                System.out.printf("Event simulation time: %.3f%n", this.simTime);
                //System.out.printf("Event Job time: %.3f%n", this.simTime - nextEvent.getEventTime());
                
                // make the next event happen
                simulatable = nextEvent.getSimulatable();
                simulatable.execute();
            }
        }
        
        System.out.printf("Last event at %.3f%n.", this.simTime);
    }
    
    private SimEngine() {
        this.endTime = 0.0;
        this.simTime = 0.0;
        this.eventQueue = new PriorityQueue<>(10);
    }
    
}
