package agentClasses;

import java.util.ArrayList;
import java.util.List;

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
import sun.applet.resources.MsgAppletViewer_sv;

@Stateful
public class ContractNetInitiator extends Agent {
	
	private int retries = 0;
	private boolean done = true;
	private Integer bestProposal;
	private AID bestProposalAgent;
	private List<AID> participants;
	private ObjectMapper mapper = new ObjectMapper();
	
	
	public ContractNetInitiator() {
		super();
	}

	public ContractNetInitiator(AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm, LogUtility log) {
		super(aid, agentType, msm, agm, log);
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		log.log(msg);
		String msgString = "";
		try {
			msgString = mapper.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(msg.getPerformative() == Performative.REQUEST) {
			if(done) {
				createCallForProposal(msg);
			} else {
				log.log("A contract net is already in process, discarding message: " + msgString);
			}
		}
		else if(!done) {
			switch (msg.getPerformative()) {
			case PROPOSE:
				handleProposal(msg);
				break;
			case REFUSE:
				handleRefusal(msg);
				break;
			case INFORM:
				finished(msg);
				break;
			case FAILURE:
				handleFailure();
				break;
			default:
				log.log("Unexpected message: " + msg);
			}
		} else if(msg.getPerformative() != Performative.INFORM) {
			log.log("Received message after completion: " + msgString);
		}
		
	}
	
	private void createCallForProposal(ACLMessage msg) {
		done = false;
		List<AID> runningAgents = agm.getActiveAgentsOnAllNodes();
		participants = new ArrayList<AID>();
		for(AID agent : runningAgents) {
			if(agent.getAgentType().getName().contains("ContractnetParticipant")) {
				participants.add(agent);
			}
		}
		long seconds = 10;
		try {
			seconds = Long.parseLong(msg.getContent());
		} catch(NumberFormatException e) {
			log.log("No or incorrect time of execution specified. Time of execution will be 10 seconds.");
		} finally {
			sendCallForProposalEndMessage(seconds);			
			sendCallForProposal(seconds);
		}
	}

	private void handleProposal(ACLMessage msg) {
		try {
			Integer receivedProposal = Integer.parseInt(msg.getContent());
			if(bestProposal == null) {
				bestProposal = receivedProposal;
				bestProposalAgent = msg.getSender();
			}
			else if(receivedProposal < bestProposal) {
				bestProposal = receivedProposal;
				bestProposalAgent = msg.getSender();
			}
			log.log("Recieved bid from agent " + msg.getSender().getName() + " of value " + msg.getContent() + ".");
		} catch(NumberFormatException e) {
			try {
				log.log("Incorrect proposal received in message: " + mapper.writeValueAsString(msg));
			} catch (JsonProcessingException e1) {
				log.log("Incorrect proposal received in message: ");
				e1.printStackTrace();
			}
		} finally {
			removeParticipant(msg.getSender());
		}
	}

	private void handleRefusal(ACLMessage msg) {
		log.log("Recieved refusal from agent " + msg.getSender().getName() + ".");
		removeParticipant(msg.getSender());
	}

	private void finished(ACLMessage msg) {
		if(msg != null && !msg.getSender().equals(getAid())) {
			log.log("The contract net process has finished successfully after confirming the lowest bid.");
			done = true;
		} else if(msg == null || retries == 2) {
			if(bestProposalAgent != null) {
				handleAcceptance();
			} else {
				handleFailure();
			}
		} else {
			retries++;
			log.log("The contractnet process has reached its timeout, but not all participants have responded.\nInitiating retry number " + retries + ".");
			sendCallForProposalEndMessage(10);
			sendCallForProposal(10);
		}
	}

	private void handleFailure() {
		log.log("The contractnet process has finished unsuccessfully.");
		done = true;
	}
	
	private void handleAcceptance() {
		log.log("The contract net process has finished.\nAccepting proposal from agent " + bestProposalAgent.getName() + " of value " + bestProposal + ".");
		
		List<AID> runningAgents = agm.getActiveAgentsOnAllNodes();
		for(AID agent : runningAgents) {
			if(agent.getAgentType().getName().contains("ContractnetParticipant")) {
				if(agent.equals(bestProposalAgent)) {
					ACLMessage accept = new ACLMessage(Performative.ACCEPT_PROPOSAL);
					accept.getReceivers().add(bestProposalAgent);
					accept.setSender(getAid());
					msm.post(accept);
				} else {
					ACLMessage reject = new ACLMessage(Performative.REJECT_PROPOSAL);
					reject.getReceivers().add(agent);
					reject.setSender(getAid());
					msm.post(reject);
				}
			}
		}
	}
	
	private void removeParticipant(AID sender) {
		for(int i = 0; i < participants.size(); i++) {
			if(participants.get(i).equals(sender)) {
				participants.remove(i);
				break;
			}
		}
		if(participants.size() == 0) {
			finished(null);
		}
	}
	
	private void sendCallForProposalEndMessage(long replyBySeconds) {
		ACLMessage endMessage = new ACLMessage(Performative.INFORM);
		endMessage.setSender(getAid());
		endMessage.getReceivers().add(getAid());
		msm.post(endMessage, replyBySeconds * 1000);
	}
	
	private void sendCallForProposal(long replyBySeconds) {
		ACLMessage callForProposal = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
		callForProposal.getReceivers().addAll(participants);
		callForProposal.setSender(getAid());
		callForProposal.setReplyBy(System.currentTimeMillis() + replyBySeconds * 1000);
		msm.post(callForProposal);
	}

}
