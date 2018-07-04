package agentClasses;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import agentUtilities.LogUtility;

public class ContractnetAgent extends Agent{

	public ContractnetAgent () {}
	
	public ContractnetAgent (AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super(aid,agentType, msm, agm, log);
	}
	
	@Override
	public void handleMessage(ACLMessage aclMessage) {
		// TODO Auto-generated method stub
		
	}
	
	

}
