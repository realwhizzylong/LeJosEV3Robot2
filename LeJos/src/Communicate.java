import java.awt.Robot;
import javax.management.loading.PrivateClassLoader;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class Communicate implements Behavior {

	public boolean suppressed;
	private PilotRobot robot;
	private MovePilot pilot;

	// Constructor - store a reference to the robot
	public Communicate(PilotRobot robot) {
		this.robot = robot;
		pilot = robot.getPilot();

	}

	public void action() {
		suppressed = false;
		//Delay.msDelay(2500);
		//robot.isRobotWaiting = true;

		while (!suppressed) {
			Thread.yield();
		}
	}

	// When called, this should stop action()
	public void suppress() {
		suppressed = true;
	}

	public boolean takeControl() {
		if (robot.RunMode == 0) {
			return true;
		}
		return false;
	}
}