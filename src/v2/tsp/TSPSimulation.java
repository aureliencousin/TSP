package v2.tsp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TSPSimulation extends JPanel implements MouseListener {
	
	private double evaporationRate;
	
	private static final long serialVersionUID = 1L;
	
	List<City> cities;
	int maxX;
	int maxY;
	int step = 0;
	int nbOfAnts;

	private boolean foundPath = false;

	public boolean paused = false;

	public TSPSimulation(double evaporationRate) {
		this.evaporationRate = evaporationRate;
		cities = new ArrayList<City>();
		this.setBackground(new Color(164,151,199));
	}
	
	
	/** Instancie nbOfCities villes dans le plan défini par [0..maxX],[0..maxY] et les lies entre elles
	 * @param nbOfCities  le nombres de villes de la simulation
	 * @param maxX abscisse maximale
	 * @param maxY ordonnée maximale
	 * @param nbOfAnts le nombre de fourmis à placer dans les villes
	 */
	public void generateCities(int nbOfCities, int maxX, int maxY, int nbOfAnts) {
		this.maxX = maxX;
		this.maxY = maxY;
		List<City> cities = new ArrayList<City>();
		Random rnd = new Random();
		for(int i=0; i< nbOfCities; i++) {
			cities.add(new City(rnd.nextFloat()*maxX, rnd.nextFloat()*maxY));
		}
		this.cities = cities;
		this.nbOfAnts = nbOfAnts;
		this.link();
		this.placeAnts(nbOfAnts);
	}
	
	/**
	 * Crée un chemin liant chaques villes entre elles
	 */
	private void link() {
		ArrayList<Path> roads = new ArrayList<Path>();
		for(City a: cities) {
			for(City b: cities) {
				if(a!=b) {
					Path p = new Path(computeDistance(a,b),a,b);
					Path exists = Path.getExistingRoad(roads,p);
					if(exists != null) {
						p = exists;
					}
					else {
						roads.add(p);
					}
					a.addPath(p);
				}
			}
		}
	}

	
	/** Place des fourmis réparties équitablement entre les villes
	 * @param nbOfAnts le nombre de fourmis à placer
	 */
	private void placeAnts(int nbOfAnts) {
		foundPath = false;
		/* ajout d'une fourmi dans chaque ville */
		for(City ct: cities) {
			ct.ants.clear();
			if(nbOfAnts > 0) {
				Ant a = new Ant();
				a.currentCity = ct;
				a.visited.add(a.currentCity);
				ct.ants.add(a);
				nbOfAnts--;
			}
		}
		/* distribution des fourmis restantes au hasard */
		Random rnd = new Random();
		while (nbOfAnts > 0) {
			int elem = rnd.nextInt(cities.size());
			Ant a = new Ant();
			a.currentCity = cities.get(elem);
			a.visited.add(a.currentCity);
			a.currentCity.ants.add(a);
			nbOfAnts--;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(City a: cities) {
			
			for(Path p: a.roads) {
				int colorvalue = 255 - (((int)(p.getPheromones()*255) > 255) ?255: (int)(p.getPheromones()*255));
				g.setColor(new Color(colorvalue,colorvalue,colorvalue));
				g.drawLine((int)a.x*(this.getWidth()/maxX), (int)a.y*(this.getHeight()/maxY), (int)p.getDestination(a).x*(this.getWidth()/maxX), (int)p.getDestination(a).y*(this.getHeight()/maxY));
				g.setColor(Color.BLACK);
				g.drawString((int)p.getDistance()+"", (int)((p.getDestination(a).x+a.x)/2*(this.getWidth()/maxX)), (int)((p.getDestination(a).y+a.y)/2*(this.getHeight()/maxY)));
			}
			
			g.setColor(Color.RED);
			g.drawRect((int)a.x*(this.getWidth()/maxX), (int)a.y*(this.getHeight()/maxY), 2, 2);
			g.drawString("["+(int)a.x+","+(int)a.y+"]", (int)a.x*(this.getWidth()/maxX), -1+(int)a.y*(this.getHeight()/maxY));
		}
	}
	
	
	/** Calcule la distance entre deux villes à partir des coordonnées cartésiennes
	 * @param a la première ville
	 * @param b la seconde ville
	 * @return la distance calculée
	 */
	private double computeDistance(City a, City b) {
		return Math.sqrt(Math.pow(b.x-a.x,2)+Math.pow(b.y-a.y,2));
	}
	
	/** @debug permet d'afficher la probabilité qu'ont les fourmis d'aller dans chaque ville
	 * 
	 */
	public void displayProbs() {
		for(City c: cities) {
			System.out.println(c);
			for(Ant a : c.ants) {
				a.getNextPathProbabilities();
			}
		}
	}
	
	
	/** @debug Trouve la fourmi ayant effectué le chemin le plus court
	 * @return la meilleure fourmi de la simulation, null s'il n'y a aucune fourmi
	 */
	public Ant getBestAnt(){
		Ant best = null;
		for(City c: cities) {
			for(Ant a : c.ants) {
				if(best == null || a.travelledDistance < best.travelledDistance) {
					best = a;
				}
			}
		}
		return best;
	}
	
	
	/**
	 * Avance la simulation d'une étape:
	 * Fait avancer chaque fourmi dans sa prochaine ville
	 * Met à jour les phéromones et réinitialise les fourmis si elles ont parcouru toutes les villes
	 */
	public void step() {
		System.out.println("step:"+this.step);
		//on a parcouru toutes les villes
		if(foundPath) {
			placeAnts(nbOfAnts);
			for(City c : cities) {
				for(Path p : c.roads) {
					p.updatePheromones(evaporationRate, cities.size());
					p.visitors.clear();
				}
			}
		}
		else {
			ArrayList<Ant> toRemove = new ArrayList<Ant>();
			ArrayList<Ant> treated = new ArrayList<Ant>();
			for(City c: cities) {
				
				for(Ant a : c.ants) {
					if(!treated.contains(a)) {
						a.chooseNextCity();
						toRemove.add(a);
						treated.add(a);
					}
				}
				
				for(Ant a: toRemove) {
					//on a visité toutes les villes
					if(a.visited.size() > cities.size()) {
						foundPath = true;
					}
				}
				
				c.ants.removeAll(toRemove);
				toRemove.clear();
			}
		}
		step++;
		repaint();
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		paused = !paused;
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	final static int alpha = 1;
	final static int beta = 2;
	final static boolean weightedRandom = false;
	
	/** Démarre une simulation de TSP
	 * @throws InterruptedException
	 */
	public static void main(String args[]) throws InterruptedException {
		TSPSimulation tsp = new TSPSimulation(0.8);
		tsp.generateCities(4, 100, 100,200);
		JFrame f = new JFrame();
		f.addMouseListener(tsp);
		f.setSize(600, 700);
		f.setVisible(true);
		f.setContentPane(tsp);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		while(true) {
			if(!tsp.paused) {
				for(City c : tsp.cities) {
					for(Ant a : c.ants) {
						System.out.println(a);
					}
				}
			
				tsp.step();
				Thread.sleep(50);
			}	
		}
	}



}
