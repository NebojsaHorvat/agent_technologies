package restControllers;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;

@Path("/messages")
@Stateless
public class MessageController {
	
	@EJB
	private MessageManager msm;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListPerformative() {

		return Response.status(Response.Status.OK).entity(Performative.values()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendACLMessage(ACLMessage aclMessage) {
		msm.post(aclMessage);
		return Response.status(Response.Status.OK).entity("Test").build();
	}
}
