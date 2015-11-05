/*
 * Copyright (C) 2015 misagh.kordi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cytoscape.myapp.internal;


import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author misagh.kordi
 */
public class graphExpanding {

    private Graph graph;

    private int[] sourceNodes;

    private int[] targetNodes;

    private int[] oftargetNodes;

    private ArrayList<Node> nodeList;

    private ArrayList<Edge> edges_List;

    private ArrayList<Node> extranodeList;

    private ArrayList<String> nameofnewnode;

    static int numberOfnodes = 0;

    a aa;

    public graphExpanding() {
    }

    public graphExpanding(Graph g, int[] sourceNodes, int[] targetNodes, int[] oftargetNodes) {

        graph = g;
        this.sourceNodes = sourceNodes;
        this.targetNodes =  targetNodes;
        this.oftargetNodes =  oftargetNodes;
        graph.setSource(g.getSource());
        graph.setTarget(g.getTarget());
        graph.setOfftarget(g.getOfftarget());
        nodeList = g.getNode_List();
        // graph.setEdges_List(g.getEdges_List());
        initial();

    }

    public void initial() {

        aa = new a();
        aa.setTitle("expanding");
        aa.setVisible(true);
        complemetatyNodes();
        compositeNodes();
     int[] newsourceNodes =  new int[sourceNodes.length];

     int[] newtargetNodes =  new int[targetNodes.length];

     int[] newoftargetNodes =  new int[oftargetNodes.length];
     
        for (int i = 0; i < newsourceNodes.length; i++) 
           newsourceNodes[i] = findIndex(nodeList.get(sourceNodes[i]).getName());
        for (int i = 0; i < newtargetNodes.length; i++) 
           newtargetNodes[i] = findIndex(nodeList.get(targetNodes[i]).getName());    
       for (int i = 0; i < newoftargetNodes.length; i++) 
           newoftargetNodes[i] = findIndex(nodeList.get(oftargetNodes[i]).getName());
        ShortestESM eG = new ShortestESM(extranodeList, newsourceNodes, newtargetNodes, newoftargetNodes);

    }

    /**
     * Get the value of oftargetNodes
     *
     * @return the value of oftargetNodes
     */
    public int[] getOftargetNodes() {
        return oftargetNodes;
    }

    /**
     * Set the value of oftargetNodes
     *
     * @param oftargetNodes new value of oftargetNodes
     */
    public void setOftargetNodes(int[] oftargetNodes) {
        this.oftargetNodes = oftargetNodes;
    }

    /**
     * Get the value of targetNodes
     *
     * @return the value of targetNodes
     */
    public int[] getTargetNodes() {
        return targetNodes;
    }

    /**
     * Set the value of targetNodes
     *
     * @param targetNodes new value of targetNodes
     */
    public void setTargetNodes(int[] targetNodes) {
        this.targetNodes = targetNodes;
    }

    /**
     * Get the value of sourceNodes
     *
     * @return the value of sourceNodes
     */
    public int[] getSourceNodes() {
        return sourceNodes;
    }

    /**
     * Set the value of sourceNodes
     *
     * @param sourceNodes new value of sourceNodes
     */
    public void setSourceNodes(int[] sourceNodes) {
        this.sourceNodes = sourceNodes;
    }

