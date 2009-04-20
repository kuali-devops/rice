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
package org.kuali.rice.ken;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ken.test.KENTestCase;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;


/**
 * Tests integration with KEW
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@BaselineMode(Mode.ROLLBACK)
public class KEWIntegrationTest extends KENTestCase {

	@Test
    public void testKEWServicesAreAccessible() throws Exception {
        
        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
        assertNotNull(identityManagementService);
        LOG.info("Default KIM IdentityManagementService: " + identityManagementService);

        KSBXMLService notification = (KSBXMLService) GlobalResourceLoader.getService(new QName("KEN", "sendNotificationKewXmlService"));
        assertNotNull(notification);
        // XmlIngesterService is = SpringServiceLocator..getXmlIngesterService();
        // check that the quickstart user is present
        //assertNotNull(userService.getWorkflowUser(new WorkflowUserId("quickstart")));
    }
}
