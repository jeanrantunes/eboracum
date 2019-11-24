package eboracum.wsn.network.node.sensor.cpu;

import ptolemy.actor.util.Time;

public class Block {
	private String processedEvent;
	private Time timeEvent;
	
	public void setEventInMemory (String event) {
		this.processedEvent = event;
	}
	
	public void setTimeOccurrentEvent (Time time) {
		this.timeEvent = time;
	}
	
	public String getProcessedEvent () {
		return this.processedEvent;
	}
	
	public Time getTimeEvent () {
		return this.timeEvent;
	}
}
