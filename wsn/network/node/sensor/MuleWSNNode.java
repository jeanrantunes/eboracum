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

public class MuleWSNNode extends BasicWirelessSensorNode {
	protected WirelessIOPort outPort, inPort;
	private static final long serialVersionUID = 1L;
	public MuleWSNNode(CompositeEntity container, String name) throws IllegalActionException, NameDuplicationException {
		super(container, name);
		inPort = new WirelessIOPort(this, "inputNodeMule", true, false);
		inPort.outsideChannel.setExpression("$CommChannelName");
		outPort = new WirelessIOPort(this, "outputNodeMule", false, true);
		outPort.outsideChannel.setExpression("$CommChannelName");
	}

	public void initialize() throws IllegalActionException {
		super.initialize();
		this.cpu = new SimpleFIFOBasedCPU();
	}
	
	public void fire() throws NoTokenException, IllegalActionException {
		super.fire();

		if(inPort.hasToken(0)) {
			if(this.hankshake()) {	
				Block aux = this.cpu.getBlockMemory();
				sendEventsToDrone(aux);
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
		return true;
	}
}
