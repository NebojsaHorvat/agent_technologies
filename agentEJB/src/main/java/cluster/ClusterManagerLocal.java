package cluster;

import java.util.List;

import javax.ejb.Local;

import agentUtilities.Host;


@Local
public interface ClusterManagerLocal {

	public void addHostToActiveList(Host host);

	public void removeHostFromActiveList(Host host);

	public List<Host> getAllHost();

	public void tellAllNodesAboutNewNode(Host host);

	public void removeHostFromActiveListAndDeleteHisStuff(Host hostToDelete);

}
