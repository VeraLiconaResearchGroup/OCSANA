package org.cytoscape.myapp.internal;

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

	private String name;
	private long ID;
	private String shared_Name;
	private long weight;
	private long capacity;
	private int degree_In;
	private int degree_Out;
	private String type;
    private String interaction;
    private Vector<Long> income_Edges = new Vector<>();
    
    public Node(String name, long iD, String shared_Name, String interaction) {
		super();
		this.name = name;
		ID = iD;
		this.shared_Name = shared_Name;
		this.interaction = interaction;
	}

	private Vector<Long> outcome_Edges = new Vector<>();
    
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
    

    /**
     * Get the value of interaction
     *
     * @return the value of interaction
     */
    public String getInteraction() {
        return interaction;
    }

    /**
     * Set the value of interaction
     *
     * @param interaction new value of interaction
     */
    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    
}
