package agentClasses;

import javax.ejb.Stateful;

import aclMessage.ACLMessage;
import agentUtilities.Agent;

@Stateful(name="PingAgent")
public class PingAgent extends Agent implements PingLocal{

	@Override
	public void odPing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(ACLMessage aclMessage) {
		// TODO Auto-generated method stub
		
	}

}
