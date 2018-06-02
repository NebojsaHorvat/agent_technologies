package agentManagement;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import agentUtilities.Agent;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Startup
@Singleton
public class AgentManager {

	private List<Agent> activeAgents;
	
	@PostConstruct
	private void init () {
		
	}
	
	@PreDestroy
	private void onDestroy() {
		
	}
	
	@Lock(LockType.READ)
	public List<Class<Agent>> addAgentClasses(Agent agent)
	{
		 return null;
	}
	
	@Lock(LockType.WRITE)
	public void addAgentToActiveList(Agent agent)
	{
		activeAgents.add(agent);
	}
	
	@Lock(LockType.WRITE)
	public void removeHostFromActiveList(Agent agent)
	{
		Agent agentForRemoval = null;
		for(Agent a : activeAgents) {
			if( a.getAid().equals(agent.getAid())) {
				agentForRemoval = a;
			}
			break;
		}
		
		if(agentForRemoval != null)
			activeAgents.remove(agentForRemoval);
	}
}
