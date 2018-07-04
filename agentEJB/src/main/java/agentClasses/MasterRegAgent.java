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

@Stateful(name="MasterRegAgent")
public class MasterRegAgent extends Agent{

	public MasterRegAgent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MasterRegAgent(AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm) {
		super(aid, agentType, msm, agm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		System.out.println("PORUKA ZA INT");
		if(msg.getPerformative().equals(Performative.REQUEST)) {
			String path = msg.getContent();
			ACLMessage newMessage = new ACLMessage(Performative.REQUEST);
			newMessage.setContent(path);
			List<AID> aids = this.agm.getActiveAgentsOnAllNodes();
			for(AID temp:aids) {
				if(temp.getAgentType().getName().equals("agentClasses.RegressionAgent")) {
					newMessage.getReceivers().add(temp);
				}
			}
			msm.post(newMessage);
		}else if(msg.getPerformative().equals(Performative.INFORM)) {
			System.out.println("evaluacija rezultati");
		}else if(msg.getPerformative().equals(Performative.QUERY_IF)) {
			System.out.println("na nekom novom primeru");
		}
		
	}

}
