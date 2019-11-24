Installing (normal users - GUI):

Download and install PtolemyII/VisualSense version 8.0.1, following the link http://ptolemy.eecs.berkeley.edu/visualsense/ 

To use this package you must place the file EboracumActors.xml on folder ptII8.0.1/ptolemy/actor/lib and include a tag on file ptII*.*.*/ptolemy/configs/basicActorLibrary.xml,
like: <input source="ptolemy/actor/lib/EboracumActors.xml"/>


Installing (developers - Eclipse):

Download and install PtolemyII/VisualSense version 8.0.1 as a normal user.

Configure the use with Eclipse:

https://cdn.rawgit.com/icyphy/ptII/master/doc/eclipse/index.htm

You will need to put "-visualsense" in the running arguments of Vergil.

Copy the Eboracum package ("eboracum" folder) inside the folder ptII8.0.1/ and refresh the Eclipse packages view.


Using:

Through Vergil (using VisualSense) open file ptII8.0.1/eboracum/SampleModel.xml


