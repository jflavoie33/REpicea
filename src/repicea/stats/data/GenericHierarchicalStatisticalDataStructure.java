/*
 * This file is part of the repicea-statistics library.
 *
 * Copyright (C) 2009-2012 Mathieu Fortin for Rouge-Epicea
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
package repicea.stats.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import repicea.math.Matrix;
import repicea.util.ObjectUtility;

/**
 * This class is the basic class for hierarchical data structure. 
 * @author Mathieu Fortin - October 2011
 */
public class GenericHierarchicalStatisticalDataStructure extends GenericStatisticalDataStructure implements HierarchicalStatisticalDataStructure {
	
	protected Map<String, DataBlock> hierarchicalStructure;		// from outer to inner levels
	protected Map<String, List<String>> randomEffectsSpecifications;

	
	/**
	 * The string represents the hierarchical level.
	 */
	protected Map<String, Matrix> matricesZ;

	/**
	 * General constructor. To be defined in derived class.
	 * @param dataSet a DataSet instance
	 */
	public GenericHierarchicalStatisticalDataStructure(DataSet dataSet) {
		super(dataSet);
		hierarchicalStructure = new LinkedHashMap<String, DataBlock>();
		matricesZ = new LinkedHashMap<String, Matrix>();
	}
	

	
	@Override
	public void constructMatrices(String modelDefinition) throws StatisticalDataException {
		modelDefinition = extractRandomEffects(modelDefinition);
		super.constructMatrices(modelDefinition);
	}

	/**
	 * This method filters the model definition for sub strings in parentheses. These substrings are then
	 * recorded as random effects and taken out of the original model definition.
	 * @param modelDefinition the model definition
	 * @return the model definition without the random effects
	 * @throws StatisticalDataException 
	 */
	private String extractRandomEffects(String modelDefinition) throws StatisticalDataException {
		List<String> occurrences = ObjectUtility.extractSequences(modelDefinition, "(", ")");
		String newModelDefinition = occurrences.remove(0);
		if (occurrences.size() > 1) {
			throw new StatisticalDataException("The model definition only supports one random effect statement!");
		}
		
		if (!occurrences.isEmpty()) {
			for (String randomEffect : occurrences) {
				recordRandomEffects(randomEffect);
			}
			
			List<String> hierarchicalLevels = new ArrayList<String>();
			for (String level : getRandomEffectsSpecifications().keySet()) {
				hierarchicalLevels.add(level);
			}
			
			sortDataAccordingToRandomEffects(hierarchicalLevels);		// we sort the data before setting the hierarchical structure
			
			setHierarchicalStructureLevel(hierarchicalLevels);

			setRandomEffectStructure();
		}

		
		return newModelDefinition;
	}
	
	
	
	
	
	private void sortDataAccordingToRandomEffects(List<String> hierarchicalLevels) {
		List<Integer> indexList = new ArrayList<Integer>();
		for (String levelName : hierarchicalLevels) {
			indexList.add(dataSet.getIndexOfThisField(levelName));
		}
		dataSet.sortObservations(indexList);
	}



	protected void recordRandomEffects(String effectName) throws StatisticalDataException {
		String randomEffectSpec = effectName.replace("(","").replace(")","");
		List<String> randomEffectComponents = ObjectUtility.decomposeUsingToken(randomEffectSpec, "|");
		if (randomEffectComponents.size() != 2) {
			throw new StatisticalDataException("The random effect " + effectName + " is not properly defined!");
		}
		
		List<String> hierarchicalLevels = ObjectUtility.decomposeUsingToken(randomEffectComponents.get(1), "/");
		for (String level : hierarchicalLevels) {
			if (dataSet.getIndexOfThisField(level) == -1) {
				throw new StatisticalDataException("Field " + level + "does not exist!");
			}
		}
		
		List<String> randomEffectsForTheseLevels = ObjectUtility.decomposeUsingToken(randomEffectComponents.get(0), "+");
		for (String randomEffect : randomEffectsForTheseLevels) {
			if (!randomEffect.equals("1")) {
				if (dataSet.getIndexOfThisField(randomEffect) == -1) {
					throw new StatisticalDataException("Field " + randomEffect + "does not exist!");
				}
			}
		}
		
		List<String> effects;
		for (String level : hierarchicalLevels) {
			if (!getRandomEffectsSpecifications().containsKey(level)) {
				getRandomEffectsSpecifications().put(level, new ArrayList<String>());
			} 
			effects = getRandomEffectsSpecifications().get(level);
			for (String effect : randomEffectsForTheseLevels) {
				if (!effects.contains(effect)) {
					effects.add(effect);
				}
			}
		}
	}
	
