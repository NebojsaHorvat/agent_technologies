package agentUtilities;

public class AID {

	private String name;
	
	private Host host;
	
	private AgentType agentType;

	public AID() {}
	
	public AID(String name, Host host, AgentType agentType) {
		super();
		this.name = name;
		this.host = host;
		this.agentType = agentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}
	
	
}
