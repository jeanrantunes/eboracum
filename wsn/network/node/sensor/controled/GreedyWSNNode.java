package eboracum.wsn.network.node.sensor.controled;

import ptolemy.actor.NoTokenException;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import eboracum.wsn.agent.GreedyPuppetAgent;
import eboracum.wsn.network.node.sensor.ControledWSNNode;
import eboracum.wsn.network.node.sensor.cpu.SimpleFIFOBasedCPU;

public class GreedyWSNNode extends ControledWSNNode{

	private static final long serialVersionUID = 1L;
	public String definedByMasterEvent;

	public GreedyWSNNode(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
		this.myAgent = new GreedyPuppetAgent();
		this.myAgent.setNode(this);
		this.definedByMasterEvent = null;
	}
	
	public void initialize() throws IllegalActionException {
		super.initialize();
		this.cpu = new SimpleFIFOBasedCPU();
		this.definedByMasterEvent = null;
	}
	
	protected void sensorNodeAction() throws NoTokenException, IllegalActionException{
		if (this.definedByMasterEvent != null) {
			if (!this.definedByMasterEvent.equals("VOID")){
				this.controledSensingManager();
				//System.out.println(this+"|"+this.tempEvent);
				//System.out.println(this.timeControler+"|"+this.getDirector().getModelTime());
			}
			this.definedByMasterEvent = null;
		}
		else {
			this.sensingManager();
		}
		this.cpuRunManager();
	}
	
	private void controledSensingManager() throws NoTokenException, IllegalActionException{
		this.tempEvent = this.definedByMasterEvent;
		if (!this.tempLastSensingEventTime.equals(this.getDirector().getModelTime())){
    		this.tempLastSensingEventTime = this.getDirector().getModelTime();
    		this.sensedEvents.setExpression(tempEvent.split("_")[0]);
    	}
    	else this.sensedEvents.setExpression(sensedEvents.getExpression()+tempEvent.split("_")[0]);
	}

	protected boolean eventSensedManager(String tempEvent) throws NoTokenException, IllegalActionException{
   		this.myAgent.eventSensed(tempEvent);
   		return false;
   	}
	
}
