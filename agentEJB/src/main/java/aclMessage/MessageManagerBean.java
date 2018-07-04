package aclMessage;

import java.util.UUID;

import javax.annotation.Resource;
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
		post(message, 0);
	}

	@Override
	public void post(ACLMessage message, long delay) {
		try {
			ObjectMessage objectMessage = context.createObjectMessage();
			objectMessage.setObject(message);
			objectMessage.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());
			if (delay > 0) {
				objectMessage.setLongProperty("_HQ_SCHED_DELIVERY", System.currentTimeMillis() + delay);
			}
			JMSProducer producer = context.createProducer();
			producer.send(destination, objectMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void test() {
		System.out.println("msm test");
	}

}
