/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.web.struts.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementDocumentFormBase;
import org.kuali.rice.kim.web.struts.form.IdentityManagementGroupDocumentForm;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IdentityManagementGroupInquiry extends IdentityManagementBaseInquiryAction {
	private static final Logger LOG = Logger.getLogger(IdentityManagementGroupInquiry.class);	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementBaseInquiryAction#loadKimObject(javax.servlet.http.HttpServletRequest, org.kuali.rice.kim.web.struts.form.IdentityManagementDocumentFormBase)
	 */
	@Override
	protected void loadKimObject(HttpServletRequest request, IdentityManagementDocumentFormBase form) {
        IdentityManagementGroupDocumentForm groupDocumentForm = (IdentityManagementGroupDocumentForm) form;
        String groupId = request.getParameter(KimConstants.PrimaryKeyConstants.GROUP_ID);
        
        GroupInfo group = null;
        if (StringUtils.isNotEmpty(groupId)) {
        	group = KIMServiceLocator.getGroupService().getGroupInfo(groupId);
        } else {
        	String namespaceCode = request.getParameter(KimConstants.UniqueKeyConstants.NAMESPACE_CODE);
        	String groupName = request.getParameter(KimConstants.UniqueKeyConstants.GROUP_NAME);
        	
        	if (!StringUtils.isBlank(namespaceCode) && !StringUtils.isBlank(groupName)) {
        		group = KIMServiceLocator.getGroupService().getGroupInfoByName(namespaceCode, groupName);
            }
        }
        if (group != null) {
        	getUiDocumentService().loadGroupDoc(groupDocumentForm.getGroupDocument(), group);
        } else {
        	LOG.error("No records found for Group Inquiry.");
            GlobalVariables.getMessageMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
        }
	}
	
}