/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import java.util.List;

import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * tests the Component section in Rice.
 *
 * @deprecated
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentWDIT extends WebDriverLegacyITBase {
    String docId;
    String componentName;
    String componentCode;

    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Component&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
            
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    
    @Test
    public void testComponentParameter() throws Exception {
        // Create New
        waitAndCreateNew();
        List<String> params;
        params = testCreateNewComponent(docId, "testName" + ITUtil.DTS_TWO, "testCode" + ITUtil.DTS_TWO);
       
        //Lookup
        open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrameIframePortlet();
        params = testLookUpComponent(params.get(0), params.get(1), params.get(2));
   
        //edit
        params = testEditComponent(params.get(0), params.get(1), params.get(2));
        
        //Verify if its edited
        open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrameIframePortlet();
        params = testVerifyEditedComponent(params.get(0), params.get(1), params.get(2));
  
        //copy
        params = testCopyComponent(params.get(0), params.get(1) + "copy", params.get(2) + "copy");
        
        //Verify if its copied
        open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrameIframePortlet();
        testVerifyCopyComponent(params.get(0), params.get(1), params.get(2));
    }
}
