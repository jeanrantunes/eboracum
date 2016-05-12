package eboracum.wsn.network.node.sensor.mobile;

import eboracum.wsn.network.node.sensor.SimpleWSNNode;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Entity;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.ChangeRequest;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.Location;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.moml.MoMLChangeRequest;

public class SimpleMobileWSNNode extends SimpleWSNNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Parameter velocityParam;   
    public Parameter directionParam;

    public SimpleMobileWSNNode(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
        this.iconColor = "{0.1, 0.8, 0.5, 1.0}";
        velocityParam = new Parameter (this, "VelocityParam"); 
        velocityParam.setExpression("10");
        directionParam = new Parameter (this, "DirectionParam"); 
        directionParam.setExpression("0");
    }
    
    public boolean move() throws IllegalActionException{
        // Move event and request for actualisation of event location parameter 
        ChangeRequest doRandomize;
        try {
            doRandomize = new MoMLChangeRequest(this, this.getContainer(), moveNode());
            this.getContainer().requestChange(doRandomize);
        }  catch (NameDuplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        workspace().incrVersion();
        return true;
    	//System.out.println(this.message);
    }
    
    /*public boolean postfire() throws IllegalActionException{
    	Location locationAttribute = (Location) this.getAttribute("_location");
    	double [] _myLocation;
    	_myLocation = locationAttribute.getLocation(); 
    	//System.out.println(_myLocation[0]+","+_myLocation[1]);
    	if (_myLocation[0] <= -100 || _myLocation[1] <= -100 || _myLocation[0] >= 1100 || _myLocation[1] >= 1100)
        	return false;
    	return super.postfire();
    }*/
    
    public void setVelocity(double velocity){
        velocityParam.setExpression(Double.toString(velocity));
     }
     
     public void setDirection(double direction){
        directionParam.setExpression(Double.toString(direction));
     }
        
     public double getVelocity(){
         return Double.parseDouble(velocityParam.getExpression());
     }
     public double getDirection(){
         return Double.parseDouble(directionParam.getExpression());
     }
     
     public String moveNode() throws IllegalActionException, NameDuplicationException{
         // Move node for a new position
         double [] _myLocation = newPosition();
         return setLocation(_myLocation[0], _myLocation[1]);// save new location onto the MoML       
     }
     
     public double [] newPosition() {
         //recalculate its location (x, y), according to movement parameters (velocity, direction, timeBetweenDirectionChanges) 
         double [] _newLocation;
         double deltaX, deltaY, directionCorrection;
         Location locationAttribute = (Location) this.getAttribute("_location");
         _newLocation = locationAttribute.getLocation(); 
         directionCorrection =getDirection(); 
         deltaX = format(Math.cos(Math.toRadians(directionCorrection)));
         if (deltaX >=0) { 
             deltaX =  Math.ceil(deltaX)*getVelocity();
         }
         else { 
             deltaX =  Math.floor(deltaX)*getVelocity();
         }
         _newLocation[0] = _newLocation[0] + deltaX; // X coordinate 
         deltaY = format(Math.sin(Math.toRadians(directionCorrection))*-1); // Position (0,0) is in the left-top instead of in the middle
         if (deltaY >=0) { 
             deltaY =  Math.ceil(deltaY)*getVelocity(); }
         else {                                               
             deltaY =  Math.floor(deltaY)*getVelocity();
         }
        _newLocation[1] = _newLocation[1] + deltaY; // Y coordinate
        return _newLocation;
      }
     
     public String setLocation(double x, double y) throws IllegalActionException{ 
         double[] p = new double[2];
         CompositeEntity container = (CompositeEntity) this.getContainer();
         p[0] = x;  
         p[1] = y; 
         // save new location onto the MoML       
         return _getLocationSetMoML(container, this, p);
     }
     
     protected String _getLocationSetMoML(CompositeEntity container,
             Entity node, double[] location) throws IllegalActionException {
         // First figure out the name of the class of the _location
         // attribute.  Usually, it is ptolemy.kernel.util.Location,
         // but another possibility is
         // ptolemy.actor.parameters.LocationParameter.
         Attribute locationAttribute = node.getAttribute("_location");
         String className = null;
         if (locationAttribute != null) {
             className = locationAttribute.getClass().getName();
             return "<property name=\"" + node.getName(container)
                     + "._location\" " + "class=\"" + className + "\" value=\"["
                     + location[0] + ", " + location[1] + "]\"/>\n";
         } else {
             // The _location attribute does not exist.
             // FIXME: We could make a new attribute first instead of
             // throwing an exception here.
             throw new IllegalActionException(
                     "The _location attribute does not exist for node = " + node
                             + "with container = " + container);
         } 
     }
      
     private static double format(double value) {
         return (double)Math.round(value * 1000000) / 1000000;
     }
 
}
