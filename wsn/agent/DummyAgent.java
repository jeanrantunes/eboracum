package eboracum.wsn.agent;

import eboracum.wsn.network.node.sensor.ControlledWSNNode;

public class DummyAgent implements BasicAgent{

	public void setNode(ControlledWSNNode myNode){}
	
	public boolean eventSensed(String tempEvent){
		return true;
	}
	
}
