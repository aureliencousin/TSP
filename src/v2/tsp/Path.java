package v2.tsp;

import java.util.ArrayList;
import java.util.List;

public class Path {

	
	private double distance;
	private double pheromones;
	public City a;
	public City b;
	public List<Ant> visitors;



	/** Cr�e un nouveau chemin reliant deux villes
	 * @param distance la distance cart�sienne s�parant les deux villes
	 * @param a la premi�re ville
	 * @param b la seconde ville
	 */
	public Path(double distance,City a, City b) {
		this.distance = distance;
		this.a = a;
		this.b = b;
		pheromones = 0;
		visitors = new ArrayList<Ant>();
	}

	/** Met � jour les ph�romones du chemin
	 * @param evaporationRate le taux d'�vaporation (entre 0 et 1)
	 * @param Q constante d'ajustement (= nombre de villes)
	 */
	public void updatePheromones(double evaporationRate,int Q) {
		double deltasum = 0;
		for (Ant a: visitors) {
			deltasum += Q / a.travelledDistance;
		}
		pheromones = (1-evaporationRate)*pheromones + deltasum;
	}

	/** Retourne la destination du chemin � partir de la ville de d�part
	 * @param from la ville de d�part
	 * @return la ville d'arriv�e
	 */
	public City getDestination(City from) {
		return (a==from)?b:a;
	}

	
	/**
	 * @return la longueur du chemin
	 */
	public double getDistance() {
		return distance;
	}



	/**
	 * @return les pheromones du chemin
	 */
	public double getPheromones() {
		return pheromones;
	}
	
	
	/** Cherche un chemin reliant deux villes dans une liste
	 * @param list la liste de chemins dans laquelle chercher
	 * @param p le chemin � chercher
	 * @return le chemin reliant les deux m�mes villes que p s'il existe, null sinon
	 */
	public static Path getExistingRoad(ArrayList<Path> list, Path p) {
		for(Path entry: list) {
			if(entry.a !=entry.b && p.a != p.b) {
				if((entry.a == p.a || entry.a == p.b) && (entry.b == p.a || entry.b == p.b))
					return entry;
			}
		}
		return null;
	}

}
