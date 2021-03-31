
// A simple application that uses the Subsumption architecture to create a
// bumper car, that drives forward, and changes direction given a collision.

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {
		PilotRobot robot = new PilotRobot();
		ColorThread cThread = new ColorThread(robot, 200);
		USThread usThread = new USThread(robot, 200);
		GyroThread gyroThread = new GyroThread(robot, 200);
		PilotMonitor myMonitor = new PilotMonitor(robot, 200, cThread);
		Network net = new Network(robot, 200);
		NetworkOut netOut = new NetworkOut(robot, 200);
//		robot.map[0][0].setOccupied(3);
//		robot.map[1][3].setOccupied(1);
//		robot.map[3][3].setOccupied(1);
//		robot.map[2][4].setOccupied(1);
//		robot.map[1][5].setOccupied(1);
//		robot.map[5][5].setOccupied(2);
//		robot.map[3][1].setOccupied(2);
//		robot.map[0][5].setOccupied(2);
//		robot.map[2][5].setOccupied(2);
//		robot.map[2][3].setOccupied(2);
//		robot.victimNum = 5; 

		// Set up the behaviours for the Arbitrator and construct it.
		Behavior b0 = new Communicate(robot);
		Behavior b1 = new TravelAndSearch(robot);
		Behavior b2 = new FindWaitingVictim(robot);
		Behavior b3 = new Rescue(robot);
		Behavior b4 = new ColorCorrectStep1(robot);
		Behavior b5 = new ColorCorrectStep2(robot);
		Behavior b6 = new particleFilterStep1(robot);
		Behavior[] bArray = { b0, b1, b2, b3, b4, b5, b6 };
		Arbitrator arby = new Arbitrator(bArray);
		for (int i = 0; i < 8; i++)
			System.out.println("");

		// Note that in the Arbritrator constructor, a robotssage is sent
		// to stdout. The following prints eight black lines to clear
		// the robotssage from the screen
		Sound.buzz();
		Network.initMap();

		netOut.start();
		cThread.start();
		usThread.start();
		gyroThread.start();
		myMonitor.start();

//		myMonitor.setMessage("Press a key to start");
//		Button.waitForAnyPress();

		// Start the Arbitrator
		arby.go();
	}
}