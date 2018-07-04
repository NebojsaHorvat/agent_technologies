package aclMessage;

public interface MessageManager {
	
	void post(ACLMessage message);
	void post(ACLMessage message, long delay);
	void test();
	
}
