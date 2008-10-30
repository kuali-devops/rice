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
package org.kuali.rice.kew.routeheader;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.web.session.UserSession;


/**
 * An extension of {@link DocumentRouteHeaderValue} which is mapped to OJB to help
 * with optimization of the loading of a user's Action List.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_DOC_HDR_T")
public class DocumentRouteHeaderValueActionListExtension extends DocumentRouteHeaderValue {

	private static final long serialVersionUID = 8458532812557846684L;

    @Transient
	private WorkflowUser actionListInitiatorUser = null;

    public WorkflowUser getActionListInitiatorUser() {
        return actionListInitiatorUser;
    }

    public void setActionListInitiatorUser(WorkflowUser actionListInitiatorUser) {
        this.actionListInitiatorUser = actionListInitiatorUser;
    }

    /**
     * Gets the initiator name, masked appropriately if restricted.
     */
    public String getInitiatorName() {
    	return UserUtils.getTransposedName(UserSession.getAuthenticatedUser(), getActionListInitiatorUser());
    }

}

