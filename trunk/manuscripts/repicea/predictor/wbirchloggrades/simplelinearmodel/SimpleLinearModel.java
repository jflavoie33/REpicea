package repicea.predictor.wbirchloggrades.simplelinearmodel;

import repicea.math.Matrix;
import repicea.simulation.REpiceaPredictor;
import repicea.stats.distributions.ChiSquaredDistribution;
import repicea.stats.estimates.GaussianErrorTermEstimate;
import repicea.stats.estimates.GaussianEstimate;

@SuppressWarnings("serial")
class SimpleLinearModel extends REpiceaPredictor {

	private ChiSquaredDistribution distributionForVCovRandomDeviates;
	
	protected static boolean R2_95Version = false;
	
	protected SimpleLinearModel(boolean isParametersVariabilityEnabled, boolean isResidualVariabilityEnabled) {
		super(isParametersVariabilityEnabled, false, isResidualVariabilityEnabled);
		init();
	}

	@Override
	protected void init() {
		Matrix beta = new Matrix(2,1);
		beta.m_afData[0][0] = 4d;
		beta.m_afData[1][0] = 3d;
		Matrix omega = new Matrix(2,2);
		omega.m_afData[0][0] = 0.025;
		omega.m_afData[1][1] = 0.0005;
		omega.m_afData[0][1] = Math.sqrt(omega.m_afData[0][0] * omega.m_afData[1][1]) * .1;
		omega.m_afData[1][0] = omega.m_afData[0][1];
		setParameterEstimates(new GaussianEstimate(beta, omega));
		Matrix residualVariance = new Matrix(1,1);
		if (R2_95Version) {
			residualVariance.m_afData[0][0] = .284;			// to ensure a R2 of 0.95
		} else {
			residualVariance.m_afData[0][0] = 2d;
		}
		setDefaultResidualError(ErrorTermGroup.Default, new GaussianErrorTermEstimate(residualVariance));
		oXVector = new Matrix(1, beta.m_iRows);
	}
	
	protected double predictY(SamplePlot plot) {
		Matrix currentBeta = getParametersForThisRealization(plot);
		oXVector.resetMatrix();
		oXVector.m_afData[0][0] = 1d;
		oXVector.m_afData[0][1] = plot.getX();
		double pred = oXVector.multiply(currentBeta).m_afData[0][0];
		pred += getResidualError().m_afData[0][0] * Math.sqrt(plot.getX());
		return pred;
	}

	
	/*
	 * For manuscript purposes.
	 */
	void replaceModelParameters() {
		int degreesOfFreedom = 98;		// assumption of 100 observations - 2 parameters
		Matrix newMean = getParameterEstimates().getRandomDeviate();
		Matrix variance = getParameterEstimates().getVariance();
		if (distributionForVCovRandomDeviates == null) {
			distributionForVCovRandomDeviates = new ChiSquaredDistribution(degreesOfFreedom, variance);
		}
		Matrix newVariance = distributionForVCovRandomDeviates.getRandomRealization();
		setParameterEstimates(new GaussianEstimate(newMean, newVariance));
		
		Matrix residualVariance = this.getDefaultResidualError(ErrorTermGroup.Default).getVariance();
		ChiSquaredDistribution residualVarianceDistribution = new ChiSquaredDistribution(degreesOfFreedom, residualVariance);
		Matrix newResidualVariance = residualVarianceDistribution.getRandomRealization();
		setDefaultResidualError(ErrorTermGroup.Default, new GaussianErrorTermEstimate(newResidualVariance));
	}

	
}