package agentManagement;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentClass;
import agentUtilities.Host;

@Local
public interface AgentManagerLocal {

	public List<AgentClass> getAgentClasses();
		
	public List<Agent> getActiveAgents();
	
	public void addAgentToActiveList(Agent agent);

	public void getAgentClassesAndTellOthersAboutNewOnes(Host host);
	
	public void addAgentClasses(List<AgentClass> newAgentClasses);
	
	public Class[] getClassesFromManager(String packageName);

	public Agent deleteRunningAgent(AID aid);
	
	public void addAgentToActiveListFromAnotherNoad(AID aid);
	
	public List<AID> getActiveAgentsOnAllNodes();

	public void removeAgentToActiveListFromAnotherNoad(AID aid);

}
