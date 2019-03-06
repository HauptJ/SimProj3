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
 * Provides a random variate from an inverse exponential distribution.
 * Typically used to determine the time between independent events determined
 * from a Poisson distribution representing events per unit time.
 * 
 * @author Gary R. Mayer
 */
public class ExponentialGenr extends RandomVariateGenr {
    private double avgRate;    // average arrival per unit time
    
    /**
     * Default constructor.
     * Default, average event rate per unit time is 1.0;
     */
    public ExponentialGenr() {
        super();
        this.avgRate = 1.0;
    }
    
    /**
     * Constructor to specify the random seed to use in pseudorandom number
     * generation. Default, average event rate is 1.0 per unit time.
     * 
     * @param seed pseudorandom number seed value
     */
    public ExponentialGenr(long seed) {
        super(seed);
        this.avgRate = 1.0;
    }
    
    /**
     * Produces the next random variate from an inverse exponential distribution.
     * Return value can typically be used to indicate the time until the next
     * arrival in a Poisson process with same arrival rate, or time until a
     * completion event.
     * 
     * @return a random variate from an exponential distribution
     */
    @Override
    public double nextVariate() {
        double RND = rand.nextDouble();     // uniform random value [0,1)
        
        // use inverse-transform technique
        return (-((1.0/this.avgRate) * Math.log(1.0 - RND)));
    }
    
    /**
     * Modifies the average event rate per unit time that is used in
     * calculating the next random variate.
     * 
     * @param eventRate average arrival rate per unit time
     * (e.g., if 2.5 customers per minute is the rate, then 2.5 should be used.)
     */
    public void setEventRate(double eventRate) {
        this.avgRate = eventRate;
    }
}