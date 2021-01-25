package eboracum.simulation.benchmarks;

import ptolemy.data.expr.Parameter;
import eboracum.simulation.BenchmarksGenerator;
import eboracum.simulation.util.HistogramSpectrogramFactory;
import eboracum.wsn.network.node.WirelessNode;

public class DroneSimulation extends BenchmarksGenerator {

	int id;

	protected void runBenchmarks() {
		this.scenarioDimensionXY = new int[] { 1000, 1000 };
		this.planning = "spiral";
//		HistogramSpectrogramFactory.newUniformSpectrogram(this.scenarioDimensionXY[1] - 100,
//				this.scenarioDimensionXY[0] - 100, "spectStartPosition.csv");
//		simBeePaperConfig("Uniform");
//		HistogramSpectrogramFactory.newNormalSpectrogram(this.scenarioDimensionXY[1]-100, this.scenarioDimensionXY[0]-100, "spectStartPosition.csv");
//		simBeePaperConfig("Normal");
		HistogramSpectrogramFactory.newInvertNormalSpectrogram(this.scenarioDimensionXY[1]-100, this.scenarioDimensionXY[0]-100, "spectStartPosition.csv");
		simBeePaperConfig("InvNormal");
	}

	public void simBeePaperConfig(String dist) {
		String simulationIdentification;
		for (int j = 0; j <= 0; j++) {
			this.id = j;
			simulationIdentification = "drone-"+this.planning+"-13-new-5ms-100Nodes" + dist + j;
			this.nodesRandomizeFlag = false;
			this.mainGatewayCenteredFlag = false;
			this.beginSetupBeePaperConfig();
			this.setupBeePaperConfig(j);
			this.endSetupBeePaperConfig(simulationIdentification);
			int numOfRounds = 1;
			for (int i = 0; i < numOfRounds; i++) {
				try {
					this.run(simulationIdentification, i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void beginSetupBeePaperConfig() {
		this.mobileSink = true;
		this.initBattery = 5400000 / 2;
		this.commCover = 105;
		this.sensorCover = 65;
		this.numOfNodes = 100;
		if (!nodesRandomizeFlag)
			generateGridPosition(numOfNodes);
		this.wirelessSensorNodesType = "GeneralType";
		this.cpuCost = 50;
		this.idleCost = 0.3;
		this.wirelessEvents.clear();
//		
//		
		this.wirelessEvents.put(new WirelessEvent("E0", 1, false, "{1.0, 0.0, 0.0, 1.0}",
				"<task id=\"0\"><cpu name=\"SimpleFIFOBasedCPU\" cost=\"3\"/></task>",
				"StochasticPeriodicJumperEvent"), 1);
//		
//		
//		// this.wirelessEvents.put(new WirelessEvent("E0", 0.0018, false,"{1.0, 0.0,
//		// 0.0, 1.0}", "<task id=\"0\"><cpu name=\"SimpleFIFOBasedCPU\"
//		// cost=\"1\"/></task>", "RandomMobileEvent"), 1);
		HistogramSpectrogramFactory.newPoissonHistogram(6, "periodHist.csv");
		this.wirelessNodes.clear();
	}

	protected void setupNodesSpecificParameters(WirelessNode e) {
		switch (this.id) {
		case 1:
			((Parameter) e.getAttribute("alpha")).setExpression("0");
			((Parameter) e.getAttribute("betha")).setExpression("0");
			((Parameter) e.getAttribute("delta")).setExpression("14");
			((Parameter) e.getAttribute("initThreshold")).setExpression("8");
			((Parameter) e.getAttribute("initStimulus")).setExpression("14");
			break;
		}
	}

	private void setupBeePaperConfig(int j) {
		switch (j) {
		case 0: // this.wirelessNodes.put("sensor.controlled.PSControlledWSNNode", numOfNodes);
			// this.wirelessNodes.put("sensor.controled.AntControledWSNNode", numOfNodes);
			this.wirelessNodes.put("sensor.UAVWSNNode", numOfNodes);
			// this.wirelessNodes.put("sensor.controled.GreedyWSNNode", numOfNodes);
			// this.wirelessNodes.put("sensor.controled.RandomControledWSNNode",
			// numOfNodes);
			// this.wirelessNodes.put("sensor.controled.AuctionControledWSNNode",
			// numOfNodes);
			break;
		}
	}

	private void endSetupBeePaperConfig(String simulationIdentification) {
		generateEventsXML();
		this.network = "SimpleAdHocNetwork";
		this.rebuildNetworkWhenGatewayDies = false;
		this.synchronizedRealTime = false;
		generateModel(simulationIdentification);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BenchmarksGenerator b = new DroneSimulation();
	}

}
