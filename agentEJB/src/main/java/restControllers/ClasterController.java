package restControllers;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agentManagement.AgentManagerLocal;
import agentUtilities.Host;
import agentUtilities.Hosts;
import agentUtilities.NewAgentInfo;
import cluster.ClusterManagerLocal;
import config.PropertiesSupplierLocal;


@Path("/cluster")
@Stateless
public class ClasterController {

	@EJB
	private ClusterManagerLocal clusterManager;
	
	@EJB
	private AgentManagerLocal agentManager;
	
	@EJB
	private PropertiesSupplierLocal prop;
	
	@POST
	@Path("/addHost")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public NewAgentInfo addHost(Host host) {
		
		// Proveravam da li taj host vec postoji
		List<Host> hosts = clusterManager.getAllHost();
		for(Host h : hosts) {
			if(h.getName().equals(host.getName()))
				return new NewAgentInfo(clusterManager.getAllHost(), agentManager.getAgentClasses(), agentManager.getActiveAgentsOnAllNodes());
		}
		// I da jesan i da nisam master ja dodajem novi cvor u listu svojih cvorova
		clusterManager.addHostToActiveList(host);
		
		// Ovde spakujem sve informacije koje su potrebne novom agentu master ih odma posalje cim se novi agent prijavi
		NewAgentInfo ret = new NewAgentInfo(clusterManager.getAllHost(), agentManager.getAgentClasses(), agentManager.getActiveAgentsOnAllNodes());
		// Ako sam master to znaci da treba da javim svim ostalim cvorovima o tome da se pojavio novi cvor
		// I master treba da trazi od novog cvora sve njegove klase i ako ima nekih novih da to javi ostalim cvorovima
		String isMaster = prop.getProperty("IS_MASTER");
		if(isMaster.equals("true")) {
			
			 new Thread() {
		         public void run() {
		        	 clusterManager.tellAllNodesAboutNewNode(host);
		 			
		 			 agentManager.getAgentClassesAndTellOthersAboutNewOnes(host);
		 			 
		         }
		      }.start();
		}
			
		return ret;
	}
	
	
	@POST
	@Path("/removeHost")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String removeHost(Host host) {
		
		String isMaster = prop.getProperty("IS_MASTER");
		if(!isMaster.equals("true"))
			return "no no";
		
		clusterManager.removeHostFromActiveList(host);
		
		return "ok";
	}
	
	@GET
	@Path("/getAllHosts")
	@Produces(MediaType.APPLICATION_JSON)
	public Hosts getAllHosts() {
		Hosts hosts = new Hosts(clusterManager.getAllHost());
		
		return hosts;
	}
	
	@GET
	@Path("/node")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkIfAlive() {
		
		return "alive";
	}
	
	@POST
	@Path("/node/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteNode(Host host) {
		
		clusterManager.removeHostFromActiveListAndDeleteHisStuff(host);
	}
	
}
