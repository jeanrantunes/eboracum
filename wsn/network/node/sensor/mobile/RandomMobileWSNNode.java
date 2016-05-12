package eboracum.wsn.network.node.sensor.mobile;

import java.util.Random;

import eboracum.wsn.network.AdHocNetwork;
import ptolemy.actor.TypedAtomicActor;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.Location;
import ptolemy.kernel.util.NameDuplicationException;

public class RandomMobileWSNNode extends SimpleMobileWSNNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Parameter jumpRadiusParam;

	public RandomMobileWSNNode(CompositeEntity container, String name)
			throws IllegalActionException, NameDuplicationException {
		super(container, name);
		jumpRadiusParam = new Parameter (this, "JumpRadiusParam"); 
		jumpRadiusParam.setExpression("20");
		// TODO Auto-generated constructor stub
	}
	
	public double [] newPosition() {
        // Recalculate event location (x, y), using its current position plus a random delta (base on the range from -10 until +10)
        double [] _newLocation;
        int inputRandomX, inputRandomY;
        Location locationAttribute = (Location) this.getAttribute("_location");
        _newLocation = locationAttribute.getLocation(); 
        Random RandomGen = new Random();
        int radius = Integer.parseInt(jumpRadiusParam.getExpression());
        inputRandomX = (RandomGen.nextInt(41))-radius; 
        inputRandomY = (RandomGen.nextInt(41))-radius;
        //System.out.println(inputRandomX+" - "+inputRandomY);
        double inputX = new Double (inputRandomX);
        _newLocation[0]+=inputX;
        double inputY = new Double (inputRandomY);
        _newLocation[1]+=inputY;
        return _newLocation;
    }
	
}
