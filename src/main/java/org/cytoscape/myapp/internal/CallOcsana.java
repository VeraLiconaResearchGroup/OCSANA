package org.cytoscape.myapp.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.*;
//import org.cytoscape.comman.util;

public class CallOcsana {
	
	private int number_of_nodes = 1;
	private int number_of_edges = 1;
	private String[] nodes_Name;
	private int[][] data_Table;
	private int[] sourceNodes;
	private int[] targetNodes;
	private int[] offTargetNodes;
	private Graph graph;
	public int getNumber_of_nodes() {
		return number_of_nodes;
	}

	public void setNumber_of_nodes(int number_of_nodes) {
		this.number_of_nodes = number_of_nodes;
	}

	public int getNumber_of_edges() {
		return number_of_edges;
	}

	public void setNumber_of_edges(int number_of_edges) {
		this.number_of_edges = number_of_edges;
	}

	public String[] getNodes_Name() {
		return nodes_Name;
	}

	public void setNodes_Name(String[] nodes_Name) {
		this.nodes_Name = nodes_Name;
	}

	public int[][] getData_Table() {
		return data_Table;
	}

	public void setData_Table(int[][] data_Table) {
		this.data_Table = data_Table;
	}

	public int[] getSourceNodes() {
		return sourceNodes;
	}

	public void setSourceNodes(int[] sourceNodes) {
		this.sourceNodes = sourceNodes;
	}

	public int[] getTargetNodes() {
		return targetNodes;
	}

	public void setTargetNodes(int[] targetNodes) {
		this.targetNodes = targetNodes;
	}

	public int[] getOffTargetNodes() {
		return offTargetNodes;
	}

	public void setOffTargetNodes(int[] offTargetNodes) {
		this.offTargetNodes = offTargetNodes;
	}

	public CyNetwork getCyNetwork() {
		return cyNetwork;
	}

	public void setCyNetwork(CyNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}

	CyNetwork cyNetwork;
	
	public void build_Graph(CyNetwork cyNetwork1){
		
		cyNetwork =cyNetwork1;
		number_of_nodes = cyNetwork.getNodeList().size();
		number_of_edges = cyNetwork.getEdgeList().size();
		String edge_SUID = null;
		
		String myNodeName ="";
		nodes_Name = new String[number_of_nodes];
		data_Table = new int[number_of_nodes][number_of_nodes];
		
		a aa = new a();
		 
		
for (int i = 0; i < number_of_nodes; i++) {
			aa.txt.append("node: "+String.valueOf(i)+"\n"+" SUID  "+cyNetwork.getNodeList().get(i).getSUID().toString()+"\n  "+
					cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("interaction", String.class) +"\n  ");
			
		}
for (int i = 0; i < number_of_edges; i++) {
	aa.txt.append("edge: "+String.valueOf(i)+"\n"+cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("shared name", String.class)+"\n  "+
			cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("interaction", String.class) +"\n  ");
	
}
		
		aa.setTitle(myNodeName);
		 aa.setName(myNodeName);
		 aa.setVisible(true);
	}
}
