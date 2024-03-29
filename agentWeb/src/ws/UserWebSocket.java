package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.AgentClass;
import jmsMessage.JMSMessageToWebSocket;
import jmsMessage.JMSMessageToWebSocketType;
import jmsMessage.WebSocketMessage;
import jmsMessage.WebSocketMessageType;

@ServerEndpoint("/Socket")
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/wsQueue") })
public class UserWebSocket implements MessageListener {

	Logger log = Logger.getLogger("Websockets endpoint");

	private static Map<String, String> userSession = new HashMap<>();
	private static Map<String, String> sessionUser = new HashMap<>();

	private static ArrayList<Session> sessions = new ArrayList<>();

	@EJB
	private AgentManagerLocal agentManager;
	
	@OnOpen
	public void onOpen(Session session) {
		if (!sessions.contains(session)) {
			sessions.add(session);
			log.info("Dodao sesiju: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", ukupno sesija: "
					+ sessions.size());
			log.info("BROJ SESIJA: " + sessions.size());
		}
	}

	@OnMessage
	public void onWSMessage(Session session, String msg, boolean last) {

		try {
			if (session.isOpen()) {
				log.info(
						"Websocket endpoint: " + this.hashCode() + " primio: " + msg + " u sesiji: " + session.getId());

				if (isA(msg, WebSocketMessage.class)) {
					handleWebSocketMessage(session, msg);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				session.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void handleWebSocketMessage(Session session, String msg) {
		WebSocketMessage webSocketMessage;
		try {

			ObjectMapper mapper = new ObjectMapper();
			webSocketMessage = mapper.readValue(msg, WebSocketMessage.class);

			if (webSocketMessage.getType() == WebSocketMessageType.MESSAGE) {
				handleSendMessage(session, webSocketMessage.getContent());	

			} else if (webSocketMessage.getType() == WebSocketMessageType.AGENT_CLASSES) {
				handleAgentClasses(session, webSocketMessage.getContent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	@OnClose
	public void close(Session session) {
		sessions.remove(session);
		String username = sessionUser.get(session.getId());

		// radim logout ako je neko ulogovan
		//userAppCommunication.logoutAttempt(username);

		userSession.remove(username);
		sessionUser.remove(session.getId());
		log.info("Zatvorio: " + session.getId() + " u endpoint-u: " + this.hashCode());

	}

	@OnError
	public void error(Session session, Throwable t) {
		sessions.remove(session);
		log.log(Level.SEVERE, "Gre�ka u sessiji: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", tekst: "
				+ t.getMessage());
		t.printStackTrace();
	}

	boolean isA(String json, Class expected) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.readValue(json, expected);
			return true;
		} catch (Exception e) {
			System.out.println("CANT CONVERT!");
			return false;
		}
	}

	private void handleSendMessage(Session session, String msg) {
		String username = sessionUser.get(session.getId());
//		if (username == null)
//			return;
//		MessageReqMsg messageReqMsg = null;
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			messageReqMsg = mapper.readValue(msg, MessageReqMsg.class);
//			messageReqMsg.setSender(username);
//
//			// send message to userApp for saving
//			userAppCommunication.sendMessage(messageReqMsg);
//
//			// send message to other users
//			chatAppCommunication.sendMessageToOtherUsers(messageReqMsg);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}


	@Override
	public void onMessage(javax.jms.Message arg0) {
		System.out.println("Stigla poruka");
		ObjectMessage objectMessage = (ObjectMessage) arg0;
		try {
			JMSMessageToWebSocket message = (JMSMessageToWebSocket) objectMessage.getObject();

			if (message.getType() == JMSMessageToWebSocketType.AGENT_CLASSES_REMOVAL) {
				List<AgentClass> agentClassesForRemoval = (List<AgentClass>) message.getContent();
				ObjectMapper mapper = new ObjectMapper();
				WebSocketMessage wsm = new WebSocketMessage();
				wsm.setType(WebSocketMessageType.AGENT_CLASSES_REMOVAL);
				String content = mapper.writeValueAsString(agentClassesForRemoval);
				wsm.setContent(content);
				String wsmJSON = mapper.writeValueAsString(wsm);
				for (Session s : sessions) {
						s.getBasicRemote().sendText(wsmJSON);
				}
			}
			else if(message.getType() == JMSMessageToWebSocketType.AGENT_CLASSES ) {
				List<AgentClass> agentClasses = (List<AgentClass>) message.getContent();
				ObjectMapper mapper = new ObjectMapper();
				WebSocketMessage wsm = new WebSocketMessage();
				wsm.setType(WebSocketMessageType.AGENT_CLASSES);
				String content = mapper.writeValueAsString(agentClasses);
				wsm.setContent(content);
				String wsmJSON = mapper.writeValueAsString(wsm);
				for (Session s : sessions) {
						s.getBasicRemote().sendText(wsmJSON);
				}
			}
			else if(message.getType() == JMSMessageToWebSocketType.ACTIVE_AGENT ) {
				AID activeAgent = (AID) message.getContent();
				ObjectMapper mapper = new ObjectMapper();
				WebSocketMessage wsm = new WebSocketMessage();
				wsm.setType(WebSocketMessageType.ACTIVE_AGENT);
				String content = mapper.writeValueAsString(activeAgent);
				wsm.setContent(content);
				String wsmJSON = mapper.writeValueAsString(wsm);
				for (Session s : sessions) {
						s.getBasicRemote().sendText(wsmJSON);
				}
			}
			else if(message.getType() == JMSMessageToWebSocketType.ACTIVE_AGENTS_REMOVAL ) {
				List<AID> activeAgent = (List<AID>) message.getContent();
				ObjectMapper mapper = new ObjectMapper();
				WebSocketMessage wsm = new WebSocketMessage();
				wsm.setType(WebSocketMessageType.ACTIVE_AGENTS_REMOVAL);
				String content = mapper.writeValueAsString(activeAgent);
				wsm.setContent(content);
				String wsmJSON = mapper.writeValueAsString(wsm);
				for (Session s : sessions) {
						s.getBasicRemote().sendText(wsmJSON);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void pushMessageToClient(MessageReqMsg_JMS messageReqMsg_JMS) {
//
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			WebSocketMessage wsm = new WebSocketMessage();
//			wsm.setType(WebSocketMessageType.MESSAGE);
//			String content = mapper.writeValueAsString(messageReqMsg_JMS);
//			wsm.setContent(content);
//			String wsmJSON = mapper.writeValueAsString(wsm);
//
//			// Nalazim na kojoj je sesiji taj user
//			String username = messageReqMsg_JMS.getUsernames().get(0);
//			String sessionId = userSession.get(username);
//			for (Session s : sessions) {
//				if (s.getId().equals(sessionId)) {
//					s.getBasicRemote().sendText(wsmJSON);
//					break;
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private void handleAgentClasses(Session session, String content) {
		
		List<AgentClass> agentClasses= agentManager.getAgentClasses();
		for (Session s : sessions) {
			if (s.getId().equals(session.getId())) {
				session = s;
			}
		}
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			WebSocketMessage wsm = new WebSocketMessage();
			wsm.setType(WebSocketMessageType.AGENT_CLASSES);
			String stringClasses = mapper.writeValueAsString(agentClasses);
			wsm.setContent(stringClasses);
			String wsmJSON = mapper.writeValueAsString(wsm);
			System.out.println(wsmJSON);
			session.getBasicRemote().sendText(wsmJSON);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
