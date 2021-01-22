import java.util.*;
import org.graphstream.algorithm.flow.FlowAlgorithmBase;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.util.ArrayList;

public class Flow extends FlowAlgorithmBase {
	
	public static Graph graph;
	
	public static void main(String[] args) {
		
		graph = new SingleGraph("Graph");
		Flow algo=new Flow();
		
		buildGraph(graph);
		
		graphToFlow(graph, algo);
	 	
		System.setProperty("org.graphstream.ui", "swing");
		graph.setAutoCreate(true);
		graph.display();	
		
		algo.compute();
		
		// set label on edges to display flows and capacities
		algo.flowGraph.edges().forEach(e -> {
			e.setAttribute("ui.label", algo.getFlow(e.getNode0(), e.getNode1())+"/"+algo.getCapacity(e.getNode0(), e.getNode1()));
			e.setAttribute("ui.style", "text-background-mode : plain; text-size:15px;");
		});
	}
	
	private static void buildGraph(Graph graph) {
		graph.addNode("s" );	
		graph.addNode("t" );
		graph.addNode("U1" );
		graph.addNode("U2" );
		graph.addNode("PF1");
		graph.addNode("PF2");
		graph.addNode("PF3");
		graph.addNode("PF4");
		graph.addNode("PF5");
		graph.addNode("C1");
		graph.addNode("C2");
		graph.addNode("C3");
		
		graph.addEdge("sU1", "s", "U1",true);
		graph.addEdge("sU2", "s", "U2", true);
		graph.addEdge("U2PF3", "U2", "PF3", true);
		graph.addEdge("U1PF3", "U1", "PF3", true);
		graph.addEdge("U2PF2", "U2", "PF2", true);
		graph.addEdge("U1PF2", "U1", "PF2", true);
		graph.addEdge("U1PF1", "U1", "PF1", true);
		graph.addEdge("PF1PF2", "PF1", "PF2", true);
		graph.addEdge("PF1PF4", "PF1", "PF4", true);
		graph.addEdge("PF2C1", "PF2", "C1", true);
		graph.addEdge("PF2C2", "PF2", "C2", true);
		graph.addEdge("PF2C3", "PF2", "C3", true);
		graph.addEdge("PF3PF5", "PF3", "PF5", true);
		graph.addEdge("PF5C2", "PF5", "C2", true);
		graph.addEdge("PF5C3", "PF5", "C3", true);
		graph.addEdge("C1t", "C1", "t", true);
		graph.addEdge("C2t", "C2", "t", true);
		graph.addEdge("C3t", "C3", "t", true);
		graph.addEdge("PF4C1","PF4","C1", true);
		graph.addEdge("PF4C2","PF4","C2", true);
		
		for (Node node : graph) {
	        node.setAttribute("ui.label", node.getId());
	        node.setAttribute("ui.style", "shape:circle;fill-color: cadetblue; size: 33px; text-alignment: center; text-style:bold;text-size:15px;");
	    }
		graph.getNode("s").setAttribute("ui.style", "fill-color:tomato; shape:diamond; size: 33px; text-alignment: center; text-style:bold;text-size:15px;");
		graph.getNode("t").setAttribute("ui.style", "fill-color:tomato; shape:diamond; size: 33px; text-alignment: center; text-style:bold;text-size:15px;");
	}
	
	private static void graphToFlow(Graph graph, Flow flow) {
		flow.init(graph,"s","t");

        flow.setCapacity(graph.getNode("s"), graph.getNode("U1"), 35);
        flow.setCapacity(graph.getNode("s"), graph.getNode("U2"), 25);
        flow.setCapacity(graph.getNode("U1"), graph.getNode("PF1"), 20);
        flow.setCapacity(graph.getNode("U1"), graph.getNode("PF2"), 15);
        flow.setCapacity(graph.getNode("U1"), graph.getNode("PF3"), 12);
        flow.setCapacity(graph.getNode("U2"), graph.getNode("PF2"), 6);
        flow.setCapacity(graph.getNode("U2"), graph.getNode("PF3"), 22);
        flow.setCapacity(graph.getNode("PF1"), graph.getNode("PF4"), 15);
        flow.setCapacity(graph.getNode("PF1"), graph.getNode("PF2"), 10);
        flow.setCapacity(graph.getNode("PF2"), graph.getNode("C1"), 10);
        flow.setCapacity(graph.getNode("PF2"), graph.getNode("C2"), 15);
        flow.setCapacity(graph.getNode("PF2"), graph.getNode("C3"), 15);
        flow.setCapacity(graph.getNode("PF3"), graph.getNode("PF5"), 22);
        flow.setCapacity(graph.getNode("PF4"), graph.getNode("C1"), 7);
        flow.setCapacity(graph.getNode("PF4"), graph.getNode("C2"), 10);
        flow.setCapacity(graph.getNode("PF5"), graph.getNode("C2"), 10);
        flow.setCapacity(graph.getNode("PF5"), graph.getNode("C3"), 10);
        flow.setCapacity(graph.getNode("C1"), graph.getNode("t"), 15);
        flow.setCapacity(graph.getNode("C2"), graph.getNode("t"), 15);
        flow.setCapacity(graph.getNode("C3"), graph.getNode("t"), 20);

        
		flow.flowGraph.edges().forEach(e -> {
			flow.setFlow(e.getNode0().getId(), e.getNode1().getId(),0);
		});
	}
	private static ArrayList<Node> predecesseurs(Node sommet) {
		ArrayList<Node> pred = new ArrayList<Node>();
	
		sommet.enteringEdges().forEach(e -> {
			pred.add(e.getNode0());
		});
		return pred;
	}
	
