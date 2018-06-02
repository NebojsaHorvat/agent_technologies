package restControllers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agentUtilities.Agent;

@Path("/agents")
public class AgentController {

	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Agent> getAgentClasses(){
		
		return null;
	}
}
