package eboracum.wsn.agent;

import eboracum.wsn.network.node.sensor.ControlledWSNNode;

public interface BasicAgent {
	
	
	boolean eventSensed(String tempEvent);

	public void setNode(ControlledWSNNode myNode);

	
}
