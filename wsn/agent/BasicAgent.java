package eboracum.wsn.agent;

import eboracum.wsn.network.node.sensor.ControledWSNNode;

public interface BasicAgent {
	
	
	boolean eventSensed(String tempEvent);

	public void setNode(ControledWSNNode myNode);

	
}
