/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.actionlist;

import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actions.asyncservices.ActionInvocation;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.preferences.service.impl.PreferencesServiceImpl;
import org.kuali.rice.kew.rule.TestRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Tests Outbox functionality
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class OutboxTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("OutboxTestConfig.xml");
    }

    private void turnOnOutboxForUser(final String principalId) {
        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                KEWServiceLocator.getUserOptionsService().save(principalId, PreferencesServiceImpl.USE_OUT_BOX, KEWConstants.PREFERENCES_YES_VAL);
                return null;
            }
        });
    }

    @Test
    public void testOutboxItemNotSavedOnSavedDocumentStatus() throws Exception {
        final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        List<String> recipients = new ArrayList<String>();
        recipients.add(rkirkendPrincipalId);
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "qualRole", recipients);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        turnOnOutboxForUser(rkirkendPrincipalId);

        document.saveDocument("");

        Collection<ActionItem> outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter());
        assertEquals("there should not be any outbox items", 0, outbox.size());
    }

    @Test
    public void testTakeActionCreatesOutboxItem() throws Exception {

    	final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        List<String> recipients = new ArrayList<String>();
        recipients.add(rkirkendPrincipalId);
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "qualRole", recipients);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        turnOnOutboxForUser(rkirkendPrincipalId);

        document.approve("");

        Collection<ActionItem> outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter());
        assertEquals("there should be an outbox item", 1, outbox.size());
    }

    @Test
    public void testSingleOutboxItemPerDocument() throws Exception {
    	final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
    	final String user1PrincipalId = getPrincipalIdForName("user1");
        List<String> recipients = new ArrayList<String>();
        recipients.add(rkirkendPrincipalId);
        recipients.add(user1PrincipalId);
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "qualRole", recipients);

        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("quickstart"), "TestDocumentType");
        document.routeDocument("");

        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        turnOnOutboxForUser(rkirkendPrincipalId);

        document.adHocRouteDocumentToPrincipal("A", "", user1PrincipalId, "", true);

        document.approve("");

        Collection<ActionItem> outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter());
        assertEquals("there should be an outbox item", 1, outbox.size());

        document = new WorkflowDocument(user1PrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        document.adHocRouteDocumentToPrincipal("A", "", rkirkendPrincipalId, "", true);

        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());
        document.approve("");

        outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter());
        assertEquals("there should be an outbox item", 1, outbox.size());
    }

    @Test
    public void testOnlyPersonWhoTookActionReceivesOutboxItem_Route() throws Exception {
    	final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
    	final String user1PrincipalId = getPrincipalIdForName("user1");
        List<String> recipients = new ArrayList<String>();
        recipients.add(getPrincipalIdForName("rkirkend"));
        recipients.add(getPrincipalIdForName("user1"));
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "qualRole", recipients);

        turnOnOutboxForUser(rkirkendPrincipalId);
        turnOnOutboxForUser(user1PrincipalId);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        // verify test is sane and users have action items
        assertFalse(KEWServiceLocator.getActionListService().getActionList(rkirkendPrincipalId, new ActionListFilter()).isEmpty());
        assertFalse(KEWServiceLocator.getActionListService().getActionList(user1PrincipalId, new ActionListFilter()).isEmpty());

        document = new WorkflowDocument(user1PrincipalId, document.getRouteHeaderId());
        document.approve("");
        // verify only user who took action has the outbox item
        assertTrue(KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).isEmpty());
        assertEquals(1, KEWServiceLocator.getActionListService().getOutbox(user1PrincipalId, new ActionListFilter()).size());
    }

    @Test
    public void testOnlyPersonWhoTookActionReceivesOutboxItem_BlanketApprove() throws Exception {
        final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        final String user1PrincipalId = getPrincipalIdForName("user1");
        List<String> recipients = new ArrayList<String>();
        recipients.add(rkirkendPrincipalId);
        recipients.add(user1PrincipalId);
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "qualRole", recipients);

        turnOnOutboxForUser(rkirkendPrincipalId);
        turnOnOutboxForUser(user1PrincipalId);

        WorkflowDocument document = new WorkflowDocument(rkirkendPrincipalId, "TestDocumentType");
        document.blanketApprove("");
        // verify only user who took action has the outbox item
        assertEquals("Wrong number of outbox items found for rkirkend", 0, KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1PrincipalId, new ActionListFilter()).size());

        document = new WorkflowDocument(rkirkendPrincipalId, "TestDocumentType");
        document.saveDocument("");
        // verify test is sane and users have action items
        assertEquals("Wrong number of action items found for rkirkend", 1, KEWServiceLocator.getActionListService().getActionList(rkirkendPrincipalId, new ActionListFilter()).size());
        // verify that outboxes of two users are clear
        assertEquals("Wrong number of outbox items found for rkirkend", 0, KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1PrincipalId, new ActionListFilter()).size());

        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        document.blanketApprove("");
        // verify only user who took action has the outbox item
        assertEquals("Wrong number of outbox items found for rkirkend", 1, KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1PrincipalId, new ActionListFilter()).size());
    }

    @Test
    public void testOnlyPersonWhoTookActionReceivesOutboxItem_Workgroup() throws Exception {
    	final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
    	final String user1PrincipalId = getPrincipalIdForName("user1");
    	final String ewestfalPrincipalId = getPrincipalIdForName("ewestfal");

        turnOnOutboxForUser(rkirkendPrincipalId);
        turnOnOutboxForUser(user1PrincipalId);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("user2"), "OutboxTestDocumentType");
        document.routeDocument("");
        // verify action items exist
        assertEquals("Wrong number of action items found for rkirkend", 1, KEWServiceLocator.getActionListService().getActionList(rkirkendPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of action items found for ewestfal", 1, KEWServiceLocator.getActionListService().getActionList(ewestfalPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of action items found for user1", 1, KEWServiceLocator.getActionListService().getActionList(user1PrincipalId, new ActionListFilter()).size());
        // verify outboxes are clear
        assertEquals("Wrong number of outbox items found for rkirkend", 0, KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for ewestfal", 0, KEWServiceLocator.getActionListService().getOutbox(ewestfalPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1PrincipalId, new ActionListFilter()).size());

        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        document.approve("");
        // verify only user who took action has the outbox item
        assertEquals("Wrong number of outbox items found for rkirkend", 1, KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for ewestfal", 0, KEWServiceLocator.getActionListService().getOutbox(ewestfalPrincipalId, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1PrincipalId, new ActionListFilter()).size());
    }

    @Test
    public void testOutBoxDefaultPreferenceOnConfigParam() throws Exception {
    	final String user1PrincipalId = getPrincipalIdForName("user1");
        Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(user1PrincipalId);
        assertTrue("By default the user's pref should be outbox on", prefs.isUsingOutbox());

        ConfigContext.getCurrentContextConfig().putProperty(Config.OUT_BOX_DEFAULT_PREFERENCE_ON, "false");
        final String natjohnsPrincipalId = getPrincipalIdForName("natjohns");
        prefs = KEWServiceLocator.getPreferencesService().getPreferences(natjohnsPrincipalId);
        assertFalse("The user's pref should be outbox off", prefs.isUsingOutbox());
    }
    
    @Test
    public void testTakeMassActions() throws Exception {
    	final String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
    	List<String> recipients = new ArrayList<String>();
        recipients.add(rkirkendPrincipalId);
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "qualRole", recipients);
        turnOnOutboxForUser(rkirkendPrincipalId);

    	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("quickstart"), "TestDocumentType");
        document.routeDocument("");
        
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());
     
        document = new WorkflowDocument(getPrincipalIdForName("user1"), "OutboxTestDocumentType");
        document.routeDocument("");
        
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());
        
        List<ActionItem> actionList = new ArrayList<ActionItem>(KEWServiceLocator.getActionListService().getActionList(rkirkendPrincipalId, new ActionListFilter()));
    	List<ActionInvocation> invocations = new ArrayList<ActionInvocation>();
        ActionToTake actionToTake = new ActionToTake();
        ActionItem actionItem = new ActionItem();

        for (ActionItem actinItem : actionList)
        {
            actionToTake.setActionItemId(actinItem.getActionItemId());
            actionToTake.setActionTakenCd(actinItem.getActionRequestCd());
            invocations.add(new ActionInvocation(actinItem.getActionItemId(), actionToTake.getActionTakenCd()));
        }
        KEWServiceLocator.getWorkflowDocumentService().takeMassActions(rkirkendPrincipalId, invocations);
        assertEquals("Wrong number of outbox items found for rkirkendPrincipalId", 2, KEWServiceLocator.getActionListService().getOutbox(rkirkendPrincipalId, new ActionListFilter()).size());
          
    }
}