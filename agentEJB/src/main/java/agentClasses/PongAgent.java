package agentClasses;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import agentUtilities.LogUtility;

public class PongAgent extends Agent {
	
	public PongAgent () {}
	
	public PongAgent ( AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super(aid,agentType, msm, agm, log);
	}
	
	@Override
	public void handleMessage(ACLMessage msg) {
		log.log(msg);
		if(msg.getPerformative() == Performative.REQUEST) {
			ACLMessage reply = new ACLMessage(Performative.INFORM);
			reply.setSender(getAid());
			reply.getReceivers().add(msg.getSender());
			reply.getUserArgs().put("pongOn", getAid().getHost());
			msm.post(reply);
		}
		
	}

}
