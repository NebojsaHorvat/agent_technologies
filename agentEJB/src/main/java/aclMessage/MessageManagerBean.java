package aclMessage;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

@Stateless
public class MessageManagerBean implements MessageManager {

	@Inject
	private JMSContext context;

	@Resource(mappedName = "java:/jms/queue/mojQueue")
	private Destination destination;
	
	@Override
	public void post(ACLMessage message) {
		try {
			ObjectMessage objectMessage = context.createObjectMessage();
			objectMessage.setObject(message);
			JMSProducer producer = context.createProducer();
			producer.send(destination, objectMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void test() {
		System.out.println("msm test");
	}

}
