package v2.tsp;

import java.util.ArrayList;

public class City {

	
	ArrayList<Path> roads;
	double x;
	double y;
	ArrayList<Ant> ants;
	
	
	/** Crée une nouvelle ville aux coordonnées sélectionnées
	 * @param x abscisse
	 * @param y ordonnée
	 */
	public City(double x, double y) {
		this.x = x;
		this.y = y;
		roads = new ArrayList<Path>();
		ants = new ArrayList<Ant>();
	}
	
	/** Ajoute un chemin partant de cette ville
	 * @param p le chemin à ajouter
	 */
	public void addPath(Path p) {
		roads.add(p);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "["+(int)x+","+(int)y+"|"+ants.size()+"]";
	}
}
