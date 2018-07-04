package agentClasses;

import javax.ejb.Stateful;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;

@Stateful(name="RegressionAgent")
public class RegressionAgent extends Agent{

	public RegressionAgent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegressionAgent(AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm) {
		super(aid, agentType, msm, agm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		if (msg.getPerformative() == Performative.REQUEST) {
			System.out.println(msg.getContent());
		}
	}

}
