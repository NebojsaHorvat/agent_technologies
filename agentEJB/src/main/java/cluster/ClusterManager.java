package cluster;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.AgentClass;
import agentUtilities.Host;
import agentUtilities.Hosts;
import agentUtilities.NewAgentInfo;
import config.PropertiesSupplierLocal;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)

@Startup
@Singleton
@DependsOn("AgentManager")
public class ClusterManager implements ClusterManagerLocal{

	@EJB
	private PropertiesSupplierLocal prop;
	
	@EJB
	private AgentManagerLocal agentManager;
	
	private List<Host> activeHosts;
	
	
	@PostConstruct
	private void init () {
		activeHosts = new ArrayList<>();
		
		Host host = new Host(
				prop.getProperty("LOCATION"),
				prop.getProperty("NAME_OF_NODE"),
				Integer.parseInt(prop.getProperty("PORT")) );
		
		String isMaster = prop.getProperty("IS_MASTER");
		if( isMaster.equals("true")) {
			activeHosts.add(host);
			return ;
		}
			
		// Salje sebe masteru da se registruje
		try {
			
		// Sad trazim on mastera da me registruje mendju postojece nodove i ubaci u evidenciju
		ResteasyClient client = new ResteasyClientBuilder().build();
		String targetString = "http://"+prop.getProperty("MASTER_LOCATION")+":"+prop.getProperty("MASTER_PORT")+"/agentWeb/rest/cluster/addHost";
		ResteasyWebTarget target = client.target(targetString);
		Response response = target.request().post(Entity.entity(host, MediaType.APPLICATION_JSON));
		
		// I posto sam prosao IF i ja nisam master onda cu dobiti od mastera nazad liste svih hostova,klasa i aktivnih agenata
		NewAgentInfo newAgentInfo = response.readEntity(NewAgentInfo.class);
		activeHosts = newAgentInfo.getListOfHosts();
		agentManager.addActiveAgentOnAllNodes(newAgentInfo.getActiveAgentsOnAllNodes());
		agentManager.addAgentClasses(newAgentInfo.getAgentClasses());
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	private void onDestroy() {
		String isMaster = prop.getProperty("IS_MASTER");
		if( isMaster.equals("true"))// pre isMaster... NE treba da ide !
			return ;
		
		// Napravim objekat Host koji predtavlja ovaj node koji salje masteru kako bi ga obrisao
		Host host = new Host(
				prop.getProperty("LOCATION"),
				prop.getProperty("NAME_OF_NODE"),
				Integer.parseInt(prop.getProperty("PORT")) );
		// Sad trazim on mastera da me izbrise iz liste registrovanih nodova
		ResteasyClient client = new ResteasyClientBuilder().build();
		String targetString = "http://"+prop.getProperty("MASTER_LOCATION")+":"+prop.getProperty("MASTER_PORT")+"/ChatWeb/rest/cluster/removeHost";
		ResteasyWebTarget target = client.target(targetString);
		Response response = target.request().post(Entity.entity(host, MediaType.APPLICATION_JSON));
	}
	
	
	
	@Lock(LockType.WRITE)
	public void addHostToActiveList(Host host)
	{
		activeHosts.add(host);
	}
	
	@Lock(LockType.WRITE)
	public void removeHostFromActiveList(Host host)
	{
		Host hostForRemoval = null;
		for(Host h : activeHosts) {
			if( h.getName().equals(host.getName())) {
				hostForRemoval = h;
				break;
			}
			
		}
		
		if(host != null)
			activeHosts.remove(hostForRemoval);
	}

	@Override
	@Lock(LockType.READ)
	public List<Host> getAllHost() {
		return activeHosts;
	}

	@Override
	public void tellAllNodesAboutNewNode(Host host) {
		
		for(Host h :activeHosts){
			
			if(host.getName().equals(h.getName()) || prop.getProperty("NAME_OF_NODE").equals(h.getName()))
				continue;
			ResteasyClient client = new ResteasyClientBuilder().build();
			String targetString = "http://"+h.getAddress()+":"+h.getPort()+"/agentWeb/rest/cluster/addHost";
			ResteasyWebTarget target = client.target(targetString);
			Response response = target.request().post(Entity.entity(host, MediaType.APPLICATION_JSON));
		}
		
	}

	@Override
	@Lock(LockType.WRITE)
	public void removeHostFromActiveListAndDeleteHisStuff(Host hostToDelete) {
		// activeHosts.remove(hostToDelete);
		
		List<AID> activeAgentsOnAllNodes = agentManager.getActiveAgentsOnAllNodes();
		List<AID> activeAgentsForRemoval = new ArrayList<>();
		for(AID aid : activeAgentsOnAllNodes) {
			if( ! aid.getHost().equals(hostToDelete)) 
				continue;
			activeAgentsForRemoval.add(aid);
		}
		agentManager.removeAgentsFromActiveListFromAnotherNoad(activeAgentsForRemoval);
		
		List<AgentClass> agentClasses = agentManager.getAgentClasses();
		List<AgentClass> agentClassesForRemoval = new ArrayList<>();
		for(AgentClass agentClass : agentClasses) {
			if( ! agentClass.getHost().equals(hostToDelete))
				continue;
			agentClassesForRemoval.add(agentClass);
		}
		agentManager.removeAgentClasses(agentClassesForRemoval);
	}
}
