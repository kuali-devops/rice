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
package edu.samplu.mainmenu.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;


/**
 * tests whether the Category Look UP is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CategoryLookUpIT extends UpgradedSeleniumITBase{
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    
    @Test
    public void testCategoryLookUp() throws Exception {
        waitAndClick("link=Category Lookup");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClick("css=button:contains(earch)");
        Thread.sleep(3000);
        waitForPageToLoad();
        isTextPresent("Actions"); // there are no actions, but the header is the only unique text from searching
// Category's don't have actions (yet)
//        waitAndClick("id=u80");
//        waitForPageToLoad();
//        waitAndClick("id=u86");
//        waitForPageToLoad();
//        selectWindow("null");
//        waitAndClick("xpath=(//input[@name='imageField'])[2]");
//        waitForPageToLoad();
    }
}