/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 z

 Question

 For pathes with length 0 like I1 to I1 .what is lrngth ?
 */
package org.cytoscape.myapp.internal;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author raha
 */
public class scorring {

    private final int maxCIsize;

    private final double maxNumberofCandidate;

    private Vector<Integer> elementaryNOdes;

    private Vector<Float> ocsanaScore;

    private int[] scoreset;

    private Vector<Vector<Integer>> elementarypathes;

    private Vector<Vector<Integer>> pathes;

    private ArrayList<Node> nodeList;

    private Graph graph;

    private int[] target;

    private int[] source;

    private float[] effect;

    private float[] sideeffect;

    private float[] firstterm;

    private float[] secondterm;

    private float[][] effectoftargetprint;

    int pathsearchnumber;

    private int[] offtarget;

    private int X;

    private int Y;

    private int[] x_i;

    private int[] y_i;

    int algorithm;

    private a scoringGUI;

    public void setSource() {
        this.source = graph.getSource();
    }

    public int[] getTarget() {
        return target;
    }

    public void setTarget() {
        this.target = graph.getTarget();
    }

    public int[] getOfftarget() {
        return offtarget;
    }

    public void setOfftarget() {
        this.offtarget = graph.getOfftarget();
    }

    public scorring(int maxCIsize, double maxNumberofCandidate, Vector<Vector<Integer>> pathes, Graph graph, int algorithm, int pathsearchnumber) {
        this.maxCIsize = maxCIsize;
        this.maxNumberofCandidate = maxNumberofCandidate;
        this.pathes = pathes;
        this.graph = graph;
        this.algorithm = algorithm;
        this.pathsearchnumber = pathsearchnumber;

        initial();

    }

    private void initial() {

        scoringGUI = new a();
        scoringGUI.setTitle("Optimal Combination of Intervention set (CIs) report");
        scoringGUI.setVisible(true);
        scoringGUI.txt.append("--- Optimal Combinations of Interventions Report ---\n\nOPTION \n\n");
        scoringGUI.txt.append("Source Nodes:");
        setNodeList();
        setTarget();
        setSource();
        setOfftarget();

        print1();
        setElementarypaths();
        setElementaryNOdes();

        print2();

        setScoreSet();
        setX_i();
        setY_i();
        setX();
        setY();
        print3();
    }

    public void print1() {
        for (int i = 0; i < source.length; i++) {
            scoringGUI.txt.append(nodeList.get(source[i]).getName() + " ");

        }
        scoringGUI.txt.append("\nTarget Nodes:");
        for (int i = 0; i < target.length; i++) {
            scoringGUI.txt.append(nodeList.get(target[i]).getName() + " ");

        }
        scoringGUI.txt.append("\nSide effect Nodes:");
        for (int i = 0; i < offtarget.length; i++) {
            scoringGUI.txt.append(nodeList.get(offtarget[i]).getName() + " ");

        }
        scoringGUI.txt.append("\n\nPath search algorithm: ");
        if (pathsearchnumber == 2) {
            scoringGUI.txt.append("Shortest Paths \n");
        } else if (pathsearchnumber == 1) {
            scoringGUI.txt.append("Optimal and suboptimal shortest paths \n");
        } else {
            scoringGUI.txt.append("All non-self-intersecting paths   \n");
        }

        scoringGUI.txt.append("Finite search radius for all non-intersecting: " + maxNumberofCandidate + "\n");
        scoringGUI.txt.append("\nCI algorithm selected: ");
        if (algorithm == 0) {
            scoringGUI.txt.append("Exact solution(Berge's algorihtm) \n");
        } else {
            scoringGUI.txt.append("Exact solution \n");
        }

        scoringGUI.txt.append("\n RESULTS\n");

        if (path.nopath.size() != 0) {
            scoringGUI.txt.append("\nWARNING:  No pathways were found for the specified parameters between:\n");
            for (int i = 0; i < path.nopath.size(); i = i + 2) {
                scoringGUI.txt.append(nodeList.get(path.nopath.get(i)).getName() + "--/-->" + nodeList.get(path.nopath.get(i + 1)).getName() + "\n");

            }

        }

    }

