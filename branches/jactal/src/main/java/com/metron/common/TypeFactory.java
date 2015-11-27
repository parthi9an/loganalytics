package com.metron.common;

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class TypeFactory {

	/**
	 * 
	 * @param typeName
	 * @return
	 */
	public static OType getOTypeByName(String typeName) {
		if (typeName.equalsIgnoreCase("integer")) {
			return OType.INTEGER;
		} else if (typeName.equalsIgnoreCase("float")) {
			return OType.FLOAT;
		} else if (typeName.equalsIgnoreCase("datetime")) {
			return OType.DATETIME;
		} else if (typeName.equalsIgnoreCase("date")) {
			return OType.DATE;
		}
		return OType.STRING;
	}

	/**
	 * 
	 * @param indexTypeName
	 * @return
	 */
	public static INDEX_TYPE getIndexTypeByName(String indexTypeName) {
		if (indexTypeName.equalsIgnoreCase("unique")) {
			return INDEX_TYPE.UNIQUE;
		} else if (indexTypeName.equalsIgnoreCase("notunique")) {
			return INDEX_TYPE.NOTUNIQUE;
		}
		return INDEX_TYPE.UNIQUE;
	}
}
