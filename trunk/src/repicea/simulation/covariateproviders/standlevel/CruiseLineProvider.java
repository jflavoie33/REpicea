/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2009-2017 Mathieu Fortin for Rouge-Epicea
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
package repicea.simulation.covariateproviders.standlevel;

/**
 * This class ensures that the plot instance can provide its cruise line.
 * @author Mathieu Fortin - June 2017
 *
 */
public interface CruiseLineProvider {

	/**
	 * This method returns the cruise line ID.  
	 * @return a String
	 */
	public String getCruiseLineID();

}
