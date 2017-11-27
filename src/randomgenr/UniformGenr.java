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
 * Generates a uniform random variable between two real values.
 * 
 * @author Gary R. Mayer
 */
public class UniformGenr extends RandomVariateGenr {
    private double min;
    private double max;
    
    /**
     * Default constructor for a random variate between 0 and 1.
     */
    public UniformGenr() {
        this(0.0, 1.0);
    }
    
    /**
     * Constructor that accepts a specific seed to be used for the pseudorandom
     * number generator.
     * 
     * @param seed pseudorandom number seed value
     */
    public UniformGenr(long seed) {
        this(1.0, 0.0);
        rand.setSeed(seed);
    }
    
    /**
     * Constructor to provide a uniform random variate between two numbers.
     * 
     * @param minValue the minimum random value
     * @param maxValue the maximum random value
     */
    public UniformGenr(double minValue, double maxValue) {
        super();
        
        if (minValue >= maxValue) {
            throw new IllegalArgumentException("Minimum value must actually be less than the maximum value");
        }
        
        this.min = minValue;
        this.max = maxValue;
    }

    /**
     * Produces the next random variate from a uniform distribution.
     * 
     * @return random variate as a double value
     */
    @Override
    public double nextVariate() {
        double RND = rand.nextDouble();     // uniform random value [0,1)
        
        // transform value across requested distribution
        // inverse-transform method
        return (this.min + ((this.max - this.min)*RND));
    }
}
