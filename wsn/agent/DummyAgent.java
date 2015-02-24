package eboracum.wsn.agent;

import eboracum.wsn.network.node.sensor.ControledWSNNode;

public class DummyAgent implements BasicAgent{

	public void setNode(ControledWSNNode myNode){}
	
	public boolean eventSensed(String tempEvent){
		return true;
	}
	
}
