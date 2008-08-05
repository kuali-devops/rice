/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package org.kuali.rice.kew.engine.node;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowServiceErrorException;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
import org.kuali.rice.kew.routetemplate.NetworkIdRoleAttribute;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.XmlHelper;


/**
 * A node which will generate an FYI request to a network ID specified in the document content.
 * 
 * @deprecated Use {@link NetworkIdRoleAttribute} instead
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FYIByNetworkId extends RequestActivationNode {
    private static final Logger LOG = Logger.getLogger(FYIByNetworkId.class);

	public SimpleResult process(RouteContext context, RouteHelper helper)
			throws Exception {

        LOG.debug("processing FYIByNetworkId simple node");
        Long routeHeaderId = context.getDocument().getRouteHeaderId();
        Element rootElement = getRootElement(new StandardDocumentContent(context.getDocument().getDocContent()));
		List fieldElements = XmlHelper.findElements(rootElement, "field");
        ListIterator elementIter = fieldElements.listIterator();
        while (elementIter.hasNext()) {
        	Element field = (Element) elementIter.next();
        	Element version = field.getParentElement();
        	if (version.getAttribute("current").getValue().equals("true")) {
        		LOG.debug("Looking for networkId field:  " + field.getAttributeValue("name"));
               	if (field.getAttribute("name")!= null && field.getAttributeValue("name").equals("networkId")) {
            		LOG.debug("Should send an FYI to netID:  " + field.getChildText("value"));
               		if (field.getChildText("value") != null) {
               			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO(field.getChildText("value")));
               			//WorkflowDocument wfDoc = new WorkflowDocument(new NetworkIdVO(field.getChildText("value")), routeHeaderId);
               			if (!context.isSimulation()) {
                   			KEWServiceLocator.getWorkflowDocumentService().appSpecificRouteDocument(user, context.getDocument(), KEWConstants.ACTION_REQUEST_FYI_REQ, null, "Notification Request", user, "Notification Request", true);
               		}
               			//wfDoc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_FYI_REQ, "Notification Request", new NetworkIdVO(field.getChildText("value")), "Notification Request", true);
                		LOG.debug("Sent FYI using the appSpecificRouteDocumentToUser function to NetworkID:  " + user.getAuthenticationUserId());
                               	break;
               	}
        	}
        }
        }
		return super.process(context, helper);
	}


    private static Element getRootElement(DocumentContent docContent) {
        Element rootElement = null;
        try {
            rootElement = XmlHelper.buildJDocument(docContent.getDocument()).getRootElement();
        } catch (Exception e) {
            throw new WorkflowServiceErrorException("Invalid XML submitted", new ArrayList<Object>());
        }
        return rootElement;
    }


	protected Object getService(String serviceName) {
		return KEWServiceLocator.getService(serviceName);
	}


}
