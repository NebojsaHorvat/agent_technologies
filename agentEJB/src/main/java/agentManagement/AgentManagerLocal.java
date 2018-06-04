package agentManagement;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import agentUtilities.Agent;
import agentUtilities.AgentClass;
import agentUtilities.Host;

@Local
public interface AgentManagerLocal {

	public List<AgentClass> getAgentClasses();
	
	public void removeAgentFromActiveList(Agent agent);
	
	public List<Agent> getActiveAgents();
	
	public void addAgentToActiveList(Agent agent);

	public void getAgentClassesAndTellOthersAboutNewOnes(Host host);
	
	public void addAgentClasses(List<AgentClass> newAgentClasses);
	
}
