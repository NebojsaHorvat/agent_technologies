package agentManagement;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.jboss.security.auth.spi.Users;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentClass;
import agentUtilities.AgentClasses;
import agentUtilities.Host;
import cluster.ClusterManagerLocal;
import config.PropertiesSupplierLocal;

@Startup
@Singleton
public class AgentManager implements AgentManagerLocal{
	
	
	@EJB
	private PropertiesSupplierLocal prop;
	
	@EJB
	private ClusterManagerLocal clusterManager;
	
	private List<Agent> activeAgents;
	
	private List<AID> activeAgentsOnAllNodes;
	
	private List<AgentClass> agentClasses;
	
	@PostConstruct
	private void init () {
		activeAgents = new ArrayList<>();
		
		agentClasses = new ArrayList<>();
		
		activeAgentsOnAllNodes = new ArrayList<>();
		
		Class[] classes = null;
		try {
			
			classes = getClasses("agentClasses");
			
		}catch(Exception e) {
			System.out.println("LOADING CLASSES FAILED\n");
			e.printStackTrace();
		}
		for(int i = 0; i < classes.length; i++) {
			Host host = new Host(
					prop.getProperty("LOCATION"),
					prop.getProperty("NAME_OF_NODE"),
					Integer.parseInt(prop.getProperty("PORT")) );
			AgentClass agentClass = new AgentClass(classes[i].getName(), host);
			agentClasses.add(agentClass);
		}
		
	}
	
	@PreDestroy
	private void onDestroy() {
		
	}
	
	@Override
	@Lock(LockType.READ)
	public List<AgentClass> getAgentClasses()
	{
		return agentClasses;
	}

	@Lock(LockType.READ)
	public List<Agent> getActiveAgents()
	{	
		return activeAgents;
	}
	
	@Lock(LockType.READ)
	public List<AID> getActiveAgentsOnAllNodes()
	{	
		return activeAgentsOnAllNodes;
	}
	
	@Lock(LockType.WRITE)
	public void addActiveAgentOnAllNodes(List<AID> newActiveAgents)
	{
		activeAgentsOnAllNodes.addAll(newActiveAgents);
		for(AID newActiveAgent : newActiveAgents) {
			if(activeAgentsOnAllNodes.contains(newActiveAgent))
				continue;
			activeAgentsOnAllNodes.add(newActiveAgent);
		}
	}
	
	
	@Lock(LockType.WRITE)
	public void addAgentClasses(List<AgentClass> newAgentClasses)
	{
		//agentClasses.addAll(newAgentClasses);
		ArrayList<AgentClass> newClasses = new ArrayList<>();
		for(AgentClass acNew: newAgentClasses) {
			boolean found = false;
			AgentClass forAdding = null;
			for(AgentClass acOld: agentClasses) {
				if( acNew.getAgentClass().equals( acOld.getAgentClass())  ){
					found = true;
					forAdding = acNew;
					break;
				}
			}
			if(found == false) {
				newClasses.add(acNew);
			}
		}
		
		if(newClasses.isEmpty())
			return;
		agentClasses.addAll(newClasses);

		// TODO Javi to na webSocket

	}
	
	@Lock(LockType.WRITE)
	public void removeAgentClasses(List<AgentClass> agentClassesForRemoval)
	{
		agentClasses.removeAll(agentClassesForRemoval);
		
		// TODO Javi to na webSocket
	}
	
	
	
	@Lock(LockType.WRITE)
	public void addAgentToActiveListFromAnotherNoad(AID aid)
	{
		activeAgentsOnAllNodes.add(aid);
		
		// TODO Javi to na webSocket

	}
	
	@Lock(LockType.WRITE)
	public void removeAgentsFromActiveListFromAnotherNoad(List<AID> aids)
	{
		activeAgentsOnAllNodes.removeAll(aids);
		
		// TODO Javi to na webSocket

	}
	
