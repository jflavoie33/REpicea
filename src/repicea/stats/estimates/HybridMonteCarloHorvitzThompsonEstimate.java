package repicea.stats.estimates;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import repicea.math.Matrix;
import repicea.stats.distributions.UnknownDistribution;

@SuppressWarnings("serial")
public class HybridMonteCarloHorvitzThompsonEstimate extends Estimate<UnknownDistribution>{

	public class VarianceEstimate {
		private final Matrix modelRelatedVariance;
		private final Matrix samplingRelatedVariance;
		private final Matrix totalVariance;
		
		private VarianceEstimate(Matrix modelRelatedVariance, Matrix samplingRelatedVariance, Matrix totalVariance) {
			this.modelRelatedVariance = modelRelatedVariance;
			this.samplingRelatedVariance = samplingRelatedVariance;
			this.totalVariance = totalVariance; 
		}

		/**
		 * This method returns the estimate of the total variance.
		 * @return a Matrix instance
		 */
		public Matrix getTotalVariance() {return totalVariance;}

		/**
		 * This method returns the estimate of the model-related variance.
		 * @return a Matrix instance
		 */
		public Matrix getModelRelatedVariance() {return modelRelatedVariance;}
		
		/**
		 * This method returns the estimate of the sampling-related variance.
		 * @return a Matrix instance
		 */
		public Matrix getSamplingRelatedVariance() {return samplingRelatedVariance;}
	
	}
	
	
	private final List<HorvitzThompsonTauEstimate> estimates;
	
	public HybridMonteCarloHorvitzThompsonEstimate() {
		super(new UnknownDistribution());
		estimates = new ArrayList<HorvitzThompsonTauEstimate>();
	}

	/**
	 * This method adds a realization of the HT estimate. The compatibility of 
	 * the instance with previously added instances is checked. If the chek fails
	 * an InvalidParameterException is thrown.
	 * @param estimate a HorvitzThompsonTauEstimate instance
	 */
	public void addHTEstimate(HorvitzThompsonTauEstimate estimate) {
		if (estimates.isEmpty() || estimates.get(0).isCompatible(estimate)) {
			estimates.add(estimate);
		} else {
			throw new InvalidParameterException("The HT estimate is not compatible with the previous estimates!");
		}
	}
	
	/**
	 * This method returns the estimate of the total.
	 * @return a Matrix instance
	 */
	public Matrix getTotal() {
		Matrix mean = null;
		for (HorvitzThompsonTauEstimate estimate : estimates) {
			if (mean == null) {
				mean = estimate.getTotal();
			} else {
				mean = mean.add(estimate.getTotal());
			}
		}
		mean = mean.scalarMultiply(1d/estimates.size());
		return mean;
	}

	/**
	 * This method returns the uncorrected variance of the total estimate. 
	 * This estimator is based on the law of total variance. 
	 * @return a Matrix
	 */
	public Matrix getTotalVarianceUncorrected() {
		MonteCarloEstimate variance = new MonteCarloEstimate();
		MonteCarloEstimate mean = new MonteCarloEstimate();
		for (HorvitzThompsonTauEstimate estimate : estimates) {
			mean.addRealization(estimate.getTotal());
			variance.addRealization(estimate.getVarianceOfTotalEstimate());
		}
		return mean.getVariance().add(variance.getMean());
	}

	/**
	 * This method returns the corrected variance of the total estimate. 
	 * This estimator is based on the law of total variance. 
	 * @return a Matrix
	 */
	public VarianceEstimate getVarianceOfTotalEstimate() {
		MonteCarloEstimate variance = new MonteCarloEstimate();
		MonteCarloEstimate mean = new MonteCarloEstimate();
		int nbObs = estimates.get(0).getObservations().size();
		double populationSize = estimates.get(0).populationSize;
		SampleMeanEstimate[] observationMeans = new SampleMeanEstimate[nbObs];
		for (int i = 0; i < nbObs; i++) {
			observationMeans[i] = new SampleMeanEstimate();
		}
		for (HorvitzThompsonTauEstimate estimate : estimates) {
			mean.addRealization(estimate.getTotal());
			variance.addRealization(estimate.getVarianceOfTotalEstimate());
			for (int i = 0; i < nbObs; i++) {
				observationMeans[i].addObservation(estimate.getObservations().get(i).observation);	// storing the realizations of the same observation in the same SampleMeanEstimate instance 
			}
		}
		HorvitzThompsonTauEstimate meanEstimate = new HorvitzThompsonTauEstimate(populationSize);
		for (int i = 0; i < nbObs; i++) {
			meanEstimate.addObservation(observationMeans[i].getMean(), estimates.get(0).getObservations().get(i).inclusionProbability);
		}
		Matrix meanContribution = mean.getVariance();
		Matrix meanDesignVariance = meanEstimate.getVarianceOfTotalEstimate();
		Matrix averageVariance = variance.getMean();
		
		Matrix samplingRelatedComponent = meanDesignVariance;
		Matrix modelRelatedComponent = meanContribution.add(meanDesignVariance).subtract(averageVariance);
		Matrix totalVariance = modelRelatedComponent.add(samplingRelatedComponent);
		VarianceEstimate varEst = new VarianceEstimate(modelRelatedComponent, samplingRelatedComponent, totalVariance);
		return varEst;
	}
	
	@Override
	public Matrix getMean() {
		return getTotal().scalarMultiply(1d/getPopulationSize());
	}
	
	protected double getPopulationSize() {
		if (!estimates.isEmpty()) {
			return estimates.get(0).getPopulationSize();
		} else {
			return 0d;
		}
	}
	
	
	protected int getNumberOfRealizations() {
		return estimates.size();
	}
	
		
}
