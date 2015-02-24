package eboracum.wsn.network.node.sensor;


import eboracum.wsn.agent.BasicAgent;
import ptolemy.actor.NoTokenException;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public abstract class ControledWSNNode extends BasicWirelessSensorNode {
	
	private static final long serialVersionUID = 1L;
	public BasicAgent myAgent; 
    
   	public ControledWSNNode(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
	}
   
   	protected boolean eventSensedManager(String tempEvent) throws NoTokenException, IllegalActionException{
   		return this.myAgent.eventSensed(tempEvent);
   	}
   	
}