package v2.tsp;

import java.util.ArrayList;

public class City {

	
	ArrayList<Path> roads;
	double x;
	double y;
	ArrayList<Ant> ants;
	
	
	/** Cr�e une nouvelle ville aux coordonn�es s�lectionn�es
	 * @param x abscisse
	 * @param y ordonn�e
	 */
	public City(double x, double y) {
		this.x = x;
		this.y = y;
		roads = new ArrayList<Path>();
		ants = new ArrayList<Ant>();
	}
	
	/** Ajoute un chemin partant de cette ville
	 * @param p le chemin � ajouter
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
