package org.cytoscape.myapp.internal;


import java.util.ArrayList;
import java.util.Hashtable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/

import java.util.Vector;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;

import org.cytoscape.myapp.internal.a;

//import GUI.ocsanaInput;

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

	private ArrayList<Node> node_List;

	private ArrayList<Edge> edges_List;

	private int[][] graph_Table;

	private int[] source;

	private int[] target;

	private int[] offtarget;

	private Node[][] graph_data;

	private int node_Count;

	private int edgeCount;
        
        final String activ= "activation";
        
        private  Integer activationNumber = 1; 
        
        private  Integer inhibitationNumber = -1; 
        
        final String inhibitation = "inhibitation";
        
	Hashtable<Long, Integer> numbers; 

	private CyNetwork cyNetwork;
	private Edge edge;
	private Node node;

	public Graph(CyNetwork cyNetwork) {
		super();
		this.cyNetwork = cyNetwork;
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
	public int[] getOfftarget() {
		return offtarget;
	}

	/**
	 * Set the value of offtarget
	 *
	 * @param offtarget
	 *            new value of offtarget
	 */
	public void setOfftarget(int[] offtarget) {
		this.offtarget = offtarget;
	}

	/**
	 * Get the value of target
	 *
	 * @return the value of target
	 */
	public int[] getTarget() {
		return target;
	}

	/**
	 * Set the value of target
	 *
	 * @param target
	 *            new value of target
	 */
	public void setTarget(int[] target) {
		this.target = target;
	}

	/**
	 * Get the value of source
	 *
	 * @return the value of source
	 */
	public int[] getSource() {
		return source;
	}

	/**
	 * Set the value of source
	 *
	 * @param source
	 *            new value of source
	 */
	public void setSource(int[] source) {
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
		for (int i = 0; i < node_List.size(); i++) {
			for (int j = 0; j < node_List.size(); j++) {
				graph_Table[i][j] = 0 ;
			}	
		}
		 
		for (int i = 0; i < node_List.size(); i++) {
			for (int j = 0; j < node_List.get(i).getNeighborHashing().size(); j++) {
				graph_Table[node_List.get(i).getHashing_map()][node_List.get(i).getNeighborHashing().get(j)] = 1 ;
			}	
		}
		
		//aa.setVisible(true);
		
		
	}

	/**
	 * Get the value of edges_List
	 *
	 * @return the value of edges_List
	 */
	public ArrayList<Edge> getEdges_List() {
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
                int sourcehash , sinkhash;
                edges_List = new ArrayList<Edge>();
		a aa= new a();
                
                aa.txt.append("**************************************************\n");
                 aa.txt.append("**************************************************\n");
		
		for (int i = 0; i < edgeCount; i++) {
                    
			iD = cyNetwork.getEdgeList().get(i).getSUID();
			iteration = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("interaction", String.class);
			name = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("name", String.class);
			shared_Name = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("shared name", String.class);
			effect = cyNetwork.getRow(cyNetwork.getEdgeList().get(i)).get("EFFECT", String.class);
			source = cyNetwork.getEdgeList().get(i).getSource().getSUID();
			sink = cyNetwork.getEdgeList().get(i).getTarget().getSUID();
			node_List.get(numbers.get(source)).setNeighborHashing(numbers.get(sink));
			node_List.get(numbers.get(source)).setNeighbor(sink);
                    	aa.txt.append(name  +"b  ** b"+String.valueOf(source) +"b ** b"+String.valueOf(sink)+"  iteration *"+iteration+"*"+"\n");
                        
			sourcehash = numbers.get(source);
                        sinkhash  = numbers.get(sink);
                       
                        node_List.get(sinkhash).setIncome_Edges(sourcehash);
                        edge= new Edge(shared_Name, iD, shared_Name, source, sink, iteration, effect,sourcehash, sinkhash);
                      
                        node_List.get(sourcehash).setDegree_Out();
                        
                        node_List.get(sinkhash).setDegree_In();
                         
                         
			edges_List.add(edge);
                        if ("activation".equals(edge.getinteration()) ){
                        node_List.get(numbers.get(source)).setTypeOfEdge(activationNumber);
                        node_List.get(sinkhash).setTypeOfincome_Edges(1);
                          //aa.txt.append("vvvvv\n" );
                        }
                        else{
                            node_List.get(numbers.get(source)).setTypeOfEdge(inhibitationNumber);
                         node_List.get(sinkhash).setTypeOfincome_Edges(-1);
                        }
                	}
		
		for (int i = 0; i < node_List.size(); i++) {
			node_List.get(i).setDegree_Out(node_List.get(i).getNeighbor().size());
		}
                
                aa.txt.append("1**************************************************\n");
		for (int i = 0; i < node_List.size(); i++) {
			aa.txt.append(i+"   name: "+node_List.get(i).getName() +"  ID   "+ node_List.get(i).getID()+
					"  hash: "+node_List.get(i).getHashing_map()+
					"  degree in: "+node_List.get(i).getDegree_In()+
					"  degree out: "+node_List.get(i).getDegree_Out()+"\n");
                         aa.txt.append("2**************************************************\n");
			for (int j = 0; j < node_List.get(i).getNeighbor().size(); j++) {
				aa.txt.append(j+"  "+String.valueOf(node_List.get(i).getNeighbor().get(j)) +"   "+String.valueOf(node_List.get(i).getNeighborHashing().get(j))
                                 +String.valueOf(node_List.get(i).getTypeOfEdge().get(j))  +" + "+"\n");
				
			}
                         aa.txt.append("3**************************************************\n");
                        for (int j = 0; j < node_List.get(i).getTypeOfincome_Edges().size(); j++) {
				aa.txt.append(j+"  "+ node_List.get(i).getTypeOfincome_Edges().get(j) +"   "+ node_List.get(node_List.get(i).getIncome_Edges().get(j)).getName()+"  * "+"\n");
				
			}
			
		}
                
                 aa.txt.append("**************************************************\n");
                  aa.txt.append("**************************************************\n");
		//aa.setVisible(true);	
	}

	/**
	 * Get the value of node_List
	 *
	 * @return the value of node_List
	 */
	public ArrayList<Node> getNode_List() {
		return node_List;
	}

	/**
	 * Set the value of nodes
	 *
	 * @param node_List
	 *            new value of nodes
	 */
	public void setNode_List() {
		
		String canonicalName, name, shared_Name;
		Long iD;
		int hashing_map;
		
		node_List = new ArrayList<Node>();
		
		for (int i = 0; i < node_Count; i++) {
			iD = cyNetwork.getNodeList().get(i).getSUID();
			canonicalName = cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("canonicalName", String.class);
			name = cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("name", String.class);
			shared_Name = cyNetwork.getRow(cyNetwork.getNodeList().get(i)).get("shared name", String.class);
			numbers.put(iD, i);
			hashing_map = i;
			node = new Node(name, iD, shared_Name, canonicalName,hashing_map);
			node_List.add(node);
		}
		//test
		//****************
		//****************
		a aa= new a();
		aa.txt.append(node_List.size() +"\n");
		for (int i = 0; i < node_List.size(); i++) {
			aa.txt.append(i+"   name: "+node_List.get(i).getName() +"  ID   "+ node_List.get(i).getID()+"  hash: "+node_List.get(i).getHashing_map()+"\n");
		}
		//aa.setVisible(true);

	}

	public void initial() {

		 numbers = new Hashtable<Long,Integer>();
		setNode_Count(cyNetwork.getNodeList().size());
		setEdgeCount(cyNetwork.getEdgeList().size());
		setNode_List();
		setEdges_List();
		
		//setGraph_Table();
	//	setGraph_data();
	//	test();
	
		
		

	}

    void setNode_List(ArrayList<Node> nodes) {
       this.node_List =  nodes;
    }

    void setEdges_List(ArrayList<Edge> edges_List) {
       this.edges_List = edges_List;
    }
	

}
