package eboracum.wsn.network.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eboracum.simulation.BenchmarksGenerator;
import eboracum.simulation.benchmarks.DroneSimulation;
import eboracum.wsn.network.node.sensor.cpu.Block;
import ptolemy.actor.NoTokenException;
import ptolemy.actor.util.Time;
import ptolemy.data.StringToken;
import ptolemy.data.Token;
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

public class UAV extends WirelessNode {
	protected WirelessIOPort inPort, outPort;
	private static final long serialVersionUID = 1L;
	private int x, y;
	private Map<String,ArrayList<Block>> memoryDrone;
	protected int[] scenarioXY;
	private int directionX;
	private int directionY;
	private int speed;
	private int detectionDistance;
	private int detectionDistanceX;
	private int detectionDistanceY;
	private int accDetectionDistance;
	private int count = 0;
	private boolean goBackToOrigin;
	private int numberOfHankShake = 0;
	private ArrayList<String> lastEventsRound;
	private int originX, originY;
	public int eventSensoredByDroneCounter;
	public int eventSensoredByDroneGenCounter;
	private int dayCounter;
	private int twoRounds;
	public long startTime;
	private String planning;
	
	public ArrayList <Integer> detailEventSensoredByDroneCounter; 
	@SuppressWarnings("unused")
	
	public UAV(CompositeEntity container, String name) throws IllegalActionException, NameDuplicationException {
		super(container, name);
		
		StringParameter planning = new StringParameter(this,"Planning");
		planning.setExpression("spiral");
		
		this.planning = planning.getExpression();
		this.scenarioXY = new int[2];
		this.directionX = 1;
		this.directionY = 1;
		this.detectionDistance = 100;
		this.detectionDistanceX = 0;
		this.detectionDistanceY = 0;
		this.accDetectionDistance = this.detectionDistance;
		this.goBackToOrigin = false;
		this.lastEventsRound = new ArrayList();
		this.eventSensoredByDroneCounter = 0;
		this.dayCounter = 1;
		this.twoRounds = 0;
		
		Parameter axisX = new Parameter(this,"Size axis X");
		Parameter axisY = new Parameter(this,"Size axis Y");
		
		axisX.setExpression("1000");
		axisY.setExpression("1000");
		
		
		this.scenarioXY[0] = Integer.parseInt(axisX.getValueAsString());
		this.scenarioXY[1] = Integer.parseInt(axisY.getValueAsString());
		
		this.x = (int) (0.10 * this.scenarioXY[0]);
		this.y = (int) (0.10 * this.scenarioXY[1]);
		this.originX = this.x;
		this.originY = this.x;
		
		this.speed = 5;
		
		this.scenarioXY[0] *= 0.90;
		this.scenarioXY[1] *= 0.90;
		
		inPort = new WirelessIOPort(this, "inputDrone", true, false);
		inPort.outsideChannel.setExpression("$CommChannelName");
		outPort = new WirelessIOPort(this, "outputDrone", false, true);
		outPort.outsideChannel.setExpression("$CommChannelName");
		memoryDrone = new HashMap<String,ArrayList<Block>>();
	}
	
	public void initialize() throws IllegalActionException{
		super.initialize();
		this.startTime = System.currentTimeMillis();
		this.eventSensoredByDroneCounter = 0;
		detailEventSensoredByDroneCounter = new ArrayList<Integer>();
	}
	
	@SuppressWarnings("null")
	public void fire() throws IllegalActionException {
		super.fire();
		if (this.planning.equals("") || this.planning.equals("bff")) {
			zigzag();
		} else if (this.planning.equals("spiral")) {
			spiral();
		}
		
		if (inPort.hasToken(0) && !this.goBackToOrigin){
			Token message = inPort.get(0);
			if (message != null) {
				addEventToTheMemoryDrone(message.toString());
			}
		} else if (!this.goBackToOrigin) {
			outPort.send(0, new StringToken("{message=DroneHello}"));
		}
	}

