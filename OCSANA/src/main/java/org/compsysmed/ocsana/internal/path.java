/**
 *
 */
package org.compsysmed.ocsana.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

/**
 * @author raha
 *
 */
public class path {

    private Graph graph;
    private Node source, sink;
    private int algorithmNumber, maxlength;
    public ArrayList<Node> nodeList;
    public ArrayList<Node> nodeList1;
    private ArrayList<Edge> edgeList;
    public int help = 0;
    private ArrayList<Integer> copyresult;
    private Vector<Vector<Integer>> allPathes;
    private Vector<Vector<Integer>> allSecondPathes;
    private Stack<Integer> stack = new Stack<Integer>();

    private int sizeOdShortestPath = 99999;
    private int sizeOdSecondShortestPath;

    private Vector<Integer> current;

    private Vector<Vector<Integer>> Finalresult;
     private Vector<Vector<Integer>> Finalresultf;

    private int k_thPath = 0;

    private final int maxCIsize;

    private final double maxNumberofCandidate;
    static Vector<Integer> nopath;
    
    int algorithm =1;

    a shortpath;
    a suboptimalpath;
    a suboptimalpath1;
    a allpath;

    public path(int maxCIsize, double maxNumberofCandidate, Graph g, int algorithmNumber, int maxlength, int algorithm) {
        super();

        setK_thPath(maxlength);
        this.graph = g;
        this.algorithmNumber = algorithmNumber;
        this.maxlength = maxlength;
        this.nodeList = g.getNode_List();
        this.edgeList = g.getEdges_List();
        this.nodeList1 = g.getNode_List();
        this.maxCIsize = maxCIsize;
        this.maxNumberofCandidate = maxNumberofCandidate;
        this.algorithm = algorithm;
        copyresult = new ArrayList<>();
        allPathes = new Vector<>();
        current = new Vector();
        initial();

    }

    public int getK_thPath() {
        return k_thPath;
    }

    public void setK_thPath(int k_thPath) {
        this.k_thPath = k_thPath;
    }

    private void initial() {
        int[] temparray1;
        int[] temparray2;
        int[] temparray3;
        temparray1 = graph.getSource();
        temparray2 = graph.getTarget();
        temparray3 = graph.getOfftarget();
        nopath =  new Vector<Integer>();
        
         Finalresultf = new Vector<>();

        if (true) {
            suboptimalpath = new a();
            suboptimalpath.setTitle("subOPtimal Pathes");
           // suboptimalpath.setVisible(true);
             suboptimalpath1 = new a();
            suboptimalpath1.setTitle("subOPtimal Pathes");
           // suboptimalpath1.setVisible(true);
            for (int i = 0; i < temparray1.length; i++) {
                source = graph.getNode_List().get(temparray1[i]);
                for (int j = 0; j < temparray2.length; j++) {
                    sink = graph.getNode_List().get(temparray2[j]);
                    suboptimalpath.txt.append("**********************" + "\n");
                    suboptimalpath.txt.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
                            + nodeList.get(sink.getHashing_map()).getName() + "\n");
                    suboptimalpath1.txt.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
                            + nodeList.get(sink.getHashing_map()).getName() + "\n");
                    if (algorithmNumber == 2) {
                        k_thShortestPath(1);
                        help = 0;
                    }
                    if (algorithmNumber == 1) {
                        k_thShortestPath(2);
                        help = 0;
                    }
                    if (algorithmNumber == 3) {
                        k_thShortestPath(50);
                       // k_thShortestPath(50);
                        help = 0;
                    }
                }

            }

