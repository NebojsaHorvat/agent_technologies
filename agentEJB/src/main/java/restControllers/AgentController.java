package restControllers;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agentManagement.AgentManagerLocal;
import agentUtilities.Agent;

@Path("/agents")
public class AgentController {

	@EJB
	private AgentManagerLocal agentManager;
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Class[] getAgentClasses(){
		
		Class[] agentClasses = agentManager.getAgentClasses();
		return agentClasses;
	}
}
