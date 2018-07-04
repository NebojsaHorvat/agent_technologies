package agentClasses;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import agentUtilities.Host;
import agentUtilities.LogUtility;

@Stateful(name="PingAgent")
public class PingAgent extends Agent{

	public PingAgent () {}
	
	public PingAgent ( AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super(aid,agentType, msm, agm, log);
		
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		log.log(msg);
		if (msg.getPerformative() == Performative.REQUEST) {
			AID pongAid = agm.getAID(msg.getContent());
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(getAid());
			msgToPong.getReceivers().add(pongAid);
			msm.post(msgToPong);
		} else if (msg.getPerformative() == Performative.INFORM) {
			ACLMessage msgFromPong = msg;
			Map<String, Object> args = new HashMap<>(msg.getUserArgs());
			args.put("pingOn", getAid().getHost());

			System.out.println("Ping-Pong interaction details: ");
			for (java.util.Map.Entry<String, Object> e : args.entrySet()) {
				System.out.println(e.getKey() + " " + ((Host)e.getValue()).getName());
			}
		}
		
	}

}
