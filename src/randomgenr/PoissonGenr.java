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
package randomgenr;

/**
 * Determines the number of independent events randomly occurring per some
 * unit time. Use the exponential variate generator to calculate time between
 * each event.
 * 
 * @author Gary R. Mayer
 */
public class PoissonGenr extends RandomVariateGenr {
    private double avgRate;     // average event per unit time
    
    /**
     * Default constructor. 
     * Default, average event rate is 1.0 per unit time.
     */
    public PoissonGenr() {
        super();
        this.avgRate = 1.0;
    }
    
    /**
     * Constructor to specify the random seed to use in pseudorandom number
     * generation. Default, average event rate is 1.0 per unit time.
     * 
     * @param seed pseudorandom number seed value
     */
    public PoissonGenr(long seed) {
        super(seed);
        this.avgRate = 1.0;
    }
    
    /**
     * Produces the next random variate from a Poisson distribution. Return
     * value indicates the number of independent events within the unit time.
     * 
     * @return number of random, independent events per unit time as a
     * double value
     */
    @Override
    public double nextVariate() {
        double RND;                         // uniform random value [0,1)
        double exp;                         // exponential value
        int numTests = 0;                   // number of tests
        double probabilityOfEvent = 1.0;  // probability of an event
        int numEvents = 0;                // number events this period
        
        // acceptance-rejection method
        exp = Math.exp(-this.avgRate);
        
        while (numEvents == 0) {
            RND = rand.nextDouble();
            probabilityOfEvent = probabilityOfEvent*RND;

            if (probabilityOfEvent < exp)
                numEvents = numTests;
            else
                numTests++;
        }
        
        return numEvents;
    }
    
    /**
     * Modifies the average event rate per unit time that is used in
     * calculating the number of events per unit time.
     * 
     * @param eventRate average event rate per unit time
     * (e.g., if 2.5 customers per minute is the rate, then 2.5 should be used.)
     */
    public void setEventRate(double eventRate) {
        this.avgRate = eventRate;
    }
}
