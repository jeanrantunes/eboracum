package eboracum.wsn.network.node.sensor;

import eboracum.wsn.network.node.sensor.cpu.SimpleFIFOBasedCPU;
import eboracum.wsn.network.node.sensor.cpu.Block;
import ptolemy.actor.NoRoomException;
import ptolemy.actor.NoTokenException;
import ptolemy.data.StringToken;
import ptolemy.domains.wireless.kernel.WirelessIOPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class UAVWSNNode extends BasicWirelessSensorNode {
	protected WirelessIOPort outPort, inPort;
	
	private static final long serialVersionUID = 1L;
	public UAVWSNNode(CompositeEntity container, String name) throws IllegalActionException, NameDuplicationException {
		super(container, name);
		inPort = new WirelessIOPort(this, "inputNodeMule", true, false);
		inPort.outsideChannel.setExpression("$CommChannelName");
		outPort = new WirelessIOPort(this, "outputNodeMule", false, true);
		outPort.outsideChannel.setExpression("$CommChannelName");
	}

	public void initialize() throws IllegalActionException {
		super.initialize();
		this.cpu = new SimpleFIFOBasedCPU();
		this.gateway.setExpression("Drone");
	}
	
	public void fire() throws NoTokenException, IllegalActionException {
		super.fire();
		
		if(inPort.hasToken(0)) {
			if(this.hankshake()) {
				Block aux = this.cpu.getBlockMemory();
				double commCost = this.eventCommCostMap.get(aux.getProcessedEvent().split("_")[0]);
			
				Boolean hasSent = sendEventsToDrone(aux);
//				communication cost when event is sent to drone
				if (hasSent) {
					this.battery.setExpression(Double.toString( ( Double.parseDouble(this.battery.getValueAsString()) - commCost )));
					this.batterySpentWithDrone += commCost;
				}
			}
		}
	}
	
	public boolean hankshake() throws NoTokenException, IllegalActionException {
		String message = inPort.get(0).toString().split("=")[1].split("}")[0];
		if(message.equals("DroneHello") && !this.cpu.isEmptyMemory()) {
			return true;
		}
		return false;
	}

	public boolean sendEventsToDrone(Block memory) throws NoRoomException, IllegalActionException {
		outPort.send(0, new StringToken("{node="+this.getFullName()+",event="+memory.getProcessedEvent()+",time="+memory.getTimeEvent().toString()+",target=Drone}"));
		this.numberOfSentMessages ++;
		return true;
	}
}
