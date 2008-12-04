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
package org.kuali.rice.kim.bo.role.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.impl.KimAbstractMemberImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name="KRIM_ROLE_MBR_T")
public class RoleMemberImpl extends KimAbstractMemberImpl {

	@Id
	@Column(name="ROLE_MBR_ID")
	protected String roleMemberId;
	
	@Column(name="ROLE_ID")
	protected String roleId;
	
	protected List<RoleMemberAttributeDataImpl> attributes;
	
	public String getRoleMemberId() {
		return this.roleMemberId;
	}
	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "roleMemberId", roleMemberId );
		m.put( "roleId", roleId );
		m.put( "memberId", getMemberId() );
		m.put( "memberTypeCode", getMemberTypeCode() );
		return m;
	}

	public List<RoleMemberAttributeDataImpl> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<RoleMemberAttributeDataImpl> attributes) {
		this.attributes = attributes;
	}

	public AttributeSet getQualifier() {
		AttributeSet m = new AttributeSet();
		for ( RoleMemberAttributeDataImpl data : getAttributes() ) {
			m.put( data.getKimAttribute().getAttributeName(), data.getAttributeValue() );
		}
		return m;
	}
	
	public boolean hasQualifier() {
		return !getAttributes().isEmpty();
	}
}