	public void zigzag() throws NoTokenException, IllegalActionException {
		if (this.goBackToOrigin == true && this.x == this.originX && this.y == this.originY) {
			this.directionX *= -1;
			this.directionY *= -1;
			this.goBackToOrigin = false;
			this.x += this.speed * this.directionX;
			this.twoRounds++;
			if (this.twoRounds == 5) {
				/*remove half items of captured events of array*/
				this.twoRounds = 0;
				int s =(int) Math.floor(this.lastEventsRound.size() * 0.2);
				int length = this.lastEventsRound.size();
				this.lastEventsRound = new ArrayList(this.lastEventsRound.subList(s, length));
			}
		} else if (this.x == this.scenarioXY[0] && this.y == this.scenarioXY[1]) {
			this.goBackToOrigin = true;
			this.directionX *= -1;
			this.directionY *= -1;
			this.x += this.speed * this.directionX;
		} else if (this.detectionDistance - this.count == 0 && this.directionX == 1) {
			this.directionX = -1;
			this.x += this.speed * this.directionX;
			this.count = 0;
		} else if (this.detectionDistance - this.count == 0 && this.directionX == -1) {
			this.directionX = 1;
			this.x += this.speed * this.directionX;
			this.count = 0;
		} else if ((this.x == this.scenarioXY[0] && this.directionX == 1) || (this.x == this.originX && this.directionX == -1)) {
			this.y += this.speed * this.directionY;
			this.count += this.speed;
		} else {
			this.x += this.speed * this.directionX;
		}
		
		move(this.x,this.y);
	}
	
	public void spiral() throws NoTokenException, IllegalActionException {
		if (this.goBackToOrigin) {
			this.twoRounds++;
			if (this.twoRounds == 5) {
				/*remove half items of captured events of array*/
				this.twoRounds = 0;
				int s =(int) Math.floor(this.lastEventsRound.size() * 0.2);
				int length = this.lastEventsRound.size();
				this.lastEventsRound = new ArrayList(this.lastEventsRound.subList(s, length));
			}
			goBackToOrigin();
		} else if (this.goBackToOrigin == false && 
				this.scenarioXY[0]/2 - this.detectionDistance/2 <= this.x && 
				this.x <= this.scenarioXY[0]/2 + this.detectionDistance/2 && 
				this.scenarioXY[1]/2 - this.detectionDistance/2 <= this.y && 
				this.y <= this.scenarioXY[1]/2 + this.detectionDistance/2) {
			this.goBackToOrigin = true;
			this.x += this.speed * this.directionX * 2;
			this.directionX = -1;
			goBackToOrigin();
		} else if ((this.y - (this.detectionDistanceY + this.detectionDistance)) <= this.originY && this.directionY == -1) { //turn right
			this.detectionDistanceX += this.detectionDistance;
			this.detectionDistanceY += this.detectionDistance;
			this.directionX = 1;
			this.directionY = 1;
			this.x += this.speed * this.directionX;
		} else if (this.x <= (this.originX + this.detectionDistanceX) && this.directionX == -1) { //top
			this.directionY = -1;
			this.y += this.speed * this.directionY;
		} else if (this.y >= (this.scenarioXY[1] - this.detectionDistanceY)) { //turn left
			this.directionX = -1;
			this.x += this.speed * this.directionX;
		} else if (this.x >= (this.scenarioXY[0] - this.detectionDistanceX)) { //bottom
			this.y += this.speed * this.directionX;
		}  
		else { //right
			this.x += this.speed * this.directionX;
		}
		
		move(this.x,this.y);
	}
	
	public void goBackToOrigin() throws NoTokenException, IllegalActionException {
		if (this.x == this.originX && this.y == this.originY) {
			this.directionX = 1;
			this.directionY = 1;
			this.detectionDistanceX = 0;
			this.detectionDistanceY = 0;
			this.goBackToOrigin = false;
		}
		else if (this.y <= this.detectionDistanceY && this.directionX == 1) {
			this.directionX = -1;
			this.directionY = 1;
			this.detectionDistanceX -= this.detectionDistance;
			this.detectionDistanceY -= this.detectionDistance;
			this.x += this.speed * this.directionX;
		} 
		else if (this.x >= (this.scenarioXY[0] - this.detectionDistanceX + this.detectionDistance)) {
			this.directionY = -1;
			this.y += this.speed * this.directionY;
		}
		else if (this.y >= (this.scenarioXY[1] - this.detectionDistanceY + this.detectionDistance)) {
			this.directionX = 1;
			this.x += this.speed * this.directionX;
		}
		else if (this.x <= this.detectionDistanceX && this.directionX == -1) {
			this.y += this.speed * this.directionY; 
		} 
		else  {
			this.x += this.speed * this.directionX;
		}
	}

