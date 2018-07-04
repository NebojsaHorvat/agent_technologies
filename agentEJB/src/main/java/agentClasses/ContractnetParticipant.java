package agentClasses;

import java.security.SecureRandom;

import javax.ejb.Stateful;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import agentUtilities.LogUtility;

@Stateful
public class ContractnetParticipant extends Agent{
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public ContractnetParticipant () {}
	
	public ContractnetParticipant (AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super(aid,agentType, msm, agm, log);
	}
	
	@Override
	public void handleMessage(ACLMessage msg) {
		log.log(msg);
		switch (msg.getPerformative()) {
		case CALL_FOR_PROPOSAL:
			handleCallForProposal(msg);
			break;
		case REJECT_PROPOSAL:
			handleRejectProposal();
			break;
		case ACCEPT_PROPOSAL:
			handleAcceptProposal(msg);
			break;
		default:
			log.log("Unexpected message: " + msg);
		}
		
	}
	
	
	private void handleCallForProposal(ACLMessage msg) {
		if(msg.getReplyBy() < System.currentTimeMillis()) {
			try {
				log.log("ReplyBy time has passed, discarding message: " + mapper.writeValueAsString(msg));
			} catch (JsonProcessingException e) {
				log.log("ReplyBy time has passed, discarding message: ");
				e.printStackTrace();
			}
		} else {
			createProposal(msg);
		}
	}

	private void handleRejectProposal() {
		log.log("Agent " + getAid().getName() + " has noted that his bid was rejected.");
	}
	
	private void handleAcceptProposal(ACLMessage msg) {
		ACLMessage reply = null;
		int success = new SecureRandom().nextInt(4);
		
		if (success == 0) {
			reply = new ACLMessage(Performative.FAILURE);
			log.log("Agent " + getAid().getName() + " has noted that his bid was accepted, but can't confirm his bid, resulting in a failure.");
		} else {
			reply = new ACLMessage(Performative.INFORM);
			log.log("Agent " + getAid().getName() + " has noted that his bid was accepted, and will confirm bid.");
		}
		
		reply.setSender(getAid());
		reply.getReceivers().add(msg.getSender());
		msm.post(reply);
	}
	
	private void createProposal(ACLMessage msg) {
		SecureRandom rnd = new SecureRandom();
		int delay = (rnd.nextInt(3) + 1) * 3;
		
		ACLMessage reply = new ACLMessage(Performative.PROPOSE);
		reply.getReceivers().add(msg.getSender());
		reply.setSender(getAid()); 
		
		if(delay < 4) {
			log.log("Agent " + getAid().getName() + " is not bidding.");
			reply.setPerformative(Performative.REFUSE); 
		} else {
			Integer bid = rnd.nextInt(40) + 20;
			log.log("Agent " + getAid().getName() + " is bidding " + bid + ".");
			reply.setContent(bid.toString()); 
		}
		
		msm.post(reply, delay * 1000);
	}
	

}
