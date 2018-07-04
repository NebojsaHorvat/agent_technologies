package agentClasses;

import java.util.List;

import javax.ejb.Stateful;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import agentUtilities.LogUtility;

@Stateful(name = "MasterRegAgent")
public class MasterRegAgent extends Agent {

	public MasterRegAgent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MasterRegAgent(AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super(aid, agentType, msm, agm, log);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		log.log(msg);
		if (msg.getPerformative().equals(Performative.REQUEST)) {
			String path = msg.getContent();
			ACLMessage newMessage = new ACLMessage(Performative.REQUEST);
			newMessage.setSender(this.getAid());
			newMessage.setContent(path);
			List<AID> aids = this.agm.getActiveAgentsOnAllNodes();
			for (AID temp : aids) {
				if (temp.getAgentType().getName().equals("agentClasses.RegressionAgent")) {
					newMessage.getReceivers().add(temp);
				}
			}
			msm.post(newMessage);
		} else if (msg.getPerformative().equals(Performative.INFORM)) {
			ACLMessage newMessage = new ACLMessage(Performative.INFORM);
			newMessage.setSender(this.getAid());
			List<AID> aids = this.agm.getActiveAgentsOnAllNodes();
			for (AID temp : aids) {
				if (temp.getAgentType().getName().equals("agentClasses.RegressionAgent")) {
					newMessage.getReceivers().add(temp);
				}
			}
			msm.post(newMessage);
		} else if (msg.getPerformative().equals(Performative.QUERY_IF)) {
			ACLMessage newMessage = new ACLMessage(Performative.QUERY_IF);
			newMessage.setSender(this.getAid());
			newMessage.setContent(msg.getContent());
			List<AID> aids = this.agm.getActiveAgentsOnAllNodes();
			for (AID temp : aids) {
				if (temp.getAgentType().getName().equals("agentClasses.RegressionAgent")) {
					newMessage.getReceivers().add(temp);
				}
			}
			msm.post(newMessage);
		} else if (msg.getPerformative().equals(Performative.AGREE)) {
			System.out.println(msg.getContent());
		} else if (msg.getPerformative().equals(Performative.NOT_UNDERSTOOD)) {
			System.out.println("NOT_UNDERSTOOD");
		}

	}

}
