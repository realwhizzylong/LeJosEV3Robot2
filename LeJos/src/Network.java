import java.net.*;
import java.util.Vector;

import lejos.hardware.Sound;

import java.io.*;

public class Network{
	private static PilotRobot robot;
	private static int port = 1234;
	private static String ip = "192.168.70.73";
	private static int times = 0;
	public static boolean work = true;
	private static boolean initFinished = false;

	public Network(PilotRobot robot, int delay) {
		this.robot = robot;
	}

	public static void initMap() {
		while (!initFinished) {
			try {
				Socket sock = new Socket(ip, port);
				InputStream in = sock.getInputStream();
				DataInputStream dIn = new DataInputStream(in);
				int count = 0;
				while (count < 12) {
					count = dIn.available();
					// Sound.beep();
					// System.out.println(count);
				}
				int occupied = dIn.readInt();
				int xPos = dIn.readInt();
				int yPos = dIn.readInt();
				if (robot.map[xPos][yPos].getOccupied() != occupied) {
					if (occupied == 2) {
						robot.victimNum += 1;
					}
					robot.map[xPos][yPos].setOccupied(occupied);
				}
				times += 1;
				if (times == 10) {
					robot.isRobotWaiting = true;
					robot.RunMode = 4;
					initFinished = true;
					robotWaiting();
				}
				dIn.close();
				in.close();
				sock.close();
			} catch (IOException e) {

			}
		}
	}

	public static void robotWaiting() {
		try {
			Socket sock = new Socket(ip, port);
			InputStream in = sock.getInputStream();
			DataInputStream dIn = new DataInputStream(in);
			int i = dIn.readInt();
			if (robot.RunMode != i) {
				robot.RunMode = i;
				robot.isRobotWaiting = false;
			}
			dIn.close();
			in.close();
			sock.close();
		} catch (IOException e) {

		}
	}
}
