package aclMessage;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue")
})
public class MDBConsumer implements MessageListener{

	@Override
	public void onMessage(Message arg0) {
		System.out.println("Dobio poruku");
		try {
			ACLMessage aclMessage = arg0.getBody(ACLMessage.class);
			System.out.println(aclMessage.getPerformative());
			//TODO zavrsi ovo 
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
