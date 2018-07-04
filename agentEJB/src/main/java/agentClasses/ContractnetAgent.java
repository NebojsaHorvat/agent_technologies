package agentClasses;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;

public class ContractnetAgent extends Agent implements ContractnetLocal{

	public ContractnetAgent () {}
	
	public ContractnetAgent (AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm) {
		super(aid, agentType, msm, agm);
	}
	
	@Override
	public void handleMessage(ACLMessage aclMessage) {
		// TODO Auto-generated method stub
		
	}
	
	

}
