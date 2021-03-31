
public class PCServer {
	private int mode;
	private CreatingMap cmap;
	
	public static Cell[][]map = new Cell[6][6];
	public static int xPos;
	public static int yPos;
	public static int xGoal;
	public static int yGoal;
	public static int victimNumber;
	public static boolean listenEV3;
	public static boolean initFinished;
	public static int count = 0;
	
	public PCServer(){
		for(int i = 0; i<6; i++) {
			for(int j=0; j<6; j++) {
				map[i][j]= new Cell();
			}
		}
		cmap = new CreatingMap();
		cmap.CreateTable(map);
		xPos = 0;
		yPos = 0;
		xGoal = 0;
		yGoal = 0;
		victimNumber = 0;
		listenEV3 = false;
		mode = 0;
		initFinished = false;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void update() {
		cmap.updateMap(map);
	}
}
