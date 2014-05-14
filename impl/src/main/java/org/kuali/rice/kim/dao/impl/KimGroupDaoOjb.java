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
package org.kuali.rice.kim.dao.impl;

import java.sql.Timestamp;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.dao.KimGroupDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of the KimGroupDaoOjb class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimGroupDaoOjb extends PlatformAwareDaoBaseOjb implements KimGroupDao {
	// KULRICE-4248 Adding logger
	private static final Logger LOG = Logger.getLogger(KimGroupDaoOjb.class);
	private KimTypeInfoService kimTypeInfoService;
	
    public List<GroupImpl> getGroups(Map<String,String> fieldValues) {
        Criteria crit = new Criteria();
        BusinessObjectEntry boEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry("org.kuali.rice.kim.bo.impl.GroupImpl");
        List lookupNames = boEntry.getLookupDefinition().getLookupFieldNames();
        String kimTypeId = null;
        for (Map.Entry<String,String> entry : fieldValues.entrySet()) {
        	if (entry.getKey().equals("kimTypeId")) {
        		kimTypeId=entry.getValue();
        		break;
        	}
        }
        AttributeDefinitionMap definitions = null;
        for (Entry<String, String> entry : fieldValues.entrySet()) {
        	if (StringUtils.isNotBlank(entry.getValue())) {
        		if (entry.getKey().contains(".")) {
        	        Criteria subCrit = new Criteria();
        			String value = entry.getValue().replace('*', '%');

                    // obey the DD forceUppercase attribute and allow the OR operator
                    // subCrit.addLike("attributeValue",value);
                    String[] values = StringUtils.split(value, KNSConstants.OR_LOGICAL_OPERATOR);
                    boolean valuesCriterionAdded = false;
                    if (values.length > 0) {
                        if (definitions == null) {
                            KimTypeInfo kimTypeInfo = getKimTypeInfoService().getKimType(kimTypeId);
                            KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimTypeInfo);
                            definitions = kimTypeService.getAttributeDefinitions(kimTypeId);
                        }
                        AttributeDefinition definition = definitions.getByAttributeName(entry.getKey().substring(0, entry.getKey().indexOf('.')));
                        
                        Criteria valuesCrit = new Criteria();
                        for (int i = 0; i < values.length; i++) {
                            String subValue = values[i];
                            if (StringUtils.isNotBlank(subValue)) {
                                Criteria valueCrit = new Criteria();
                                // null means uppercase it, so do !Boolean.FALSE.equals
                                if (!Boolean.FALSE.equals(definition.getForceUppercase())) {
                                    valueCrit.addLike(getDbPlatform().getUpperCaseFunction() + "(attributeValue)", subValue.toUpperCase());
                                }
                                else {
                                    valueCrit.addLike("attributeValue", subValue); 
                                }
                                valuesCriterionAdded = true;
                                valuesCrit.addOrCriteria(valueCrit);
                            }
                        }
                        subCrit.addAndCriteria(valuesCrit);
                        
                        subCrit.addEqualTo("kimAttributeId",entry.getKey().substring(entry.getKey().indexOf(".")+1, entry.getKey().length()));
                        subCrit.addEqualTo("kimTypeId", kimTypeId);
                        
                        subCrit.addEqualToField(KIMPropertyConstants.Group.GROUP_ID, Criteria.PARENT_QUERY_PREFIX + KIMPropertyConstants.Group.GROUP_ID);
                        
                        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(GroupAttributeDataImpl.class, subCrit);
                        if (valuesCriterionAdded) {
                            crit.addExists(subQuery);
                        }
                    }

        		} else {
        			if (lookupNames.contains(entry.getKey())) {
            			String value = entry.getValue().replace('*', '%');
                        String[] values = StringUtils.split(value, KNSConstants.OR_LOGICAL_OPERATOR);
                        Criteria valuesCrit = new Criteria();
                        for (int i = 0; i < values.length; i++) {
                            String subValue = values[i];
                            if (StringUtils.isNotBlank(subValue)) {
                                Criteria valueCrit = new Criteria();
                                // null means uppercase it, so do !Boolean.FALSE.equals
                                if (KimConstants.UniqueKeyConstants.GROUP_NAME.equals(entry.getKey())) {
                                    valueCrit.addLike(getDbPlatform().getUpperCaseFunction() + "(groupName)", subValue.toUpperCase());
                                }
                                else {
                                    valueCrit.addLike(entry.getKey(), subValue);
                                }
                                valuesCrit.addOrCriteria(valueCrit);
                            }
                        }
                        crit.addAndCriteria(valuesCrit);

        			} else {
        				if (entry.getKey().equals(KIMPropertyConstants.Person.PRINCIPAL_NAME)) {

        					// KULRICE-4248: Retrieve Principal using the Identity Management Service
        					Criteria memberSubCrit = new Criteria();
        					memberSubCrit.addEqualToField(KIMPropertyConstants.Group.GROUP_ID, Criteria.PARENT_QUERY_PREFIX + KIMPropertyConstants.Group.GROUP_ID);
        					// Get the passed-in Principal Name
        					String principalName = entry.getValue();
        					// Search for the Principal using the Identity Management service
        					LOG.debug("Searching on Principal Name: " + entry.getValue());
        					KimPrincipalInfo principalInfo = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
        					// If a Principal is returned, plug in the Principal ID as the Member ID
        					if (principalInfo != null)
        					{
        						LOG.debug("Retrieved Principal: " + principalInfo.getPrincipalName());
        						String principalId = principalInfo.getPrincipalId();
        						LOG.debug("Plugging in Principal ID: " + principalId + "as Member ID");
        						memberSubCrit.addLike(KIMPropertyConstants.GroupMember.MEMBER_ID, principalId);

                   	        	// KULRICE-4232: Only return groups that the principal is an active member of.
                   	        	Timestamp now = KNSServiceLocator.getDateTimeService().getCurrentTimestamp();
                   	        	Criteria afterActiveFromSubCrit = new Criteria();
                   	        	afterActiveFromSubCrit.addLessOrEqualThan(KIMPropertyConstants.GroupMember.ACTIVE_FROM_DATE, now);
                    	        Criteria nullActiveFromSubCrit = new Criteria();
                    	        nullActiveFromSubCrit.addIsNull(KIMPropertyConstants.GroupMember.ACTIVE_FROM_DATE);
                    	        
                    	        Criteria ActiveMemberSubCrit1 = new Criteria();
                    	        ActiveMemberSubCrit1.addOrCriteria(afterActiveFromSubCrit);
                    	        ActiveMemberSubCrit1.addOrCriteria(nullActiveFromSubCrit);
                    	        
                    	       	Criteria afterActiveToSubCrit = new Criteria();
                   	        	afterActiveToSubCrit.addGreaterOrEqualThan(KIMPropertyConstants.GroupMember.ACTIVE_TO_DATE, now);
                    	        Criteria nullActiveToSubCrit = new Criteria();
                    	        nullActiveToSubCrit.addIsNull(KIMPropertyConstants.GroupMember.ACTIVE_TO_DATE);
                    	        
                    	        Criteria ActiveMemberSubCrit2 = new Criteria();
                    	        ActiveMemberSubCrit2.addOrCriteria(afterActiveToSubCrit);
                    	        ActiveMemberSubCrit2.addOrCriteria(nullActiveToSubCrit);
                   	        	
                    	        memberSubCrit.addAndCriteria(ActiveMemberSubCrit1);
                    	        memberSubCrit.addAndCriteria(ActiveMemberSubCrit2);			
        					}
        					// Otherwise, plug in a blank string as the Member ID
        					else
                	        {
        						LOG.debug("No Principal ID, plugging in blank string as Member ID");
        						memberSubCrit.addLike(KIMPropertyConstants.GroupMember.MEMBER_ID, "");
                	        }
        					/*
                	        Criteria subCrit = new Criteria();
                			String principalName = entry.getValue().replace('*', '%');
                			subCrit.addLike(KIMPropertyConstants.Person.PRINCIPAL_NAME, principalName );
                	        subCrit.addEqualToField(KIMPropertyConstants.Person.PRINCIPAL_ID, Criteria.PARENT_QUERY_PREFIX + "memberId");
                			ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(KimPrincipalImpl.class, subCrit);
                	        Criteria memberSubCrit = new Criteria();
                	        memberSubCrit.addEqualToField(KIMPropertyConstants.Group.GROUP_ID, Criteria.PARENT_QUERY_PREFIX + KIMPropertyConstants.Group.GROUP_ID);
                	        memberSubCrit.addExists(subQuery);
                	        */
                			ReportQueryByCriteria memberSubQuery = QueryFactory.newReportQuery(GroupMemberImpl.class, memberSubCrit);
                			crit.addExists(memberSubQuery);
        				}
        			}
        		}
        	}
        }
        Query q = QueryFactory.newQuery(GroupImpl.class, crit);

        return (List)getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }

    protected KimTypeInfoService getKimTypeInfoService() {
    	if (kimTypeInfoService == null) {
    		kimTypeInfoService = KIMServiceLocator.getTypeInfoService();
    	}
    	return kimTypeInfoService;
    }
}