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
package org.kuali.rice.kim.bo.entity.impl;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.EntityPhone;
import org.kuali.rice.kim.bo.reference.PhoneType;
import org.kuali.rice.kim.bo.reference.impl.PhoneTypeImpl;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_PHONE_T")
public class EntityPhoneImpl extends DefaultableEntityDataBase implements EntityPhone {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_PHONE_ID")
	protected String entityPhoneId;
	
	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;
	
	@Column(name = "PHONE_TYP_CD")
	protected String phoneTypeCode;
	
	@Column(name = "PHONE_NBR")
	protected String phoneNumber;
	
	@Column(name = "PHONE_EXTN_NBR")
	protected String extensionNumber;
	
	@Column(name = "POSTAL_CNTRY_CD")
	protected String countryCode;
	
	@ManyToOne(targetEntity=PhoneTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "PHONE_TYP_CD", insertable = false, updatable = false)
	protected PhoneType phoneType;

	// Waiting until we pull in from KFS
	// protected Country country;
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#getCountryCode()
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#getEntityPhoneId()
	 */
	public String getEntityPhoneId() {
		return entityPhoneId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#getExtensionNumber()
	 */
	public String getExtensionNumber() {
		return extensionNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#getPhoneNumber()
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#getPhoneTypeCode()
	 */
	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#setCountryCode(java.lang.String)
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#setExtensionNumber(java.lang.String)
	 */
	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#setPhoneNumber(java.lang.String)
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityPhone#setPhoneTypeCode(java.lang.String)
	 */
	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.DefaultableEntityTypeData#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.DefaultableEntityTypeData#setEntityTypeCode(java.lang.String)
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "entityPhoneId", entityPhoneId );
		m.put( "phoneNumber", phoneNumber );
		m.put( "extensionNumber", extensionNumber );
		m.put( "countryCode", countryCode );
		return m;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public PhoneType getPhoneType() {
		return this.phoneType;
	}

	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public void setEntityPhoneId(String entityPhoneId) {
		this.entityPhoneId = entityPhoneId;
	}

	public String getFormattedPhoneNumber() {
		StringBuffer sb = new StringBuffer( 30 );
		
		// TODO: get extension from country code table
		// TODO: append "+xxx" if country is not the default country
		sb.append( getPhoneNumber() );
		if ( StringUtils.isNotBlank( getExtensionNumber() ) ) {
			sb.append( " x" );
			sb.append( getExtensionNumber() );
		}
		
		return sb.toString();
	}
}
