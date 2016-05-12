package eboracum.simulation.benchmarks;

import eboracum.simulation.BenchmarksGenerator;
import eboracum.simulation.util.HistogramSpectrogramFactory;

public class MobileNodesSimulation extends BenchmarksGenerator{

	protected void runBenchmarks(){
		setupBasicConfig("mobile");
		try {
			this.run("mobile",0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
}

private void setupBasicConfig(String simulationIdentification){
	this.scenarioDimensionXY = new int[]{1000,1000};
	HistogramSpectrogramFactory.newUniformSpectrogram(this.scenarioDimensionXY[0]-100, this.scenarioDimensionXY[0]-100, "spectStartPosition.csv");
	HistogramSpectrogramFactory.newPoissonHistogram(100, "periodHist.csv");
	this.initBattery = 200/2;
	this.commCover = 160;
	this.sensorCover = 120;
	int numOfNodes = 81;
	this.cpuCost = 50;
	this.idleCost = 0.3;
	this.nodesRandomizeFlag = false;
	if (!nodesRandomizeFlag) generateGridPosition(numOfNodes);
	this.mainGatewayCenteredFlag = false;
	this.wirelessSensorNodesType = "GeneralType";
	this.wirelessNodes.put("sensor.mobile.DynamicReorganizedMobileWSNNode", numOfNodes);
	this.wirelessEvents.put(new WirelessEvent("E0", 0.0018, false,"{1.0, 0.0, 0.0, 1.0}", "<task id=\"0\"><cpu name=\"SimpleFIFOBasedCPU\" cost=\"1\"/></task>", "StochasticPeriodicJumperEvent"), 1);
	generateEventsXML();
	this.network = "SimpleAdHocNetwork";
	this.rebuildNetworkWhenGatewayDies= true;
	this.synchronizedRealTime = true;
	generateModel(simulationIdentification);
}

@SuppressWarnings("unused")
public static void main(String[] args){
	BenchmarksGenerator b = new MobileNodesSimulation();
}

}
