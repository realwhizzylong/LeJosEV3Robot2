import java.io.*;
import java.net.*;

public class NetworkOut extends Thread {
//	private final String ip = "192.168.70.62";
	private final int port = 1234;
	private PilotRobot robot;
	private int delay;
	private boolean work;

	public NetworkOut(PilotRobot robot, int delay) {
		this.robot = robot;
		this.delay = delay;
		work = true;
	}

	public void run() {
		while (work) {
			try {
				ServerSocket server = new ServerSocket(port);
				Socket client = server.accept();
				OutputStream out = client.getOutputStream();
				DataOutputStream dOut = new DataOutputStream(out);
				dOut.writeInt(robot.GetX());
				dOut.writeInt(robot.GetY());
				dOut.writeInt(robot.map[robot.GetX()][robot.GetY()].getCritical());
				dOut.writeInt(robot.map[robot.GetX()][robot.GetY()].getOccupied());
				dOut.writeInt(robot.xGoal);
				dOut.writeInt(robot.yGoal);
				dOut.writeInt(robot.victimNum);
				if (robot.isRobotWaiting) {
					dOut.writeInt(1);
				} else {
					dOut.writeInt(0);
				}
				out.close();
				dOut.flush();
				dOut.close();
				server.close();
			} catch (IOException e) {
			}
			try {
				sleep(delay);
			} catch (Exception e) {

			}
		}

	}

}
