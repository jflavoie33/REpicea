/*
 * Capsis - Computer-Aided Projections of Strategies in Silviculture
 *
 * Authors: M. Fortin and J-F Lavoie - Canadian Forest Service 
 * Copyright (C) 2020-21 Her Majesty the Queen in right of Canada
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package repicea.simulation.metamodel;

import java.util.List;

import repicea.math.Matrix;
import repicea.stats.data.DataSet;

/**
 * Handle the result of a simulation performed through the ExtScript class
 * @author Mathieu Fortin - January 2021
 */
public class ScriptResult {
	
	final DataSet dataset;
	final List<String> outputTypes;
	final Matrix y;
	final Matrix modelVariance;
	final Matrix samplingVariance;
	final int nbRealizations;
	
	/**
	 * Constructor.
	 * @param nbRealizations
	 * @param dataset
	 * @param outputTypes
	 * @param y
	 * @param modelVariance
	 * @param samplingVariance
	 */
	public ScriptResult(int nbRealizations, DataSet dataset, List<String> outputTypes, Matrix y, Matrix modelVariance, Matrix samplingVariance) {
		this.nbRealizations = nbRealizations;
		this.dataset = dataset;
		this.outputTypes = outputTypes;
		this.y = y;
		this.modelVariance = modelVariance;
		this.samplingVariance = samplingVariance;
	}

	public int getNbRealizations() {return nbRealizations;}
	
	public DataSet getDataSet() {return dataset;}
	
	public List<String> getOutputTypes() {return this.outputTypes;}

	public Matrix getMeanPredictedValues() {return y;}
	
	public Matrix getModelVariance() {return modelVariance;}
	
	public Matrix getSamplingVariance() {return samplingVariance;}
	
	public Matrix getTotalVariance() {return getModelVariance().add(getSamplingVariance());}
	
	public Matrix getTotalVarianceWithoutGroupCovariance() {
		Matrix totalVariance = getTotalVariance();
		
		int outputTypeIndex = getDataSet().getFieldNames().indexOf("OutputType");
		for (int i = 0; i < totalVariance.m_iRows; i++) {
			for (int j = 0; j < totalVariance.m_iRows; j++) {
				if (i != j) {
					Object valueI = getDataSet().getObservations().get(i).toArray()[outputTypeIndex];
					Object valueJ = getDataSet().getObservations().get(j).toArray()[outputTypeIndex];
					if (!valueI.equals(valueJ)) {
						totalVariance.setValueAt(i, j, 0d);
					}
				}
			}
		}
		return totalVariance;
	}

	public boolean isCompatible(ScriptResult result) {
		if (dataset.getFieldNames().equals(result.dataset.getFieldNames())) {
			if (dataset.getFieldTypes().equals(this.dataset.getFieldTypes())) {
				if (outputTypes.equals(result.outputTypes)) {
					return true;
				}
			}
		}
		return false;
	}

}