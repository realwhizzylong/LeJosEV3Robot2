import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Network extends Thread{
	private String EV3IP;
	private int port;
	private int delay;
	private int count;
	private boolean work;
	private PCServer ser;
	
	public Network(int delay, PCServer ser) {
		EV3IP = "192.168.70.62";
		port = 1234;
		this.delay = delay;
		work = true;
		this.ser = ser;
	}
	
	public void closeThread() {
		work = false;
	}
	
	public void run(){
		while(work){
			try{
				Socket sock = new Socket(EV3IP, port);
				//System.out.println("Connected"); //This is test code
				InputStream in = sock.getInputStream();
				DataInputStream dIn = new DataInputStream(in);
				count = 0;
				while(count < 32) {
					count = dIn.available();
				}
				//System.out.println("222");	//this is test code
				PCServer.xPos = dIn.readInt();
				PCServer.yPos = dIn.readInt();
				PCServer.map[PCServer.xPos][PCServer.yPos].critical=dIn.readInt();
				PCServer.map[PCServer.xPos][PCServer.yPos].occupied=dIn.readInt();
				PCServer.xGoal = dIn.readInt();
				PCServer.yGoal = dIn.readInt();
				PCServer.victimNumber = dIn.readInt();
				if(dIn.readInt() == 0){
					PCServer.listenEV3 = false;
				}else{
					PCServer.listenEV3 = true;
				}
				ser.update();
				dIn.close();
				in.close();
				sock.close();
				try {
					sleep(delay);
				}catch(Exception e) {
					
				}
			}catch(IOException e) {
				
			}
		}
	}
}