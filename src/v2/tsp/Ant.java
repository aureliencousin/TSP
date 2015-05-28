package v2.tsp;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class Ant {

	public List<City> visited = new ArrayList<City>();
	public City currentCity = null;
	public Path destination;
	public double travelledDistance;
	
	
	public Ant() {
	}
	
	
	/** fonction de sélection aléatoire pondérée à partir d'une table d'intensités
	 * @param intensities la Map contenant les probabilités de chaque chemin
	 * @return l'entrée contenant le chemin choisi et sa probabilité
	 */
	public Entry<Path,Double> randomFromProbs(HashMap<Path,Double> intensities) {
		/* Not working ?
		double weightsum = 0; 
		for(Entry<Path,Double> en: intensities.entrySet()) {
			weightsum += en.getValue();
		}	*/
		double randomNum;	
		Random rnd = new Random();
		randomNum = rnd.nextDouble();
		Entry<Path,Double> last = null;
		for(Entry<Path,Double> en: intensities.entrySet()) {
			randomNum -= en.getValue();

		    if(randomNum <= 0) {
		    	return en;
		    }
		    last = en;
		}
		return last;
	}
	
	
	/** Choisit la prochaine ville à visiter et déplace la fourmi dessus en fonction de l'intensité des chemins
	 * @return la nouvelle ville courante
	 */
	public City chooseNextCity() {
		Path max = null;
		HashMap<Path,Double> intensities = getNextPathProbabilities();
		if(TSPSimulation.weightedRandom) {
			try {
				max = randomFromProbs(intensities).getKey();
			} catch(NullPointerException npe) {
			}
		}
		else {
			double maxvalue = 0;
			for(Entry<Path, Double> e: intensities.entrySet()) {
				if(e.getValue() >= maxvalue && !visited.contains(e.getKey().getDestination(currentCity))) {
					max = e.getKey();
					maxvalue = e.getValue();
				}
			}
			
		}
		// on a visité toutes les villes, on retourne à la première
		if(max == null) {
			max = findPath(currentCity,visited.get(0));
		}
		max.visitors.add(this);
		currentCity = max.getDestination(currentCity);
		currentCity.ants.add(this);
		travelledDistance += max.getDistance();
		visited.add(currentCity);
		return currentCity;
	}
	
	public Path findPath(City from,City to) {
		for(Path p: from.roads) {
			if(p.getDestination(from) == to) {
				return p;
			}
		}
		return null;
	}
	
	/** Calcule la probabilité de choisir chaque chemin
	 * @return une map contenant le probabilité associée à chaque chemin
	 */
	public HashMap<Path,Double> getNextPathProbabilities() {
		double alpha = TSPSimulation.alpha;
		double beta = TSPSimulation.beta;
		HashMap<Path,Double> probs = new HashMap<Path,Double>();
		double totalIntensity = 0;
		for(Path p: currentCity.roads) {
			totalIntensity += getSegmentIntensity(p, alpha, beta);
		}
		for(Path p: currentCity.roads) {
			if(!hasVisited(p.getDestination(currentCity))) {
				if(totalIntensity > 0) {
					double prob = getSegmentIntensity(p,alpha,beta)/totalIntensity;
					probs.put(p, prob);
				} else {
					probs.put(p, 0.0);
				}
			}
		}
		return probs;
	}
	
	
	/** Calcule l'intensité d'un chemin en fonction de sa distance et ses pheromones
	 * @param p le chemin dont on souhaite calculer l'intensité
	 * @param alpha constante d'ajustement des pheromones
	 * @param beta constante d'ajustement de distance
	 * @return l'intensité du chemin
	 */
	private double getSegmentIntensity(Path p,double alpha,double beta) {
		return Math.pow(p.getPheromones(),alpha)*Math.pow(1/p.getDistance(), beta);
	}
	
	/** Permet de savoir si la fourmi est déjà passée par une ville
	 * @param c la ville à vérifier
	 * @return true si la fourmi est déjà passée, false sinon
	 */
	public boolean hasVisited(City c) {
		return this.visited.contains(c);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "dTravelled:"+(int)travelledDistance+" {";
		for(City c: visited) {
			s+= c;
		}
		s+= "}";
		return s;
	}

}
