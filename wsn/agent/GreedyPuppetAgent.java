package eboracum.wsn.agent;


import eboracum.wsn.agent.central.GreedyCentralizedLoadBalancer;
import eboracum.wsn.network.node.sensor.ControledWSNNode;

public class GreedyPuppetAgent implements BasicAgent{

	public GreedyCentralizedLoadBalancer masterOfPuppets;
	public ControledWSNNode myNode;	

	public boolean eventSensed(String tempEvent){
		try {
			this.masterOfPuppets.eventSensed(tempEvent,	myNode.getDirector().getModelTime(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setNode(ControledWSNNode myNode){
		this.myNode = myNode;
	}
}
