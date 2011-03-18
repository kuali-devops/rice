/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;

public class IndicatorYNNullValuesFinder extends KeyValuesBase {

    @Override
	public List<KeyValue> getKeyValues() {
    	List<KeyValue> activeLabels = new ArrayList<KeyValue>();
        activeLabels.add(new ConcreteKeyValue("", ""));
        activeLabels.add(new ConcreteKeyValue("Yes", "Yes"));
        activeLabels.add(new ConcreteKeyValue("No", "No"));
        return activeLabels;
    }

}
