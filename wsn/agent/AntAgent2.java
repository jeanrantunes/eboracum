package eboracum.wsn.agent;

import ptolemy.actor.NoTokenException;
import ptolemy.actor.util.Time;
import ptolemy.kernel.util.IllegalActionException;
import eboracum.wsn.network.node.sensor.ControlledWSNNode;
import eboracum.wsn.network.node.sensor.SimpleWSNNode;
import eboracum.wsn.network.node.sensor.mobile.ControlledDRMobileWSNNode;

public class AntAgent2 implements BasicAgent{
	
	public SimpleWSNNode myNode;
	private Double threshold;
	public int agentsSensed;
	public String tempEvent;
	public int totalNodes;
	public Time lastSensorTime;	
	public Double initThreshold = 1.0;
	public Double initStimulus = 20.0;
	public Double ksi = 0.0007;
	public Double ro = 0.001;
	public Double delta = 20.0;

	public AntAgent2(){
		super();
		this.agentsSensed = 1;
		this.tempEvent = null;
		this.totalNodes = 1;
		this.lastSensorTime = Time.NEGATIVE_INFINITY;
	}
	
	public void nodeAction() throws NoTokenException, IllegalActionException{
		if (this.tempEvent != null){
			if (this.lastSensorTime != this.myNode.getDirector().getModelTime() && this.lastSensorTime != Time.NEGATIVE_INFINITY)
			{
				this.tempEvent = deliberate(tempEvent);
				tempEvent = null;
				agentsSensed=1;
				if (this.tempEvent != null) {
					if (!this.myNode.tempLastSensingEventTime.equals(this.myNode.getDirector().getModelTime())){
						this.myNode.tempLastSensingEventTime = this.myNode.getDirector().getModelTime();
						this.myNode.sensedEvents.setExpression(tempEvent.split("_")[0]);
					}
					else this.myNode.sensedEvents.setExpression(this.myNode.sensedEvents.getExpression()+tempEvent.split("_")[0]);
				}
			}
		}
		else {
			this.myNode.sensingManager();
			lastSensorTime = this.myNode.getDirector().getModelTime();
		}
		this.myNode.cpuRunManager();
	}
	
	public boolean sensorReceiveMessage(String tempMessage) throws NumberFormatException, IllegalActionException{
		String message = tempMessage.substring(2, tempMessage.length()-2);
		if (message.split(",")[1].split("=")[1].equals("ALL")){
			this.updateStimulus(tempMessage.split(",")[0].split("=")[1]);
			return false;
		}
		else 
			return true;
	}
	
	public boolean eventSensed(String tempEvent){
		this.tempEvent = tempEvent;
		this.propagate(tempEvent);
		try {
			this.myNode.getDirector().fireAt(this.myNode, this.myNode.getDirector().getModelTime().add(1));
		} catch (IllegalActionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String deliberate(String tempEvent){
		Double rand = Math.random();
		double stimulus;
		stimulus = (this.initStimulus-this.delta)*(this.agentsSensed/((double)this.totalNodes+1.0));
		if (rand < (Math.pow(stimulus,2)/(Math.pow(stimulus,2)+Math.pow(threshold,2)))){
			this.threshold += this.ro;
			return tempEvent;
		}
		else {
			this.threshold -= this.ksi;
			return null;
		}
	}
	
	public void propagate(String message){
		try {
			this.myNode.sendMessageToNeighbours(message,  0.0006);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateStimulus(String tempEvent){
		try {
			if (this.tempEvent!=null) {
				this.agentsSensed++;
				if (this.totalNodes > this.agentsSensed) this.totalNodes = this.agentsSensed;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setNode(ControlledWSNNode myNode){
		this.myNode = myNode;
		this.threshold = this.initThreshold;
	}

	public void setNode(ControlledDRMobileWSNNode myNode) {
		this.myNode = myNode;
		this.threshold = this.initThreshold;
	}

}