	private static ArrayList<Node> successeurs (Node sommet) {
		ArrayList<Node> succ = new ArrayList<Node>();
		
		sommet.leavingEdges().forEach(e -> {
			succ.add(e.getNode1());
		});
		return succ;
	}
	
	public  void compute() {
		System.out.println("source node: "+ this.getFlowSourceId());
		System.out.println("target node: "+ this.getFlowSinkId());
		double F=0; // flow max
		HashMap<Node,Node> chaine= new HashMap<Node,Node>(); // couples enfant/parent de la chaine trouvee
		
		while (chaineAmeliorante(chaine)) {
			double sigma=calculAugm(chaine);
			F+=sigma;
			augmenter(chaine, sigma);
			chaine.clear();
			System.out.println();
			System.out.println("final flow: "+ F);
		}
		this.maximumFlow=F;
	}
	
	private Double calculAugm(HashMap<Node,Node> parent) {
		Double sigma=Double.MAX_VALUE;
		Node t = this.flowGraph.getNode(this.getFlowSinkId());
		String chaine=t.getId() ;
		while (t!=this.flowGraph.getNode(this.getFlowSourceId())) {
			Node s = parent.get(t);
			String c= s.getId()+", ";
			chaine =c.concat(chaine);
			if ( predecesseurs(t).contains(s)) { // arc avant
				sigma = Math.min(sigma, this.getCapacity(s,t)-this.getFlow(s,t));
			} else sigma =Math.min(sigma, this.getFlow(t,s)); // arc arrière
			t=s;
		}
		System.out.println("augPath: ["+chaine+ "]" );
		System.out.println("min res capacity: "+ sigma );
		return sigma;
	}
	
	private  void augmenter(HashMap<Node,Node> parent, Double sigma) {
		Node t = this.flowGraph.getNode(this.getFlowSinkId());
		while (t!=this.flowGraph.getNode(this.getFlowSourceId())) {
			Node s = parent.get(t);
			if ( predecesseurs(t).contains(s)) { // arc avant
				this.setFlow(s,t, this.getFlow(s, t) + sigma);
			} else this.setFlow(t, s, this.getFlow(t, s) - sigma); // arc arrière
			t=s;
		}
	}
	
	private  boolean chaineAmeliorante(HashMap<Node, Node> parent){
		
		boolean pathFound=false;
		LinkedList<Node> queue = new LinkedList<Node>();
		
		queue.add(this.flowGraph.getNode(this.getFlowSourceId()));
		parent.put(this.flowGraph.getNode(this.getFlowSourceId()),null);
		
		// init nodes not marked
		for(int iter=0;iter<this.flowGraph.getNodeCount();iter++) {
			this.flowGraph.getNode(iter).setAttribute("marked", "false");
		}
		
		do{
			Node i=queue.remove();
			
			successeurs(i).forEach(j->{
				if (this.getFlow(i, j)<this.getCapacity(i, j)){
					queue.add(j);
					parent.put(j,i);
					j.setAttribute("marked", "true");
				}
			});
		
			// Si y a des arcs arrières
			predecesseurs(i).forEach(j->{
				if (this.getFlow(j,i)>0 && !j.getAttribute("marked").equals("true")){
					queue.add(j);
					parent.put(j,i);
					j.setAttribute("marked", "true");
				}
			});
		} while (!queue.isEmpty());
		
		pathFound= this.flowGraph.getNode(this.getFlowSinkId()).getAttribute("marked").equals("true");
		return pathFound;
	}
	
}
