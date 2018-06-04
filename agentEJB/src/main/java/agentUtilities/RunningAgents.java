package agentUtilities;

import java.util.List;

public class RunningAgents {

	List<Agent> runningAgents;

	public RunningAgents() {}
	
	public RunningAgents(List<Agent> runningAgents) {
		super();
		this.runningAgents = runningAgents;
	}

	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}
	
	
}