	private Map<String, List<String>> getRandomEffectsSpecifications() {
		if (randomEffectsSpecifications == null) {
			randomEffectsSpecifications = new LinkedHashMap<String, List<String>>();
		}
		return randomEffectsSpecifications;
	}
	
	protected void setRandomEffectStructure() throws StatisticalDataException {
		// FIXME it works only for covariates and not for class effect such as species for instance
		matricesZ.clear();
		Matrix matrixZ;
		if (!getRandomEffectsSpecifications().isEmpty()) {
			for (String level : getRandomEffectsSpecifications().keySet()) {
//				int indexOfThisLevel = dataSet.getIndexOfThisField(level);
				List<String> effects = getRandomEffectsSpecifications().get(level);
				matrixZ = new Matrix(getNumberOfObservations(), effects.size());
				for (int i = 0; i < getNumberOfObservations(); i++) {
					for (int j = 0; j < effects.size(); j++) {
						if (effects.get(j).equals("1")) {
							matrixZ.m_afData[i][j] = 1;
						} else {
							String effectName = effects.get(j);
							int indexOfEffectName = dataSet.getIndexOfThisField(effectName);
							matrixZ.m_afData[i][j] = (Double) dataSet.getValueAt(i, indexOfEffectName);
						}
					}
				}
				matricesZ.put(level, matrixZ);
			}
		}
	}

	
	@Override
	public Set<String> getHierarchicalStructureLevel() {return hierarchicalStructure.keySet();}

	@Override
	public Map<String, DataBlock> getHierarchicalStructure() {return hierarchicalStructure;}

	@Override
	public void setHierarchicalStructureLevel(List<String> hierarchicalStructureLevels) throws StatisticalDataException {
		hierarchicalStructure.clear();
		for (int i = 0; i < getNumberOfObservations(); i++) {
			DataBlock currentBlock = null;
			DataBlock parentBlock = null;
			for (int levelIndex = 0; levelIndex < hierarchicalStructureLevels.size(); levelIndex++) {
				String level = hierarchicalStructureLevels.get(levelIndex);
				
				int index = dataSet.getIndexOfThisField(level);	// first check if the field exists
				if (index < 0) {
					throw new StatisticalDataException("Error : This field is not part of the data set : " + level);
				}
				
				String levelFieldValue = dataSet.getValueAt(i, index).toString();

				if (levelIndex == 0) {
					if (!hierarchicalStructure.containsKey(levelFieldValue)) {
						hierarchicalStructure.put(levelFieldValue, new DataBlock(level, levelFieldValue));
					}
					currentBlock = hierarchicalStructure.get(levelFieldValue);
				} else {
					parentBlock = currentBlock;
					if (!parentBlock.containsKey(levelFieldValue)) {
						parentBlock.put(levelFieldValue, new DataBlock(level, levelFieldValue));
					}
					currentBlock = parentBlock.get(levelFieldValue);
				}
				
				currentBlock.addIndex(i);
				
			}
			
		}
	}
	
	
	@Override
	public boolean isThereAnyHierarchicalStructure() {return !hierarchicalStructure.isEmpty();}

	
	@Override
	public Map<String, Matrix> getMatrixZ() {
		return matricesZ;
	}

}