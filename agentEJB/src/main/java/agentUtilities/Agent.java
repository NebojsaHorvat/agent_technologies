package agentUtilities;

import javax.ejb.EJB;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import agentManagement.AgentManagerLocal;

public abstract class Agent {
	
	private AID aid;
	
	private AgentType agentType;
	
	@EJB
	protected MessageManager msm;
	@EJB
	protected AgentManagerLocal agm;
	@EJB
	protected LogUtility log;

	public Agent() {}
	
	public Agent(AID aid, AgentType agentType,  MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super();
		this.aid = aid;
		this.agentType = agentType;
		this.msm = msm;
		this.agm = agm;
		this.log = log;
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	public abstract void handleMessage(ACLMessage msg);
}
