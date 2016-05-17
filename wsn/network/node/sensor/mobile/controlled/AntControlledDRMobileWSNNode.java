package eboracum.wsn.network.node.sensor.mobile.controlled;

import eboracum.wsn.agent.AntAgent;
import eboracum.wsn.network.node.sensor.mobile.ControlledDRMobileWSNNode;
import ptolemy.actor.NoTokenException;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class AntControlledDRMobileWSNNode extends ControlledDRMobileWSNNode{

	private static final long serialVersionUID = 1L;

	public AntControlledDRMobileWSNNode(CompositeEntity container, String name)
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
