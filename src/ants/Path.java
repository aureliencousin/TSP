package ants;
import java.util.ArrayList;
import java.util.List;


public class Path {
	
	public Node[] nodes;
	private List<Integer> pheromones = new ArrayList<Integer>();

	public Path(int nodescount) {
		nodes = new Node[nodescount];
		for(int i = 0; i < nodescount; i++) {
			nodes[i] = new Node();
		}
	}

	public int size() {
		return nodes.length;
	}
	
	public int getPheromones() {
		return pheromones.size();
	}

	public void addPheromones(int pheromones, int timer) {
		for(int i=0; i< pheromones; i++) {
			this.pheromones.add(timer);
		}
	}
	
	public void stepPheromones() {
		List<Integer> newphero = new ArrayList<Integer>();
		for(Integer i: pheromones) {
			i -= 1;
			if(i>0) {
				newphero.add(i);
			}
		}
		pheromones = newphero;
	}
	
	public Node[] move(ArrayList<Ant> newAnts, Node food) { 
		Node[] arrived = new Node[2];
		Node[] newnodes = new Node[nodes.length];
		ArrayList<Ant> ants;
		for(int i = 0; i < newnodes.length; i++) {
			newnodes[i] = new Node();
		}
		arrived[0] = new Node();
		arrived[1] = new Node();
		
		for(int i=0; i < nodes.length; i++) {
			
			ants = nodes[i].getAnts();
		
			
			for(Ant a: ants) {
				//deplacer les fourmis qui vont vers FOOD
				if(a.getDirection() == Direction.FOOD) {
					if(i < nodes.length-1)
						newnodes[i+1].add(a);
					else
						arrived[1].add(a);
				}
				//deplacer les fourmis qui vont vers NEST
				else {
					if(i > 0)
						newnodes[i-1].add(a);
					else
						arrived[0].add(a);
				}
			}
			
			
		}
		ants = newAnts;
		for(Ant a: ants) {
			//deplacer les fourmis qui vont vers FOOD
			if(a.getDirection() == Direction.FOOD) {
				newnodes[0].add(a);
			}
			
		}
		ants = food.getAnts();
		for(Ant a: ants) {
			//deplacer les fourmis qui vont vers FOOD
			if(a.getDirection() == Direction.NEST) {
				newnodes[newnodes.length-1].add(a);
			}
			
		}
		nodes = newnodes;
		
		
		
		return arrived;
	}
	
	public Node getNode(int idx) {
		return nodes[idx];
	}
	
	public String toString() {
		String s = "";
		int[] dir;
		for(int i=0; i < nodes.length;i++) {
			dir = nodes[i].getAntDirections();
			s+= "[N:"+dir[0]+",F:"+dir[1]+"]";	
		}
		return s;
	}

	
}
