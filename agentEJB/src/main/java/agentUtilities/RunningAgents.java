package agentUtilities;

import java.util.List;

public class RunningAgents {

	List<AID> runningAgents;

	public RunningAgents() {}

	public RunningAgents(List<AID> runningAgents) {
		super();
		this.runningAgents = runningAgents;
	}

	public List<AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<AID> runningAgents) {
		this.runningAgents = runningAgents;
	}
	
}
