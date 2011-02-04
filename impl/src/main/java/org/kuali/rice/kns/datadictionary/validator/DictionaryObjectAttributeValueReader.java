/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.datadictionary.validator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.kns.dto.Constrained;

/**
 * This class allows a dictionary object to expose information about its fields / attributes, including the values of
 * those fields, with some guidance from the DataDictionaryEntry object. 
 * 
 * @author James Renfro, University of Washington 
 */
public class DictionaryObjectAttributeValueReader extends BaseAttributeValueReader {

	protected Map<String, Class<?>> attributeTypeMap;
	protected Map<String, Object> attributeValueMap;

	protected Object object;
	protected DataDictionaryEntry entry;
	protected String entryName;

	protected Map<String, PropertyDescriptor> beanInfo;
	
	public DictionaryObjectAttributeValueReader(Object object, String entryName, DataDictionaryEntry entry) {
		this.object = object;
		this.entry = entry;
		this.entryName = entryName;

		if (object != null)
			this.beanInfo = getBeanInfo(object.getClass());
		
		this.attributeTypeMap = new HashMap<String, Class<?>>();
		this.attributeValueMap = new HashMap<String, Object>();
	}
	
	@Override
	public Constrained getDefinition(String attributeName) {
		return entry != null ? entry.getAttributeDefinition(attributeName) : null;
	}
	
	@Override
	public List<Constrained> getDefinitions() {
		if (entry instanceof DataDictionaryEntryBase) {
			DataDictionaryEntryBase entryBase = (DataDictionaryEntryBase)entry;
			List<Constrained> definitions = new ArrayList<Constrained>();
			List<AttributeDefinition> attributeDefinitions = entryBase.getAttributes();
			definitions.addAll(attributeDefinitions);
			return definitions;
		}
		
		return null;
	}
	
	@Override
	public DataDictionaryEntry getEntry() {
		return entry;
	}
	
	@Override
	public String getPath() {
		return entryName != null ? entryName : "";
	}

	@Override
	public Class<?> getType(String attributeName) {
		Class<?> attributeType = attributeTypeMap != null ? attributeTypeMap.get(attributeName) : null;
		
		if (attributeType != null)
			return attributeType;
		
		PropertyDescriptor propertyDescriptor = beanInfo.get(attributeName);
		attributeType = propertyDescriptor.getPropertyType();
		if (attributeType != null)
			attributeTypeMap.put(attributeName, attributeType);
		
		return attributeType;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> X getValue(String attributeName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		X attributeValue = (X) attributeValueMap.get(attributeName);
		
		if (attributeValue != null)
			return attributeValue;
		
		PropertyDescriptor propertyDescriptor = beanInfo.get(attributeName);
		Method readMethod = propertyDescriptor.getReadMethod();
		
		attributeValue = (X) readMethod.invoke(object);
		
		if (attributeValue != null)
			attributeValueMap.put(attributeName, attributeValue);
		
		//			JLR : KS has code to handle dynamic attributes -- not sure whether this is really needed anymore if we're actually relying on types
		//            // Extract dynamic attributes
		//            if(DYNAMIC_ATTRIBUTE.equals(propName)) {
		//                dataMap.putAll((Map<String, String>)value);
		//            } else {
		//				dataMap.put(propName, value);
		//            }
		
		return attributeValue;
	}
	
	@Override
	public boolean hasField(String attributeName) {
		return getType(attributeName) != null;
	}

	private Map<String, PropertyDescriptor> getBeanInfo(Class<?> clazz) {
		Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo
		.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			properties.put(propertyDescriptor.getName(), propertyDescriptor);
		}
		return properties;
	}


}