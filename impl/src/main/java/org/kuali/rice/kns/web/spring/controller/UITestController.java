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
package org.kuali.rice.kns.web.spring.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.core.util.type.KualiPercent;
import org.kuali.rice.kns.web.spring.form.UITestForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.bo.TravelAccount;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/uitest")
public class UITestController extends UifControllerBase {
	private static final String testViewId = "testView1";

	@Override
    protected UITestForm createInitialForm(HttpServletRequest request) {
        return new UITestForm();
    }

	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UITestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		// populate model for testing
		TravelAccount travelAccount = new TravelAccount();
		travelAccount.setNumber("101");
		travelAccount.setName("Account 101");

		FiscalOfficer fiscalOfficer = new FiscalOfficer();
		fiscalOfficer.setId(new Long(1));

		List<TravelAccount> officerAccounts = new ArrayList<TravelAccount>();

		TravelAccount travelAccount2 = new TravelAccount();
		travelAccount2.setNumber("102");
		travelAccount2.setName("Account 102");
		travelAccount2.setSubAccount("34");
		travelAccount2.setSubAccountName("G34 So");
		travelAccount2.setSubsidizedPercent(new KualiPercent(45.0));
		officerAccounts.add(travelAccount2);

		TravelAccount travelAccount3 = new TravelAccount();
		travelAccount3.setNumber("103");
		travelAccount3.setName("Account 103");
		officerAccounts.add(travelAccount3);

		TravelAccount travelAccount4 = new TravelAccount();
		travelAccount4.setNumber("104");
		travelAccount4.setName("Account 104");
		travelAccount4.setSubAccount("i84n");
		travelAccount4.setSubAccountName("Supplies");
		travelAccount4.setSubsidizedPercent(new KualiPercent(10));
		officerAccounts.add(travelAccount4);

		fiscalOfficer.setAccounts(officerAccounts);
		travelAccount.setFiscalOfficer(fiscalOfficer);

		uiTestForm.setTravelAccount1(travelAccount);
		uiTestForm.setTravelAccount2(travelAccount);
		uiTestForm.setTravelAccount3(travelAccount);

		return getUIFModelAndView(uiTestForm, testViewId, "page1");
	}

	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=save")
	public ModelAndView save(@ModelAttribute("KualiForm") UITestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		return getUIFModelAndView(uiTestForm, testViewId, "page2");
	}
	
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=close")
	public ModelAndView close(@ModelAttribute("KualiForm") UITestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		return getUIFModelAndView(uiTestForm, testViewId, "page1");
	}

}
