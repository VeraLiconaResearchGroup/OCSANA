package org.cytoscape.myapp.internal;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/

import java.util.Vector;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.myapp.internal.a;
import org.cytoscape.*;

/**
 *
 * @author raha
 */
public class Graph {

	/**
	 *
	 */
	public Graph() {
	}

	private Vector<Node> node_List;

	private Vector<Edge> edges_List;

	private int[][] graph_Table;

	private Vector<Node> source;

	private Vector<Node> target;

	private Vector<Node> offtarget;

	private Node[][] graph_data;

	private int node_Count;

	private int edgeCount;

	private CyNetwork cyNetwork;
	private Edge edge;
	private Node node;

	public Graph(CyNetwork cyNetwork) {
		super();
		this.cyNetwork = cyNetwork;
		a aa= new a();
		aa.setVisible(true);
		initial();
	}

	public CyNetwork getCyNetwork() {
		return cyNetwork;
	}

	public void setCyNetwork(CyNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}

	/**
	 * Get the value of edgeCount
	 *
	 * @return the value of edgeCount
	 */
	public int getEdgeCount() {
		return edgeCount;
	}

	/**
	 * Set the value of edgeCount
	 *
	 * @param edgeCount
	 *            new value of edgeCount
	 */
	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
	}

	/**
	 * Get the value of node_Count
	 *
	 * @return the value of node_Count
	 */
	public int getNode_Count() {
		return node_Count;
	}

	/**
	 * Set the value of node_Count
	 *
	 * @param node_Count
	 *            new value of node_Count
	 */
	public void setNode_Count(int node_Count) {
		this.node_Count = node_Count;
	}

	/**
	 * Get the value of graph_data
	 *
	 * @return the value of graph_data
	 */
	public Node[][] getGraph_data() {
		return graph_data;
		
	}

	/**
	 * Set the value of graph_data
	 *
	 * @param graph_data
	 *            new value of graph_data
	 */
	public void setGraph_data() {
		
		graph_data = new  Node[node_Count][node_Count];
		for (int i = 0; i < node_Count; i++) {
			for (int j = 0; j <node_Count; j++) {
				if(cyNetwork.containsEdge(cyNetwork.getNodeList().get(i),cyNetwork.getNodeList().get(j)) == true)
					graph_data[i][j] = node_List.get(i);
				else {
					graph_data[i][j] = null;
				}
				
			}
			
		}
		
	}

	/**
	 * Get the value of offtarget
	 *
	 * @return the value of offtarget
	 */
	public Vector<Node> getOfftarget() {
		return offtarget;
	}

	/**
	 * Set the value of offtarget
	 *
	 * @param offtarget
	 *            new value of offtarget
	 */
	public void setOfftarget(Vector<Node> offtarget) {
		this.offtarget = offtarget;
	}

	/**
	 * Get the value of target
	 *
	 * @return the value of target
	 */
	public Vector<Node> getTarget() {
		return target;
	}

	/**
	 * Set the value of target
	 *
	 * @param target
	 *            new value of target
	 */
	public void setTarget(Vector<Node> target) {
		this.target = target;
	}

	/**
	 * Get the value of source
	 *
	 * @return the value of source
	 */
	public Vector<Node> getSource() {
		return source;
	}

	/**
	 * Set the value of source
	 *
	 * @param source
	 *            new value of source
	 */
	public void setSource(Vector<Node> source) {
		this.source = source;
	}

	/**
	 * Get the value of graph_Table
	 *
	 * @return the value of graph_Table
	 */
	public int[][] getGraph_Table() {
		return graph_Table;
	}

	/**
	 * Set the value of graph_Table
	 *
	 * @param graph_Table
	 *            new value of graph_Table
	 */
	public void setGraph_Table() {
		a aa= new a();
		graph_Table = new  int[cyNetwork.getNodeList().size()][cyNetwork.getNodeList().size()];
		aa.setTitle(String.valueOf(node_Count));
		//cyNetwork.getEdgeList().get(1).getSource().getSUID()
		for (int i = 0; i < cyNetwork.getNodeList().size()-1; i++) {
			for (int j = i; j < cyNetwork.getNodeList().size(); j++) {
				if(cyNetwork.containsEdge(cyNetwork.getNodeList().get(i),cyNetwork.getNodeList().get(j)) )
					
					graph_Table[i][j] = 1;
				else {
					aa.setTitle(String.valueOf(node_Count+"  fv"));
					graph_Table[i][j] = 0;
				}
				
			}
			
		}
		
		aa.setVisible(true);
		
		
	}

	/**
	 * Get the value of edges_List
	 *
	 * @return the value of edges_List
	 */
	public Vector<Edge> getEdges_List() {
		return edges_List;
	}

	/**
	 * Set the value of edges_List
	 *
	 * @param edges_List
	 *            new value of edges_List
	 */
	public void setEdges_List() {
		String iteration, name, shared_Name,effect;
		Long iD,sink=(long) 0;
		Long source=(long) 0;
		edges_List = new Vector<>();
		
		for (int i = 0; i < edgeCount; i++) {
			iD = cyNetwork.getEdgeList().get(i).getSUID();
			iteration = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("interaction", String.class);
			name = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("name", String.class);
			shared_Name = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("shared name", String.class);
			effect = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("EFFECT", String.class);
			
			//String[] parts = shared_Name.split(" (");
			//String[] parts1 = parts[1] .split(") ");
			
			/*for (int j = 0; j <node_Count; j++) {
				
				if(node_List.get(j).getName() == parts[0])
					source = node_List.get(j).getID(); // 004
				else {
					if (node_List.get(j).getName() == parts[1]) {
						source = node_List.get(j).getID(); // 004
					}
				}
			} */
			edge= new Edge(shared_Name, iD, shared_Name, source, sink, iteration, effect);
			edges_List.add(edge);
			
		}
		
	}

	/**
	 * Get the value of node_List
	 *
	 * @return the value of node_List
	 */
	public Vector<Node> getNode_List() {
		return node_List;
	}

	/**
	 * Set the value of nodes
	 *
	 * @param node_List
	 *            new value of nodes
	 */
	public void setNode_List() {
		String interaction, name, shared_Name;
		Long iD;
		
		for (int i = 0; i < node_Count; i++) {
			iD = cyNetwork.getNodeList().get(i).getSUID();
			interaction = cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("interaction", String.class);
			name = cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("name", String.class);
			shared_Name = cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("shared name", String.class);
			node = new Node(name, iD, shared_Name, interaction);
			node_List = new Vector<>();
			node_List.add(node);
			
			
			
		}
		
	}

	public void initial() {

		setNode_Count(cyNetwork.getNodeList().size());
		setEdgeCount(cyNetwork.getEdgeList().size());
		setNode_List();
		setEdges_List();
		setGraph_Table();
	//	setGraph_data();
	//	test();
	
		
		

	}
	public void test() {
		a aa= new a();
		for (int i = 0; i <node_Count; i++) {
			aa.jTextArea1.append("name: " + node_List.get(i).getName() + "ID: \n" + String.valueOf(node_List.get(i).getID()) + " \n" +"ID" + node_List.get(i).getInteraction() );
			
		}
		for (int i = 0; i <edgeCount; i++) {
			aa.jTextArea2.append("name: " + edges_List.get(i).getName() + "ID: \n" + String.valueOf(edges_List.get(i).getID()) + " \n" +"ID" + edges_List.get(i).getIteration() + "source"  +  String.valueOf(edges_List.get(i).getSource()) 
			+ "sink"  +  String.valueOf(edges_List.get(i).getSink()));
			
		}
		aa.setVisible(true);
		
		
	}

}
