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
 * Simulation event that pairs a Simulatable object with its next event time.
 * Implements a Comparator interface to allow the object to sorted by the event
 * time.
 * 
 * @author Gary R. Mayer
 */
public class SimEvent implements Comparable<SimEvent> {
    private final Simulatable simulatable;
    private Double time
;    
    /**
     * Creates a SimEvent instance.
     * 
     * @param simulatable the Simulatable object with the upcoming event
     * @param eventTime the event time
     */
    public SimEvent(Simulatable simulatable, Double eventTime) {
        this.simulatable = simulatable;
        this.time = eventTime;
    }
        
    /**
     * Comparison of two simulation events for the purposes of ordering. The
     * method returns a comparison based upon the event time delta values.
     * 
     * @param event simulation event to which this one should be compared
     * @return 
     */
    @Override
    public int compareTo(SimEvent event) {
        // Assume neither SimEvent is null
        Double eventTime2 = event.time;
        
        // use Double's innate numeric comparator
        return this.time.compareTo(eventTime2);
    }
    
    /**
     * The simulatable instance that corresponds to this event.
     * 
     * @return an instance that implements Simulatable
     */
    public final Simulatable getSimulatable() {
        return this.simulatable;
    }
    
    /**
     * Provides the event time. This may be either the event time delta--the time
     * from when the upcoming simulation event was recorded, that the event will
     * occur--or the time of the actual event.
     * 
     * @return the event time delta
     */
    public Double getEventTime() {
        return this.time;
    }

    public void setEventTime(Double eventTime) {
        this.time = eventTime;
    }
    
}
