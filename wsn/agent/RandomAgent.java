package eboracum.wsn.agent;

import eboracum.wsn.network.node.sensor.ControlledWSNNode;

public class RandomAgent implements BasicAgent{

	public ControlledWSNNode myNode;
	private double threshold;
	
	public void setNode(ControlledWSNNode myNode){
		this.myNode = myNode;
	}
	
	public RandomAgent(double threshold){
		this.threshold = threshold;
	}
	
	public boolean eventSensed(String tempEvent){
		double rand = Math.random();
		if (rand <= this.threshold) return true;
		else return false;
	}
	
}
