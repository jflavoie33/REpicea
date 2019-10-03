/*
 * This file is part of the repicea library.
 *
 * Copyright (C) 2009-2017 Mathieu Fortin for Rouge-Epicea
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package repicea.stats;

import java.security.InvalidParameterException;
import java.util.Random;

import repicea.stats.distributions.utility.NegativeBinomialUtility;

@SuppressWarnings("serial")
public class REpiceaRandom extends Random {
	
	private static final double OneThird = 1d/3;
		
	private double getRandomGammaForShapeGreaterThanOrEqualToOne(double shape) {
		double d = shape - OneThird;
		double c = 1d / Math.sqrt(9 * d);
		boolean found = false;
		double z, u;
		double v = 0d;
		while (!found) {
			z = nextGaussian();
			u = nextDouble();
			v = Math.pow(1 + c * z, 3d);
			boolean firstCondition = z > -1d/c;
			boolean secondCondition = Math.log(u) < .5 * z*z + d - d * v + d * Math.log(v);
			if (firstCondition) {
				if (secondCondition) {
					found = true; 
				}
			}
		}
		return d * v;
	}

	private double getRandomGammaForAnyShape(double shape) {
		if (shape >= 1) {
			return getRandomGammaForShapeGreaterThanOrEqualToOne(shape);
		} else {
			double x = this.getRandomGammaForShapeGreaterThanOrEqualToOne(shape + 1);
			return x * Math.pow(nextDouble(), 1d/shape);
		}
	}
	
	/**
	 * THis method returns a random gamma distributed value following Marsaglia and Tsang's method. The 
	 * mean of the distribution is obtained through the product of the shape and the scale.
	 * @param shape a double larger than 0
	 * @param scale a double larger than 0
	 * @return a double
	 */
	public double nextGamma(double shape, double scale) {
		if (shape <= 0d || scale <= 0d) {
			throw new InvalidParameterException("The shape and the scale must be larger than 0!");
		}
		double x = getRandomGammaForAnyShape(shape);
		return x * scale;
	}
	
	
	/**
	 * This method returns a random integer that follows negative binomial distribution.
	 * @param mean the mean of the distribution
	 * @param dispersion the dispersion parameter
	 * @return an integer
	 */
	public int nextNegativeBinomial(double mean, double dispersion) {
		double threshold = nextDouble();	// to determine how many recruits there are
		double cumulativeProb = 0.0;
		int output = -1;
		
		while (threshold > cumulativeProb) {		
			output++;
			double massProb = NegativeBinomialUtility.getMassProbability(output, mean, dispersion);
			cumulativeProb += massProb;
		}
		return output;
	}

	
	/**
	 * Returns a random deviate from the standard Student's t distribution. The algorithm behind 
	 * the random deviate generation is that of Bailey (1994) based on polar generation.
	 * @see Bailey, R.W. 1994. Polar generation of random variances with the t-distribution. 
	 * Mathematics of Computation 62(206): 779-781.
	 */
	public double nextStudentT(double degreesOfFreedom) {
		double W = 2d;
		double U = 0;
		while (W > 1) {
			U = nextDouble();
			double V = nextDouble();
			U = 2 * U - 1;
			V = 2 * V - 1;
			W = U * U + V * V;
		}
		double C2 = U * U / W;
		double R2 = degreesOfFreedom * (Math.pow(W, - 2d / degreesOfFreedom) - 1);
		double result;
		if (nextDouble() < .5) {
			result = Math.sqrt(R2*C2);
		} else {
			result = - Math.sqrt(R2*C2);
		}
		return result;
	}
	
}
