package aclMessage;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.Host;
import config.PropertiesSupplierLocal;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue")
})
public class MDBConsumer implements MessageListener{
	
	@EJB
	private PropertiesSupplierLocal prop;
	@EJB
	private AgentManagerLocal agm;
	
	@Override
	public void onMessage(Message arg0) {
		try {
			ACLMessage aclMessage = arg0.getBody(ACLMessage.class);
			Set<Host> hosts = new HashSet<>();
			if(!aclMessage.getForward()) {
				for(AID receiver : aclMessage.getReceivers()) {
					if(receiver != null)
						hosts.add(receiver.getHost());
				}
				aclMessage.setForward(true);
				for(Host host : hosts) {
					if(!host.getName().equals(prop.getProperty("NAME_OF_NODE"))) {
						ResteasyClient client = new ResteasyClientBuilder().build();
						String targetString = "http://"+host.getAddress()+":"+host.getPort()+"/agentWeb/rest/messages";
						ResteasyWebTarget target = client.target(targetString);
						Response response = target.request().post(Entity.entity(aclMessage, MediaType.APPLICATION_JSON));
					}
				}
			}
			
			for (AID receiver : aclMessage.getReceivers()) {
				if(receiver == null)
					continue;
				
				if(receiver.getHost().getName().equals(prop.getProperty("NAME_OF_NODE"))) {
					for (Agent agent : agm.getActiveAgents()) {
						if(agent.getAid().equals(receiver))
							agent.handleMessage(aclMessage);
					}
				}
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
