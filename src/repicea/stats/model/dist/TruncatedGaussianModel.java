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
package repicea.stats.model.dist;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import repicea.math.Matrix;
import repicea.math.ParameterBound;
import repicea.stats.distributions.TruncatedGaussianBound;
import repicea.stats.distributions.TruncatedGaussianBound.TruncatedGaussianBoundCompatible;
import repicea.stats.distributions.utility.GaussianUtility;
import repicea.stats.estimators.Estimator;
import repicea.stats.estimators.MaximumLikelihoodEstimator;
import repicea.stats.estimators.MaximumLikelihoodEstimator.MaximumLikelihoodCompatibleModel;
import repicea.stats.model.AbstractStatisticalModel;
import repicea.stats.model.CompositeLogLikelihood;
import repicea.stats.model.IndividualLogLikelihood;
import repicea.stats.model.SimpleCompositeLogLikelihood;

public class TruncatedGaussianModel extends AbstractStatisticalModel implements MaximumLikelihoodCompatibleModel, TruncatedGaussianBoundCompatible {

	
	private class TruncatedGaussianLogLikelihood implements IndividualLogLikelihood {

		private final Matrix parameters;
		private Matrix yVector;
		
		private TruncatedGaussianLogLikelihood() {
			parameters = new Matrix(2, 1);
		}
		
		@Override
		public void setYVector(Matrix yVector) {
			if (yVector.m_iCols > 1 || yVector.m_iRows > 1)
				throw new InvalidParameterException("The y value should be embedded in a 1x1 matrix!");
			this.yVector = yVector;
		}

		@Override
		public Matrix getYVector() {return yVector;}

		@Override
		public Matrix getPredictionVector() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNumberOfParameters() {return parameters.m_iRows;}

		@Override
		public int getNumberOfVariables() {return 0;}

		@Override
		public Matrix getParameters() {return parameters;}

		@Override
		public void setBounds(int parameterIndex, ParameterBound bound) {}

		@Override
		public void setVariableValue(int variableIndex, double variableValue) {
			throw new InvalidParameterException("The WeibullModel class does not implement variables!");
		}

		@Override
		public void setParameterValue(int parameterIndex, double parameterValue) {
			parameters.setValueAt(parameterIndex, 0, parameterValue);
		}

		@Override
		public void setParameters(Matrix beta) {
			for (int i = 0; i < beta.m_iRows; i++) {
				setParameterValue(i, beta.getValueAt(i, 0));
			}
		}

		@Override
		public void setVariables(Matrix xVector) {
			throw new InvalidParameterException("The WeibullModel class does not implement variables!");
		}

		@Override
		public double getVariableValue(int variableIndex) {
			throw new InvalidParameterException("The WeibullModel class does not implement variables!");
		}

		@Override
		public double getParameterValue(int parameterIndex) {
			return parameters.getValueAt(parameterIndex, 0);
		}
		
		@Override
		public Double getValue() {
			double mu = parameters.getValueAt(0, 0);
			double sigma2 = parameters.getValueAt(1, 0);
			double y = getYVector().getValueAt(0, 0);
			double Z = upperBound.getCdfValue() - lowerBound.getCdfValue();
			double llk = -0.5 * Math.log(sigma2) - 0.5 * Math.log(2 * Math.PI) - 0.5 * (y - mu) * (y -mu) / sigma2 - Math.log(Z);
			double llk2 = Math.log(GaussianUtility.getProbabilityDensity(y, mu, sigma2) / Z);
			return llk;
		}