            for (int i = 0; i < temparray1.length; i++) {
                source = graph.getNode_List().get(temparray1[i]);
                for (int j = 0; j < temparray3.length; j++) {
                    sink = graph.getNode_List().get(temparray3[j]);
                    suboptimalpath.txt.append("**********************" + "\n");
                    suboptimalpath.txt.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
                            + nodeList.get(sink.getHashing_map()).getName() + "\n");
                     suboptimalpath1.txt.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
                            + nodeList.get(sink.getHashing_map()).getName() + "\n");
                    if (algorithmNumber == 2) {
                        k_thShortestPath(1);
                        help = 0;
                    }
                    if (algorithmNumber == 1) {
                        k_thShortestPath(2);
                        help = 0;
                    }
                    if (algorithmNumber == 3) {
                        k_thShortestPath(50);
                        help = 0;
                    }
                }

            }

        }

        scorring scr = new scorring(maxCIsize, maxNumberofCandidate, Finalresultf, graph, algorithm,algorithmNumber);
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getSink() {
        return sink;
    }

    public void setSink(Node sink) {
        this.sink = sink;
    }

    public int getAlgorithmNumber() {
        return algorithmNumber;
    }

    public void setAlgorithmNumber(int algorithmNumber) {
        this.algorithmNumber = algorithmNumber;
    }

    public void shortestPath() {

        int indexsource, indexsink;

        sizeOdShortestPath = 9999;

        int[] visited = new int[nodeList1.size()];

        for (int i = 0; i < visited.length; i++) {

            visited[i] = 0;
        }

        current = new Vector();

        indexsource = source.getHashing_map();
        indexsink = sink.getHashing_map();

        visited[indexsource] = 1;
        current.add(indexsource);

        int casestop = 0;

        int indexcurrent = 0;

        int t = 0;

        int index = 0;

        int tt = 0;

        while (tt == 0) {

            t = 0;

            while (t == 0) {

                indexcurrent = current.get(current.size() - 1);

                if (indexcurrent == indexsink) {

                    casestop = 1;
                    t = 1;

                    if (current.size() < sizeOdShortestPath) {

                        sizeOdShortestPath = current.size();

                        allPathes.clear();

                        allPathes.addElement(current);
                       //I changed pprint(current);

                        // printAllPathes();
                    } else {
                        allPathes.addElement(current);
                       //I change pprint(current);

                        //printAllPathes();
                    }
                    break;
                }
                t = 1;

                for (int j = 0; j < nodeList.get(current.get(current.size() - 1)).getNeighborHashing().size(); j++) {

                    if (visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(j)] == 0) {

                        t = 0;

                        visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(j)] = 1;

                        current.add(nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(j));

                        break;
                    }

                }

                if (t == 1 & current.size() > 1) {

                    visited[current.get(current.size() - 1)] = 1;

                    current.add(current.get(current.size() - 1));

                    casestop = 3;

                }

                if (current.size() > sizeOdShortestPath) {

                    t = 1;

                    casestop = 2;

                }

            }

            if (current.size() < 3) {

                tt = 1;
                break;

            }

            visited[current.get(current.size() - 1)] = 0;

            current.remove(current.size() - 1);

            index = nodeList.get(current.get(current.size() - 2)).getNeighborHashing()
                    .indexOf(current.get(current.size() - 1));

            visited[current.get(current.size() - 1)] = 0;

            current.remove(current.size() - 1);

            int z = 0;
            int zz = 0;
            int index11 = 0;

            while (true) {
                zz = 0;
                z = 0;
                for (int i = index + 1; i < nodeList.get(current.get(current.size() - 1)).getNeighborHashing()
                        .size(); i++) {

                    if (visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(i)] == 0) {

                        z = 1;
                        index11 = i;
                        break;
                    }
                }

                if (z == 1) {

                    break;
                }

                if (current.size() == 1) {

                    tt = 1;

                    break;
                }
                index = nodeList.get(current.get(current.size() - 2)).getNeighborHashing()
                        .indexOf(current.get(current.size() - 1));

                visited[current.get(current.size() - 1)] = 0;

                current.remove(current.size() - 1);

            }

            if (tt == 1) {
                break;
            }

            if (z == 1) {
                visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(index11)] = 1;
                current.add(nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(index11));

            }
        }

    }

    public void SubOptimal() {

        int indexsource, indexsink;

        sizeOdShortestPath = 9999;
        sizeOdSecondShortestPath = 9999;

        int[] visited = new int[nodeList1.size()];

        for (int i = 0; i < visited.length; i++) {

            visited[i] = 0;
        }

        current = new Vector();

        indexsource = source.getHashing_map();
        indexsink = sink.getHashing_map();

        visited[indexsource] = 1;
        current.add(indexsource);

        int pp = 0;
        int casestop = 0;

        int indexcurrent = 0;

        int t = 0;

        int index = 0;

        int tt = 0;

        while (tt == 0) {

            t = 0;

            while (t == 0) {

                indexcurrent = current.get(current.size() - 1);
                pp++;

                if (indexcurrent == indexsink) {

                    casestop = 1;
                    t = 1;

                    if (current.size() < sizeOdShortestPath) {

                        sizeOdSecondShortestPath = sizeOdShortestPath;

                        sizeOdShortestPath = current.size();

                        allSecondPathes = allPathes;

                        allPathes.clear();

                        allPathes.add(current);

                        printAllPathes();
                    }
                    if (current.size() > sizeOdShortestPath & current.size() < sizeOdSecondShortestPath) {

                        sizeOdSecondShortestPath = current.size();

                        allSecondPathes.clear();

                        allSecondPathes.add(current);

                        printAllPathes();

                    }

                    break;
                }
                t = 1;

                for (int j = 0; j < nodeList.get(current.get(current.size() - 1)).getNeighborHashing().size(); j++) {

                    if (visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(j)] == 0) {

                        t = 0;

                        visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(j)] = 1;

                        current.add(nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(j));

                        break;
                    }

                }

                if (t == 1 & current.size() > 1) {

                    visited[current.get(current.size() - 1)] = 1;

                    current.add(current.get(current.size() - 1));

                    casestop = 3;

                }

                if (current.size() > sizeOdSecondShortestPath) {

                    t = 1;

                    casestop = 2;

                }

            }

            if (current.size() < 3) {

                tt = 1;
                break;

            }

            visited[current.get(current.size() - 1)] = 0;

            current.remove(current.size() - 1);

            index = nodeList.get(current.get(current.size() - 2)).getNeighborHashing()
                    .indexOf(current.get(current.size() - 1));

            visited[current.get(current.size() - 1)] = 0;

            current.remove(current.size() - 1);

            int z = 0;
            int zz = 0;
            int index11 = 0;

            while (true) {
                zz = 0;
                z = 0;
                for (int i = index + 1; i < nodeList.get(current.get(current.size() - 1)).getNeighborHashing()
                        .size(); i++) {

                    if (visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(i)] == 0) {

                        z = 1;
                        index11 = i;
                        break;
                    }
                }

                if (z == 1) {

                    break;
                }

                if (current.size() == 1) {

                    tt = 1;

                    break;
                }
                index = nodeList.get(current.get(current.size() - 2)).getNeighborHashing()
                        .indexOf(current.get(current.size() - 1));

                visited[current.get(current.size() - 1)] = 0;

                current.remove(current.size() - 1);

            }

            if (tt == 1) {
                break;
            }

            if (z == 1) {
                visited[nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(index11)] = 1;
                current.add(nodeList.get(current.get(current.size() - 1)).getNeighborHashing().get(index11));

            }
        }

    }

    public void findAllPath(int s, int d, boolean[] visited) {
        // allpath.txt.append(s + " " +nodeList.get(s).getName() );
        stack.add(s);

        if (s == d) {
            printStack(stack, 3);
        }

        if (visited[s] != true) {
            visited[s] = true;
        }

        ArrayList<Integer> adjNodes = new ArrayList<>();
        adjNodes = nodeList.get(s).getNeighborHashing(); // getAdjacentNodes(s);

        if (adjNodes.size() > 0 & stack.size() < maxlength) {
            // if (adjNodes.size() > 0) {
            for (int i = 0; i < adjNodes.size(); i++) {
                if (visited[adjNodes.get(i)] != true) {
                    // allpath.txt.append( "
                    // "+nodeList.get(adjNodes.get(i)).getCanonicalName());
                    findAllPath(adjNodes.get(i), d, visited);
                }
            }
        }

        visited[s] = false;
        stack.remove(stack.size() - 1);
    }

    private void printStack(Stack<Integer> stack, int type) {

        if (type == 3) {

            allpath.txt.append(maxlength + "\n");
            for (int i = 0; i < stack.size(); i++) {
                allpath.txt.append(nodeList.get(stack.get(i)).getName() + "->");
            }

            allpath.txt.append("\n");

        }

        if (type == 1) {

            for (int i = 0; i < stack.size(); i++) {
                shortpath.txt.append(nodeList.get(stack.get(i)).getName() + "->");
            }
            shortpath.txt.append("\n");
        }
        if (type == 2) {

            for (int i = 0; i < stack.size(); i++) {
                suboptimalpath.txt.append(nodeList.get(stack.get(i)).getName() + "->");
            }
            suboptimalpath.txt.append("\n");

        }
    }

    private void printAllPathes() {

        // shortpath.txt.append("nnnnnnnmmmmmmm");
        for (int i = 0; i < allPathes.size(); i++) {
            for (int j = 0; j < (i + 1) * allPathes.get(i).size(); j++) {
                shortpath.txt.append(nodeList.get(allPathes.get(i).get(j)).getName() + "->");
            }

            shortpath.txt.append("\n\n");
        }

        // }
    }

    public void pr(ArrayList<Integer> a, int i) {

        for (int j = 0; j < a.size(); j++) {

            shortpath.txt.append(nodeList.get(a.get(j)).getName() + "  ");

        }
        shortpath.txt.append(i + "\n\n");

    }

    private void k_thShortestPath(int numberOfk_thPath) {

        int sourceIndex, sinkIndex, temp, temp1, indextemp, numberOfCurentK_thPath, currentLevel, nextLevel, ii;

        numberOfCurentK_thPath = 0;

        temp = 0;

        temp1 = 0;

        indextemp = 0;

        currentLevel = 0;

        nextLevel = 0;

        ii = 0;

        int finish = 0;

        Node currentNodeTemp = new Node();

        Vector<Integer> ini = new Vector();
        ini.add(-10);

        Vector<Vector<Vector<Integer>>> vectoeAllPathData = new Vector<>();

        Finalresult = new Vector<>();
        current.clear();
        Vector<Vector<Vector<Integer>>> tempVectoeAllPathData = new Vector<>();

        vectoeAllPathData = initialVector(vectoeAllPathData);

        tempVectoeAllPathData = initialVector(tempVectoeAllPathData);

        sourceIndex = source.getHashing_map();

        sinkIndex = sink.getHashing_map();

        current.add(sourceIndex);

        currentLevel = 1;
        if (sourceIndex == sinkIndex) {
            vectoeAllPathData.get(sinkIndex).get(0).add(sourceIndex);
            finish = 1;
            Vector<Integer> tempvectorresulr = new Vector<>();
            tempvectorresulr.addElement(-10);
            tempvectorresulr.addElement(sourceIndex);
            Finalresult.add(tempvectorresulr);
        }

        suboptimalpath.txt.append("*************************************\n while************************************* ii \n" + ii + "\n");
        while ((numberOfCurentK_thPath < numberOfk_thPath + 1) & current.size() != 0 & finish != 1) {

            suboptimalpath.txt.append("*************************************\n while************************************* ii \n" + ii + "\n");
            printcurrent(current);
            suboptimalpath.txt.append(numberOfCurentK_thPath + "   " + numberOfk_thPath + "   " + current.size() + "\n");
            suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
            suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
            suboptimalpath.txt.append("current:  " + current + "\n");

            ii++;
            temp = current.get(0);
            currentNodeTemp = nodeList.get(temp);
            //  suboptimalpath.txt.append("1***********************\n");
            for (int i = 0; i < currentNodeTemp.getNeighborHashing().size() && finish != 1; i++) {

              //suboptimalpath.txt.append("*************************************\n for************************************* \n");
                // suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
                // suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
                // suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
                // suboptimalpath.txt.append("current:  " + current + "\n");
                indextemp = currentNodeTemp.getNeighborHashing().get(i);

                if (indextemp == sinkIndex) {

                //    suboptimalpath.txt.append("*************************************\n if************************************* \n");
                    // suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
                    // suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("current:  " + current + "\n");
                    Vector<Vector<Integer>> ttemvec = new Vector<>();
                    if ((tempVectoeAllPathData.get(indextemp)).get(0).size() > 1) {
                        ttemvec = (Vector<Vector<Integer>>) tempVectoeAllPathData.get(indextemp).clone();
                    }

                    for (int j = 0; j < vectoeAllPathData.get(currentNodeTemp.getHashing_map()).size(); j++) {

                        Vector<Integer> ttemvec2 = (Vector<Integer>) vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).clone();
                        // suboptimalpath.txt.append("22:  "+ttemvec2 +"\n");
                        if (vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).get(vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).size() - 1)
                                != currentNodeTemp.getHashing_map()) {
                            ttemvec2.addElement(currentNodeTemp.getHashing_map());
                        }
                        // suboptimalpath.txt.append(currentNodeTemp.getHashing_map()+ "\n");
                        // suboptimalpath.txt.append("ttemvec2:  " + ttemvec2 + "\n");
                        ttemvec2.addElement(sinkIndex);
                        if (!Finalresult.contains(ttemvec2) && ttemvec2.size()<= maxlength +1) {
                            
                            
                            Finalresult.addElement(ttemvec2);
                            if(Finalresult.size() >= 2 && numberOfCurentK_thPath >= numberOfk_thPath && Finalresult.get(Finalresult.size()-1).size() > Finalresult.get(Finalresult.size() - 2).size() )
                                Finalresult.remove(Finalresult.size()-1);
                            else if (Finalresult.size() >= 2 && Finalresult.get(Finalresult.size() - 1).size() > Finalresult.get(Finalresult.size() - 2).size()) {
                                numberOfCurentK_thPath++;
                            }
                                
                                }
                       if (Finalresult.size() == 1) {
                          numberOfCurentK_thPath++;
                        }
                        
                        ttemvec.addElement(ttemvec2);
                    }
                    tempVectoeAllPathData.setElementAt(ttemvec, indextemp);

                //   suboptimalpath.txt.append("*************************************\n end if************************************* \n");
                    // suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
                    // suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("current:  " + current + "\n");
                } else {

                 //  suboptimalpath.txt.append("*************************************\n else************************************* \n");
                    // suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
                    //suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("current:  " + current + "\n");
                    // suboptimalpath.txt.append("4***********************\n");
                    Vector<Vector<Integer>> temvec = new Vector<>();

                    if ((tempVectoeAllPathData.get(indextemp)).get(0).size() > 1) {
                        temvec = (Vector) tempVectoeAllPathData.get(indextemp);

                    }
                    int ztemp = 0;
                    for (int j = 0; j < vectoeAllPathData.get(currentNodeTemp.getHashing_map()).size(); j++) {
                       //  suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData +"   "+ indextemp+"\n");

                        if (vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).contains(indextemp) != true) {
                            suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j) + "   " + indextemp + "\n");
                            ztemp++;
                            Vector<Integer> temvec2 = (Vector<Integer>) vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).clone();

                            if (vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).get(vectoeAllPathData.get(currentNodeTemp.getHashing_map()).get(j).size() - 1)
                                    != currentNodeTemp.getHashing_map()) {
                                temvec2.add(currentNodeTemp.getHashing_map());
                            }

                            temvec.addElement(temvec2);

                        }
                    }

                    if (ztemp != 0) {
                        if (temvec.size() == 0) {
                            temvec.add(ini);
                        }
                        // suboptimalpath.txt.append ("1temvec:  "+temvec +"\n");
                        tempVectoeAllPathData.setElementAt(temvec, indextemp);
                        if ((current.lastIndexOf(indextemp) < currentLevel) | current.lastIndexOf(indextemp) == -1) {
                            current.add(indextemp);
                            nextLevel++;
                        }
                    }
                //    suboptimalpath.txt.append("*************************************\n end else************************************* \n");
                    // suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
                    // suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
                    // suboptimalpath.txt.append("current:  " + current + "\n");
                }
            }
           // suboptimalpath.txt.append("*************************************\n befor if************************************* \n");
            //  suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
            //  suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
            //  suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
            //  suboptimalpath.txt.append("current:  " + current + "\n");

            current.remove(0);
            if (current.size() == 0) {
                break;
            }

            if (currentLevel > 1) {
                currentLevel--;

            } else {
                for (int i = 0; i < vectoeAllPathData.size(); i++) {

                    Vector<Vector<Integer>> asd = new Vector<>();

                    asd = (Vector<Vector<Integer>>) vectoeAllPathData.get(i).clone();
                    for (int j = 0; j < vectoeAllPathData.get(i).size(); j++) {
                        tempVectoeAllPathData.get(i).remove(vectoeAllPathData.get(i).get(j));
                    }

                    asd.addAll(tempVectoeAllPathData.get(i));
                    if (asd.size() > 1) {
                        asd.remove(ini);
                    }
                    // suboptimalpath.txt.append ("*asd:  "+asd +"\n");                 
                    vectoeAllPathData.setElementAt(asd, i);
                    if (vectoeAllPathData.get(i).get(vectoeAllPathData.get(i).size()-1).size()>maxlength+2) {
                        finish = 1;
                        
                    }
                    
                }

                initialVector(tempVectoeAllPathData);
                currentLevel = nextLevel;
                nextLevel = 0;
            }
            // suboptimalpath.txt.append("*************************************\n befor wile************************************* \n");
            //  suboptimalpath.txt.append ( numberOfCurentK_thPath + "   "+ numberOfk_thPath + "   "+ current.size()+"\n");
            //  suboptimalpath.txt.append("vectoeAllPathData:  " + vectoeAllPathData + "\n");
            //  suboptimalpath.txt.append("tempVectoeAllPathData:  " + tempVectoeAllPathData + "\n");
            //  suboptimalpath.txt.append("current:  " + current + "\n");
        }

        suboptimalpath.txt.append("***********************\n" + Finalresult + "***********************\n");
        if (Finalresult.size() == 0) {
            nopath.add(sourceIndex);
              nopath.add(sinkIndex);
            
        }
        for (int i = 0; i < Finalresult.size(); i++) {
           Finalresultf.add(Finalresult.get(i));
            
        }
