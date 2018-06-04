package agentClasses;

import javax.ejb.Stateful;

import aclMessage.ACLMessage;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;

@Stateful(name="PingAgent")
public class PingAgent extends Agent implements PingLocal{

	public PingAgent () {}
	
	public PingAgent ( AID aid, AgentType agentType) {
		super(aid,agentType);
		
	}
	@Override
	public void odPing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(ACLMessage aclMessage) {
		// TODO Auto-generated method stub
		
	}

}