    public void print2() {

        scoringGUI.txt.append("\n\nFound  " + elementarypathes.size() + " elementary paths and " + elementaryNOdes.size() + " elementary nodes\n");

        for (int j = 0; j < elementarypathes.size(); j++) {
            Vector<Integer> get1 = elementarypathes.get(j);

            for (int k = 1; k < get1.size() - 1; k++) {

                scoringGUI.txt.append(nodeList.get(get1.get(k)).getName());
                int index = nodeList.get(get1.get(k)).getNeighborHashing().indexOf(get1.get(k + 1));
                if (nodeList.get(get1.get(k)).getTypeOfEdge().get(index) == 1) {
                    scoringGUI.txt.append("--->");
                } else {
                    scoringGUI.txt.append("---|");
                }
            }

            scoringGUI.txt.append(nodeList.get(get1.get(get1.size() - 1)).getName() + "\n");

        }

    }

    public void print3() {

        totalOcsanaScore();
        if (algorithm == 0) {
            scoringGUI.txt.append("OCSANA score for each elementary node:\n\n");
            scoringGUI.txt.append("\nElementary node	OCSANA score\n");
            for (int i = 0; i < elementaryNOdes.size(); i++) {
                scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "\t" + ocsanaScore.get(elementaryNOdes.get(i)) + "\n");
            }
            scoringGUI.txt.append("\nEFFECT_ON_TARGETS x SET Score matrix (rows = elementary nodes x columns = target nodes):\n\n\t");
            for (int i = 0; i < target.length; i++) {
                scoringGUI.txt.append(nodeList.get(target[i]).getName() + "\t");
            }
            scoringGUI.txt.append("\n");
            for (int i = 0; i < elementaryNOdes.size(); i++) {
                scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "\t");
                for (int j = 0; j < target.length; j++) {
                    scoringGUI.txt.append(scoreset[elementaryNOdes.get(i)] * effectOnTarget(elementaryNOdes.get(i), target[j]) + "\t");
                }
                scoringGUI.txt.append("\n");
            }

