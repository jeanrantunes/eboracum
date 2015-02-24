package eboracum.wsn.network.node.sensor.controled;

import eboracum.wsn.agent.RandomAgent;
import eboracum.wsn.network.node.sensor.ControledWSNNode;
import eboracum.wsn.network.node.sensor.cpu.SimpleFIFOBasedCPU;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class RandomControledWSNNode extends ControledWSNNode{

	private static final long serialVersionUID = 1L;
	public Parameter Threshold;

	public RandomControledWSNNode(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
		Threshold = new Parameter(this,"Threshold");
		Threshold.setExpression("0.50");
	}
	
	public void initialize() throws IllegalActionException {
		super.initialize();
		this.cpu = new SimpleFIFOBasedCPU();
		this.myAgent = new RandomAgent(Double.parseDouble(Threshold.getValueAsString()));
	}

}
