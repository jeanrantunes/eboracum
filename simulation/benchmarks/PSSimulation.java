package eboracum.simulation.benchmarks;

import eboracum.simulation.BenchmarksGenerator;
import eboracum.simulation.util.HistogramSpectrogramFactory;

public class PSSimulation extends BenchmarksGenerator {

	protected void runBenchmarks(){
			this.scenarioDimensionXY = new int[]{1000,1000};
			HistogramSpectrogramFactory.newUniformSpectrogram(this.scenarioDimensionXY[1]-100, this.scenarioDimensionXY[0]-100, "spectStartPosition.csv");
//			simBeePaperConfig("Uniform", 49, "sensor.controlled.PSControlledWSNNode", 160);
			//simBeePaperConfig("Uniform", 64, "sensor.controlled.PSControlledWSNNode", 140);
			//simBeePaperConfig("Uniform", 81, "sensor.controlled.PSControlledWSNNode", 120);
			//simBeePaperConfig("Uniform", 100, "sensor.controlled.PSControlledWSNNode", 120);
			simBeePaperConfig("Uniform", 49, "sensor.controlled.RandomControlledWSNNode", 160);
			/*simBeePaperConfig("Uniform", 64, "sensor.controlled.RandomControlledWSNNode", 140);
			simBeePaperConfig("Uniform", 81, "sensor.controlled.RandomControlledWSNNode", 120);
			simBeePaperConfig("Uniform", 100, "sensor.controlled.RandomControlledWSNNode", 120);
			*/
			//simBeePaperConfig("Uniform", 100, "sensor.mobile.DynamicReorganizedMobileWSNNode", 120);
			/*simBeePaperConfig("Uniform", 64, "sensor.SimpleWSNNode", 140);
			simBeePaperConfig("Uniform", 81, "sensor.SimpleWSNNode", 120);
			simBeePaperConfig("Uniform", 100, "sensor.SimpleWSNNode", 120);*/
//			simBeePaperConfig("Uniform", 49, "sensor.controlled.GreedyWSNNode", 160);
			/*simBeePaperConfig("Uniform", 64, "sensor.Controlled.GreedyWSNNode", 140);
			simBeePaperConfig("Uniform", 81, "sensor.Controlled.GreedyWSNNode", 120);
			*/
			//simBeePaperConfig("Uniform", 100, "sensor.Controlled.GreedyWSNNode", 120);
			//simBeePaperConfig("Uniform", 49, "sensor.controlled.AntControlledWSNNode", 160);
			//simBeePaperConfig("Uniform", 64, "sensor.controlled.AntControlledWSNNode", 140);
			//simBeePaperConfig("Uniform", 81, "sensor.controlled.AntControlledWSNNode", 120);
			//simBeePaperConfig("Uniform", 100, "sensor.controlled.AntControlledWSNNode", 120);
			/*HistogramSpectrogramFactory.newNormalSpectrogram(this.scenarioDimensionXY[1]-100, this.scenarioDimensionXY[0]-100, "spectStartPosition.csv");
			simBeePaperConfig("Normal", 49, "sensor.Controlled.PSControlledWSNNode", 160);
			simBeePaperConfig("Normal", 64, "sensor.Controlled.PSControlledWSNNode", 140);
			simBeePaperConfig("Normal", 81, "sensor.Controlled.PSControlledWSNNode", 120);
			simBeePaperConfig("Normal", 100, "sensor.Controlled.PSControlledWSNNode", 120);
			simBeePaperConfig("Normal", 49, "sensor.Controlled.RandomControlledWSNNode", 160);
			simBeePaperConfig("Normal", 64, "sensor.Controlled.RandomControlledWSNNode", 140);
			simBeePaperConfig("Normal", 81, "sensor.Controlled.RandomControlledWSNNode", 120);
			simBeePaperConfig("Normal", 100, "sensor.Controlled.RandomControlledWSNNode", 120);
			simBeePaperConfig("Normal", 49, "sensor.SimpleWSNNode", 160);
			simBeePaperConfig("Normal", 64, "sensor.SimpleWSNNode", 140);
			simBeePaperConfig("Normal", 81, "sensor.SimpleWSNNode", 120);
			simBeePaperConfig("Normal", 100, "sensor.SimpleWSNNode", 120);
			simBeePaperConfig("Normal", 49, "sensor.Controlled.GreedyWSNNode", 160);
			simBeePaperConfig("Normal", 64, "sensor.Controlled.GreedyWSNNode", 140);
			simBeePaperConfig("Normal", 81, "sensor.Controlled.GreedyWSNNode", 120);
			simBeePaperConfig("Normal", 100, "sensor.Controlled.GreedyWSNNode", 120);
			simBeePaperConfig("Normal", 49, "sensor.Controlled.AntControlledWSNNode", 160);
			simBeePaperConfig("Normal", 64, "sensor.Controlled.AntControlledWSNNode", 140);
			simBeePaperConfig("Normal", 81, "sensor.Controlled.AntControlledWSNNode", 120);
			simBeePaperConfig("Normal", 100, "sensor.Controlled.AntControlledWSNNode", 120);	*/		
			//HistogramSpectrogramFactory.newInvertNormalSpectrogram(this.scenarioDimensionXY[1]-100, this.scenarioDimensionXY[0]-100, "spectStartPosition.csv");
			//simBeePaperConfig("InvNormal");
	}
		
	public void simBeePaperConfig(String dist, int size, String algo, int commcover) {
		String simulationIdentification;
		simulationIdentification = "NodeGrid"+size+"_SideSink_EventSpaceDist"+dist+"_Rebuild_"+algo;
		this.nodesRandomizeFlag = false;
		this.mainGatewayCenteredFlag = false;
		this.setupBeePaperConfig(simulationIdentification, size, algo, commcover);
		int numOfRounds = 1;
		for (int i=0; i<numOfRounds; i++) {
			try {
				this.run(simulationIdentification,i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setupBeePaperConfig(String simulationIdentification, int size, String algo, int commcover){
		this.initBattery = 540/2;
		this.commCover = commcover;
		this.sensorCover = 120;
		int numOfNodes = size;
		if (!nodesRandomizeFlag) generateGridPosition(numOfNodes);
		//if (!nodesRandomizeFlag) generateRandomPosition(numOfNodes);
		this.wirelessSensorNodesType = "GeneralType";
		this.cpuCost = 50;
		this.idleCost = 0.3;
		this.wirelessNodes.clear();
		this.wirelessNodes.put(algo, numOfNodes);
		this.wirelessEvents.clear();
		this.wirelessEvents.put(new WirelessEvent("E0", 0.0018, false,"{1.0, 0.0, 0.0, 1.0}", "<task id=\"0\"><cpu name=\"SimpleFIFOBasedCPU\" cost=\"1\"/></task>", "StochasticPeriodicJumperEvent"), 1);
		HistogramSpectrogramFactory.newPoissonHistogram(120, "periodHist.csv");
		generateEventsXML();
		this.network = "SimpleAdHocNetwork";
		this.rebuildNetworkWhenGatewayDies= true;
		this.synchronizedRealTime = false;
		generateModel(simulationIdentification);
	}

		@SuppressWarnings("unused")
		public static void main(String[] args){
			BenchmarksGenerator b = new PSSimulation();
		}
		
}
