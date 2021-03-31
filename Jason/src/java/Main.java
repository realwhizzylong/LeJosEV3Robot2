
public class Main {
	public static void main(String[] args) {
		PCServer ser = new PCServer();
		NetworkOut netOut = new NetworkOut(0, ser);
		Network net = new Network(0,ser);
		
		PCServer.map[0][0].occupied = 3;
		PCServer.map[1][1].occupied = 1;
		PCServer.map[1][4].occupied = 1;
		PCServer.map[4][4].occupied = 1;
		PCServer.map[4][1].occupied = 1;
		PCServer.map[0][5].occupied = 2;
		PCServer.map[2][0].occupied = 2;
		PCServer.map[2][2].occupied = 2;
		PCServer.map[2][4].occupied = 2;
		PCServer.map[5][4].occupied = 2;
		PCServer.victimNumber = 5;
		
		for(int i=0; i<6; i++){
			for(int j=0; j<6; j++) {
				netOut.initMap(PCServer.map[i][j].occupied,i,j);
			}
		}
		netOut.initFinished();
		netOut.start();
		net.start();
	}
}
