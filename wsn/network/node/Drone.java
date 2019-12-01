package eboracum.wsn.network.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eboracum.wsn.network.node.sensor.cpu.Block;
import ptolemy.actor.NoTokenException;
import ptolemy.actor.util.Time;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.expr.StringParameter;
import ptolemy.domains.wireless.kernel.WirelessIOPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Entity;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.ChangeRequest;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.moml.MoMLChangeRequest;

public class Drone extends NetworkMainGateway {
	protected WirelessIOPort inPort, outPort;
	private static final long serialVersionUID = 1L;
	private int x, y;
	private Map<String,ArrayList<Block>> memoryDrone;
	protected int[] scenarioXY;
	private int directionX;
	private int directionY;
	private int speed;
	private int detectionDistance;
	private int count = 0;
	private boolean goBackToOrigin;
	
	public Drone(CompositeEntity container, String name) throws IllegalActionException, NameDuplicationException {
		super(container, name);
		this.x = 0;
		this.y = 0;
		this.scenarioXY = new int[2];
		this.directionX = 1;
		this.directionY = 1;
		this.speed = 100;
		this.detectionDistance = 100;
		this.goBackToOrigin = false;
		
		Parameter axisX = new Parameter(this,"Size axis X");
		Parameter axisY = new Parameter(this,"Size axis Y");
		axisX.setExpression("1000");
		axisY.setExpression("1000");
		
		this.scenarioXY[0] = Integer.parseInt(axisX.getValueAsString());
		this.scenarioXY[1] = Integer.parseInt(axisY.getValueAsString());

		inPort = new WirelessIOPort(this, "inputDrone", true, false);
		inPort.outsideChannel.setExpression("$CommChannelName");
		outPort = new WirelessIOPort(this, "outputDrone", false, true);
		outPort.outsideChannel.setExpression("$CommChannelName");
		memoryDrone = new HashMap<String,ArrayList<Block>>();
	}
	
	public void initialize() throws IllegalActionException{
		super.initialize();
	}
	
	@SuppressWarnings("null")
	public void fire() throws IllegalActionException {
		super.fire();
		zigzag();
		if(inPort.hasToken(0)){
			addEventToTheMemoryDrone(inPort.get(0).toString());
		}
		outPort.send(0, new StringToken("{message=DroneHello}"));
	}

	public void zigzag() throws NoTokenException, IllegalActionException {
		/*finished scenario*/
		if (this.goBackToOrigin) {
			goBackToOrigin();
		} else if (this.x == this.scenarioXY[0] && this.y == this.scenarioXY[1]) {
			this.goBackToOrigin = true;
			goBackToOrigin();
		} else if (this.detectionDistance - this.count == 0 && this.directionX == 1) {
			this.directionX = -1;
			this.x += this.speed * this.directionX;
			this.count = 0;
		} else if (this.detectionDistance - this.count == 0 && this.directionX == -1) {
			this.directionX = 1;
			this.x += this.speed * this.directionX;
			this.count = 0;
		} else if ((this.x == this.scenarioXY[0] && this.directionX == 1) || (this.x == 0 && this.directionX == -1)) {
			this.y += this.speed * this.directionY;
			this.count += this.speed;
		} else {
			this.x += this.speed * this.directionX;
		}
		
		move(this.x,this.y);
	}
	
	public void goBackToOrigin() throws NoTokenException, IllegalActionException {
		if (this.x == 0 && this.y == 0)	{
			this.goBackToOrigin = false;
		}
		
		if (this.x == 0 && this.y < 0) {
			this.y += this.speed;
		} else if (this.x == 0 && this.y > 0) {
			this.y -= this.speed;
		} else if (this.x > 0) {
			this.x -= this.speed;
		} else if (this.x < 0) {
			this.x += this.speed;
		}
		
		if (this.y == 0 && this.x < 0) {
			this.x += this.speed;
		} else if (this.y == 0 && this.x > 0) {
			this.x -= this.speed;
		} else if (this.y > 0) {
			this.y -= this.speed;
		} else if (this.y < 0) {
			this.y += this.speed;
		} 
	}

	public void addEventToTheMemoryDrone(String received) throws NumberFormatException, IllegalActionException {
		String[] aux = received.split("[\\=\\,\\{\\}\\\"]");
		System.out.println(aux);
		if(aux[aux.length-1].equals("Drone")) {
			String node = null;
			Block b = new Block();

			for(int i=0; i < aux.length; i++) {
				if(aux[i].equals("node")) {
					node = aux[i+1];
				}
				else if(aux[i].equals("event")) {
					b.setEventInMemory(aux[i+1]);
				}
				else if(aux[i].equals("time")) {
					b.setTimeOccurrentEvent(new Time(this.getDirector(),Double.parseDouble(aux[i+1])));
				}
			}
			
			if(memoryDrone.containsKey(node)) {
				memoryDrone.get(node).add(b);
			}
			else { 
				memoryDrone.put(node, new ArrayList<Block>(Arrays.asList(b)));
			}
		}
	}
	
	public void move(int x, int y) throws NoTokenException, IllegalActionException {
        ChangeRequest doRandomize;
        try {
            doRandomize = new MoMLChangeRequest(this, this.getContainer(), moveNode(x, y));
            this.getContainer().requestChange(doRandomize);
        }  catch (NameDuplicationException e) {
            e.printStackTrace();
        }
        workspace().incrVersion();
    }
	
	public String moveNode(int x, int y) throws IllegalActionException, NameDuplicationException{
    	double[] _newLocation = new double[2]; 
	    double inputX = new Double (x);
	    _newLocation[0]= inputX;
	    double inputY = new Double (y);
	    _newLocation[1]= inputY;
        return setLocation(_newLocation[0], _newLocation[1]);      
    }
	
	public String setLocation(double x, double y) throws IllegalActionException{ 
        double[] p = new double[2];
        CompositeEntity container = (CompositeEntity) this.getContainer();
        p[0] = x;  
        p[1] = y; 
        return _getLocationSetMoML(container, this, p);
    }
    
    protected String _getLocationSetMoML(CompositeEntity container,
        Entity node, double[] location) throws IllegalActionException {
        Attribute locationAttribute = node.getAttribute("_location");
        String className = null;
        if (locationAttribute != null) {
            className = locationAttribute.getClass().getName();
            return "<property name=\"" + node.getName(container)
                    + "._location\" " + "class=\"" + className + "\" value=\"["
                    + location[0] + ", " + location[1] + "]\"/>\n";
        } else {
            throw new IllegalActionException(
                    "The _location attribute does not exist for node = " + node
                            + "with container = " + container);
        } 
    }
}
