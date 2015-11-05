package org.compsysmed.ocsana.internal;

import java.util.ArrayList;
import java.util.Hashtable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Vector;

/**
 *
 * @author raha
 */
public class Node {
    
    /** constructor: initial name, iD, shared_Name, canonicalName, hashing map number for object*/
    public Node(String name, long iD, String shared_Name, String canonicalName, Integer hashmaping) {
        super();
        initial();
        setName(name);
        setID(iD);
        setShared_Name(shared_Name);
        setCanonicalName(canonicalName);
        setHashing_map(hashmaping); 
    }

    /** constructor: */
    public Node() {
    }
    
    /** node's Name at Cytoscape network*/
    private String name;
    
    /** node's SUID at Cytoscape network*/
    private long ID;
    
    /** node's shared Name at Cytoscape network*/
    private String shared_Name;
    
    /** node's canonicalName at Cytoscape network*/
    private String canonicalName;
   
    /** number of edges that this node, is their tail at Cytoscape network*/
     int degree_In=0;
    
    /** number of edges that this node, is their head at Cytoscape network*/
     int degree_Out = 0;
  
    /** node's interaction(activation or inhibition ) at Cytoscape network*/
    private String interaction;

    /** node's Hashing map number, that keeps position of this node on nodelist */
    private Integer hashing_map;
    
    /** list of SUID all adjacent nodes for this node(this node is a tail of edges) */
    private ArrayList<Integer> income_Edges;
    
    /** list of SUID all adjacent nodes for this node(this node is a tail of edges) */
    private ArrayList<Integer> typeOfincome_Edges;

    public ArrayList<Integer> getTypeOfincome_Edges() {
        return typeOfincome_Edges;
    }

    public void setTypeOfincome_Edges(ArrayList<Integer> typeOfincome_Edges) {
        this.typeOfincome_Edges = typeOfincome_Edges;
    }
    
      public void setTypeOfincome_Edges(Integer a) {
        this.typeOfincome_Edges.add(a);
    }
   
    /** list of SUID all adjacent nodes for this node(this node is a head of edges) */
    private ArrayList<Long> Neighbor;
    
    /** list of hashing number all adjacent nodes for this node(this node is a head of edges) */
    private ArrayList<Integer> NeighborHashing;
    
    /** list of edges type(activation or inhibition ) of this node(this node is a head of edges).Value 1 and -1 for activation or inhibition,respectively*/
    private ArrayList<Integer> typeOfEdge;
    
    /** list of edges type(activation or inhibition ) of this node(this node is a tail of edges).Value 1 and -1 for activation or inhibition,respectively*/
    private ArrayList<Integer> typeOftailEdge;

    public ArrayList<Integer> getTypeOftailEdge() {
        return typeOftailEdge;
    }
    
    /**
     *
     * @return the typeOfEdge list of current object
     */
    public ArrayList<Integer> getTypeOfEdge() {
        return typeOfEdge;
    }

    /**
     * Add integer at end of typeOfEdge as type of edge between current node and corresponding node  Neighbor list of this node.
     *
     * @param typeOfEdge new value of typeOfEdge
     * 
     */
    public void setTypeOfEdge(Integer typeOfEdge) {
        this.typeOfEdge.add(typeOfEdge);
    }

    /**
     *
     * @return the CanonicalName of current object
     */
    public String getCanonicalName() {
        return canonicalName;
    }

    /**
     * Assign value of property canonicalName. .
     *
     * @param canonicalName new value of canonicalName
     * 
     */
    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    /**
     * Assign value of array list Neighbor to property Neighbor
     *
     * @param neighbor new array list for  Neighbor
     * 
     */
    public void setNeighbor(ArrayList<Long> neighbor) {
        Neighbor = neighbor;
    }

    /**
     * Assign value of array list NeighborHashing to property NeighborHashing
     *
     * @param neighborHashing new array list for  NeighborHashing
     * 
     */
    public void setNeighborHashing(ArrayList<Integer> neighborHashing) {
        NeighborHashing = neighborHashing;
    }
    
    /**
     *
     * @return array list Neighbor of current object
     */
    public ArrayList<Long> getNeighbor() {
        return Neighbor;
    }

    /**
     * Add value of neighbor to end of  Neighbor array list.
     *
     * @param neighbor new value  Neighbor.
     * 
     */
    public void setNeighbor(Long neighbor) {
        this.Neighbor.add(neighbor);
    }

