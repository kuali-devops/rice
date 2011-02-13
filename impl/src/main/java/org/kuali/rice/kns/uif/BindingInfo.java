/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.uif;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.container.View;

/**
 * Provides binding configuration for an DataBinding component (attribute or
 * collection)
 * 
 * <p>
 * From the binding configuration the binding path is determined (if not
 * manually set) and used to set the path in the UI or to get the value from the
 * model
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BindingInfo implements Serializable {
	private static final long serialVersionUID = -7389398061672136091L;

	private boolean bindToForm;

	private String bindingName;
	private String bindByNamePrefix;
	private String bindingObjectPath;

	private String bindingPath;

	public BindingInfo() {
		bindToForm = false;
	}

	public void setDefaults(View view, Component component) {
		if (StringUtils.isBlank(bindingName)) {
			bindingName = component.getName();
		}

		if (StringUtils.isBlank(bindingObjectPath)) {
			bindingObjectPath = view.getDefaultBindingObjectPath();
		}
	}

	/**
	 * Path to the property on the model the component binds to. Uses standard
	 * dot notation for nested properties. If the binding path was manually set
	 * it will be returned as it is, otherwise the path will be formed by using
	 * the binding object path and the bind prefix
	 * 
	 * <p>
	 * e.g. Property name 'foo' on a model would have binding path "foo", while
	 * property name 'name' of the nested model property 'account' would have
	 * binding path "account.name"
	 * </p>
	 * 
	 * @return String binding path
	 */
	public String getBindingPath() {
		if (StringUtils.isNotBlank(bindingPath)) {
			return bindingPath;
		}

		String formedBindingPath = "";

		if (!bindToForm && StringUtils.isNotBlank(bindingObjectPath)) {
			formedBindingPath = bindingObjectPath + ".";
		}

		if (StringUtils.isNotBlank(bindByNamePrefix)) {
			formedBindingPath += bindByNamePrefix + "." + bindingName;
		}
		else {
			formedBindingPath += bindingName;
		}

		return formedBindingPath;
	}

	/**
	 * Setter for the binding path. Can be left blank in which the path will be
	 * determined from the binding configuration
	 * 
	 * @param bindingPath
	 */
	public void setBindingPath(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	/**
	 * Indicates whether the component binds directly to the form (that is its
	 * bindingName gives a property available through the form), or whether is
	 * binds through a nested form object. If bindToForm is false, it is assumed
	 * the component binds to the object given by the form property whose path
	 * is configured by bindingObjectPath.
	 * 
	 * @return boolean true if component binds directly to form, false if it
	 *         binds to a nested object
	 */
	public boolean isBindToForm() {
		return this.bindToForm;
	}

	/**
	 * Setter for the bind to form indicator
	 * 
	 * @param bindToForm
	 */
	public void setBindToForm(boolean bindToForm) {
		this.bindToForm = bindToForm;
	}

	/**
	 * Gives the name of the property that the component binds to. The name can
	 * be nested but not the full path, just from the parent object or in the
	 * case of binding directly to the form from the form object
	 * 
	 * <p>
	 * If blank this will be set from the name field of the component
	 * </p>
	 * 
	 * @return String name of the bind property
	 */
	public String getBindingName() {
		return this.bindingName;
	}

	/**
	 * Setter for the bind property name
	 * 
	 * @param bindingName
	 */
	public void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}

	/**
	 * Prefix that will be used to form the binding path from the component
	 * name. Typically used for nested collection properties
	 * 
	 * @return String binding prefix
	 */
	public String getBindByNamePrefix() {
		return this.bindByNamePrefix;
	}

	/**
	 * Setter for the prefix to use for forming the binding path by name
	 * 
	 * @param bindByNamePrefix
	 */
	public void setBindByNamePrefix(String bindByNamePrefix) {
		this.bindByNamePrefix = bindByNamePrefix;
	}

	/**
	 * For attribute fields that do not belong to the default form object (given
	 * by the view), this field specifies the path to the object (on the form)
	 * the attribute does belong to.
	 * 
	 * <p>
	 * e.g. Say we have an attribute field with property name 'number', that
	 * belongs to the object given by the 'account' property on the form. The
	 * form object path would therefore be set to 'account'. If the property
	 * belonged to the object given by the 'document.header' property of the
	 * form, the binding object path would be set to 'document.header'. Note if
	 * the binding object path is not set for an attribute field (or any
	 * <code>DataBinding</code> component), the binding object path configured
	 * on the <code>View</code> will be used (unless bindToForm is set to true,
	 * where is assumed the property is directly available from the form).
	 * </p>
	 * 
	 * @return String path to object from form
	 */
	public String getBindingObjectPath() {
		return this.bindingObjectPath;
	}

	/**
	 * Setter for the object path on the form
	 * 
	 * @param bindingObjectPath
	 */
	public void setBindingObjectPath(String bindingObjectPath) {
		this.bindingObjectPath = bindingObjectPath;
	}

}
