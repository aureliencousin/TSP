package ants;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Simulation extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public int UPBSIZE;
	public int LBSIZE;

	
	int newAnts;
	
	
	int stepcount;
	int nstep=1;
	
	int evaporation;
	
	Node nest;
	Node food;
	
	Path upperb;
	Path lowerb;
	
	Node[] overflow;
	Node[] overflow2;
	
	Random rand;
	
	BufferedImage imgfood;
	BufferedImage imgnest;
	
	public Simulation(int upbsize, int lbsize) {
		UPBSIZE = upbsize;
		LBSIZE = lbsize;
		init();
	}
	
	/** 
	 * Réinitialisation des deux chemins
	 */
	public void init() {
		nest = new Node();
		food = new Node();
		upperb = new Path(UPBSIZE);
		lowerb = new Path(LBSIZE);
		rand = new Random();
		try {
			imgfood = ImageIO.read(new File("img/arrow-food.png"));
			imgnest = ImageIO.read(new File("img/arrow-nest.png"));
		} catch (IOException e) {
			System.out.println("Fichiers images non trouvés");
		}
		stepcount = 0;
		this.repaint();
	}
	
	
	/** Choisit le nombre de fourmis à envoyer dans chaque branche
	 * @param nbOfAnts le nombre de fourmis à séparer
	 * @return une map contenant le nombre de fourmis associé à chaque branche 
	 */
	public HashMap<Path,Integer> weightedSplit(int nbOfAnts) {
		HashMap<Path,Integer> map = new HashMap<Path,Integer>();
		map.put(upperb, 0);
		map.put(lowerb, 0);
		int weightsum = upperb.getPheromones() + lowerb.getPheromones(); 
		int randomNum;
		//etat initial
		if(weightsum == 0) {
			int upants = (int) nbOfAnts / 2 ;
			map.put(upperb, upants);
			map.put(lowerb, nbOfAnts - upants);
			return map;
		}
		for(int i = 0; i < nbOfAnts; i++) {
		    randomNum = rand.nextInt(weightsum);
		    randomNum -= upperb.getPheromones();
		    if(randomNum <= 0) {
		    	map.put(upperb, map.get(upperb) + 1);
		    }
		    else {
		    	map.put(lowerb, map.get(lowerb) + 1);
		    }
		}
	    
		return map;
	}
	
	/**
	 * Calcule la prochaine étape de la simulation
	 */
	public void step() {
		//calcul du choix du chemin des nouvelles fourmis
		HashMap<Path,Integer> split = weightedSplit(nest.getAntDirections()[1]);
		ArrayList<Ant> upnew = new ArrayList<Ant>();
		ArrayList<Ant> lonew = new ArrayList<Ant>();
		for(int i = 0; i < split.get(upperb);i++) {
			upnew.add(new Ant(Direction.FOOD));
		}
		for(int i = 0; i < split.get(lowerb);i++) {
			lonew.add(new Ant(Direction.FOOD));
		}
		
		//calcul du choix du chemin pour rentrer
		split = weightedSplit(food.getAntDirections()[0]);
		Node upback = new Node();
		Node loback = new Node();
		for(int i = 0; i < split.get(upperb);i++) {
			upback.add(new Ant(Direction.NEST));
		}
		for(int i = 0; i < split.get(lowerb);i++) {
			loback.add(new Ant(Direction.NEST));
		}
		
		//deplacement des fourmis
		overflow = upperb.move(upnew, upback);
		overflow2 = lowerb.move(lonew, loback);
		
		//enregistrement des fourmis arrivées à la nourriture et au nid
		food.clear();
		for(Ant a : overflow[1].getAnts()){
			a.setDirection(Direction.NEST);
			food.add(a);
		}
		upperb.addPheromones(overflow[0].getAntDirections()[0],evaporation);
		for(Ant a : overflow2[1].getAnts()){
			a.setDirection(Direction.NEST);
			food.add(a);
		}
		lowerb.addPheromones(overflow2[0].getAntDirections()[0],evaporation);
		
		
		
		System.out.println(upperb);
		System.out.println(lowerb);
		System.out.println("..............");
		nest.clear();
		if(stepcount%nstep == 0) {
			nest.populate(newAnts, Direction.FOOD);
		}
		lowerb.stepPheromones();
		upperb.stepPheromones();
		stepcount++;
		this.repaint();
	}
	
	/** Récupérer le nombre de fourmis ajoutées à chaque étape (selon nStep)
	 * @return le nombre de fourmis
	 */
	public int getNewAnts() {
		return newAnts;
	}

	/** Enregistre le nombre de fourmis à ajouter à chaque n steps
	 * @param newAnts le nombre de fourmis
	 */
	public void setNewAnts(int newAnts) {
		this.newAnts = newAnts;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		int imgsize = 24;
		super.paintComponent(g);
		int[] dir;
		//dessine le chemin du haut
		for(int i=0; i < upperb.nodes.length;i++) {
			dir = upperb.nodes[i].getAntDirections();
			g.drawRect(90+i*2*(imgsize+20),97, imgsize+30, 2*imgsize+10);
			if(dir[1]> 0) {
				g.drawImage(imgfood, 120+i*2*(imgsize+20),100, imgsize,imgsize, null);
				g.drawString(dir[1]+"", 100+i*2*(imgsize+20),120);
			}
			if(dir[0]> 0) {
				g.drawImage(imgnest, 120+i*2*(imgsize+20),100+imgsize+5, imgsize,imgsize, null);
				g.drawString(dir[0]+"", 100+i*2*(imgsize+20),120+imgsize+5);
			}
		}
		g.drawString("Pheromones: "+upperb.getPheromones(), 100,80);
		//dessine le chemin du bas
		for(int i=0; i < lowerb.nodes.length;i++) {
			dir = lowerb.nodes[i].getAntDirections();
			g.drawRect(90+i*2*(imgsize+20),297, imgsize+30, 2*imgsize+10);
			if(dir[1]> 0) {
				g.drawImage(imgfood, 120+i*2*(imgsize+20),300, imgsize,imgsize, null);
				g.drawString(dir[1]+"", 100+i*2*(imgsize+20),320);
			}
			if(dir[0]> 0) {
				g.drawImage(imgnest, 120+i*2*(imgsize+20),300+imgsize+5, imgsize,imgsize, null);
				g.drawString(dir[0]+"", 100+i*2*(imgsize+20),320+imgsize+5);
			}
		}
		g.drawString("Pheromones: "+lowerb.getPheromones(), 100,280);
		//dessine le nid
		dir = nest.getAntDirections();
		g.drawRect(5,197, imgsize+30, 2*imgsize+10);
		g.drawString("Nest", 5,187);
		if(dir[1]> 0) {
			g.drawImage(imgfood, 30,200, imgsize,imgsize, null);
			g.drawString(dir[1]+"", 10,220);
		}
		
		//dessine la nourriture
		int maxi = (lowerb.nodes.length > upperb.nodes.length)?lowerb.nodes.length:upperb.nodes.length;
		dir = food.getAntDirections();
		g.drawRect(90+(maxi)*2*(imgsize+20),197, imgsize+30, 2*imgsize+10);
		g.drawString("Food", 90+(maxi)*2*(imgsize+20),187);

		if(dir[0]> 0) {
			g.drawImage(imgnest, 120+(maxi)*2*(imgsize+20),200+imgsize+5, imgsize,imgsize, null);
			g.drawString(dir[0]+"", 100+(maxi)*2*(imgsize+20),220+imgsize+5);
		}
		//compteur
		g.drawString("Step "+stepcount, 300,50);
	}

	/** Retourne le nombre de d'étapes nécessaires à un nouvel ajout de fourmis
	 * @return le nombre d'étapes
	 */
	public int getNstep() {
		return nstep;
	}

	/** Enregistrer le nombre de d'étapes nécessaires à un nouvel ajout de fourmis
	 * @param nstep le nombre d'etapes
	 */
	public void setNstep(int nstep) {
		this.nstep = nstep;
	}
	
	
	/** Enregistrer le nombre d'etapes necessaires à l'évaporation d'un phéromone
	 * @param evaporation
	 */
	public void setEvaporation(int evaporation) {
		this.evaporation = evaporation;
	}

}