    /**
     *
     * @return array list NeighborHashing of current object
     */
    public ArrayList<Integer> getNeighborHashing() {
        return NeighborHashing;
    }

    /**
     * Add value of neighborHashing to end of  neighborHashing array list.
     *
     * @param neighborHashing, new value  neighborHashing.
     * 
     */
    public void setNeighborHashing(Integer neighborHashing) {
        this.NeighborHashing.add(neighborHashing);
    }

    /**
     * Assign value of hashing_map as position of current node on node list.
     *
     * @param hashing_map new value  hashing_map.
     * 
     */
    public void setHashing_map(Integer hashing_map) {
        this.hashing_map = hashing_map;
    }

    /**
     *
     * @return all hashing of all edges that their tail is current node.
     */
    public ArrayList<Integer> getIncome_Edges() {
        return income_Edges;
    }

    /**
     * Assign value of array list income_Edges to property income_Edges.
     * Assign hashing map all of edges that current node is their tail.
     *
     * @param income_Edges, new array list for  income_Edges
     * 
     */
    public void setIncome_Edges(ArrayList<Integer> income_Edges) {
        this.income_Edges = income_Edges;
    }
    
    /**
     * Assign value of array list income_Edges to property income_Edges.
     * Assign hashing map all of edges that current node is their tail.
     *
     * @param income_Edges, new array list for  income_Edges
     * 
     */
    public void setIncome_Edges(Integer a) {
        this.income_Edges.add(a);
    }

    /**
     *
     * @return index of place that current node mapped there on node list
     */
    public int getHashing_map() {
        return hashing_map;
    }

    /**
     * Assign value of hashing_map to property hashing_map.
     * Assign number as hashing map of current node .
     *
     * @param hashing_map, new array list for  hashing_map
     * 
     */
    public void setHashing_map(int hashing_map) {
        this.hashing_map = hashing_map;
    }

    /**
     *
     * @return name of current node
     */
    public String getName() {
        return name;
    }

    /**
     * Assign value name to property name.
     * Assign string as name of current node .
     *
     * @param name, new array list for  name.
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return SUID of current node
     */
    public long getID() {
        return ID;
    }

    /**
     * Assign value ID to property ID.
     * Assign value as SUID of current node .
     *
     * @param ID, new array list for  ID.
     * 
     */
    public void setID(long ID) {
        this.ID = ID;
    }

    /**
     *
     * @return Shared Name of current node
     */
    public String getShared_Name() {
        return shared_Name;
    }

    /**
     * Assign value string shared_Name to property shared_Name.
     * Assign string as shared Name of current node .
     *
     * @param shared_Name, new array list for  shared_Name.
     * 
     */
    public void setShared_Name(String shared_Name) {
        this.shared_Name = shared_Name;
    }
    
    /**
     *
     * @return Degree In of current node
     */
    public int getDegree_In() {
        return degree_In;
    }
    
    /**
     * Assign value degree_In to property degree_In.
     * Assign integer number as degree in current node,
     * that is number of edges on a graph that current node is their tail.
     *
     * @param degree_In, new array list for  degree_In.
     * 
     */
    public void setDegree_In(int degree_In) {
        this.degree_In = degree_In;
    }
    
     public void setDegree_In() {
        this.degree_In++;
    }

    /**
     *
     * @return Degree Out of current node
     */
    public int getDegree_Out() {
        return degree_Out;
    }

    /**
     * Assign value degree_Out to property degree_Out.
     * Assign integer number as degree out current node,
     * that is number of edges on a graph that current node is their head.
     *
     * @param degree_Out, new array list for  degree_Out.
     * 
     */
    public void setDegree_Out(int degree_Out) {
        this.degree_Out = degree_Out;
    }
    
     public void setDegree_Out() {
        this.degree_Out++;
    }
    
    /**
     *
     * @return Interaction of current node
     */
    public String getInteraction() {
        return interaction;
    }

    /**
     * Assign string  interaction to property interaction.
     *
     * @param interaction, new array list for  interaction.
     * 
     */
    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    /**
     * create object for array list  income_Edges, Neighbor, NeighborHashing and typeOfEdge. 
     *
     * @param.
     * 
     */
    public void initial() {

        income_Edges = new ArrayList<Integer>();
        Neighbor = new ArrayList<Long>();
        NeighborHashing = new ArrayList<Integer>();
        typeOfEdge = new ArrayList<>();
        typeOfincome_Edges =  new ArrayList<>();
    }

}
