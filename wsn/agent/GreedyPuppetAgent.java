package eboracum.wsn.agent;


import eboracum.wsn.agent.central.GreedyCentralizedLoadBalancer;
import eboracum.wsn.network.node.sensor.ControlledWSNNode;

public class GreedyPuppetAgent implements BasicAgent{

	public GreedyCentralizedLoadBalancer masterOfPuppets;
	public ControlledWSNNode myNode;	

	public boolean eventSensed(String tempEvent){
		try {
			this.masterOfPuppets.eventSensed(tempEvent,	myNode.getDirector().getModelTime(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setNode(ControlledWSNNode myNode){
		this.myNode = myNode;
	}
}
