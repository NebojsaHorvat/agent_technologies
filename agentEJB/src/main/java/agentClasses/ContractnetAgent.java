package agentClasses;

import aclMessage.ACLMessage;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;

public class ContractnetAgent extends Agent implements ContractnetLocal{

	ContractnetAgent () {}
	
	ContractnetAgent (AID aid, AgentType agentType) {
		super(aid,agentType);
	}
	
	@Override
	public void handleMessage(ACLMessage aclMessage) {
		// TODO Auto-generated method stub
		
	}
	
	

}
