/*
 * This file is part of the repicea-statistics library.
 *
 * Copyright (C) 2009-2016 Mathieu Fortin for Rouge-Epicea
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
package repicea.stats.estimates;

import repicea.math.Matrix;
import repicea.stats.sampling.PopulationUnitWithUnequalInclusionProbability;

/**
 * This class implements the classical Horvitz-Thompson estimator of the total (tau) in a
 * context of random sampling WITHOUT replacement. 
 * @author Mathieu Fortin - September 2016
 */
@SuppressWarnings("serial")
public class PopulationTotalEstimate extends PointEstimate<PopulationUnitWithUnequalInclusionProbability> {

	/**
	 * Constructor.
	 */
	public PopulationTotalEstimate() {
		super();
	}

	/**
	 * Constructor with population size.
	 * @param populationSize the number of units in the population
	 */
	public PopulationTotalEstimate(double populationSize) {
		super(populationSize);
	}

	/**
	 * This method returns the value of the Horvitz-Thompson estimator (tau hat).
	 * @return a Matrix instance
	 */
	@Override
	public Matrix getMean() {
		Matrix total = new Matrix(nRows, nCols);
		int sampleSize = getObservations().size();
		for (PopulationUnitWithUnequalInclusionProbability observation : getObservations()) {
			total = total.add(observation.getData().scalarMultiply(1d/(sampleSize * observation.getInclusionProbability())));
		}
		return total;
	}

	/**
	 * This method returns the variance of the tau estimate.
	 * @return a Matrix
	 */
	@Override
	public Matrix getVariance() {
		int n = getObservations().size();
		PopulationUnitWithUnequalInclusionProbability obs_i;
		PopulationUnitWithUnequalInclusionProbability obs_j;
		double pi_i;
		double pi_j;
		double pi_ij;
		Matrix varianceContribution;
		Matrix variance = null;
		for (int i = 0; i < getObservations().size(); i++) {
			for (int j = i; j < getObservations().size(); j++) {
				obs_i = getObservations().get(i);
				obs_j = getObservations().get(j);
				pi_i = n * obs_i.getInclusionProbability();
				pi_j = n * obs_j.getInclusionProbability();
				if (i == j) {
					varianceContribution = obs_i.getData().multiply(obs_i.getData().transpose()).scalarMultiply((1 - pi_i)/(pi_i*pi_i));
				} else {
					pi_ij = pi_i * (n-1) * obs_j.getInclusionProbability() / (1 - obs_i.getInclusionProbability());
					double factor = (pi_ij - pi_i * pi_j)/(pi_i * pi_j * pi_ij);
					varianceContribution = obs_i.getData().multiply(obs_j.getData().transpose()).scalarMultiply(2 * factor);
				}
				if (variance == null) {
					variance = varianceContribution;
				} else {
					variance = variance.add(varianceContribution);
				}
			}
		}
		return variance;
	}

	
}