		@Override
		public Matrix getGradient() {
			double mu = parameters.getValueAt(0, 0);
			double sigma2 = parameters.getValueAt(1, 0);
			double sigma = Math.sqrt(sigma2);
			double y = getYVector().getValueAt(0, 0);
			double Z = upperBound.getCdfValue() - lowerBound.getCdfValue();
			
			double dAlpha_dMu = -1/sigma;
			double dBeta_dMu = -1/sigma;
			double dAlpha_dSigma2 = - 0.5 * lowerBound.getStandardizedValue() / sigma2;
			double dBeta_dSigma2 = - 0.5 * upperBound.getStandardizedValue() / sigma2;

			Matrix gradient = new Matrix(parameters.m_iRows, 0);

			double dL_dMu =  (y - mu)/sigma2 - 
					(upperBound.getPdfValueOnStandardNormal() * dBeta_dMu - 
					 lowerBound.getPdfValueOnStandardNormal() * dAlpha_dMu) / Z;
			gradient.setValueAt(0, 0, dL_dMu);

			double dL_dSigma2 = -0.5/sigma2 + 
					0.5  * (y - mu) * (y -mu) / (sigma2 * sigma2) -
					 (upperBound.getPdfValueOnStandardNormal() * dBeta_dSigma2 - 
					  lowerBound.getPdfValueOnStandardNormal() * dAlpha_dSigma2) / Z;
			gradient.setValueAt(1, 0, dL_dSigma2);
			
			return gradient;
		}

		@Override
		public Matrix getHessian() {
			double mu = parameters.getValueAt(0, 0);
			double sigma2 = parameters.getValueAt(1, 0);
			double sigma = Math.sqrt(sigma2);
			double y = getYVector().getValueAt(0, 0);
			double Z = upperBound.getCdfValue() - lowerBound.getCdfValue();

			double dAlpha_dMu = -1/sigma;
			double dBeta_dMu = -1/sigma;
			double dAlpha_dSigma2 = - 0.5 * lowerBound.getStandardizedValue() / sigma2;
			double dBeta_dSigma2 = - 0.5 * upperBound.getStandardizedValue() / sigma2;
			double d2Alpha_d2Sigma2 = 0.75 * lowerBound.getStandardizedValue() / (sigma2 * sigma2);
			double d2Beta_d2Sigma2 = 0.75 * upperBound.getStandardizedValue() / (sigma2 * sigma2);
			double d2Alpha_dMu_dSigma2 = 0.5 * (sigma2 * sigma);
			double d2Beta_dMu_dSigma2 = 0.5 * (sigma2 * sigma);
			
			
			Matrix hessian = new Matrix(parameters.m_iRows, parameters.m_iRows);
			double d2L_d2Mu = - 1d/sigma2 - 
					(- Math.pow(
							(upperBound.getPdfValueOnStandardNormal() * dBeta_dMu - lowerBound.getPdfValueOnStandardNormal() * dAlpha_dMu) / Z,
							2) 
					+ (-upperBound.getPdfValueOnStandardNormal() * upperBound.getStandardizedValue() * dBeta_dMu * dBeta_dMu - 
					   -lowerBound.getPdfValueOnStandardNormal() * lowerBound.getStandardizedValue() * dAlpha_dMu * dAlpha_dMu) / Z); 
			hessian.setValueAt(0, 0, d2L_d2Mu);	
			
			double d2L_d2Sigma2 = 0.5/(sigma2*sigma2) -  
					(y - mu) * (y -mu) / (sigma2*sigma2*sigma2) -
					(- Math.pow(
							(upperBound.getPdfValueOnStandardNormal() * dBeta_dSigma2 - 
					       lowerBound.getPdfValueOnStandardNormal() * dAlpha_dSigma2) / Z, 2) +
					((-upperBound.getPdfValueOnStandardNormal() * upperBound.getStandardizedValue() * dBeta_dSigma2 * dBeta_dSigma2 
							+ upperBound.getPdfValueOnStandardNormal() * d2Beta_d2Sigma2) -
					 (-lowerBound.getPdfValueOnStandardNormal() * lowerBound.getStandardizedValue() * dAlpha_dSigma2 * dAlpha_dSigma2
							+ lowerBound.getPdfValueOnStandardNormal() * d2Alpha_d2Sigma2)) / Z);
			hessian.setValueAt(1, 1, d2L_d2Sigma2);	
			double d2L_dMu_dSigma2 = - (y - mu) / (sigma2 * sigma2) -
					(-(upperBound.getPdfValueOnStandardNormal() * upperBound.getPdfValueOnStandardNormal() * dBeta_dMu * dBeta_dSigma2 -
						lowerBound.getPdfValueOnStandardNormal() * lowerBound.getPdfValueOnStandardNormal() * dAlpha_dMu * dAlpha_dSigma2) / (Z * Z) +
					 ((-upperBound.getPdfValueOnStandardNormal() * upperBound.getStandardizedValue() * dBeta_dMu * dBeta_dSigma2 +
							 upperBound.getPdfValueOnStandardNormal() * d2Beta_dMu_dSigma2) -
					  (-lowerBound.getPdfValueOnStandardNormal() * lowerBound.getStandardizedValue() * dAlpha_dMu * dAlpha_dSigma2 +
							 lowerBound.getPdfValueOnStandardNormal() * d2Beta_dMu_dSigma2)) / Z);
			hessian.setValueAt(0, 1, d2L_dMu_dSigma2);	
			hessian.setValueAt(1, 0, d2L_dMu_dSigma2);	
			return hessian;
		}
	}
	
	
	private final TruncatedGaussianBound lowerBound;
	private final TruncatedGaussianBound upperBound;
	private final List<Double> values;
	private final SimpleCompositeLogLikelihood cLL;
	private final IndividualLogLikelihood individualLLK;
	
