package agentUtilities;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import jmsMessage.JMSMessageToWebSocket;
import jmsMessage.JMSMessageToWebSocketType;

@Stateless
public class LogUtility {
	@Resource(mappedName = "java:/jms/queue/wsQueue")
	private Destination destination;
	@Inject
	private JMSContext context;
	
	public void log(Object log) {
		JMSMessageToWebSocket message = new JMSMessageToWebSocket();
		message.setType(JMSMessageToWebSocketType.LOG);
		message.setContent(log);
		try {
			ObjectMessage objectMessage = context.createObjectMessage();
			objectMessage.setObject(message);
			JMSProducer producer = context.createProducer();
			producer.send(destination, objectMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
