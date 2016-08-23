/*
 * This file is part of the repicea-util library.
 *
 * Copyright (C) 2009-2012 Mathieu Fortin for Rouge Epicea.
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
package repicea.serial.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import repicea.lang.reflect.ReflectUtility;

/**
 * The XmlEntry is the basic representation of a field in an object.
 * @author Mathieu Fortin - November 2012
 */
@XmlType
@XmlRootElement
final class XmlEntry {
	
	@XmlElement
	String fieldName;
	
	@XmlElement
	Object value;
	
	XmlEntry() {}
	
	XmlEntry(XmlMarshaller marshaller, String fieldName, Object value) {
		this.fieldName = fieldName;
		if (value != null) {
			if (!value.getClass().isPrimitive() && !ReflectUtility.PrimitiveWrappers.contains(value.getClass())) {			// not a primitive
//				if (!value.getClass().equals(String.class)) {		// not a String
//					if (value.getClass().getSuperclass() != null) {	// not a simple Object instance
//						value = marshaller.marshall(value);
//					}
//				}
				if (!XmlMarshallingUtilities.isStringOrSimpleObject(value)) {
					value = marshaller.marshall(value);
				}
			}
		}
		
		this.value = value;
	}
	
	@Override
	public String toString() {
		String valueString;
		if (value == null) {
			valueString = "null";
		} else {
			valueString = value.toString();
		}
		return fieldName + "; " + valueString;
	}


}