	@Override
	public void getAgentClassesAndTellOthersAboutNewOnes(Host host) {

		ResteasyClient client = new ResteasyClientBuilder().build();
		String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/agents/classes";
		ResteasyWebTarget target = client.target(targetString);
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		String jsonClasses = response.readEntity(String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		AgentClasses as = null;
		List<AgentClass> newHostsClasses = null;
		try {
			as = objectMapper.readValue(jsonClasses, AgentClasses.class);
			newHostsClasses = as.getAgentClasses();
		} catch (Exception e) {
			System.out.println("COULD NOT CONVERT AGENT_CLASSES");
			e.printStackTrace();
			return;
		}
		
		// Prodjem kroz sve klase koje ima novi host vidim da li ona vec postoji kod mene u listi
		// Ako ne postoji to znaci da je ona nova i da treba javiti ostalim hostovima da postoji nova klasa
		
		ArrayList<AgentClass> newClasses = new ArrayList<>();
		for(AgentClass acNew: newHostsClasses) {
			boolean found = false;
			AgentClass forAdding = null;
			for(AgentClass acOld: agentClasses) {
				if( acNew.getAgentClass().equals( acOld.getAgentClass())  ){
					found = true;
					forAdding = acNew;
					break;
				}
			}
			if(found == false) {
				newClasses.add(acNew);
			}
		}
		
		if(newClasses.isEmpty())
			return;
		
		// Sada treba sebi (masteru) da dodam nove klase i da prodjem kroz ostale hostove i da njima dodam nove klase
		agentClasses.addAll(newClasses);
		
		List<Host> hosts = clusterManager.getAllHost();
		for(Host h :hosts){
			if(h.getName().equals(host.getName()))
				continue;
			targetString = "http://"+h.getAddress()+":"+h.getPort()+"/agentWeb/rest/agents/classes";
			target = client.target(targetString);
			response = target.request().post(Entity.entity(newClasses, MediaType.APPLICATION_JSON));
		}
	}
	
	@Lock(LockType.WRITE)
	public void addAgentToActiveList(Agent agent)
	{
		activeAgents.add(agent);
		activeAgentsOnAllNodes.add(agent.getAid());
		
		// Sad bih trebao da kazem i svim ostalim hostovima da sam dosao tog agenta
		List<Host> hosts = clusterManager.getAllHost();
		for(Host host : hosts) {
			if(prop.getProperty("NAME_OF_NODE").equals(host.getName()))
				continue;
			ResteasyClient client = new ResteasyClientBuilder().build();
			String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/agents/tellAboutNewAgent";
			ResteasyWebTarget target = client.target(targetString);
			Response response = target.request().post(Entity.entity(agent.getAid(), MediaType.APPLICATION_JSON));
		}
		
		// TODO Javi to na webSocket

	}

	@Override
	@Lock(LockType.WRITE)
	public Agent deleteRunningAgent(AID aid) {
		Agent agentToRemove = null;
		for(Agent agent : activeAgents ) {
			if(agent.getAid().equals(aid)) {
				agentToRemove = agent;
				break;
			}
		}
		if(agentToRemove == null)
			return null;
		activeAgents.remove(agentToRemove);
		activeAgentsOnAllNodes.remove(aid);
		
		// Sad bih trebao da kazem i svim ostalim hostovima da sam dosao tog agenta
		List<Host> hosts = clusterManager.getAllHost();
		for(Host host : hosts) {
			if(prop.getProperty("NAME_OF_NODE").equals(host.getName()))
				continue;
			ResteasyClient client = new ResteasyClientBuilder().build();
			String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/agents/tellAboutStopedAgent";
			ResteasyWebTarget target = client.target(targetString);
			Response response = target.request().post(Entity.entity(aid, MediaType.APPLICATION_JSON));
		}
		
		// TODO Javi to na webSocket
		
		return agentToRemove;
	}
	
	
	@Override
	public Class[] getClassesFromManager(String packageName) {
		try {
			return getClasses(packageName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	 /**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class[] getClasses(String packageName)
	        throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class> classes = new ArrayList<Class>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}

	

	

	
}
