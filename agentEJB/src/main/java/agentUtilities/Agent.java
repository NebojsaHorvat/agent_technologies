package agentUtilities;

public class Agent {
	
	private AID aid;
	
	private AgentType agentType;

	public Agent() {}
	
	public Agent(AID aid, AgentType agentType) {
		super();
		this.aid = aid;
		this.agentType = agentType;
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

}
