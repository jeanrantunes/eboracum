package eboracum.wsn.network.node.sensor.controled;

import ptolemy.actor.NoTokenException;
import ptolemy.actor.util.Time;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.vergil.kernel.attributes.EllipseAttribute;

import eboracum.wsn.agent.PSAgent;
import eboracum.wsn.network.node.sensor.ControledWSNNode;
import eboracum.wsn.network.node.sensor.cpu.SimpleFIFOBasedCPU;

public class PSControledWSNNode extends ControledWSNNode{

	private static final long serialVersionUID = 1L;
	protected EllipseAttribute _circle_queen;
	public Parameter threshold;
	public Parameter diffCycleTime;
	public Parameter hQN;
	public Parameter thresholdHopCount;
	public Parameter kHopDecay;
	public Parameter decayTime;
	public Parameter kTimeDecay;
	private Time lastDiferentiationTime;
	private Time lastDecayTime;

	public PSControledWSNNode(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
		threshold = new Parameter(this,"threshold");
		threshold.setExpression("0.14");
		diffCycleTime = new Parameter(this,"diffCycleTime");
		diffCycleTime.setExpression("14400"); //900
		hQN = new Parameter(this,"hQN");
		hQN.setExpression("50");
		thresholdHopCount = new Parameter(this,"thresholdHopCount");
		thresholdHopCount.setExpression("2");
		kHopDecay = new Parameter(this,"kHopDecay");
		kHopDecay.setExpression("0.25");
		decayTime = new Parameter(this,"decayTime");
		decayTime.setExpression("2000"); //600
		kTimeDecay = new Parameter(this,"kTimeDecay");
		kTimeDecay.setExpression("0.5");
	}
	
	public void initialize() throws IllegalActionException {
		super.initialize();
		this.cpu = new SimpleFIFOBasedCPU();
		this.myAgent = new PSAgent();
		this.myAgent.setNode(this);
		this._fireAt(this.getDirector().getModelStartTime());
		_circle_queen.fillColor.setToken("{1.0, 1.0, 1.0, 0.0}");
		_circle_sensor.lineColor.setToken("{1.0, 1.0, 1.0, 0.0}");
		lastDiferentiationTime = Time.NEGATIVE_INFINITY;
		lastDecayTime = Time.NEGATIVE_INFINITY;
	}
	
	public void fire() throws NoTokenException, IllegalActionException {
		super.fire();
		//if(this.in.hasToken(0)){
//			Token channelProperties = this.in.getProperties(0);
			//System.out.println(channelProperties);
	//	}
		//else
		if ((((Double.parseDouble(battery.getValueAsString())/Double.parseDouble(idleEnergyCost.getExpression()))) > 0)) {
			
			if ((this.getDirector().getModelTime()).getDoubleValue()%Double.parseDouble(this.diffCycleTime.getValueAsString())==0){
				if (this.lastDiferentiationTime != this.getDirector().getModelTime()){
					//if ( this.getName().equals("Node0"))
						//if (this.getDirector().getModelTime().getDoubleValue()%900 == 0)
						//	System.out.println(this.getDirector().getModelTime()+";"+System.currentTimeMillis());
				//	if ( this.getName().equals("Node2"))
				//System.out.println(this.getName()+" Diff -> "+this.lastDiferentiationTime+" ; "+this.getDirector().getModelTime());
					this.lastDiferentiationTime = this.getDirector().getModelTime();
					((PSAgent)this.myAgent).differentiate();
					this._fireAt(this.getDirector().getModelTime().add(Double.parseDouble(diffCycleTime.getValueAsString())));
				}
			}
			if ((this.getDirector().getModelTime()).getDoubleValue()%Double.parseDouble(this.decayTime.getValueAsString())==0){
				if (this.lastDecayTime != this.getDirector().getModelTime()){
				//	if ( this.getName().equals("Node2"))
					//	System.out.println(this.getName()+" Decay -> "+this.lastDecayTime+" ; "+this.getDirector().getModelTime());
					this.lastDecayTime = this.getDirector().getModelTime();
					((PSAgent)this.myAgent).tdecay();
					this._fireAt(this.getDirector().getModelTime().add(Double.parseDouble(decayTime.getValueAsString())));
				}
			}
		}
		else ((PSAgent)this.myAgent).isQueen = false;
		if (((PSAgent)this.myAgent).isQueen) {
			_circle_queen.fillColor.setToken("{1.0, 1.0, 1.0, 1.0}");
			_circle_sensor.lineColor.setToken("{1.0, 1.0, 1.0, 1.0}");
		}
		else {
			_circle_queen.fillColor.setToken("{1.0, 1.0, 1.0, 0.0}");
			_circle_sensor.lineColor.setToken("{1.0, 1.0, 1.0, 0.0}");
		}
	}
	
	public boolean postfire() throws IllegalActionException{
		Boolean flag = super.postfire();
		if (!flag){
			_circle_queen.fillColor.setToken("{1.0, 1.0, 1.0, 0.0}");
			return false;
		}
		else return flag;
	}
	
	protected boolean receiveMessage(String tempMessage) throws NumberFormatException, IllegalActionException{
		String message = tempMessage.substring(2, tempMessage.length()-2);
		if (message.split(",")[1].split("=")[1].equals("ALL")){
			((PSAgent)this.myAgent).propagate(tempMessage.split(",")[0]);
			return false;
		}
		else 
			return super.receiveMessage(tempMessage);
	}
	
	protected void buildIcon() throws IllegalActionException, NameDuplicationException {
		super.buildIcon();
		_circle_queen = new EllipseAttribute(this.node_icon, "_circle_queen");
		_circle_queen.centered.setToken("true");
		_circle_queen.width.setToken("15");
		_circle_queen.height.setToken("15");
		_circle_queen.fillColor.setToken("{1.0, 1.0, 1.0, 0.0}");
		_circle_queen.lineColor.setToken("{1.0, 1.0, 1.0, 0.0}");
	}

}
