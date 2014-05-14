/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Encapsulates a set of statically generated (typically during startup)
 * DataDictionary indexes 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionaryIndex implements Runnable {
	private static final Log LOG = LogFactory.getLog(DataDictionaryIndex.class);
	
	private DefaultListableBeanFactory ddBeans;
	
	// keyed by BusinessObject class
	private Map<String, BusinessObjectEntry> businessObjectEntries;
	// keyed by documentTypeName
	private Map<String, DocumentEntry> documentEntries;
	// keyed by other things
	private Map<Class, DocumentEntry> documentEntriesByBusinessObjectClass;
	private Map<Class, DocumentEntry> documentEntriesByMaintainableClass;
	private Map<String, DataDictionaryEntry> entriesByJstlKey;

	// keyed by a class object, and the value is a set of classes that may block the class represented by the key from inactivation 
	private Map<Class, Set<InactivationBlockingMetadata>> inactivationBlockersForClass;

	public DataDictionaryIndex(DefaultListableBeanFactory ddBeans) {
		this.ddBeans = ddBeans;
	}

	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		return this.businessObjectEntries;
	}

	public Map<String, DocumentEntry> getDocumentEntries() {
		return this.documentEntries;
	}

	public Map<Class, DocumentEntry> getDocumentEntriesByBusinessObjectClass() {
		return this.documentEntriesByBusinessObjectClass;
	}

	public Map<Class, DocumentEntry> getDocumentEntriesByMaintainableClass() {
		return this.documentEntriesByMaintainableClass;
	}

	public Map<String, DataDictionaryEntry> getEntriesByJstlKey() {
		return this.entriesByJstlKey;
	}

	public Map<Class, Set<InactivationBlockingMetadata>> getInactivationBlockersForClass() {
		return this.inactivationBlockersForClass;
	}

	private void buildDDIndicies() {
        // primary indices
        businessObjectEntries = new HashMap<String, BusinessObjectEntry>();
        documentEntries = new HashMap<String, DocumentEntry>();

        // alternate indices
        documentEntriesByBusinessObjectClass = new HashMap<Class, DocumentEntry>();
        documentEntriesByMaintainableClass = new HashMap<Class, DocumentEntry>();
        entriesByJstlKey = new HashMap<String, DataDictionaryEntry>();
        
        // loop over all beans in the context
        Map<String,BusinessObjectEntry> boBeans = ddBeans.getBeansOfType(BusinessObjectEntry.class);
        for ( BusinessObjectEntry entry : boBeans.values() ) {
            //String entryName = (entry.getBaseBusinessObjectClass() != null) ? entry.getBaseBusinessObjectClass().getName() : entry.getBusinessObjectClass().getName();
            if ((businessObjectEntries.get(entry.getJstlKey()) != null) 
                    && !((BusinessObjectEntry)businessObjectEntries.get(entry.getJstlKey())).getBusinessObjectClass().equals(entry.getBusinessObjectClass())) {
                throw new DataDictionaryException(new StringBuffer("Two business object classes may not share the same jstl key: this=").append(entry.getBusinessObjectClass()).append(" / existing=").append(((BusinessObjectEntry)businessObjectEntries.get(entry.getJstlKey())).getBusinessObjectClass()).toString());
            }

            businessObjectEntries.put(entry.getBusinessObjectClass().getName(), entry);
            businessObjectEntries.put(entry.getBusinessObjectClass().getSimpleName(), entry);
            // If a "base" class is defined for the entry, index the entry by that class as well.
            if (entry.getBaseBusinessObjectClass() != null) {
                businessObjectEntries.put(entry.getBaseBusinessObjectClass().getName(), entry);
                businessObjectEntries.put(entry.getBaseBusinessObjectClass().getSimpleName(), entry);
            }
            entriesByJstlKey.put(entry.getJstlKey(), entry);
        }
        Map<String,DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
        for ( DocumentEntry entry : docBeans.values() ) {
            String entryName = entry.getDocumentTypeName();

            if ((entry instanceof TransactionalDocumentEntry) 
                    && (documentEntries.get(entry.getFullClassName()) != null) 
                    && !((DocumentEntry)documentEntries.get(entry.getFullClassName())).getDocumentTypeName()
                            .equals(entry.getDocumentTypeName())) {
                throw new DataDictionaryException(new StringBuffer("Two transactional document types may not share the same document class: this=")
                        .append(entry.getDocumentTypeName())
                        .append(" / existing=")
                        .append(((DocumentEntry)documentEntries.get(entry.getDocumentClass().getName())).getDocumentTypeName()).toString());
            }
            if ((entriesByJstlKey.get(entry.getJstlKey()) != null) && !((DocumentEntry)documentEntries.get(entry.getJstlKey())).getDocumentTypeName().equals(entry.getDocumentTypeName())) {
                throw new DataDictionaryException(new StringBuffer("Two document types may not share the same jstl key: this=").append(entry.getDocumentTypeName()).append(" / existing=").append(((DocumentEntry)documentEntries.get(entry.getJstlKey())).getDocumentTypeName()).toString());
            }

            documentEntries.put(entryName, entry);
            //documentEntries.put(entry.getFullClassName(), entry);
            documentEntries.put(entry.getDocumentClass().getName(), entry);
            if (entry.getBaseDocumentClass() != null) {
            	documentEntries.put(entry.getBaseDocumentClass().getName(), entry);
            }
            entriesByJstlKey.put(entry.getJstlKey(), entry);

            if (entry instanceof TransactionalDocumentEntry) {
                TransactionalDocumentEntry tde = (TransactionalDocumentEntry) entry;

                documentEntries.put(tde.getDocumentClass().getSimpleName(), entry);
                if (tde.getBaseDocumentClass() != null) {
                	documentEntries.put(tde.getBaseDocumentClass().getSimpleName(), entry);
                }
            }
            if (entry instanceof MaintenanceDocumentEntry) {
                MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) entry;

                documentEntriesByBusinessObjectClass.put(mde.getBusinessObjectClass(), entry);
                documentEntriesByMaintainableClass.put(mde.getMaintainableClass(), entry);
                documentEntries.put(mde.getBusinessObjectClass().getSimpleName() + "MaintenanceDocument", entry);
            }
        }
    }

    private void buildDDInactivationBlockingIndices() {
        inactivationBlockersForClass = new HashMap<Class, Set<InactivationBlockingMetadata>>();
        Map<String,BusinessObjectEntry> boBeans = ddBeans.getBeansOfType(BusinessObjectEntry.class);
        for ( BusinessObjectEntry entry : boBeans.values() ) {
            List<InactivationBlockingDefinition> inactivationBlockingDefinitions = entry.getInactivationBlockingDefinitions();
            if (inactivationBlockingDefinitions != null && !inactivationBlockingDefinitions.isEmpty()) {
                for (InactivationBlockingDefinition inactivationBlockingDefinition : inactivationBlockingDefinitions) {
                    registerInactivationBlockingDefinition(inactivationBlockingDefinition);
                }
            }
        }
    }
    
    private void registerInactivationBlockingDefinition(InactivationBlockingDefinition inactivationBlockingDefinition) {
        Set<InactivationBlockingMetadata> inactivationBlockingDefinitions = inactivationBlockersForClass.get(inactivationBlockingDefinition.getBlockedBusinessObjectClass());
        if (inactivationBlockingDefinitions == null) {
            inactivationBlockingDefinitions = new HashSet<InactivationBlockingMetadata>();
            inactivationBlockersForClass.put(inactivationBlockingDefinition.getBlockedBusinessObjectClass(), inactivationBlockingDefinitions);
        }
        boolean duplicateAdd = ! inactivationBlockingDefinitions.add(inactivationBlockingDefinition);
        if (duplicateAdd) {
            throw new DataDictionaryException("Detected duplicate InactivationBlockingDefinition for class " + inactivationBlockingDefinition.getBlockingReferenceBusinessObjectClass().getClass().getName());
        }
    }
    
    public void run() {
        LOG.info( "Starting DD Index Building" );
        buildDDIndicies();
        LOG.info( "Completed DD Index Building" );
//        LOG.info( "Starting DD Validation" );
//        validateDD();
//        LOG.info( "Ending DD Validation" );
        LOG.info( "Started DD Inactivation Blocking Index Building" );
        buildDDInactivationBlockingIndices();
        LOG.info( "Completed DD Inactivation Blocking Index Building" );
    }
}