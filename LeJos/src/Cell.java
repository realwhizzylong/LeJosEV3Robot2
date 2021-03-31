public class Cell {
	private int occupied; // 0 is empty, 1 is obstacle, 2 is a victim, 3 is hospital
	private int critical; // 2 is critical, 1 is non-critical, 0 is empty
	private int value; // the weight in wavefront algorithm.
	private int path;
	private boolean found;

	public Cell() {
		occupied = 0;
		critical = 0;
		value = -1;
		path = -1;
		found = false;
	}

	public Cell(int occupied, int critical) {
		this.occupied = occupied;
		this.critical = critical;
	}

	public int getOccupied() {
		return occupied;
	}

	public void setOccupied(int occupied) {
		this.occupied = occupied;
	}

	public int getCritical() {
		return critical;
	}

	public void setCritical(int critical) {
		this.critical = critical;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getPath() {
		return path;
	}

	public void setPath(int path) {
		this.path = path;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}
}