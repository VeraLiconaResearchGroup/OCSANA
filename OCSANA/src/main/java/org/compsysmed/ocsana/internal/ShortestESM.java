/*
 * Copyright (C) 2015 mkordi
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
package org.compsysmed.ocsana.internal;

import java.util.ArrayList;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import org.compsysmed.ocsana.internal.Graph;

import org.compsysmed.ocsana.internal.Node;
import org.compsysmed.ocsana.internal.a;

/**
 *
 * @author mkordi
 */
public class ShortestESM {

    private ArrayList<Node> nodeList;

    private int[] sourceNodes;

    private int[] targetNodes;

    private int[] oftargetNodes;

    private ArrayList<Integer> compositeNode;

    private ArrayList<Integer> complementaryNode;

    private ArrayList<Integer> orginalNode;

    a aa;

    public ShortestESM(ArrayList<Node> nodeList, int[] sourceNodes, int[] targetNodes, int[] oftargetNodes) {
        this.nodeList = nodeList;
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.oftargetNodes = oftargetNodes;

        //for Test
        aa = new a();
        aa.setTitle("ShortestPathEG");
        aa.setVisible(true);

        aa.txt.append("************Nodelist*********************\n");
        for (int i = 0; i < nodeList.size(); i++) {
            aa.txt.append(i + ")  name: " + nodeList.get(i).getName() + "\t ID" + nodeList.get(i).getID() + "\t Hashingmap" + nodeList.get(i).getHashing_map()
                    + "\t DegreeIn" + nodeList.get(i).getDegree_In() + "\t DegreeOut" + nodeList.get(i).getDegree_Out() + "\t neibosize" + nodeList.get(i).getNeighborHashing().size() + "\n incom nodes: \t");
            for (int j = 0; j < nodeList.get(i).getIncome_Edges().size(); j++) {
                aa.txt.append(nodeList.get(nodeList.get(i).getIncome_Edges().get(j)).getName() + "\t");
            }
            aa.txt.append("\n outcome Edges: \t");
            for (int j = 0; j < nodeList.get(i).getNeighborHashing().size(); j++) {
                aa.txt.append(nodeList.get(nodeList.get(i).getNeighborHashing().get(j)).getName() + "\t");
            }
            aa.txt.append("\n");
        }
        aa.txt.append("************Source NOde*********************\n");
        for (int i = 0; i < sourceNodes.length; i++) {
            aa.txt.append(nodeList.get(sourceNodes[i]).getName() + "\t");
        }
        aa.txt.append("\n************Target NOde*********************\n");
        for (int i = 0; i < targetNodes.length; i++) {
            aa.txt.append(nodeList.get(targetNodes[i]).getName() + "\t");
        }
        aa.txt.append("\n************Off Target NOde*********************\n");
        for (int i = 0; i < oftargetNodes.length; i++) {
            aa.txt.append(nodeList.get(oftargetNodes[i]).getName() + "\t");
        }

        initial();
    }

    private void initial() {

        compositeNode = new ArrayList<>();
        complementaryNode = new ArrayList<>();
        orginalNode = new ArrayList<>();
        initialNodes(nodeList);
        for (int i = 0; i < sourceNodes.length; i++) {
            for (int j = 0; j < targetNodes.length; j++) {
                ShortestESM(sourceNodes[i], targetNodes[j]);
            }
            for (int j = 0; j < oftargetNodes.length; j++) {
                ShortestESM(sourceNodes[i], oftargetNodes[j]);
            }
        }

        aa.txt.append("finish \n");
    }

    public ArrayList<Integer> getCompositeNode() {
        return compositeNode;
    }

