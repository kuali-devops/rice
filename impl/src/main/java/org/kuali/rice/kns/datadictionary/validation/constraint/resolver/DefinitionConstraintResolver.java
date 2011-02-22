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
package org.kuali.rice.kns.datadictionary.validation.constraint.resolver;

import java.util.Collections;
import java.util.List;

import org.kuali.rice.kns.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.kns.datadictionary.validation.capability.LengthConstrainable;
import org.kuali.rice.kns.datadictionary.validation.constraint.Constraint;

/**
 * An object that returns the constrainable definition itself as a list for a definition implementing the capability {@link Constrainable}.
 * This definition must also implement the interface {@link Constraint}, or a ClassCastException will be thrown. 
 * 
 * An example is {@link LengthConstrainable}, where members of the definition itself need to be made available to the ConstraintProcessor.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DefinitionConstraintResolver<T extends Constrainable> implements ConstraintResolver<T> {

	@Override
	public <C extends Constraint> List<C> resolve(T definition) throws ClassCastException {
		if (definition instanceof Constraint) {
			@SuppressWarnings("unchecked")
			C constraint = (C)definition;
			return Collections.singletonList(constraint);
		}
		throw new ClassCastException("DefinitionConstraintResolver can only be used for a definition that implements both Constraint and Constrainable, or derives from a class that does.");
	}
	
}
