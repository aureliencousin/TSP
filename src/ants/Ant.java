package ants;

public class Ant {

	private Direction dir;
	
	public Ant(Direction d) {
		setDirection(d);
	}

	/**
	 * @return la direction de la fourmi
	 */
	public Direction getDirection() {
		return dir;
	}

	/**
	 * @param dir la nouvelle direction
	 */
	public void setDirection(Direction dir) {
		this.dir = dir;
	}
	
	
}
