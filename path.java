/**
 * 
 */
package org.cytoscape.myapp.internal;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import javax.swing.text.BadLocationException;

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
	static int help = 0;
	ArrayList<Integer> copyresult;

	public path(Graph g, int algorithmNumber, int maxlength) {
		super();

		this.graph = g;
		this.algorithmNumber = algorithmNumber;
		this.maxlength = maxlength;
		this.nodeList = g.getNode_List();
		this.edgeList = g.getEdges_List();
		this.nodeList1 = g.getNode_List();
		copyresult = new ArrayList<>();
		initial();

	}

	a shortpath;
	a suboptimalpath;
	a allpath;

	private void initial() {
		int[] temparray1;
		int[] temparray2;
		int[] temparray3;
		temparray1 = graph.getSource();
		temparray2 = graph.getTarget();
		temparray3 = graph.getOfftarget();

		
		if (algorithmNumber == 1) {
			shortpath = new a();
			shortpath.setTitle("shortest path");
			shortpath.setVisible(true);
			for (int i = 0; i < temparray1.length; i++) {
				source = nodeList.get(temparray1[i]);
				for (int j = 0; j < temparray2.length; j++) {
					sink = nodeList.get(temparray2[j]);
					help = 2;
					shortpath.jTextArea1.append("**********************" + "\n");
					shortpath.jTextArea1.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
							+ nodeList.get(sink.getHashing_map()).getName() + "\n");
					shortestPath();
				}

			}

			for (int i = 0; i < temparray1.length; i++) {
				source = nodeList.get(temparray1[i]);
				for (int j = 0; j < temparray3.length; j++) {
					sink = nodeList.get(temparray3[j]);
					help = 2;
					shortpath.jTextArea1.append("**********************" + "\n");
					shortpath.jTextArea1.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
							+ nodeList.get(sink.getHashing_map()).getName() + "\n");
					shortestPath();
				}

			}

		}
		if (algorithmNumber == 2) {
			suboptimalpath = new a();
			suboptimalpath.setTitle("subOPtimal Pathes");
			suboptimalpath.setVisible(true);
			for (int i = 0; i < temparray1.length; i++) {
				source = graph.getNode_List().get(temparray1[i]);
				for (int j = 0; j < temparray2.length; j++) {
					sink = graph.getNode_List().get(temparray2[j]);
					suboptimalpath.jTextArea1.append("**********************" + "\n");
					suboptimalpath.jTextArea1.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
							+ nodeList.get(sink.getHashing_map()).getName() + "\n");
					shortestPath();
					help = 0;
					
				}

			}

			for (int i = 0; i < temparray1.length; i++) {
				source = graph.getNode_List().get(temparray1[i]);
				for (int j = 0; j < temparray3.length; j++) {
					sink = graph.getNode_List().get(temparray3[j]);
					suboptimalpath.jTextArea1.append("**********************" + "\n");
					suboptimalpath.jTextArea1.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
							+ nodeList.get(sink.getHashing_map()).getName() + "\n");
					shortestPath();
					help = 0;
					
				}

			}

		}

		if (algorithmNumber == 3) {
			allpath = new a();
			allpath.setTitle("allpaath");
			allpath.setVisible(true);
			boolean[] visited = new boolean[nodeList.size()];

			for (int i = 0; i < temparray1.length; i++) {
				source = graph.getNode_List().get(temparray1[i]);
				for (int j = 0; j < temparray2.length; j++) {
					sink = graph.getNode_List().get(temparray2[j]);
					for (int i1 = 0; i1 < nodeList.size(); i1++) {
						visited[i1] = false;
					}
					allpath.jTextArea1.append("**********************" + "\n");
					allpath.jTextArea1.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
							+ nodeList.get(sink.getHashing_map()).getName() + "\n");
					findAllPath(source.getHashing_map(), sink.getHashing_map(), visited);
				}

			}

			for (int i = 0; i < temparray1.length; i++) {
				source = graph.getNode_List().get(temparray1[i]);
				for (int j = 0; j < temparray3.length; j++) {
					sink = graph.getNode_List().get(temparray3[j]);
					for (int i1 = 0; i1 < nodeList.size(); i1++) {
						visited[i1] = false;
					}
					allpath.jTextArea1.append("**********************" + "\n");

					allpath.jTextArea1.append("\t" + nodeList.get(source.getHashing_map()).getName() + "->"
							+ nodeList.get(sink.getHashing_map()).getName() + "\n");

					findAllPath(source.getHashing_map(), sink.getHashing_map(), visited);
				}

			}

		}

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

		int indexsource, indexsink, temp, finish;
		
		
		copyresult.add(-100);

		ArrayList<Integer> reversresult = new ArrayList<>();
		Stack<Integer> result = new Stack<Integer>();

		int[] visited = new int[nodeList1.size()];
		for (int i = 0; i < visited.length; i++)
			visited[i] = 0;

		int[] previousIndex = new int[nodeList1.size()];
		for (int i = 0; i < previousIndex.length; i++)
			previousIndex[i] = -1;

		ArrayList<Integer> current = new ArrayList<>();

		indexsource = source.getHashing_map();
		indexsink = sink.getHashing_map();

		visited[indexsource] = 1;
		current.add(indexsource);
		finish = 0;

		while (finish != 1 & current.size() != 0) {

			for (int i = 0; i < nodeList.get(current.get(0)).getNeighborHashing().size(); i++) {

				if (visited[nodeList.get(current.get(0)).getNeighborHashing().get(i)] == 0 	& (copyresult.indexOf(current.get(0)) + 1 != copyresult
								.indexOf(nodeList.get(current.get(0)).getNeighborHashing().get(i)))) {

					visited[nodeList.get(current.get(0)).getNeighborHashing().get(i)] = 1;
					previousIndex[nodeList.get(current.get(0)).getNeighborHashing().get(i)] = current.get(0);
					current.add(nodeList.get(current.get(0)).getNeighborHashing().get(i));

					if (nodeList.get(current.get(0)).getNeighborHashing().get(i) == indexsink) {

						finish = 1;
						break;
					}
				}
				if (finish == 1)
					break;
			}
			current.remove(0);
		}

		if (previousIndex[indexsink] != -1 || indexsink == indexsource) {

			temp = previousIndex[indexsink];
			reversresult.add(indexsink);
			while (temp != -1) {
				reversresult.add(temp);
				temp = previousIndex[temp];
			}
		}

		for (int i = 0; i < reversresult.size(); i++) 
			result.add(reversresult.get(reversresult.size() - i - 1));
			
		
		if(help == 2)
		printStack(result, 1);
		else
			printStack(result, 2);
			
	
		if (copyresult.size() > 0)
			copyresult.clear();
		if (result.size() > 0)
			for (int i = 0; i < result.size(); i++)
				copyresult.add((Integer) result.get(i));
		
		if (help == 0) {

			help = 1;
			shortestPath();

		}
		

	}

	Stack<Integer> stack = new Stack<Integer>();

	public void findAllPath(int s, int d, boolean[] visited) {
		// allpath.jTextArea1.append(s + " " +nodeList.get(s).getName() );
		stack.add(s);

		if (s == d) {
			printStack(stack,3);
		}

		if (visited[s] != true)
			visited[s] = true;

		ArrayList<Integer> adjNodes = new ArrayList<>();
		adjNodes = nodeList.get(s).getNeighborHashing(); // getAdjacentNodes(s);

	//	if (adjNodes.size() > 0 & stack.size()<maxlength) {
		if (adjNodes.size() > 0) {
			for (int i = 0; i < adjNodes.size(); i++) {
				if (visited[adjNodes.get(i)] != true) {
					// allpath.jTextArea1.append( "
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
		
			allpath.jTextArea1.append("\n");
			for (int i = 0; i < stack.size(); i++) 
				allpath.jTextArea1.append(nodeList.get(stack.get(i)).getName() + "->");
		
			allpath.jTextArea1.append("\n");
		
		}
		
		if (type == 1){
			
			for (int i = 0; i < stack.size(); i++) 
				shortpath.jTextArea1.append(nodeList.get(stack.get(i)).getName() + "->");
			shortpath.jTextArea1.append("\n");
		}
		if(type == 2){

			for (int i = 0; i < stack.size(); i++) 
				suboptimalpath.jTextArea1.append(nodeList.get(stack.get(i)).getName() + "->");
			suboptimalpath.jTextArea1.append("\n");
			
			
			
		}
	}

}
