package eboracum.wsn.network.node.sensor.cpu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ptolemy.actor.util.Time;

public class SimpleFIFOBasedCPU implements SensorCPU{
	
	private Map<String, Double> taskCostMap;
	private Queue<String> queue;
	private Time nextTimeFree;
	private List<Object> tempReturn;
	private List<Block> memory;
	private Block block;
	
	public SimpleFIFOBasedCPU() {
		this.taskCostMap = new HashMap<String, Double>();
		this.processTaskCostMap();
		this.queue = new LinkedList<String>();
		this.nextTimeFree = Time.NEGATIVE_INFINITY;
		this.queue.clear();
		this.tempReturn = new ArrayList<Object>();
		this.memory = new ArrayList<Block>();
		this.block = new Block(); 
	}
	
	public List<Object> run(String task, Time currentTime){
//		System.out.println(task);
		String processedEvent = null;
		if (task != null)
			this.queue.add(task);
		if (this.queue.size() > 0 && currentTime.compareTo(this.nextTimeFree) == 0) {
			processedEvent = this.queue.remove();
			this.nextTimeFree = Time.NEGATIVE_INFINITY;
        }
		if (this.nextTimeFree.equals(Time.NEGATIVE_INFINITY) && this.queue.size() > 0) {
			Double taskCost = taskCostMap.get(this.queue.element().split("_")[0]);
			if (taskCost != null) {
				this.nextTimeFree = currentTime.add(round(taskCostMap.get(this.queue.element().split("_")[0])));
			}
		}
		//if (task != null) processedEvent = task;
		this.tempReturn.clear();
		tempReturn.add(new Integer(this.queue.size()));
		tempReturn.add(processedEvent);
		//System.out.println(this.nextTimeFree+"  "+task);
		tempReturn.add(this.nextTimeFree);
		//System.out.print(queue+";");
		
		if (processedEvent != null) {
			this.block = new Block(); 
			block.setEventInMemory(processedEvent);
			block.setTimeOccurrentEvent(currentTime);
			this.memory.add(block);
		}
		
		return tempReturn;
	}
	
	public boolean isEmptyMemory() {
		return this.memory.isEmpty();
	}
	
	public Block getBlockMemory() {
		Block b = this.memory.get(0);
		this.memory.remove(0);
		return b;
	}
	
	public void showDataMemory() {
		for(Block item: this.memory) {
			
			System.out.println("[ " +item.getProcessedEvent()+"--"+item.getTimeEvent() +" ]");
		}
		System.out.println("--");
		System.out.println();
	}
	
	protected void processTaskCostMap(){
		try {
			File fXmlFile = new File(SimpleFIFOBasedCPU.PLATFORMCONFIG);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("event");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nEvent = nList.item(i);
				if (nEvent.getNodeType() == Node.ELEMENT_NODE) {
					double eventCost = 0.0;
					for (int j = 0; j < ((Element)nEvent).getElementsByTagName("task").getLength(); j++) {
						Node nTask = (Element)((Element)nEvent).getElementsByTagName("task").item(j);
						for (int k = 0; k < ((Element)((Element)(nTask))).getElementsByTagName("cpu").getLength(); k++) {
							Node nCpu = ((Element)((Element)(nTask))).getElementsByTagName("cpu").item(k);
							if (this.getClass().getSimpleName().equals(((Element)nCpu).getAttribute("name"))){
								eventCost += Double.parseDouble(((Element)nCpu).getAttribute("cost"));
							}
						}
					}
					this.taskCostMap.put(((Element)nEvent).getAttribute("type"), eventCost);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public double round(double value) {
		return Math.round(value*10000000.0)/10000000.0;
	}
		
}