	public void addEventToTheMemoryDrone(String received) throws NumberFormatException, IllegalActionException {
		String[] aux = received.split("[\\=\\,\\{\\}\\\"]");
	
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
//			System.out.println(this.lastEventsRound.size());
//			System.out.println(this.lastEventsRound);
			if (!this.lastEventsRound.contains(b.getProcessedEvent())) {
				this.lastEventsRound.add(b.getProcessedEvent());
				if(memoryDrone.containsKey(node)) {
					memoryDrone.get(node).add(b);
					
					this.eventSensoredByDroneGenCounter++;
					this.eventSensoredByDroneCounter++;
					if (this.getDirector().getModelTime().getDoubleValue()/(3600*24) > this.dayCounter){	
						detailEventSensoredByDroneCounter.add(this.eventSensoredByDroneCounter);
						this.dayCounter++;
						this.eventSensoredByDroneCounter = 0;
					}
					
				}
				else { 
					this.eventSensoredByDroneCounter++;
					memoryDrone.put(node, new ArrayList<Block>(Arrays.asList(b)));
				}
			}
		}
	}
	
	public int eventsInDroneMemory() {
		Set<Entry<String, ArrayList<Block>>> set = this.memoryDrone.entrySet();
		Iterator it = set.iterator();
		int count = 0;
		while(it.hasNext()){
			Entry<String, ArrayList<Block>> entry = (Entry)it.next();
			count += entry.getValue().size();
		}
		
		return count;
	}
	
	public void hasDuplicatedItemsOnDroneMemory() {
		System.out.println("\n events repeted: \n");
		
		Set<Entry<String, ArrayList<Block>>> set = this.memoryDrone.entrySet();
		Iterator it = set.iterator();
		ArrayList<String> eventsTemp = new ArrayList<String>();
		
		while(it.hasNext()){
			Entry<String, ArrayList<Block>> entry = (Entry)it.next();
			for(Block block: entry.getValue()) {
				eventsTemp.add(block.getProcessedEvent());
//				System.out.println("- blocks " + block.getProcessedEvent());
			}
		}
		int count = 0;
		for (int i = 0; i < eventsTemp.size(); i++) {
			for (int j = 0; j < eventsTemp.size(); j++) {
				if (eventsTemp.get(i).equals(eventsTemp.get(j)) && i!=j) {
//					System.out.println("- repeted item i: " + eventsTemp.get(i));
//					System.out.println("- repeted item j: " + eventsTemp.get(j));
//					System.out.println("- index: i" + i + " index: j" + j);
					System.out.println("- There are repeted items");
					return;
				}
			}
		}
		System.out.println("- There are not repeted items");
		
//		System.out.println("-repeted items :" +count);
		
//		int count = 0, count2 = 0;
//		String lastNodeWithItem = "";
//		
//		for(String item: this.lastEventsRound) {
//			Set<Entry<String, ArrayList<Block>>> set = this.memoryDrone.entrySet();
//			Iterator it = set.iterator();
//			count = 0;
//			lastNodeWithItem = "";
//			while(it.hasNext()){
//				
//				Entry<String, ArrayList<Block>> entry = (Entry)it.next();
////				System.out.println("node -> " + entry.getKey());
//				for(Block block: entry.getValue()) {
////					System.out.println("- blocks " + block.getProcessedEvent() + "  " + item);
//					if (block.getProcessedEvent() == item) {
//						count ++; 
//						count2 ++;
//					}
//					if (count == 1) {
//						lastNodeWithItem = entry.getKey();
//					} else if (count > 1) {
//						System.out.println("======\n");
//						System.out.println("Have repeated items: " + block.getProcessedEvent() + " - " + item);
//						System.out.println("In the nodes " + entry.getKey() + " and " + lastNodeWithItem + "\n\n");
//						System.out.println("======");
//					}
//				}
//			}
//		}
	}
	
	public String getMemory() {
		Set<Entry<String, ArrayList<Block>>> set = this.memoryDrone.entrySet();
		Iterator it = set.iterator();
		String events = "\nNodes in drone memory: \n";
		
		while(it.hasNext()){
			Entry<String, ArrayList<Block>> entry = (Entry)it.next();
//		    System.out.println(entry.getKey() + ":\n");
//			events += entry.getKey() + ";" + entry.getValue().size() + "\n";
			events += "-->" + entry.getKey() + " :\n";
			for(Block item: entry.getValue()) {
				events += "[" +item.getProcessedEvent()+"--"+item.getTimeEvent() +"]\n";
//				System.out.println("[ " +item.getProcessedEvent()+"--"+item.getTimeEvent() +" ]");
			}
		    
		}
	    
		return events;
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
