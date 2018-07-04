package restControllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.inject.spi.Bean;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import aclMessage.MessageManager;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentClass;
import agentUtilities.AgentClasses;
import agentUtilities.AgentType;
import agentUtilities.Host;
import agentUtilities.LogUtility;
import agentUtilities.RunningAgents;
import config.PropertiesSupplierLocal;

@Path("/agents")
@EJB(name="Pi", beanInterface = Bean.class)
public class AgentController {

	@EJB
	private AgentManagerLocal agentManager;
	@EJB
	private MessageManager msm;
	@EJB
	private LogUtility log;
	
	@EJB
	private PropertiesSupplierLocal prop;
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public AgentClasses getAgentClasses(){
		
		return new AgentClasses(agentManager.getAgentClasses());
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public RunningAgents getRunningAgents(){
		
		return new RunningAgents(agentManager.getActiveAgentsOnAllNodes());
	}
	
	@POST
	@Path("/tellAboutNewAgent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void tellAboutNewAgent( AID aid) {
		
		agentManager.addAgentToActiveListFromAnotherNoad(aid);
		
	}
	
	@POST
	@Path("/tellAboutStopedAgent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void tellAboutStopedAgent( AID aid) {

		List<AID> aids = new ArrayList<>();
		aids.add(aid);
		agentManager.removeAgentsFromActiveListFromAnotherNoad(aids);
	}
	
	@POST
	@Path("/running/delete")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Agent deleteRunningAgent( AID aid) {
		
		Host agentHost = aid.getHost();
		if( prop.getProperty("NAME_OF_NODE").equals(agentHost.getName()) ) {
			agentManager.deleteRunningAgent(aid);
		}else {
			// TODO ako agent nije na ovom nodu posalt drugom nodu da ga obirse
			ResteasyClient client = new ResteasyClientBuilder().build();
			String targetString = "http://"+agentHost.getAddress()+":"+agentHost.getPort()+"/agentWeb/rest/agents/running/delete";
			ResteasyWebTarget target = client.target(targetString);
			Response response = target.request().post(Entity.entity(aid, MediaType.APPLICATION_JSON));
		}
		
		return null;
	}
	@POST
	@Path("/running/{className}/{agentName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Agent startAgent(@PathParam("className") String className,@PathParam("agentName") String agentName,Host host ) {
		
		String[] names = className.split("\\.");
		
		for (AID aid : agentManager.getActiveAgentsOnAllNodes()) {
			if(aid.getName().equals(agentName))
				return null;
		}
		
		try {
			//TODO Trebalo bi naci agenta preko JNDI-a
			//Agent agent = (Agent)(new InitialContext().lookup("java:app/serverapp/"+names[1]));
			AgentType agentType = new AgentType(className, "idk");
			AID aid = new AID(agentName, host, agentType);
			
			String thisNode = prop.getProperty("NAME_OF_NODE");
			if(thisNode.equals(host.getName())) {
				
				Class[] classes = agentManager.getClassesFromManager("agentClasses");
				Class agentClass = null;
				for(Class c : classes) {
					String cName = c.getName();
					if(cName.equals(className)){
						agentClass = c;
						break;
					}
				}
				if(agentClass == null)
					return null;
				
				Constructor constructor = null;
				Agent agent = null;
				try {
					constructor = agentClass.getDeclaredConstructor(AID.class,AgentType.class, MessageManager.class, AgentManagerLocal.class, LogUtility.class);
					agent = (Agent)constructor.newInstance(aid,agentType, msm , agentManager, log);
				}catch(Exception e) {
					e.printStackTrace();
				}
				if(agent == null)
					return null;
				
				agentManager.addAgentToActiveList(agent);
				return agent;
			
			}else{
				ResteasyClient client = new ResteasyClientBuilder().build();
				String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/agents/running/"+className+"/"+agentName;
				ResteasyWebTarget target = client.target(targetString);
				Response response = target.request().post(Entity.entity(host, MediaType.APPLICATION_JSON));
				
				return null;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Path("/classes")
	@Consumes(MediaType.APPLICATION_JSON)
	public void receiveNewClasses(List<AgentClass> agentClasses){
		agentManager.addAgentClasses(agentClasses);
	}
	
	
}
