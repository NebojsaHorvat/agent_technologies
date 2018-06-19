package restControllers;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import aclMessage.ACLMessage;
import jmsMessage.JMSMessageToWebSocket;
import jmsMessage.JMSMessageToWebSocketType;

@Path("/messages")
@Stateless
public class MessageController {

	@Inject
	private JMSContext context;

	@Resource(mappedName = "java:/jms/queue/mojQueue")
	private Destination destination;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListPerformative() {

		return Response.status(Response.Status.OK).entity("Test").build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendACLMessage(ACLMessage aclMessage) {
		System.out.println(aclMessage.getPerformative());
		try {
			System.out.println("Saljem poruku");
			ObjectMessage objectMessage = context.createObjectMessage();
			objectMessage.setObject(aclMessage);
			JMSProducer producer = context.createProducer();
			producer.send(destination, objectMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.OK).entity("Test").build();
	}
}
