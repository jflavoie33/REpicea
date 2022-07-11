/*
 * This file is part of the repicea library.
 *
 * Copyright (C) 2009-2022 Mathieu Fortin for Rouge-Epicea
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
package repicea.stats.estimators;

import repicea.stats.data.DataSet;
import repicea.stats.data.StatisticalDataStructure;
import repicea.stats.estimates.Estimate;
import repicea.stats.model.StatisticalModel;

public interface Estimator {

	/**
	 * The EstimatorException class encompasses all the exception that can be thrown when the
	 * optimizer fails to reach convergence.
	 * @author Mathieu Fortin - November 2015
	 */
	public static class EstimatorException extends Exception {
		private static final long serialVersionUID = 20110614L;

		public EstimatorException(String message) {
			super(message);
		}
	}
	
	public boolean doEstimation(StatisticalModel<? extends StatisticalDataStructure> model)	throws EstimatorException;

	/**
	 * This method returns true if the estimator successfully estimated the parameters.
	 * @return a boolean
	 */
	public boolean isConvergenceAchieved();

	/**
	 * This method returns the parameter estimates.
	 * @return an Estimate instance
	 */
	public Estimate<?> getParameterEstimates();

	/**
	 * Produces a DataSet instance with the convergence status.
	 * @return a DataSet instance
	 */
	public DataSet getParameterEstimatesReport();

	/**
	 * Produces a DataSet instance with the convergence status.
	 * @return a DataSet instance
	 */
	public DataSet getConvergenceStatusReport();

	
	/**
	 * Provides the report on the convergence and parameter estimates.
	 * @param model
	 * @return
	 */
	public default String getReport() {
		if (!isConvergenceAchieved()) {
			return "The log-likelihood function has not been or cannot be optimized.";
		} else {
			StringBuilder sb = new StringBuilder();
			DataSet convergenceDataset = getConvergenceStatusReport();
			sb.append(convergenceDataset.toString() + System.lineSeparator());
			DataSet parameterDataset = getParameterEstimatesReport();
			sb.append(parameterDataset.toString() + System.lineSeparator());
			return sb.toString();
		}

	}

}
