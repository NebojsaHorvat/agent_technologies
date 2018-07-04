package restControllers;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cluster.ClusterManagerLocal;

@Path("/heart")
@Stateless
public class HeartBeatContoller {

	@EJB
	private ClusterManagerLocal clusterManager;
	
	@GET
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkIfAlive() {
		
		return "alive";
	}
}
