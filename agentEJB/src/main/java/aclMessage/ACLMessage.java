package aclMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agentUtilities.AID;

public class ACLMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1919902411543453036L;

	private Performative performative;

	private AID sender;

	private List<AID> receivers;

	private AID replyTo;

	private String content;

	private Object contentObj;

	private HashMap<String, Object> userArgs;

	private String language;

	private String encoding;

	private String ontology;

	private String protocol;

	private String conversationId;

	private String replyWith;
	
	private String inReplyTo;
	
	private String replyBy;
	
	private Boolean forward;

	public ACLMessage() {
		super();
		this.performative = Performative.NOT_UNDERSTOOD;
		this.receivers = new ArrayList<>();
		this.userArgs = new HashMap<>();
		forward = false;
	}
	
	public ACLMessage(Performative performative) {
		this.performative = performative;
		this.receivers = new ArrayList<>();
		this.userArgs = new HashMap<>();
		forward = false;
	}

	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	public AID getSender() {
		return sender;
	}

	public void setSender(AID sender) {
		this.sender = sender;
	}

	public List<AID> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<AID> receivers) {
		this.receivers = receivers;
	}

	public AID getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getContentObj() {
		return contentObj;
	}

	public void setContentObj(Object contentObj) {
		this.contentObj = contentObj;
	}

	public HashMap<String, Object> getUserArgs() {
		return userArgs;
	}

	public void setUserArgs(HashMap<String, Object> userArgs) {
		this.userArgs = userArgs;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(String replyBy) {
		this.replyBy = replyBy;
	}

	public Boolean getForward() {
		return forward;
	}

	public void setForward(Boolean forward) {
		this.forward = forward;
	}
	
}
