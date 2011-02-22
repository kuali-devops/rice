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
package org.kuali.rice.kns.datadictionary.validation.processor;


import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.kns.datadictionary.validation.ErrorLevel;
import org.kuali.rice.kns.datadictionary.validation.MockAddress;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;

/**
 * Things this test should check:
 * 
 * 1. city and state entered, but no postal code (success) {@link #testCityStateNoPostalSuccess()}
 * 2. city entered, no state or postal code (failure) {@link #testCityNoStateNoPostalFailure()}
 * 3. postal code entered but no city or state (success) {@link #testPostalNoCityStateSuccess()}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class MustOccurConstraintProcessorTest extends BaseConstraintProcessorTest<MustOccurConstraintProcessor> {

	private MockAddress noPostalCodeAddress = new MockAddress("893 Presidential Ave", "Suite 800", "Washington", "DC", "", "USA", null);
	private MockAddress noStateOrPostalCodeAddress = new MockAddress("893 Presidential Ave", "Suite 800", "Washington", "", "", "USA", null);
	private MockAddress noCityStateAddress = new MockAddress("893 Presidential Ave", "Suite 800", "", "", "12340", "USA", null);
	
	@Test
	public void testCityStateNoPostalSuccess() {
		ConstraintValidationResult result = process(noPostalCodeAddress, null, topLevelConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testCityNoStateNoPostalFailure() {
		ConstraintValidationResult result = process(noStateOrPostalCodeAddress, null, topLevelConstraint);
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testPostalNoCityStateSuccess() {
		ConstraintValidationResult result = process(noCityStateAddress, null, topLevelConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.processor.BaseConstraintProcessorTest#newProcessor()
	 */
	@Override
	protected MustOccurConstraintProcessor newProcessor() {
		return new MustOccurConstraintProcessor();
	}

}