	public TruncatedGaussianModel(List<Double> values, double lowBound, double uppBound, Matrix startingValues) {
		super();
		this.values = new ArrayList<Double>();
		this.values.addAll(values);
		this.individualLLK = new TruncatedGaussianLogLikelihood();
		cLL = new SimpleCompositeLogLikelihood(individualLLK, new Matrix(values));
		setParameters(startingValues);
		try {
			setModelDefinition("pdf(y) = phi((y-mu)/sigma) / (sigma * [PHI((b-mu)/sigma) - PHI((a-mu)/sigma))]");
		} catch (Exception e) {}
		if (uppBound <= lowBound) {
			throw new InvalidParameterException("The uppBound parameter must be larger than the lowBound parameter!");
		}
		this.lowerBound = new TruncatedGaussianBound(this, false);
		this.lowerBound.setBoundValue(new Matrix(1,1,lowBound, 0));
		this.upperBound = new TruncatedGaussianBound(this, true);
		this.upperBound.setBoundValue(new Matrix(1,1,uppBound, 0));
	}

	public TruncatedGaussianModel(List<Double> values, double lowBound, double uppBound) {
		this(values, lowBound, uppBound, null);
	}
	
	@Override
	public void setParameters(Matrix beta) {
		if (beta == null) {
			Matrix betaDefault = new Matrix(2,1);
			betaDefault.setValueAt(0, 0, 1);
			betaDefault.setValueAt(1, 0, 1);
			individualLLK.setParameters(betaDefault);
		} else {
			individualLLK.setParameters(beta);
		}
	}

	@Override
	public Matrix getParameters() {
		return individualLLK.getParameters();
	}

	@Override
	protected Estimator instantiateDefaultEstimator() {return new MaximumLikelihoodEstimator(this);}

	@Override
	public boolean isInterceptModel() {return false;}

	@Override
	public List<String> getEffectList() {
		List<String> effectList = new ArrayList<String>(); 
		effectList.add("Mu parameter");
		effectList.add("Sigma2 parameter");
		return effectList;
	}

	@Override
	public int getNumberOfObservations() {return values.size();}

	@Override
	public double getConvergenceCriterion() {
		return 1E-8;
	}

	@Override
	public CompositeLogLikelihood getCompleteLogLikelihood() {return cLL;}

	
	@Override
	public String toString() {
		return "Truncated distribution model";
	}

	@Override
	public double getMuValue() {
		return individualLLK.getParameterValue(0);
	}

	@Override
	public double getSigma2Value() {
		return individualLLK.getParameterValue(1);
	}

}
