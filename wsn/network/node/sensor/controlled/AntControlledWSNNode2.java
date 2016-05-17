package eboracum.wsn.network.node.sensor.controlled;

import ptolemy.actor.NoTokenException;
import ptolemy.actor.util.Time;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import eboracum.wsn.agent.AntAgent;
import eboracum.wsn.network.node.sensor.ControlledWSNNode;

public class AntControlledWSNNode2 extends ControlledWSNNode{

	private static final long serialVersionUID = 1L;

	public AntControlledWSNNode2(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
	}
	
	public void initialize() throws IllegalActionException {
		super.initialize();
		this.myAgent = new AntAgent();
		this.myAgent.setNode(this);
	}
	
	protected void sensorNodeAction() throws NoTokenException, IllegalActionException{
		((AntAgent)this.myAgent).nodeAction();
	}
	
	public boolean receiveMessage(String tempMessage) throws NumberFormatException, IllegalActionException{
		if (((AntAgent)this.myAgent).sensorReceiveMessage(tempMessage))
			return false;
		else 
			return super.receiveMessage(tempMessage);
	}
	
}
