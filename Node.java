package org.cytoscape.myapp.internal;

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

	public Node(String name, long iD, String shared_Name, String canonicalName, Integer hashmaping ) {
		super();
		initial();
		this.name = name;
		ID = iD;
		this.shared_Name = shared_Name;
		this.canonicalName = canonicalName;
		this.hashing_map = hashmaping;
	}
	
	
	private String name;
	private long ID;
	private String shared_Name;
	private String canonicalName;
	private long weight;
	private long capacity;
	private int degree_In;
	private int degree_Out;
	private String type;
    private String interaction;
    private ArrayList<Long> income_Edges;
    private Integer hashing_map;
    private ArrayList<Long> Neighbor;
    private ArrayList<Integer> NeighborHashing;
    private Vector<Long> outcome_Edges = new Vector<>();
    
    public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public void setNeighbor(ArrayList<Long> neighbor) {
		Neighbor = neighbor;
	}

	public void setNeighborHashing(ArrayList<Integer> neighborHashing) {
		NeighborHashing = neighborHashing;
	}
	
    public ArrayList<Long> getNeighbor() {
		return Neighbor;
	}

	public void setNeighbor(Long neighbor) {
		this.Neighbor.add(neighbor) ;
	}

	public ArrayList<Integer> getNeighborHashing() {
		return NeighborHashing;
	}

	public void setNeighborHashing(Integer neighborHashing) {
		this.NeighborHashing.add(neighborHashing) ;
	}

	public void setHashing_map(Integer hashing_map) {
		this.hashing_map = hashing_map;
	}

	public ArrayList<Long> getIncome_Edges() {
		return income_Edges;
	}

	public void setIncome_Edges(ArrayList<Long> income_Edges) {
		this.income_Edges = income_Edges;
	}

	public int getHashing_map() {
		return hashing_map;
	}

	public void setHashing_map(int hashing_map) {
		this.hashing_map = hashing_map;
	}

    public Vector<Long> getOutcome_Edges() {
		return outcome_Edges;
	}

	public void setOutcome_Edges(Vector<Long> outcome_Edges) {
		this.outcome_Edges = outcome_Edges;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getShared_Name() {
        return shared_Name;
    }

    public void setShared_Name(String shared_Name) {
        this.shared_Name = shared_Name;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public int getDegree_In() {
        return degree_In;
    }

    public void setDegree_In(int degree_In) {
        this.degree_In = degree_In;
    }

    public int getDegree_Out() {
        return degree_Out;
    }

    public void setDegree_Out(int degree_Out) {
        this.degree_Out = degree_Out;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
       
    public void initial(){
    	
    	income_Edges = new ArrayList<Long>();
		Neighbor = new ArrayList<Long>();
		NeighborHashing = new ArrayList<Integer>();
    }

    
}
