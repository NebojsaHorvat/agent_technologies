package cluster;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agentUtilities.Host;
import config.PropertiesSupplierLocal;

@Singleton
public class HeartBeat {

	@EJB
	private ClusterManagerLocal clusterManager;
	
	@EJB
	private PropertiesSupplierLocal prop;
	
	@Schedule(hour = "*", minute = "*/1")
	public void doHeartBeat() {
		
		List<Host> hosts = clusterManager.getAllHost();
		
		String nameOfThisNode = prop.getProperty("NAME_OF_NODE");
		for(Host host :hosts) {
			
			if(nameOfThisNode.equals(host.getName()))
				continue;
			try {
				ResteasyClient client = new ResteasyClientBuilder().build();
				String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/cluster/node";
				ResteasyWebTarget target = client.target(targetString);
				Response response = target.request().get();
				String ret = response.readEntity(String.class);
				
				if(ret.equals("alive"))
					continue;
				else
					tellAllHostsThatHostIsDead(hosts,host,nameOfThisNode);

			}catch(Exception e) {
				tellAllHostsThatHostIsDead(hosts,host,nameOfThisNode);
			}
			
		}
	}

	private void tellAllHostsThatHostIsDead(List<Host> hosts, Host hostToDelete,String nameOfThisNode) {
		// Izbacim ga iz liste hostova kojima treba da posljem da je host umro
		hosts.remove(hostToDelete);
		// Izbacim ga i iz liste hostova na nodu koji je skontao da je on umro i onda sam sebi ne posaljem delete
		clusterManager.removeHostFromActiveListAndDeleteHisStuff(hostToDelete);
		
		for(Host host :hosts) {
			if(nameOfThisNode.equals(host.getName()))
				continue;
			
			ResteasyClient client = new ResteasyClientBuilder().build();
			String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/cluster/node/delete";
			ResteasyWebTarget target = client.target(targetString);
			Response response = target.request().post(Entity.entity(host, MediaType.APPLICATION_JSON));
			
		}
		
	}
}
