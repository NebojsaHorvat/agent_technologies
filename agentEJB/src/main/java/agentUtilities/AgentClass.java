package agentUtilities;

import java.io.Serializable;

public class AgentClass implements Serializable{

	String agentClass;
	
	Host host;

	public AgentClass( ) {}
	
	public AgentClass(String agentClass, Host host) {
		super();
		this.agentClass = agentClass;
		this.host = host;
	}

	public String getAgentClass() {
		return agentClass;
	}

	public void setAgentClass(String agentClass) {
		this.agentClass = agentClass;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}
	
	
}
