package restControllers;

import javax.ejb.EJB;
import javax.enterprise.inject.spi.Bean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import agentUtilities.Host;
import config.PropertiesSupplierLocal;

@Path("/agents")
@EJB(name="Pi", beanInterface = Bean.class)
public class AgentController {

	@EJB
	private AgentManagerLocal agentManager;
	
	@EJB
	private PropertiesSupplierLocal prop;
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Class[] getAgentClasses(){
		
		Class[] agentClasses = agentManager.getAgentClasses();
		return agentClasses;
	}
	
	@POST
	@Path("/running/{className}/{agentName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Agent startAgent(@PathParam("className") String className,@PathParam("agentName") String agentName ) {
		
		String[] names = className.split("\\.");
		try {
			//TODO Trebalo bi naci agenta preko JNDI-a
			//Agent agent = (Agent)(new InitialContext().lookup("java:app/serverapp/"+names[1]));
			
			Host host = new Host(
					prop.getProperty("LOCATION"),
					prop.getProperty("NAME_OF_NODE"),
					Integer.parseInt(prop.getProperty("PORT")) );
			AgentType agentType = new AgentType(className, "idk");
			AID aid = new AID(agentName, host, agentType);
			Agent agent = new Agent(aid, agentType);
			agentManager.addAgentToActiveList(agent);
			return agent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
