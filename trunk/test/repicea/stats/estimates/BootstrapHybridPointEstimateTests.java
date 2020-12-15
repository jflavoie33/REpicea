package repicea.stats.estimates;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import repicea.math.Matrix;
import repicea.stats.estimates.BootstrapHybridPointEstimate.VariancePointEstimate;
import repicea.stats.sampling.PopulationUnitWithEqualInclusionProbability;

public class BootstrapHybridPointEstimateTests {

	private static final Random RANDOM = new Random();
	
//	@Ignore
	@Test
	public void simpleTestWithoutModelVariability() {
		PopulationMeanEstimate pe = new PopulationMeanEstimate();
		Matrix obs;
		for (int i = 0; i < 50; i++) {
			obs = new Matrix(1,1);
			obs.m_afData[0][0] = RANDOM.nextGaussian() * 2 + 12;
			pe.addObservation(new PopulationUnitWithEqualInclusionProbability(obs));
		}

		BootstrapHybridPointEstimate bhpe = new BootstrapHybridPointEstimate(); 
		for (int i = 0; i < 1000; i++) {
			bhpe.addPointEstimate(pe);
		}
		
		double expectedMean = pe.getMean().m_afData[0][0];
		double actualMean = bhpe.getMean().m_afData[0][0];
		
		Assert.assertEquals("Testing mean estimates", expectedMean, actualMean, 1E-8);
		
		
		double expectedVariance = pe.getVariance().m_afData[0][0];
		double actualVariance = bhpe.getVariance().m_afData[0][0];
		System.out.println("Expected variance = " + expectedVariance + " - actual variance = " + actualVariance);
		
		Assert.assertEquals("Testing variance estimates", expectedVariance, actualVariance, 1E-8);
	}
	

//	@Ignore
	@Test
	public void simpleTestWithoutSamplingVariability() {
		BootstrapHybridPointEstimate bhpe = new BootstrapHybridPointEstimate(); 
		for (int i = 0; i < 100000; i++) {
			PopulationMeanEstimate pe = new PopulationMeanEstimate();
			double deviate = RANDOM.nextGaussian() * 2 + 12;
			Matrix obs;
			for (int j = 0; j < 50; j++) {
				obs = new Matrix(1,1);
				obs.m_afData[0][0] = deviate;
				pe.addObservation(new PopulationUnitWithEqualInclusionProbability(obs));
			}
			bhpe.addPointEstimate(pe);
		}
		
		double expectedMean = 12d;
		double actualMean = bhpe.getMean().m_afData[0][0];
		System.out.println("Expected mean = " + expectedMean + " - actual mean = " + actualMean);
		Assert.assertEquals("Testing mean estimates", expectedMean, actualMean, 1E-1);
		
		
		double expectedVariance = 4d;
		double actualVariance = bhpe.getVariance().m_afData[0][0];
		System.out.println("Expected variance = " + expectedVariance + " - actual variance = " + actualVariance);
		
		Assert.assertEquals("Testing variance estimates", expectedVariance, actualVariance, 1E-1);
	}

	
	@Test
	public void simpleTestWithCompleteVariability() {
		BootstrapHybridPointEstimate bhpe = new BootstrapHybridPointEstimate(); 
		int sampleSize = 10;
		double meanX = 20;
		double stdPopUnit = 15;
		PopulationMeanEstimate pe = new PopulationMeanEstimate();
		Matrix obs;
		for (int j = 0; j < sampleSize; j++) {
			obs = new Matrix(1,1);
			obs.m_afData[0][0] = meanX + RANDOM.nextGaussian() * stdPopUnit;
			pe.addObservation(new PopulationUnitWithEqualInclusionProbability(obs));
		}
		
		double mu_x_hat = pe.getMean().m_afData[0][0];
		double var_mu_x_hat = pe.getVariance().m_afData[0][0];
		
		double meanModel = 0.7;
		double stdModel = .15;
		double stdRes = 1.1;
		
		for (int i = 0; i < 100000; i++) {
			PopulationMeanEstimate peNew = new PopulationMeanEstimate();
			Matrix obsNew;
			double slope = meanModel + RANDOM.nextGaussian() * stdModel; 
			for (int j = 0; j < sampleSize; j++) {
				obsNew = new Matrix(1,1);
				double x = pe.getObservations().get(j).getData().m_afData[0][0];
				obsNew.m_afData[0][0] =  x * slope + stdRes * RANDOM.nextGaussian();
				peNew.addObservation(new PopulationUnitWithEqualInclusionProbability(obsNew));
			}
			bhpe.addPointEstimate(peNew);
		}
		
		double expectedMean = meanModel * mu_x_hat;
		double actualMean = bhpe.getMean().m_afData[0][0];
		
		Assert.assertEquals("Testing mean estimates", expectedMean, actualMean, 3E-2);
		
		
		double expectedVariance = mu_x_hat * mu_x_hat * stdModel * stdModel + 
				meanModel * meanModel * var_mu_x_hat -	
				stdModel * stdModel * var_mu_x_hat;	// when dealing with the estimate of the mean, the contribution of the residual error tends to 0, i.e. N * V.bar(e_i) / N^2 = V.bar(e_i) / N. MF2020-12-14
		VariancePointEstimate varPointEstimate = bhpe.getCorrectedVariance();
		System.out.println("Model-related variance = " + varPointEstimate.getModelRelatedVariance());
		System.out.println("Sampling-related variance = " + varPointEstimate.getSamplingRelatedVariance());
		System.out.println("Total variance = " + varPointEstimate.getTotalVariance());
		double actualVariance = varPointEstimate.getTotalVariance().m_afData[0][0];
//		double formerActualVariance = bhpe.getFormerCorrectedVariance().getTotalVariance().m_afData[0][0];
//		Assert.assertEquals("Testing former implementation", actualVariance, formerActualVariance, 1E-8);

		Assert.assertEquals("Testing variance estimates", expectedVariance, actualVariance, 7E-2);

		Matrix empiricalCorrection = varPointEstimate.getTotalVariance().subtract(varPointEstimate.getModelRelatedVariance()).subtract(varPointEstimate.getSamplingRelatedVariance());
		double theoreticalCorrection = -stdModel * stdModel * var_mu_x_hat;
		System.out.println("Theoretical correction = " + theoreticalCorrection);
		System.out.println("Empirical correction = " + empiricalCorrection);
		Assert.assertEquals("Comparing variance bias correction", theoreticalCorrection, empiricalCorrection.m_afData[0][0], 1E-2);
	}

	
	
}
