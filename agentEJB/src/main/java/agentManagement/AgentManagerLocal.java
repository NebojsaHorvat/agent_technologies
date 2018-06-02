package agentManagement;

import java.util.Set;

import javax.ejb.Local;

import agentUtilities.Agent;

@Local
public interface AgentManagerLocal {

	public Class[] getAgentClasses();
	
	
	
}