pprintfinalresult(Finalresultf, sinkIndex);
    }

    private Vector<Vector<Vector<Integer>>> updatePathesData(Vector<Vector<Vector<Integer>>> vectoeAllPathData, Vector<Vector<Vector<Integer>>> tempVectoeAllPathData) {

        Vector<Vector<Integer>> temvec = new Vector<>();

        //Vector<Integer> temvec2 = null  ;
        for (int i = 0; i < tempVectoeAllPathData.size(); i++) {

            if (tempVectoeAllPathData.get(i).get(0).get(0) != -10) {

                if (vectoeAllPathData.get(i).get(0).get(0) != -10) {
                    temvec = (Vector) vectoeAllPathData.get(i);
                }

                for (int j = 0; j < tempVectoeAllPathData.get(i).size(); j++) {

                    temvec.addElement((Vector<Integer>) ((Vector) tempVectoeAllPathData.get(i)).get(j));
                }

                vectoeAllPathData.setElementAt(temvec, i);

                temvec = new Vector<>();

            }

        }
        return vectoeAllPathData;
    }

    private Vector<Vector<Vector<Integer>>> initialVector(Vector<Vector<Vector<Integer>>> tempVectoeAllPathData) {

        Vector<Integer> vectorTemp1 = new Vector<>();

        Vector<Vector<Integer>> vectorTemp2 = new Vector<>();

        tempVectoeAllPathData.clear();

        vectorTemp1.add(-10);

        vectorTemp2.add(vectorTemp1);

        for (int i = 0; i < nodeList.size(); i++) {

            tempVectoeAllPathData.add(vectorTemp2);
        }

        return tempVectoeAllPathData;
    }

    private void prinAllPathesData(Vector<Vector<Vector<Integer>>> vectoeAllPathData) {

        for (int i = 0; i < vectoeAllPathData.size(); i++) {
            Vector<Vector<Integer>> get = vectoeAllPathData.get(i);
            //suboptimalpath.txt.append(nodeList.get(i).getName()+  "\n");
            suboptimalpath.txt.append(get + "\n");

            /*  for (int j = 0; j < get.size(); j++) {
             Vector<Integer> get1 = get.get(j);
             suboptimalpath.txt.append (j+"    ");

             for (int k = 0; k < get1.size(); k++) {
             Integer get2 = get1.get(k);
             //suboptimalpath.txt.append(nodeList.get(get2).getName() + "->");
             suboptimalpath.txt.append( get2+ "->");
             } */
            suboptimalpath.txt.append("\n");
        }
    }

    // suboptimalpath.setVisible(true);
    private Vector<Integer> coppyvector(Vector<Integer> first) {
        Vector<Integer> last = new Vector<>();
        for (int i = 0; i < first.size(); i++) {
            last.set(i, first.get(i));
        }

        return last;
    }

    private Vector<Vector<Vector<Integer>>> coppyvector2(Vector<Vector<Vector<Integer>>> first, Vector<Vector<Vector<Integer>>> last) {

        last = new Vector<>();

        Vector<Integer> vectorTemp1 = new Vector<>();

        Vector<Vector<Integer>> vectorTemp2 = new Vector<>();

        for (int i = 0; i < first.size(); i++) {

            vectorTemp2.clear();

            vectorTemp1.clear();

            for (int j = 0; j < first.get(i).size(); j++) {

                vectorTemp1 = coppyvector(first.get(i).get(j));

                vectorTemp2.addElement(vectorTemp1);
            }
            last.addElement(vectorTemp2);

        }

        return last;
    }

    private void printresult(Vector<Vector<Integer>> result) {

        for (int i = 0; i < result.size(); i++) {
            // suboptimalpath.txt.append(result.size() + "\n");
            for (int j = 0; j < result.get(i).size(); j++) {

                // suboptimalpath.txt.append(result.get(i).size() + "\n");
                suboptimalpath.txt.append(result.get(i).get(j) + "->");
            }
            suboptimalpath.txt.append("\n");
        }

    }

    private void printresult1(Vector<Integer> result) {

        suboptimalpath.txt.append(result.size() + "\n");
        for (int j = 0; j < result.size(); j++) {

            suboptimalpath.txt.append(result.size() + "    :size \n");
            suboptimalpath.txt.append(result.get(j) + "->");
        }
        suboptimalpath.txt.append("\n");

    }

    public void pprintfinalresult(Vector<Vector<Integer>> f, int sinkIndex) {

         suboptimalpath1.txt.append("\n"+ f.size()+"\n");
        for (int j = 0; j < f.size(); j++) {
            Vector<Integer> get1 = f.get(j);

            for (int k = 1; k < get1.size(); k++) {

                suboptimalpath1.txt.append(nodeList.get(get1.get(k)).getName()+"  ");
               // int index = nodeList.get(get1.get(k)).getNeighborHashing().indexOf(get1.get(k + 1));
               
            }
            suboptimalpath1.txt.append("\n");

           

        }

        //}
    }

    public void printcurrent(Vector<Integer> current) {
        for (int i = 0; i < current.size(); i++) {
            suboptimalpath.txt.append(nodeList.get(current.get(i)).getName() + "  \t ");

        }

    }
}
