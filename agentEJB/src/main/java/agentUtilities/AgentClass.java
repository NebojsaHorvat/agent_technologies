package agentUtilities;

public class AgentClass {

	Class agentClass;
	
	Host host;

	public AgentClass( ) {}
	
	public AgentClass(Class agentClass, Host host) {
		super();
		this.agentClass = agentClass;
		this.host = host;
	}

	public Class getAgentClass() {
		return agentClass;
	}

	public void setAgentClass(Class agentClass) {
		this.agentClass = agentClass;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}
	
	
}
