package org.cytoscape.myapp.internal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author raha
 */
public class Edge {
    
    private String name;

    private long ID;

    private String shared_Name;

    private long source;

    private long sink;
    
    private String iteration;

    private String type;
    
    private String effect;

    public Edge(String name, long iD, String shared_Name, long source, long sink, String iteration, String effect) {
		super();
		this.name = name;
		ID = iD;
		this.shared_Name = shared_Name;
		this.source = source;
		this.sink = sink;
		this.iteration = iteration;
		this.effect = effect;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	/**
     * Get the value of type
     *
     * @return the value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the value of iteration
     *
     * @return the value of iteration
     */
    public String getIteration() {
        return iteration;
    }

    /**
     * Set the value of iteration
     *
     * @param iteration new value of iteration
     */
    public void setIteration(String iteration) {
        this.iteration = iteration;
    }


    /**
     * Get the value of sink
     *
     * @return the value of sink
     */
    public long getSink() {
        return sink;
    }

    /**
     * Set the value of sink
     *
     * @param sink new value of sink
     */
    public void setSink(long sink) {
        this.sink = sink;
    }

    /**
     * Get the value of source
     *
     * @return the value of source
     */
    public long getSource() {
        return source;
    }

    /**
     * Set the value of source
     *
     * @param source new value of source
     */
    public void setSource(long source) {
        this.source = source;
    }

    /**
     * Get the value of shared_Name
     *
     * @return the value of shared_Name
     */
    public String getShared_Name() {
        return shared_Name;
    }

    /**
     * Set the value of shared_Name
     *
     * @param shared_Name new value of shared_Name
     */
    public void setShared_Name(String shared_Name) {
        this.shared_Name = shared_Name;
    }

    /**
     * Get the value of ID
     *
     * @return the value of ID
     */
    public long getID() {
        return ID;
    }

    /**
     * Set the value of ID
     *
     * @param ID new value of ID
     */
    public void setID(long ID) {
        this.ID = ID;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

}
