package org.kuali.rice.kns.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum DataType {
	STRING(String.class), DATE(Date.class), TRUNCATED_DATE(Date.class), BOOLEAN(Boolean.class), INTEGER(Integer.class), FLOAT(Float.class), DOUBLE(Double.class), LONG(Long.class), COMPLEX(Object.class);
	
	private Class<?> type;
	
	private DataType(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
}