    public void initialNodes(ArrayList<Node> list) {

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getName().startsWith("null")) {
                compositeNode.add(i);
            } else if (list.get(i).getName().contains("bar")) {
                complementaryNode.add(i);
            } else {
                orginalNode.add(i);
            }
        }
        for (int i = 0; i < compositeNode.size(); i++) {
            aa.txt.append(nodeList.get(compositeNode.get(i)).getName() + "\t");
        }
        aa.txt.append("\n");
        for (int i = 0; i < complementaryNode.size(); i++) {
            aa.txt.append(nodeList.get(complementaryNode.get(i)).getName() + "\t");
        }
        aa.txt.append("\n");
        for (int i = 0; i < orginalNode.size(); i++) {
            aa.txt.append(nodeList.get(orginalNode.get(i)).getName() + "\t");
        }
        aa.txt.append("\n");

    }

    public ArrayList<Integer> getComplementaryNode() {
        return complementaryNode;
    }

    public void setComplementaryNode(ArrayList<Integer> complementaryNode) {
        this.complementaryNode = complementaryNode;
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

    public int[] getOftargetNodes() {
        return oftargetNodes;
    }

    public void setOftargetNodes(int[] oftargetNodes) {
        this.oftargetNodes = oftargetNodes;
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

    private int ShortestESM(int s, int o) {

        aa.txt.append("*****************************\n");
        aa.txt.append(nodeList.get(s).getName() + "\n");
        aa.txt.append(nodeList.get(o).getName() + "\n");
        aa.txt.append("*****************************\n");
        ArrayList<Integer> d = new ArrayList<>();
        ArrayList<Integer> p = new ArrayList<>();
        ArrayList<Integer> P_assigned = new ArrayList<>();
        ArrayList<Integer> Q = new ArrayList<>();
        int result = -1;

        //initialization
        for (int i = 0; i < nodeList.size(); i++) {

            if (!compositeNode.contains(i)) {

                aa.txt.append(nodeList.get(i).getIncome_Edges().size() + "\t " + s + "\t size \n");
                for (int j = 0; j < nodeList.get(i).getIncome_Edges().size(); j++) {
                    aa.txt.append(nodeList.get(nodeList.get(i).getIncome_Edges().get(j)).getName() + "\t ");
                }
                aa.txt.append("\n");
                if (nodeList.get(i).getIncome_Edges().contains(s)) {
                    d.add(1);
                    p.add(s);
                } else {
                    d.add(Integer.MAX_VALUE);
                    p.add(-1);
                }
            } else {
                aa.txt.append(nodeList.get(i).getIncome_Edges().size() + "\t " + nodeList.get(i).getName() + "\t size *\n");
                for (int j = 0; j < nodeList.get(i).getIncome_Edges().size(); j++) {
                    aa.txt.append(nodeList.get(nodeList.get(i).getIncome_Edges().get(j)).getName() + "\t **");
                }
                aa.txt.append("\n");
                
                if (nodeList.get(i).getIncome_Edges().contains(s) && nodeList.get(i).getIncome_Edges().size() == 1) {
                       aa.txt.append(nodeList.get(nodeList.get(i).getIncome_Edges().get(0)).getName() + "\t ***");
                    d.add(1);
                    p.add(s);
                } else {
                    d.add(Integer.MIN_VALUE);
                    p.add(-1);
                }
            }
            Q.add(i);
        }

        P_assigned = new ArrayList<>();

        for (int i = 0; i < d.size(); i++) {
            aa.txt.append(nodeList.get(i).getName()+": "+d.get(i) + "\t");
        }
        aa.txt.append("\n");

        //Update Distance Iteratively
        int iteration = 0;
        int u = -1;

        while (Q.size() != 0 && iteration < nodeList.size()) {
            aa.txt.append(iteration + "\n");
            iteration++;
            u = ExtractMIn(s, Q, d, P_assigned, p);
            aa.txt.append(u + " u: " + d.get(u) + "\n");
            P_assigned.add(u);
            Q.remove(FindIndexOf(u, nodeList));

            for (int i = 0; i < nodeList.get(u).getDegree_In(); i++) {

                if (!compositeNode.contains(nodeList.get(u).getIncome_Edges().get(i))) {
                    if (d.get(nodeList.get(u).getIncome_Edges().get(i)) > d.get(u) + 1) {

                        d.set(nodeList.get(u).getIncome_Edges().get(i), d.get(u) + 1);
                        p.set(nodeList.get(u).getIncome_Edges().get(i), u);
                    }
                } else if (d.get(nodeList.get(u).getIncome_Edges().get(i)) < d.get(u) + 1) {

                    d.set(nodeList.get(u).getIncome_Edges().get(i), d.get(u) + 1);
                    p.set(nodeList.get(u).getIncome_Edges().get(i), u);
                }
            }
        }
        result = d.get(o);
        aa.txt.append(result + "result \n");

        return result;
    }

    public int ExtractMIn(int s, ArrayList<Integer> Q, ArrayList<Integer> d, ArrayList<Integer> P_assigned, ArrayList<Integer> p) {
        //return Id not index
        int result = -1;
        int minindex = 0;
        int min = Integer.MAX_VALUE;
        boolean containAllParrent = true;

        for (int i = 0; i < Q.size(); i++) {

            if (!compositeNode.contains(Q.get(i)) && d.get(i) <= min) {
               // aa.txt.append("!compositeNode.contains(Q.get(i)) && d.get(i) <= min \n");
                min = d.get(i);
                minindex = i;
            } else if (compositeNode.contains(Q.get(i))) {
               // aa.txt.append("else if (compositeNode.contains(Q.get(i))) \n");

                for (int j = 0; j < nodeList.get(Q.get(i)).getDegree_In(); j++) {

                    if (!P_assigned.contains(nodeList.get(Q.get(i)).getIncome_Edges().get(j))) {
                        containAllParrent = false;
                        break;
                    }
                }

                if (containAllParrent) {
                    if (nodeList.get(i).getIncome_Edges().contains(s)) {
                        d.set(i, 1);
                        p.set(i, s);
                    }
                }

                if (containAllParrent && d.get(i) <= min) {
                    //aa.txt.append("else if (compositeNode.contains(Q.get(i))) containAllParrent && d.get(i) <= min \n");
                    min = d.get(i);
                    minindex = i;
                }
            }
        }
        result = minindex;

        return result;
    }

    public int FindIndexOf(int Id, ArrayList<Node> extra) {

        int index = -1;

        for (int i = 0; i < extra.size(); i++) {
            if (extra.get(i).getID() == Id) {
                index = i;
                break;
            }
        }
        return index;
    }

}
