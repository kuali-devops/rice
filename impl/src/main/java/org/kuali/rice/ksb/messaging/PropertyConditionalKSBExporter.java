/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.messaging;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.ConfigContext;
import org.springframework.beans.factory.BeanInitializationException;

/**
 * A KSBExporter which only exports the service if the specified property is set
 * to true.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PropertyConditionalKSBExporter extends KSBExporter {

	private List<String> exportIf = new ArrayList<String>();
	private List<String> exportUnless = new ArrayList<String>();
	private boolean exportIfPropertyNotSet = true;

	public void afterPropertiesSet() throws Exception {
		if (shouldRemoteThisService()) {
			super.afterPropertiesSet();
		}
	}
	
	protected boolean shouldRemoteThisService() throws Exception {
		if (exportIf.isEmpty() && exportUnless.isEmpty()) {
			return true;
		}
		boolean remoteThisService = false;
		String serviceValue = null;
		// Check the value in the clients config file for services in the list
		// of property named 'exportIf' loaded by Spring.
		// if any are ="true" then set boolean to true and exit loop, so far the
		// service will be published.
		for (String expIf : exportIf) {
			serviceValue = ConfigContext.getCurrentContextConfig().getProperty(expIf);
			// if any are true, set boolean and exit loop.
			if (!StringUtils.isBlank(serviceValue)) {
				remoteThisService = new Boolean(serviceValue);
				if (remoteThisService) {
					break;
				}
			} else if (exportIfPropertyNotSet) {
				remoteThisService = true;
				break;
			}
		}
		// Check a second list, if any are ="true" DON"T publish the service.
		for (String expUnless : exportUnless) {
			serviceValue = ConfigContext.getCurrentContextConfig()
					.getProperty(expUnless);
			// if any are true, set boolean and exit loop.
			if (!StringUtils.isBlank(serviceValue)) {
				remoteThisService = new Boolean(serviceValue);
				if (remoteThisService) {
					remoteThisService = new Boolean("false");
					break;
				}
			}
		}
		return remoteThisService;
	}

	public List getExportIf() {
		return this.exportIf;
	}

	public void setExportIf(List exportIf) throws BeanInitializationException {
		this.exportIf = exportIf;
	}

	public List getExportUnless() {
		return this.exportUnless;
	}

	public void setExportUnless(List exportUnless)
			throws BeanInitializationException {
		this.exportUnless = exportUnless;
	}

	public boolean isExportIfPropertyNotSet() {
		return this.exportIfPropertyNotSet;
	}

	public void setExportIfPropertyNotSet(boolean exportIfPropertyNotSet) {
		this.exportIfPropertyNotSet = exportIfPropertyNotSet;
	}

}