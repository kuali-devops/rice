<%--
 Copyright 2006-2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="view" classname="org.kuali.rice.kns.uif.container.View"/>

<!-- begin of view render -->
<krad:html view="${view}">
   
   <!----------------------------------- #VIEW HEADER --------------------------------------->   
   <div id="viewheader_div">
      <krad:template component="${view.header}"/>
   </div>
   
   <!----------------------------------- #VIEW NAVIGATION --------------------------------------->
   <div id="viewnavigation_div">
      <krad:template component="${view.navigation}" currentPageId="{view.currentPageId}"/>
   </div>
   
   <!----------------------------------- #VIEW PAGE --------------------------------------->  
   <div id="viewpage_div">
      <krad:template component="${view.currentPage}"/>
   </div>
       
   <%-- write out view id as hidden so the view can be reconstructed if necessary --%>
   <c:if test="${view.renderForm}">
       <form:hidden path="viewId"/>
   </c:if>
    
   <!----------------------------------- #VIEW FOOTER --------------------------------------->
   <div id="viewfooter_div">
      <krad:template component="${view.footer}"/>
   </div>
        
</krad:html>
<!-- end of view render -->