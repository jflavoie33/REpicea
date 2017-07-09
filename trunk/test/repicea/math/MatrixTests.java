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
package repicea.math;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import repicea.io.FormatField;
import repicea.io.javacsv.CSVField;
import repicea.io.javacsv.CSVWriter;
import repicea.stats.StatisticalUtility;
import repicea.stats.StatisticalUtility.TypeMatrixR;
import repicea.util.ObjectUtility;

/**
 * This test class performs some tests on matrix calculation.
 * @author Mathieu Fortin - September 2013
 */
public class MatrixTests {

	/**
	 * This test is performed on the calculation of the inverse of a blocked matrix.
	 */
	@Test
	public void blockedInversedMatrixTest() {
		
		Matrix mat = new Matrix(9,9);
		mat.m_afData[0][0] = 5.49;
		mat.m_afData[0][4] = 1.85;
		mat.m_afData[1][1] = 3.90;
		mat.m_afData[2][2] = 2.90;
		mat.m_afData[2][3] = 1.02;
		mat.m_afData[2][5] = 0.70;
		mat.m_afData[2][6] = 0.76;
		mat.m_afData[2][7] = 0.77;
		mat.m_afData[2][8] = 0.80;
		mat.m_afData[3][3] = 3.20;
		mat.m_afData[3][5] = 0.89;
		mat.m_afData[3][6] = 0.87;
		mat.m_afData[3][7] = 0.89;
		mat.m_afData[3][8] = 0.93;
		mat.m_afData[4][4] = 4.55;
		mat.m_afData[5][5] = 2.70;
		mat.m_afData[5][6] = 0.66;
		mat.m_afData[5][7] = 0.67;
		mat.m_afData[5][8] = 0.70;
		mat.m_afData[6][6] = 2.69;
		mat.m_afData[6][7] = 0.66;
		mat.m_afData[6][8] = 0.69;
		mat.m_afData[7][7] = 2.70;
		mat.m_afData[7][8] = 0.70;
		mat.m_afData[8][8] = 2.76;
		
		for (int i = 0; i < mat.m_iRows; i++) {
			for (int j = i; j < mat.m_iCols; j++) {
				if (i != j) {
					mat.m_afData[j][i] = mat.m_afData[i][j];
				}
			}
		}
		
		Matrix invMat = mat.getInverseMatrix();
		Matrix ident = mat.multiply(invMat);
		Matrix diff = ident.subtract(Matrix.getIdentityMatrix(ident.m_iCols)).getAbsoluteValue();
		boolean equalToIdentity = !diff.anyElementLargerThan(1E-15);
		Assert.assertEquals(true, equalToIdentity);
	}
	
	/**
	 * This test is performed on the calculation of the inverse of a large symmetric matrix with many zero cells.
	 */
	@Test
	public void inversionWithZeroCellsTest() {
		
		Matrix coordinates = new Matrix(20,1,0,1);
		
		Matrix rMatrix = StatisticalUtility.constructRMatrix(coordinates, 2, 0.2, TypeMatrixR.LINEAR);
		Matrix invMat = rMatrix.getInverseMatrix();
	
		Matrix ident = rMatrix.multiply(invMat);

		Matrix diff = ident.subtract(Matrix.getIdentityMatrix(ident.m_iCols)).getAbsoluteValue();

		boolean equalToIdentity = !diff.anyElementLargerThan(1E-10);
		
		Assert.assertEquals(true, equalToIdentity);
	}

	public void speedTestInversionMatrix(int iMax) throws IOException {
		String filename = ObjectUtility.getPackagePath(getClass()) + "inversionTimes.csv";
		CSVWriter writer = new CSVWriter(new File(filename), false);
		List<FormatField> fields = new ArrayList<FormatField>();
		fields.add(new CSVField("dimension"));
		fields.add(new CSVField("time"));
		writer.setFields(fields);
		
		long startingTime;
		Object[] record = new Object[2];
		int size;
		for (int i = 1; i < iMax; i++) {
			size = i * 10;
			record[0] = size;
			startingTime = System.currentTimeMillis();
			Matrix coordinates = new Matrix(size,1,0,1);
			Matrix rMatrix = StatisticalUtility.constructRMatrix(coordinates, 2, 0.2, TypeMatrixR.LINEAR);
			rMatrix.getInverseMatrix();
			record[1] = (System.currentTimeMillis() - startingTime) * .001;
			writer.addRecord(record);
		}
		writer.close();
	}
	
	public void speedTestMatrixMultiplication() {
		int i = 1000;
		Matrix oMat = Matrix.getIdentityMatrix(i);
		long startingTime;
		startingTime = System.currentTimeMillis();
		oMat.multiply(oMat);
		System.out.println("Elapsed time = " + ((System.currentTimeMillis() - startingTime) * .001));
	}
	public static void main(String[] args) throws IOException {
		MatrixTests test = new MatrixTests();
//		test.speedTestInversionMatrix(100);
		test.speedTestMatrixMultiplication();
	}
	
}
