package agentClasses;

import java.util.Arrays;
import java.util.Random;

import javax.ejb.Stateful;

import aclMessage.ACLMessage;
import aclMessage.MessageManager;
import aclMessage.Performative;
import agentManagement.AgentManagerLocal;
import agentUtilities.AID;
import agentUtilities.Agent;
import agentUtilities.AgentType;
import aiUtils.AIUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

@Stateful(name = "RegressionAgent")
public class RegressionAgent extends Agent {

	private Instances dataset;
	private Instances trainDataSet;
	private Instances testDataSet;
	private LinearRegression linearRegression;
	private Evaluation evaluation;

	public RegressionAgent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegressionAgent(AID aid, AgentType agentType, MessageManager msm, AgentManagerLocal agm) {
		super(aid, agentType, msm, agm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		if (msg.getPerformative() == Performative.REQUEST) {
			try {
				dataset = AIUtils.loadDataset(msg.getContent());
				int trainSize = (int) Math.round(dataset.numInstances() * 0.8);
		        int testSize = dataset.numInstances() - trainSize;
		        dataset.randomize(new Random());
		        trainDataSet = new Instances(dataset, 0, trainSize);
		        testDataSet = new Instances(dataset, trainSize, testSize);
				linearRegression = AIUtils.createLR(trainDataSet);
				System.out.println(Arrays.toString(linearRegression.coefficients()));
				ACLMessage newMessage = new ACLMessage(Performative.AGREE);
				newMessage.setSender(this.getAid());
				newMessage.setContent(Arrays.toString(linearRegression.coefficients()));
				newMessage.getReceivers().add(msg.getSender());
				msm.post(newMessage);
			} catch (Exception e) {
				ACLMessage newMessage = new ACLMessage(Performative.NOT_UNDERSTOOD);
				newMessage.setSender(this.getAid());
				newMessage.getReceivers().add(msg.getSender());
				msm.post(newMessage);
			}
		} else if (msg.getPerformative().equals(Performative.INFORM)) {
			try {
				evaluation = new Evaluation(testDataSet);
				evaluation.evaluateModel(linearRegression, testDataSet);
				System.out.println(evaluation.toSummaryString());
				ACLMessage newMessage = new ACLMessage(Performative.AGREE);
				newMessage.setSender(this.getAid());
				newMessage.setContent(evaluation.toSummaryString());
				newMessage.getReceivers().add(msg.getSender());
				msm.post(newMessage);
			} catch (Exception e) {
				ACLMessage newMessage = new ACLMessage(Performative.NOT_UNDERSTOOD);
				newMessage.setSender(this.getAid());
				newMessage.getReceivers().add(msg.getSender());
				msm.post(newMessage);
			}

		} else if (msg.getPerformative().equals(Performative.QUERY_IF)) {
			try {
				String content = msg.getContent();
				String[] tokens = content.split(",");
				double[] arr = new double[tokens.length + 1];
				int i = 0;
				Instance instance = new Instance(dataset.numAttributes());
				for (String st : tokens) {
					arr[i] = Double.valueOf(st);
					System.out.println(arr[i]);
					instance.setValue(i, arr[i]);
					i++;
				}

				double retVal = linearRegression.classifyInstance(instance);
				System.out.println(retVal);
				ACLMessage newMessage = new ACLMessage(Performative.AGREE);
				newMessage.setSender(this.getAid());
				newMessage.setContent(Double.toString(retVal));
				newMessage.getReceivers().add(msg.getSender());
				msm.post(newMessage);
			} catch (Exception e) {
				ACLMessage newMessage = new ACLMessage(Performative.NOT_UNDERSTOOD);
				newMessage.setSender(this.getAid());
				newMessage.getReceivers().add(msg.getSender());
				msm.post(newMessage);
			}
		}
	}

}
