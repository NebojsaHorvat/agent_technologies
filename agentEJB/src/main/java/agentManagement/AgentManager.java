package agentManagement;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import agentUtilities.Agent;
import agentUtilities.AgentClass;
import agentUtilities.Host;
import config.PropertiesSupplierLocal;

@Startup
@Singleton
public class AgentManager implements AgentManagerLocal{
	
	
	@EJB
	private PropertiesSupplierLocal prop;
	
	private List<Agent> activeAgents;
	
	private List<AgentClass> agentClasses;
	
	@PostConstruct
	private void init () {
		activeAgents = new ArrayList<>();
		
		agentClasses = new ArrayList<>();
		
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
			AgentClass agentClass = new AgentClass(classes[i], host);
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
//		try {
//			
//		return getClasses("agentClasses");
//		
//		}catch(Exception e) {
//			System.out.println("LOADING FAILED\n");
//			e.printStackTrace();
//		}
		return agentClasses;
	}

	@Lock(LockType.READ)
	public List<Agent> getActiveAgents()
	{	
		return activeAgents;
	}
	
	@Lock(LockType.WRITE)
	public void addAgentToActiveList(Agent agent)
	{
		activeAgents.add(agent);
	}
	
	@Lock(LockType.WRITE)
	public void removeAgentFromActiveList(Agent agent)
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
