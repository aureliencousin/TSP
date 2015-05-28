package ants;
import java.util.ArrayList;


public class Node {

	private ArrayList<Ant> ants ;
	
	public Node() {
		ants = new ArrayList<Ant>();
	}
	
	/* ajoute la fourmi "a" au noeud */
	public boolean add(Ant a) {
		return ants.add(a);
	}
	
	/* supprime la fourmi "a" du noeud */
	public boolean remove(Ant a) {
		return ants.remove(a);
	}
	
	public void clear() {
		ants.clear();
	}
	
	public ArrayList<Ant> getAnts() {
		return ants;
	}
	
	/* retourne un tableau contenant le nombre de fourmis allant vers le nid [0]
	et vers la nourriture [1] */
	public int[] getAntDirections() {
		int[] directions = new int[2];
		directions[0]=0;
		directions[1]=0;
		for (Ant a: ants) {
			if(a.getDirection() == Direction.NEST)
				directions[0]++;
			else
				directions[1]++;
				
		}
		return directions;
	}
	
	public void populate(int count, Direction direction) {
		for(int i=0; i < count; i++) {
			add(new Ant(direction));
		}
	}
	
	public int size() {
		return ants.size();
	}
	
}
