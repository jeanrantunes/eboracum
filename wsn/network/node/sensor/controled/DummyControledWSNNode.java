package eboracum.wsn.network.node.sensor.controled;

import eboracum.wsn.agent.DummyAgent;
import eboracum.wsn.network.node.sensor.ControledWSNNode;
import eboracum.wsn.network.node.sensor.cpu.SimpleFIFOBasedCPU;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class DummyControledWSNNode extends ControledWSNNode{

	private static final long serialVersionUID = 1L;

	public DummyControledWSNNode(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
		this.myAgent = new DummyAgent();
	}
	
	public void initialize() throws IllegalActionException {
		super.initialize();
		this.cpu = new SimpleFIFOBasedCPU();
	}

}
