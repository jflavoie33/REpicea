package repicea.stats;

import org.junit.Assert;
import org.junit.Test;

import repicea.math.Matrix;
import repicea.stats.estimates.MonteCarloEstimate;

public class REpiceaRandomTest {

	@Test
	public void testGammaMean() {
		double dDispersionGamma = 0.61766452969979;
		double fGammaMean = 1.813311388045523;
//		double scale = dDispersionGamma;
//		double shape = fGammaMean / dDispersionGamma;
		double shape = dDispersionGamma;
		double scale = fGammaMean / dDispersionGamma;
		REpiceaRandom randomGenerator = new REpiceaRandom();
		
		double mean = 0;
		int maxIter = 500000;
		double meanFactor = 1d / maxIter;
		for (int i = 0; i < maxIter; i++) {
			double randomDeviate = 0;
			try {
				randomDeviate = randomGenerator.nextGamma(shape, scale);
				mean += randomDeviate * meanFactor;
			} catch (Exception e) {
			}

		}
		
		System.out.println ("Simulated mean = " + mean + "; Expected mean = " + fGammaMean);
		Assert.assertEquals("Testing mean for gamma random values", fGammaMean, mean, 1E-2);
	}

	
	@Test
	public void testGammaVariance() {
		double dDispersionGamma = 0.61766452969979;
		double fGammaMean = 1.813311388045523;
//		double scale = dDispersionGamma;
//		double shape = fGammaMean / dDispersionGamma;
		double shape = dDispersionGamma;
		double scale = fGammaMean / dDispersionGamma;
		REpiceaRandom randomGenerator = new REpiceaRandom();
//		RandomDataImpl randomGenerator = new RandomDataImpl();
		double variance = fGammaMean * fGammaMean / dDispersionGamma;
		
		int maxIter = 500000;
		MonteCarloEstimate estimate = new MonteCarloEstimate();
		Matrix realization;
		for (int i = 0; i < maxIter; i++) {
			realization = new Matrix(1,1);
			realization.m_afData[0][0] = randomGenerator.nextGamma(shape, scale);
			estimate.addRealization(realization);
		}
		double actual = estimate.getVariance().m_afData[0][0];
		@SuppressWarnings("unused")
		double mean = estimate.getMean().m_afData[0][0];
		System.out.println ("Simulated variance = " + actual + "; Expected variance = " + variance);
		Assert.assertEquals("Testing mean for gamma random values", variance, actual, 6E-2);
	}

	@Test
	public void testNegativeBinomialMean() {
		double dispersion = 2;
		double meanDistribution = 1.813311388045523;
		REpiceaRandom randomGenerator = new REpiceaRandom();
		
		double mean = 0;
		int maxIter = 500000;
		double meanFactor = 1d / maxIter;
		for (int i = 0; i < maxIter; i++) {
			double randomDeviate = 0;
			try {
				randomDeviate = randomGenerator.nextNegativeBinomial(meanDistribution, dispersion);
				mean += randomDeviate * meanFactor;
			} catch (Exception e) {
			}

		}
		
		System.out.println ("Simulated mean = " + mean + "; Expected mean = " + meanDistribution);
		Assert.assertEquals("Testing mean for gamma random values", meanDistribution, mean, 1E-2);
	}

	
	@Test
	public void testNegativeBinomialVariance() {
		double dispersion = 2;
		double meanDistribution = 1.813311388045523;
		REpiceaRandom randomGenerator = new REpiceaRandom();
		double variance = meanDistribution + dispersion * meanDistribution * meanDistribution;
		int maxIter = 500000;
		MonteCarloEstimate estimate = new MonteCarloEstimate();
		Matrix realization;
		for (int i = 0; i < maxIter; i++) {
			realization = new Matrix(1,1);
			realization.m_afData[0][0] = randomGenerator.nextNegativeBinomial(meanDistribution, dispersion);
			estimate.addRealization(realization);
		}
		double actual = estimate.getVariance().m_afData[0][0];
		@SuppressWarnings("unused")
		double mean = estimate.getMean().m_afData[0][0];
		System.out.println ("Simulated variance = " + actual + "; Expected variance = " + variance);
		Assert.assertEquals("Testing mean for gamma random values", variance, actual, 1E-1);
	}

}