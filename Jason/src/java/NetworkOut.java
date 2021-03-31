import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkOut{
	private int port;
	private int delay;
	private PCServer ser;
	private int mode = 0;
	private boolean work;
	private int count = 0;
	
	public NetworkOut(int delay,PCServer server) {
		port = 1234;
		this.delay = delay;
		ser = server;
		work = true;
	}
	
	public void closeThread() {
		work = false;
	}
	
	public void initMap(int objectType, int x, int y){
		try{
			ServerSocket server = new ServerSocket(port);
	        Socket client = server.accept();
	        OutputStream out = client.getOutputStream();
	        DataOutputStream dOut = new DataOutputStream(out);
			dOut.writeInt(objectType);
			dOut.writeInt(x);
			dOut.writeInt(y);
	        out.close();
		    dOut.flush();
		    dOut.close();
			server.close();
		}catch(IOException e){
		}
	}
	
	public void toldToDo(int mode) {
		try{
			ServerSocket server = new ServerSocket(port);
	        Socket client = server.accept();
	        OutputStream out = client.getOutputStream();
	        DataOutputStream dOut = new DataOutputStream(out);
	        dOut.writeInt(mode);
			System.out.println("told ev3 to do"+mode);
	        out.close();
		    dOut.flush();
		    dOut.close();
			server.close();
		}catch(IOException e){
		}
	}
}
