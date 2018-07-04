package agentClasses;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;

public class PongAgent extends Agent {
	
	public PongAgent () {}
	
	public PongAgent ( AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm) {
		super(aid,agentType, msm, agm);
	}
	
	@Override
	public void handleMessage(ACLMessage msg) {
		System.out.println("message received pong " + msg.getPerformative());
		if(msg.getPerformative() == Performative.REQUEST) {
			ACLMessage reply = new ACLMessage(Performative.INFORM);
			reply.setSender(getAid());
			reply.getReceivers().add(msg.getSender());
			reply.getUserArgs().put("pongOn", getAid().getHost());
			msm.post(reply);
		}
		
	}

}
