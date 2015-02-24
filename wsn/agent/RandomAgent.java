package eboracum.wsn.agent;

import eboracum.wsn.network.node.sensor.ControledWSNNode;

public class RandomAgent implements BasicAgent{

	private double threshold;
	
	public void setNode(ControledWSNNode myNode){}
	
	public RandomAgent(double threshold){
		this.threshold = threshold;
	}
	
	public boolean eventSensed(String tempEvent){
		double rand = Math.random();
		if (rand <= this.threshold) return true;
		else return false;
	}
	
}
