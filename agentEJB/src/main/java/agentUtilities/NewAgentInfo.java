package agentUtilities;

import java.util.List;

public class NewAgentInfo {

	private List<Host> listOfHosts;

	private List<AgentClass> agentClasses;

	private List<AID> activeAgentsOnAllNodes;
	
	public NewAgentInfo () {}

	public NewAgentInfo(List<Host> listOfHosts, List<AgentClass> agentClasses, List<AID> activeAgentsOnAllNodes) {
		super();
		this.listOfHosts = listOfHosts;
		this.agentClasses = agentClasses;
		this.activeAgentsOnAllNodes = activeAgentsOnAllNodes;
	}

	public List<Host> getListOfHosts() {
		return listOfHosts;
	}

	public void setListOfHosts(List<Host> listOfHosts) {
		this.listOfHosts = listOfHosts;
	}

	public List<AgentClass> getAgentClasses() {
		return agentClasses;
	}

	public void setAgentClasses(List<AgentClass> agentClasses) {
		this.agentClasses = agentClasses;
	}

	public List<AID> getActiveAgentsOnAllNodes() {
		return activeAgentsOnAllNodes;
	}

	public void setActiveAgentsOnAllNodes(List<AID> activeAgentsOnAllNodes) {
		this.activeAgentsOnAllNodes = activeAgentsOnAllNodes;
	}
	
	
}
