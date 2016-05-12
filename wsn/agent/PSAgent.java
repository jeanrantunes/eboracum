package eboracum.wsn.agent;

import ptolemy.actor.NoRoomException;
import ptolemy.kernel.util.IllegalActionException;
import eboracum.wsn.network.node.sensor.ControlledWSNNode;
import eboracum.wsn.network.node.sensor.controled.PSControlledWSNNode;

public class PSAgent implements BasicAgent{
	
	public ControlledWSNNode myNode;
	public boolean isQueen;
	public boolean willStopBeQueen;
	public double h;

	public PSAgent(){
		super();
		this.isQueen = false;
	}
	
	public boolean eventSensed(String tempEvent){
		if (isQueen) return true;
		else return false;
	}
	
	public void setNode(ControlledWSNNode myNode){
		this.myNode = myNode;
		this.h = Double.parseDouble(((PSControlledWSNNode)this.myNode).hQN.getValueAsString());
		this.willStopBeQueen= false; 
	}
	
	public void differentiate() throws NoRoomException, IllegalActionException{
			//System.out.println(this.myNode.getName()+" ; "+h+" ; "+((PSControledWSNNode)this.myNode).threshold.getValueAsString()+" ; "+this.myNode.getDirector().getModelTime()+" ; "+this.isQueen);
//			System.out.println((this.myNode.getDirector().getModelTime().getDoubleValue()/(Double.parseDouble(((PSControledWSNNode)this.myNode).thresholdHopCount.getValueAsString())+1))%Double.parseDouble(((PSControledWSNNode)this.myNode).diffCycleTime.getValueAsString()));
			//if ( this.myNode.getName().equals("Node54")){
			//	System.out.println(this.isQueen+ " " +this.willStopBeQueen);
			//}
		
			if (h < Double.parseDouble(((PSControlledWSNNode)this.myNode).threshold.getValueAsString())){
				this.isQueen = true;
				this.willStopBeQueen = false;
				//System.out.println("Became queen ->"+this.myNode.getName()+";"+((PSAgent)this).h);
				this.myNode.sendMessageToNeighbours("0;"+(((PSControlledWSNNode)this.myNode).hQN).getExpression(), 0.0006);
			}
			else {
				if (willStopBeQueen) {
					this.isQueen = false;
					this.willStopBeQueen = false;
				}
				//if ((this.myNode.getDirector().getModelTime().getDoubleValue()/3)%Double.parseDouble(((PSControledWSNNode)this.myNode).diffCycleTime.getValueAsString())!=0) 
				else this.willStopBeQueen = true;
			}
	}
	
	public void propagate(String message) throws NoRoomException, NumberFormatException, IllegalActionException{
		if (Integer.parseInt(message.split("=")[1].split(";")[0]) < Integer.parseInt(((PSControlledWSNNode)this.myNode).thresholdHopCount.getValueAsString())){
			//if ( this.myNode.getName().equals("Node2"))
				//System.out.println(this.myNode.getName()+";"+((PSAgent)this).h+";"+((Integer.parseInt(message.split("=")[1].split(";")[0])+1))+";"+(Double.parseDouble(message.split("=")[1].split(";")[1])*Double.parseDouble(((PSControledWSNNode)this.myNode).kHopDecay.getValueAsString())));
			//if (!this.isQueen) 
				h += Double.parseDouble(message.split("=")[1].split(";")[1]);
			try {
				//if ( this.myNode.getName().equals("Node2"))
					//System.out.println(this.myNode.getName()+";"+((PSAgent)this).h+";"+((Integer.parseInt(message.split("=")[1].split(";")[0])+1))+";"+(Double.parseDouble(message.split("=")[1].split(";")[1])*Double.parseDouble(((PSControledWSNNode)this.myNode).kHopDecay.getValueAsString())));
				this.myNode.sendMessageToNeighbours((Integer.parseInt(message.split("=")[1].split(";")[0])+1)+";"+(Double.parseDouble(message.split("=")[1].split(";")[1])*Double.parseDouble(((PSControlledWSNNode)this.myNode).kHopDecay.getValueAsString())), 0.0006);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void tdecay(){
		if (!this.isQueen || (this.isQueen && this.willStopBeQueen))
			//|| ((this.myNode.getDirector().getModelTime().getDoubleValue()/3)%Double.parseDouble(((PSControledWSNNode)this.myNode).diffCycleTime.getValueAsString())==0 || (this.myNode.getDirector().getModelTime().getDoubleValue()/5)%Double.parseDouble(((PSControledWSNNode)this.myNode).diffCycleTime.getValueAsString())==0))
			//if ( this.myNode.getName().equals("Node2"))
				//System.out.println("Decay: "+this.myNode.getName()+";"+((PSControledWSNNode)this.myNode).kTimeDecay.getValueAsString());
			h *= Double.parseDouble(((PSControlledWSNNode)this.myNode).kTimeDecay.getValueAsString());
	}
	
}