            bergeAlgorithm berge = new bergeAlgorithm(maxCIsize, maxNumberofCandidate, elementarypathes, elementaryNOdes, ocsanaScore);
            Vector<Vector<Integer>> result = berge.getResult();
            scoringGUI.txt.append("\nfound " + result.size() + " optimal CIs.\n\n");
            scoringGUI.txt.append("Optimal CI\t\t\t" + "Size\t" + "OCSANAscore of the CI sets\t" + "xi/x|EFFECT_ON_TARGETS|*SET score of the whole CI set yi/y*SIDE-EFFECT*SET of whole CI set\n");
            for (int i = 0; i < result.size(); i++) {
                Vector<Integer> get = result.get(i);
                scoringGUI.txt.append("[ ");
                // scoringGUI.jTable.setValueAt("[", i, 1);
                float temp = 0;
                float temp1 = 0;
                float temp2 = 0;
                for (int j = 0; j < get.size(); j++) {
                    Integer get1 = get.get(j);
                    
                    temp = temp + ocsanaScore.get(get1);
                    temp1 = temp1 + firstterm[elementaryNOdes.indexOf(get1)] * scoreset[elementaryNOdes.indexOf(get1)];//(float) ((float) x_i[elementaryNOdes.indexOf(get1)] / (float) X) * (float)abs(effectOfTargets(elementaryNOdes.indexOf(get1)));
                    temp2 = temp2 + secondterm[elementaryNOdes.indexOf(get1)] * scoreset[elementaryNOdes.indexOf(get1)];//(float) ((float) y_i[elementaryNOdes.indexOf(get1)] / (float) Y) * (float)abs(sideEffects(elementaryNOdes.indexOf(get1)));
                    if (j < get.size() - 1) {
                        scoringGUI.txt.append(nodeList.get(get1).getName() + " , ");
                    } else {
                        scoringGUI.txt.append(nodeList.get(get1).getName());
                    }

                }
                scoringGUI.txt.append(" ]\t\t\t" + get.size() + "\t\t\t" + temp + "\t\t" + temp1 + "\t\t" + temp2 + "\n");
            }

        } else {
            scoringGUI.txt.append("\n\nOCSANA score for each elementary node:\n\n");
            scoringGUI.txt.append("\nElementary node	OCSANA score\n");
            for (int i = 0; i < elementaryNOdes.size(); i++) {
                scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "\t" + ocsanaScore.get(elementaryNOdes.get(i)) + "\n");
            }
            scoringGUI.txt.append("\nEFFECT_ON_TARGETS x SET Score matrix (rows = elementary nodes x columns = target nodes):\n\n\t");
            for (int i = 0; i < target.length; i++) {
                scoringGUI.txt.append(nodeList.get(target[i]).getName() + "\t");
            }
            scoringGUI.txt.append("\n");
            for (int i = 0; i < elementaryNOdes.size(); i++) {
                scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "\t");
                for (int j = 0; j < target.length; j++) {
                     scoringGUI.txt.append(scoreset[elementaryNOdes.get(i)] * effectOnTarget(elementaryNOdes.get(i), target[j]) + "\t");
                }
                scoringGUI.txt.append("\n");
            }

            scoringGUI.txt.append("OCSANA score for each elementary node:\n\n");
           
            hittingSetGreedyAlgorithm hittingSetGreedy = new hittingSetGreedyAlgorithm(maxCIsize, maxNumberofCandidate, elementarypathes, ocsanaScore, scoreset, elementaryNOdes);
            Vector<Vector<Integer>> totalOcsanaScoreGreedy = hittingSetGreedy.getCIset();
            float[] scorpath = hittingSetGreedy.getCIocsanascore();
            scoringGUI.txt.append("\nfound " + totalOcsanaScoreGreedy.size() + " optimal CIs.\n\n");
            scoringGUI.txt.append("Optimal CI\t\t\t" + "Size\t" + "OCSANAscore of the CI sets\t" + "xi/x|EFFECT_ON_TARGETS|*SET score of the whole CI set yi/y*SIDE-EFFECT*SET of whole CI set\n");
            for (int i = 0; i < totalOcsanaScoreGreedy.size(); i++) {
                Vector<Integer> get = totalOcsanaScoreGreedy.get(i);
                scoringGUI.txt.append("[ ");
                float temp = 0;
                float temp1 = 0;
                float temp2 = 0;
                for (int j = 0; j < get.size(); j++) {
                    Integer get1 = get.get(j);
                    temp = temp + ocsanaScore.get(get1);
                    temp1 = temp1 + firstterm[get1] * scoreset[get1];//(float) ((float) x_i[elementaryNOdes.indexOf(get1)] / (float) X) * (float)abs(effectOfTargets(elementaryNOdes.indexOf(get1)));
                    temp2 = temp2 + secondterm[get1] * scoreset[get1];//(float) ((float) y_i[elementaryNOdes.indexOf(get1)] / (float) Y) * (float)abs(sideEffects(elementaryNOdes.indexOf(get1)));
                    if (j < get.size() - 1) {
                        scoringGUI.txt.append(nodeList.get(get1).getName() + " , ");
                    } else {
                        scoringGUI.txt.append(nodeList.get(get1).getName());
                    }
                }
                scoringGUI.txt.append(" ]\t\t\t" + get.size() + "\t\t\t" + scorpath[i] + "\t\t" + temp1 + "\t\t" + temp2 + "\n");
            }
            

        }
    }

    /**
     * Set the value of nodeList
     *
     * @param
     */
    public void setNodeList() {
        this.nodeList = graph.getNode_List();
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
     * Set the value of elementary paths that contains all of elementary paths
     *
     * @param
     */
    public void setElementarypaths() {

        elementarypathes = new Vector<>();
        for (int i = 0; i < pathes.size(); i++) {
            Vector<Integer> getV = pathes.get(i);

            int sink;

            sink = getV.get(getV.size() - 1);
            for (int j = 0; j < target.length; j++) {

                if (target[j] == sink) {

                    elementarypathes.add(getV);
                    break;
                }
            }
        }

        /*for (int i = 0; i < elementarypathes.size(); i++) {
         for (int j = 0; j < elementarypathes.get(i).size(); j++) {
         if(elementarypathes.get(i).get(j) != -10)
         scoringGUI.txt.append(nodeList.get(elementarypathes.get(i).get(j)).getName()+"  ");
         }
         scoringGUI.txt.append("  \n");
         }*/
    }

    /**
     * assign hash mapping of all elementary nodes
     *
     * @param
     */
    public void setElementaryNOdes() {

        this.elementaryNOdes = new Vector();

        for (int j = 0; j < nodeList.size(); j++) {

            for (int i = 0; i < elementarypathes.size(); i++) {
                if (elementarypathes.get(i).contains(nodeList.get(j).getHashing_map()) && elementarypathes.get(i).get(elementarypathes.get(i).size()-1) != nodeList.get(j).getHashing_map()) {
                    
                    elementaryNOdes.add(nodeList.get(j).getHashing_map());
                    break;

                }
            }
        }

       scoringGUI.txt.append("******************************************************************\n  "
         + "             elementary Nose \n ******************************************************************\n");
         for (int i = 0; i < elementaryNOdes.size(); i++) {
         scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "    ");
         }
         scoringGUI.txt.append("\n******************************************************************\n \n ");
    }

    /**
     * @return setscore of node with hash mapping v_i that is number of path
     * that contain this node and have sink as target node
     *
     * @param v_i, that is hash mapping one elementary node
     */
    public void setScoreSet() {
        scoreset = new int[elementaryNOdes.size()];
        int index = 0;
        for (int i = 0; i < scoreset.length; i++) {
            scoreset[i] = 0;

        }
        for (int i = 0; i < elementarypathes.size(); i++) {
            for (int j = 0; j < elementarypathes.get(i).size(); j++) {
                if (elementarypathes.get(i).get(j) != -10 && elementaryNOdes.contains(elementarypathes.get(i).get(j))) {

                    index = elementaryNOdes.indexOf(elementarypathes.get(i).get(j));
                    scoreset[index]++;

                }
            }
        }
    }

    /**
     * Set the value of pathes
     *
     * @param pathes new value of pathes
     */
    public void setPathes(Vector<Vector<Integer>> pathes) {
        this.pathes = pathes;
    }

    /**
     * @return length of path between node with hash mapping elementaryNode and
     * sink of path path
     *
     *
     * @param path path and hashing map elementary node elementaryNode
     */
    private int lengthOfPath(Vector<Integer> path, int elementaryNode) {

        int lengthOfPath = 0;

        lengthOfPath = path.size() - path.indexOf(elementaryNode) - 1;

        return lengthOfPath;

    }

    /**
     * @return sign of path between node with hash mapping elementaryNode and
     * sink of path path,that is multiple of all of sign of these edge while
     * activationNumber is 1 and inhibitationNumber is -1
     *
     *
     * @param path path and hashing map elementary node elementaryNode
     */
    private int signOfPath(Vector< Integer> path, int elementaryNode) {

        int signOfPath = 1;

        int signOfEdge = 1;

        int nodeHashingNumber;

        int indexElementaryNode;

        indexElementaryNode = path.indexOf(elementaryNode);

        for (int i = indexElementaryNode; i < path.size() - 1; i++) {

            nodeHashingNumber = path.get(i);

            for (int j = 0; j < nodeList.get(nodeHashingNumber).getNeighborHashing().size(); j++) {

                if (nodeList.get(nodeHashingNumber).getNeighborHashing().get(j) == path.get(i + 1)) {

                    signOfEdge = nodeList.get(nodeHashingNumber).getTypeOfEdge().get(j);
                    signOfPath = signOfPath * signOfEdge;
                    break;
                }
            }
        }
        return signOfPath;
    }

    /**
     * @return all of path that start from node with hash mapping
     * elementaryNode, while sink of these path are node with hash mapping
     * sinkId
     *
     * @param hash mapping two nodes elementaryNode and sinkId
     */
    private Vector<Vector<Integer>> allPath(int elementaryNode, int sinkId) {

        Vector<Vector<Integer>> allPath = new Vector<>();

        for (int i = 0; i < pathes.size(); i++) {

            if (pathes.get(i).contains(elementaryNode) & pathes.get(i).get(pathes.get(i).size() - 1) == sinkId) {
                allPath.add(pathes.get(i));
            }
        }
        return allPath;
    }

    /**
     * @return effect on target for elementary node, hash mapping
     * elementaryNode, with target node ,with hash mapping sinkId
     *
     * @param hash mapping two nodes elementaryNode and sinkId
     */
    private float effectOnTarget(int elementaryNode, int sinkId) {

        int numberOfPath = 0;

        float effectOnTarget = 0;

        Vector<Vector<Integer>> allPathes = new Vector<>();

        allPathes = allPath(elementaryNode, sinkId);

      //  scoringGUI.txt.append("\n ******************************************************************\n  "
        //    + "             effectOnTarget \n ******************************************************************\n");
        //   scoringGUI.txt.append("\n elementaryNode:   " + nodeList.get(elementaryNode).getName() + "  sinkId:    " + nodeList.get(sinkId).getName() + "\n");
        numberOfPath = allPathes.size();

        for (int i = 0; i < numberOfPath; i++) {

            if (elementaryNode != sinkId) {

            //  scoringGUI.txt.append("lengthOfPath:  " + lengthOfPath(allPathes.get(i), elementaryNode) + "\t");
                //  scoringGUI.txt.append("signOfPath:  " + (float) signOfPath(allPathes.get(i), elementaryNode) + "\n");
                if (lengthOfPath(allPathes.get(i), elementaryNode) != 0) {
                    effectOnTarget = effectOnTarget + (float) ((float) (1 / (float) lengthOfPath(allPathes.get(i), elementaryNode))) * signOfPath(allPathes.get(i), elementaryNode);
                }
            }
        }

        // scoringGUI.txt.append("effectOnTarget :  " + effectOnTarget + "\n");
        return effectOnTarget;
    }

    /**
     * @return effectOnTarget for this node and all of target nodes
     *
     * @param hash mapping node elementaryNode
     */
    private float effectOfTargets(int elementaryNode) {

        float effectOfTargets = 0;

        int[] target = getTarget();

        for (int i = 0; i < target.length; i++) {

            effectoftargetprint[elementaryNode][i] = effectOnTarget(elementaryNode, target[i]);
            effectOfTargets = effectOfTargets + effectOnTarget(elementaryNode, target[i]);
        }
        // scoringGUI.txt.append("effectOfTargets :  " + effectOfTargets + "\n");
        return effectOfTargets;
    }

    /**
     * @return effectOnTarget for this node and all of offtarget nodes
     *
     * @param hash mapping node sourceId
     */
    private float sideEffects(int sourceId) {

        float effectOfTargets = 0;

        int[] oftarget = getOfftarget();

        for (int i = 0; i < oftarget.length; i++) {

            effectOfTargets = abs(effectOnTarget(sourceId, oftarget[i])) + effectOfTargets;
        }
        //  scoringGUI.txt.append("sideEffects :  " + effectOfTargets + "\n");
        return effectOfTargets;
    }

    /**
     * set X as number of target node that can be reached from at least one of
     * source node
     *
     * @param
     */
    public void setX() {

        int x = 0;
        List<Integer> al = new ArrayList<>();

        for (int i = 0; i < pathes.size(); i++) {

            al.add(pathes.get(i).get(pathes.get(i).size() - 1));

        }

        Set<Integer> hs = new HashSet<>();

        hs.addAll(al);
        for (int i = 0; i < graph.getTarget().length; i++) {
            if (hs.contains(graph.getTarget()[i])) {
                x++;
            }
        }
        X = x;
    }

    /**
     * Set Y as number of offtarget node that can be reached from at least one
     * of source node
     *
     * @param
     */
    public void setY() {

        int x = 0;
        List<Integer> al = new ArrayList<>();

        for (int i = 0; i < pathes.size(); i++) {

            al.add(pathes.get(i).get(pathes.get(i).size() - 1));

        }

        Set<Integer> hs = new HashSet<>();

        hs.addAll(al);

        for (int i = 0; i < graph.getOfftarget().length; i++) {
            if (hs.contains(graph.getOfftarget()[i])) {
                x++;
            }
        }
        Y = x;
    }

    /**
     * Compute x_i, for each of elementary nodes, that is number of target nodes
     * than can reached from elementary node v_i
     *
     * @param
     */
    public void setX_i() {
        x_i = new int[elementaryNOdes.size()];
        for (int i = 0; i < elementaryNOdes.size(); i++) {
            x_i[i] = 0;
        }
        int x = 0;

        for (int i = 0; i < elementaryNOdes.size(); i++) {
            List<Integer> al = new ArrayList<>();
            x = 0;
            x_i[i] = 0;
            for (int j = 0; j < pathes.size(); j++) {

                if (pathes.get(j).contains(elementaryNOdes.get(i))) {
                    al.add(pathes.get(j).get(pathes.get(j).size() - 1));
                }
            }
            Set<Integer> hs = new HashSet<>();

            hs.addAll(al);

            for (int l = 0; l < graph.getTarget().length; l++) {

                if (hs.contains(graph.getTarget()[l])) {
                    x++;
                }
            }
            x_i[i] = x;
            // scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "     x_i" + "    " + x + " \n   ");
        }
    }

    /**
     * Compute y_i, for each of elementary nodes, that is number of offtarget
     * nodes than can reached from elementary node v_i
     *
     * @param
     */
    public void setY_i() {

        y_i = new int[elementaryNOdes.size()];

        for (int i = 0; i < elementaryNOdes.size(); i++) {
            y_i[i] = 0;
        }
        int y = 0;

        for (int i = 0; i < elementaryNOdes.size(); i++) {

            List<Integer> al = new ArrayList<>();
            y = 0;
            y_i[i] = 0;

            for (int j = 0; j < pathes.size(); j++) {

                if (pathes.get(j).contains(elementaryNOdes.get(i))) {
                    al.add(pathes.get(j).get(pathes.get(j).size() - 1));
                }
            }
            Set<Integer> hs = new HashSet<>();

            hs.addAll(al);

            for (int l = 0; l < graph.getOfftarget().length; l++) {

                if (hs.contains(graph.getOfftarget()[l])) {
                    y++;
                }
            }
            y_i[i] = y;
            //  scoringGUI.txt.append(nodeList.get(elementaryNOdes.get(i)).getName() + "     y_i" + "    " + y + " \n   ");
        }
    }

    /**
     * @return overallscore for this node,
     *
     * @param hash mapping node v_i
     */
    private float overAllScore(int v_i) {

        float overAllScor = 0;

        float firstTerm = 0;

        float secondTerm = 0;
        //  scoringGUI.txt.append("22222222222\n");

        effect[v_i] = (float) abs(effectOfTargets(elementaryNOdes.get(v_i)));

        sideeffect[v_i] = (float) abs(sideEffects(elementaryNOdes.get(v_i)));

        // scoringGUI.txt.append("33333333\n");
        if (X != 0) {
            firstTerm = (float) (x_i[v_i] / (float) X) * (float) abs(effectOfTargets(elementaryNOdes.get(v_i)));
        }

        firstterm[v_i] = firstTerm;

        if (Y != 0) {
            secondTerm = (float) (y_i[v_i] / (float) Y) * (float) abs(sideEffects(elementaryNOdes.get(v_i)));
        }

        secondterm[v_i] = secondTerm;

        if (firstTerm > secondTerm) {

            overAllScor = firstTerm - secondTerm;
        }
      //  scoringGUI.txt.append("\t(" + x_i[v_i]+"/"+X +")*"+effect[v_i]+"firstTerm     "+firstTerm+" - [("+
        //    y_i[v_i]+ "/" + Y+")*" + sideeffect[v_i]+"secondTerm      "+secondTerm+"] \t ="+
        //   overAllScor + "\n");

        return overAllScor;
    }

    /**
     * @return ocsanaScore for this node, that compute based on its overAllScore
     * and its setScoreSet
     *
     * @param hash mapping node v_i
     */
    private float ocsanaScore(int v_i) {

        float ocsanaScore = 0, temp1 = 0, temp2 = 0;

        // scoringGUI.txt.append("11111\n");
        temp1 = overAllScore(v_i);
          scoringGUI.txt.append("overAllScore is:  " + "\t"+temp1+"\n");

        temp2 = (float) scoreset[v_i];
        scoringGUI.txt.append("setScoreSet is :  " + "\t"+ temp2+"\n");

        ocsanaScore = temp1 * temp2;

        scoringGUI.txt.append("ocsanaScore is :  " + ocsanaScore + "\n");
        return ocsanaScore;
    }

    /**
     * @return ocsanaScore of all elementary nodes , that is summation of all
     * ocsanaScore for all elementary nodes
     *
     * @param
     */
    private float totalOcsanaScore() {

        // scoringGUI.txt.append("\t totalOcsanaScore  " + "\n");
        float totalOcsanaScore = 0;

        float temp = 0;

        ocsanaScore = new Vector<>();

        effect = new float[elementaryNOdes.size()];

        sideeffect = new float[elementaryNOdes.size()];

        firstterm = new float[elementaryNOdes.size()];

        secondterm = new float[elementaryNOdes.size()];

        effectoftargetprint = new float[elementaryNOdes.size()][target.length];

        for (int i = 0; i < elementaryNOdes.size(); i++) {
            for (int j = 0; j < target.length; j++) {
                effectoftargetprint[i][j] = 0;
            }

        }

        for (int i = 0; i < elementaryNOdes.size(); i++) {

               scoringGUI.txt.append("Node  " + nodeList.get(elementaryNOdes.get(i)).getName() + " \n");
           scoringGUI.txt.append("ocsanaScore :  " + "\n");
            temp = ocsanaScore(elementaryNOdes.get(i));
            temp = ocsanaScore(i);
            ocsanaScore.add(temp);

            totalOcsanaScore = totalOcsanaScore + temp;
            scoringGUI.txt.append("totalOcsanaScore  " + totalOcsanaScore + "\n");
        }
          scoringGUI.txt.append("totalOcsanaScore  " + totalOcsanaScore + "\n");
        return totalOcsanaScore;
    }

}