    /**
     * Get the value of graph
     *
     * @return the value of graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Set the value of graph
     *
     * @param graph new value of graph
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Get the value of nodeList
     *
     * @return the value of nodeList
     */
    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    /**
     * Set the value of nodeList
     *
     * @param nodeList new value of nodeList
     */
    public void setNodeList(ArrayList<Node> nodeList) {
        this.nodeList = nodeList;
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
     * @param edges_List new value of edges_List
     */
    public void setEdges_List(ArrayList<Edge> edges_List) {
        this.edges_List = edges_List;
    }

    public void complemetatyNodes() {

        extranodeList = new ArrayList<>();

        nameofnewnode = new ArrayList<>();

        int temp = 0, x1 = 0, x2 = 0, x3 = 0, x4 = 0;

        for (int i = 0; i < nodeList.size(); i++) {

            if (nodeList.get(i).getDegree_In() == 1) {//case 1

                aa.txt.append("0   " + nodeList.get(i).getName() + "\n");

                temp = nodeList.get(i).getIncome_Edges().get(0);//coming edge is inhibitation

                aa.txt.append("1   " + nodeList.get(temp).getName() + nodeList.get(i).getTypeOfincome_Edges().size() + "\n");

                if (nodeList.get(i).getTypeOfincome_Edges().get(0) == -1) {

                    aa.txt.append("2   " + nodeList.get(temp).getName() + "\n");

                    if (!nameofnewnode.contains(nodeList.get(i).getName() + "bar")) {

                        Node sinkbar = new Node(nodeList.get(i).getName() + "bar", numberOfnodes, nodeList.get(i).getShared_Name() + "bar", nodeList.get(i).getCanonicalName() + "bar", numberOfnodes);

                        extranodeList.add(sinkbar);

                        numberOfnodes++;

                        nameofnewnode.add(nodeList.get(i).getName() + "bar");
                    }
                    if (!nameofnewnode.contains(nodeList.get(i).getName())) {

                        Node sink = new Node(nodeList.get(i).getName(), numberOfnodes, nodeList.get(i).getShared_Name(), nodeList.get(i).getCanonicalName(), numberOfnodes);

                        extranodeList.add(sink);

                        numberOfnodes++;

                        nameofnewnode.add(nodeList.get(i).getName());
                    }
                    if (!nameofnewnode.contains(nodeList.get(temp).getName())) {

                        Node source = new Node(nodeList.get(temp).getName(), numberOfnodes, nodeList.get(temp).getShared_Name(), nodeList.get(temp).getCanonicalName(), numberOfnodes);

                        extranodeList.add(source);

                        numberOfnodes++;

                        nameofnewnode.add(nodeList.get(temp).getName());
                    }
                    if (!nameofnewnode.contains(nodeList.get(temp).getName() + "bar")) {

                        Node sourcebar = new Node(nodeList.get(temp).getName() + "bar", numberOfnodes, nodeList.get(temp).getShared_Name() + "bar", nodeList.get(temp).getCanonicalName() + "bar", numberOfnodes);

                        extranodeList.add(sourcebar);

                        numberOfnodes++;

                        nameofnewnode.add(nodeList.get(temp).getName() + "bar");
                    }

                    aa.txt.append("*********************************\n");
                    aa.txt.append("*********************************\n");
                    aa.txt.append("*********************************\n");

                    for (int j = 0; j < extranodeList.size(); j++) {

                        aa.txt.append(extranodeList.get(j).getName() + "   ");

                    }

                    aa.txt.append("\n");
                    x2 = findIndex(nodeList.get(i).getName());

                    aa.txt.append(extranodeList.get(x2).getName() + "   " + x2 + "  ***\n");

                    x4 = findIndex(nodeList.get(temp).getName() + "bar");

                    aa.txt.append(extranodeList.get(x4).getName() + "   " + x4 + "  ***\n");

                    assignproperties(x4, x2);

                    x1 = findIndex(nodeList.get(i).getName() + "bar");

                    aa.txt.append(extranodeList.get(x1).getName() + "   " + x1 + " ***\n");

                    x3 = findIndex(nodeList.get(temp).getName());

                    aa.txt.append(extranodeList.get(x3).getName() + "   " + x3 + " ***\n");

                    assignproperties(x3, x1);
                    aa.txt.append("*********************************\n");
                    aa.txt.append("*********************************\n");
                    aa.txt.append("*********************************\n");

                    aa.txt.append("else*********************************\n");
                    for (int ii = 0; ii < extranodeList.size(); ii++) {

                        aa.txt.append(ii + "   name: " + extranodeList.get(ii).getName() + "  ID   " + extranodeList.get(ii).getID()
                                + "  hash: " + extranodeList.get(ii).getHashing_map()
                                + "  degree in: " + extranodeList.get(ii).getDegree_In()
                                + "  degree out: " + extranodeList.get(ii).getDegree_Out() + "\n");
                        aa.txt.append(extranodeList.get(ii).getNeighbor().size() + "   02**************************************************\n");
                        for (int j = 0; j < extranodeList.get(ii).getNeighbor().size(); j++) {
                            aa.txt.append(j + "  " + extranodeList.get(Integer.parseInt(extranodeList.get(ii).getNeighbor().get(j).toString())).getName() + "\n");
                        }
                        aa.txt.append("03**************************************************\n"
                                + extranodeList.get(ii).getTypeOfincome_Edges().size() + "\n");
                    }

                    aa.txt.append("else*********************************\n");

                } else {

                    aa.txt.append("3   " + nodeList.get(temp).getName() + "\n");
                    if (!nameofnewnode.contains(nodeList.get(i).getName())) {

                        Node sink = new Node(nodeList.get(i).getName(), numberOfnodes, nodeList.get(i).getShared_Name(), nodeList.get(i).getCanonicalName(), numberOfnodes);

                        extranodeList.add(sink);

                        numberOfnodes++;

                        nameofnewnode.add(nodeList.get(i).getName());
                    }
                    if (!nameofnewnode.contains(nodeList.get(temp).getName())) {

                        Node source = new Node(nodeList.get(temp).getName(), numberOfnodes, nodeList.get(temp).getShared_Name(), nodeList.get(temp).getCanonicalName(), numberOfnodes);

                        extranodeList.add(source);

                        numberOfnodes++;

                        nameofnewnode.add(nodeList.get(temp).getName());
                    }

                    aa.txt.append("00**************************************************\n");

                    x2 = findIndex(nodeList.get(i).getName());

                    aa.txt.append("\n" + x2);

                 //   x4 = extranodeList.indexOf(nameofnewnode.indexOf(nodeList.get(temp).getName()));
                    x4 = findIndex(nodeList.get(temp).getName());

                    aa.txt.append("\n" + x4);

                    assignproperties(x4, x2);

                }
            }
        }
        aa.txt.append("for*********************************\n");
        for (int ii = 0; ii < extranodeList.size(); ii++) {

            aa.txt.append(ii + "   name: " + extranodeList.get(ii).getName() + "  ID   " + extranodeList.get(ii).getID()
                    + "  hash: " + extranodeList.get(ii).getHashing_map()
                    + "  degree in: " + extranodeList.get(ii).getDegree_In()
                    + "  degree out: " + extranodeList.get(ii).getDegree_Out() + "\n");
            aa.txt.append(extranodeList.get(ii).getNeighbor().size() + "   02**************************************************\n");
            for (int j = 0; j < extranodeList.get(ii).getNeighbor().size(); j++) {
                aa.txt.append(j + "  " + extranodeList.get(Integer.parseInt(extranodeList.get(ii).getNeighbor().get(j).toString())).getName() + "\n");
            }
            aa.txt.append("03**************************************************\n"
                    + extranodeList.get(ii).getTypeOfincome_Edges().size() + "\n");
        }

        aa.txt.append("for*********************************\n");
        aa.txt.append("for*********************************\n");

    }

    public void compositeNodes() {

        aa.txt.append("**************Composition*******************\n");

        for (int i = 0; i < nodeList.size(); i++) {

            if (nodeList.get(i).degree_In > 1) {

                aa.txt.append(nodeList.get(i).getName() + "\n");
                int k = 0;

                for (int j = 0; j < nodeList.get(i).getTypeOfincome_Edges().size(); j++) {

                    k = k + nodeList.get(i).getTypeOfincome_Edges().get(j);
                }

                aa.txt.append(k + "   k \n");
                if (k == nodeList.get(i).getTypeOfincome_Edges().size()) {

                    aa.txt.append("**************all activation*******************\n");

                    String compositeName = null;

                    for (int j = 0; j < nodeList.get(i).getTypeOfincome_Edges().size(); j++) {

                        String name = nodeList.get(nodeList.get(i).getIncome_Edges().get(j)).getName();

                        compositeName = compositeName + name;

                        if (!nameofnewnode.contains(name)) {

                            Node source = new Node(name, numberOfnodes, name, name, numberOfnodes);

                            extranodeList.add(source);

                            numberOfnodes++;

                            nameofnewnode.add(name);
                        }
                    }

                    Node compositeNode = new Node(compositeName, numberOfnodes, compositeName, compositeName, numberOfnodes);

                    extranodeList.add(compositeNode);

                    nameofnewnode.add(compositeName);

                    numberOfnodes++;

                    for (int j = 0; j < extranodeList.size(); j++) {
                        aa.txt.append(extranodeList.get(j).getName() + "   ");
                    }

                    aa.txt.append("  \n ");

                    int x2, x4;

                    for (int j = 0; j < nodeList.get(i).getTypeOfincome_Edges().size(); j++) {

                        x2 = findIndex(compositeName);

                        aa.txt.append("\n" + x2);

                        x4 = findIndex(nodeList.get(nodeList.get(i).getIncome_Edges().get(j)).getName());

                        aa.txt.append("\n" + x4);

                        assignproperties(x4, x2);
                    }

                    x2 = findIndex(compositeName);
                    x4 = findIndex(nodeList.get(i).getName());
                    aa.txt.append("\n" + x4);
                    assignproperties(x2, x4);

                    aa.txt.append("for*********************************\n");
                    for (int ii = 0; ii < extranodeList.size(); ii++) {

                        aa.txt.append(ii + "   name: " + extranodeList.get(ii).getName() + "  ID   " + extranodeList.get(ii).getID()
                                + "  hash: " + extranodeList.get(ii).getHashing_map()
                                + "  degree in: " + extranodeList.get(ii).getDegree_In()
                                + "  degree out: " + extranodeList.get(ii).getDegree_Out() + "\n");
                        aa.txt.append(extranodeList.get(ii).getNeighbor().size() + "   02**************************************************\n");
                        for (int j = 0; j < extranodeList.get(ii).getNeighbor().size(); j++) {
                            aa.txt.append(j + "  " + extranodeList.get(Integer.parseInt(extranodeList.get(ii).getNeighbor().get(j).toString())).getName() + "\n");
                        }
                        aa.txt.append("03**************************************************\n"
                                + extranodeList.get(ii).getTypeOfincome_Edges().size() + "\n");
                    }

                    aa.txt.append("for*********************************\n");
                    aa.txt.append("for*********************************\n");

                } else {
                    aa.txt.append("**************inhibitation *******************\n");
                    ArrayList<String> t1 = new ArrayList<>();

                    ArrayList<String> t2 = new ArrayList<>();

                    String compositeName = null;

                    for (int j = 0; j < nodeList.get(i).getTypeOfincome_Edges().size(); j++) {

                        String name = nodeList.get(nodeList.get(i).getIncome_Edges().get(j)).getName();

                        int index = nodeList.get(i).getIncome_Edges().get(j);

                        int indextemp = nodeList.get(index).getNeighbor().indexOf(nodeList.get(i).getID());
                        aa.txt.append("0) " + nodeList.get(index).getName() + "    " + name + "\n");

                        if (nodeList.get(index).getTypeOfEdge().get(indextemp) == -1) {

                            aa.txt.append("1) \n");

                            if (!nameofnewnode.contains(name + "bar")) {

                                aa.txt.append("2) \n");

                                aa.txt.append(name + "   name \n");

                                Node source = new Node(name + "bar", numberOfnodes, name + "bar", name + "bar", numberOfnodes);

                                extranodeList.add(source);

                                numberOfnodes++;

                                nameofnewnode.add(name + "bar");

                            }
                            compositeName = compositeName + name + "bar";
                            t1.add(name + "bar");

                            if (!nameofnewnode.contains(name)) {

                                aa.txt.append("3) \n");

                                Node source = new Node(name, numberOfnodes, name, name, numberOfnodes);

                                extranodeList.add(source);

                                numberOfnodes++;

                                nameofnewnode.add(name);
                            }
                            t2.add(name);

                            aa.txt.append("t1 \n");

                            for (int l = 0; l < t1.size(); l++) {

                                aa.txt.append(t1.get(l) + "  ");

                            }

                            aa.txt.append("\n t2 \n");

                            for (int l = 0; l < t2.size(); l++) {

                                aa.txt.append(t2.get(l) + "  ");
                            }
                            aa.txt.append(" \n");
                        } else {

                            aa.txt.append("4) \n");

                            if (!nameofnewnode.contains(name)) {

                                aa.txt.append("5) \n");

                                Node source = new Node(name, numberOfnodes, name, name, numberOfnodes);

                                extranodeList.add(source);

                                numberOfnodes++;

                                nameofnewnode.add(name);

                            }
                            compositeName = compositeName + name;
                            t1.add(name);
                            if (!nameofnewnode.contains(name + "bar")) {

                                aa.txt.append("6) \n");

                                Node source = new Node(name + "bar", numberOfnodes, name + "bar", name + "bar", numberOfnodes);

                                extranodeList.add(source);

                                numberOfnodes++;

                                nameofnewnode.add(name + "bar");

                            }
                            t2.add(name + "bar");
                            aa.txt.append("t1 \n");

                            for (int l = 0; l < t1.size(); l++) {

                                aa.txt.append(t1.get(l) + "  ");

                            }

                            aa.txt.append("\n t2 \n");

                            for (int l = 0; l < t2.size(); l++) {

                                aa.txt.append(t2.get(l) + "  ");
                            }
                            aa.txt.append(" \n");

                        }
                    }

                    aa.txt.append("7) \n");
                    Node compositeNode = new Node(compositeName, numberOfnodes, compositeName, compositeName, numberOfnodes);

                    extranodeList.add(compositeNode);

                    nameofnewnode.add(compositeName);

                    numberOfnodes++;

                    int x2 = 0, x4;

                    aa.txt.append("8) \n" + compositeName + "\n");
                    for (int j = 0; j < t1.size(); j++) {

                        x2 = findIndex(compositeName);
                        x4 = findIndex(t1.get(j));

                        assignproperties(x4, x2);
                    }
                    String namee = nodeList.get(i).getName();
                    if (!nameofnewnode.contains(nodeList.get(i).getName())) {

                        Node source = new Node(namee, numberOfnodes, namee, namee, numberOfnodes);

                        extranodeList.add(source);

                        numberOfnodes++;

                        nameofnewnode.add(namee);
                    }
                    x4 = findIndex(compositeName);
                    x2 = findIndex(namee);

                    assignproperties(x4, x2);

                    String name1 = null;
                    aa.txt.append("9) \n");
                    if (!nameofnewnode.contains(nodeList.get(i).getName() + "bar")) {

                        String name = nodeList.get(i).getName() + "bar";

                        Node source = new Node(name, numberOfnodes, name, name, numberOfnodes);

                        extranodeList.add(source);

                        numberOfnodes++;

                        nameofnewnode.add(name);
                    }

                    name1 = nodeList.get(i).getName() + "bar";

                    aa.txt.append(name1 + "  10) \n");

                    for (int j = 0; j < t2.size(); j++) {

                        x2 = findIndex(name1);
                        aa.txt.append(name1 + "  " + x2 + "  x2\n");
                        x4 = findIndex(t2.get(j));
                        aa.txt.append(t2.get(j) + "   " + x4 + "  x4\n");
                        assignproperties(x4, x2);
                    }

                    aa.txt.append("for*********************************\n");
                    for (int ii = 0; ii < extranodeList.size(); ii++) {

                        aa.txt.append(ii + "   name: " + extranodeList.get(ii).getName() + "  ID   " + extranodeList.get(ii).getID()
                                + "  hash: " + extranodeList.get(ii).getHashing_map()
                                + "  degree in: " + extranodeList.get(ii).getDegree_In()
                                + "  degree out: " + extranodeList.get(ii).getDegree_Out() + "\n");
                        aa.txt.append(extranodeList.get(ii).getNeighbor().size() + "   02**************************************************\n");
                        for (int j = 0; j < extranodeList.get(ii).getNeighbor().size(); j++) {
                            aa.txt.append(j + "  " + extranodeList.get(Integer.parseInt(extranodeList.get(ii).getNeighbor().get(j).toString())).getName() + "\n");
                        }
                        aa.txt.append("03**************************************************\n"
                                + extranodeList.get(ii).getTypeOfincome_Edges().size() + "\n");
                    }

                    aa.txt.append("for*********************************\n");
                    aa.txt.append("for*********************************\n");
                }
            }
        }

        aa.txt.append("1) \n");
        aa.txt.append("for*********************************\n");
        for (int ii = 0; ii < extranodeList.size(); ii++) {

            aa.txt.append(ii + "   name: " + extranodeList.get(ii).getName() + "  ID   " + extranodeList.get(ii).getID()
                    + "  hash: " + extranodeList.get(ii).getHashing_map()
                    + "  degree in: " + extranodeList.get(ii).getDegree_In()
                    + "  degree out: " + extranodeList.get(ii).getDegree_Out() + "\n");
            aa.txt.append(extranodeList.get(ii).getNeighbor().size() + "   02**************************************************\n");
            for (int j = 0; j < extranodeList.get(ii).getNeighbor().size(); j++) {
                aa.txt.append(j + "  " + extranodeList.get(Integer.parseInt(extranodeList.get(ii).getNeighbor().get(j).toString())).getName() + "\n");
            }
            aa.txt.append("03**************************************************\n"
                    + extranodeList.get(ii).getTypeOfincome_Edges().size() + "\n");
        }

        aa.txt.append("for*********************************\n");
        aa.txt.append("for*********************************\n");

        for (int i = 0; i < extranodeList.size(); i++) {

            aa.txt.append(i + "   *" + extranodeList.get(i).getName() + "   *" + extranodeList.get(i).getDegree_In() + "   *" + extranodeList.get(i).getDegree_Out() + "*\n");

        }

    }

    public void assignproperties(int a, int b) {

        extranodeList.get(a).setNeighborHashing(b);

        extranodeList.get(a).setTypeOfEdge(1);

        extranodeList.get(a).setNeighbor((long) b);

        extranodeList.get(a).setHashing_map(a);

        extranodeList.get(a).setDegree_Out();

        extranodeList.get(b).setDegree_In();

        extranodeList.get(b).setIncome_Edges(a);

        extranodeList.get(b).setTypeOfincome_Edges(1);

    }
    
    public int findIndex(String s) {

        int index = -10;

        for (int i = 0; i < extranodeList.size(); i++) {

            if (s.equals(extranodeList.get(i).getName())) {
                index = i;
                break;
            } else {
            }
        }
        return index;

    }
}
