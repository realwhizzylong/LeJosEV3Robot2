import java.awt.Robot;
import javax.management.loading.PrivateClassLoader;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class ColorCorrectStep1 implements Behavior {

	public boolean suppressed;
	private PilotRobot robot;
	private MovePilot pilot;

	// Constructor - store a reference to the robot
	public ColorCorrectStep1(PilotRobot robot) {
		this.robot = robot;
		pilot = robot.getPilot();
	}

	public void action() {
		// Allow this method to run
		suppressed = false;
		robot.isRobotWaiting = false;
		Sound.buzz();
		robot.getPilot().setLinearSpeed(2);
		float front, left, right;
		front = USThread.disSample[0];
		robot.setmotorM(90);
		left = USThread.disSample[0];
		robot.setmotorM(-180);
		right = USThread.disSample[0];
		robot.setmotorM(90);
		ColorThread.updatePos = false;
		if (front > 0.25) {
			robot.getPilot().forward();
			while (!suppressed) {
				Thread.yield();
			}
		} else if (left > 0.25) {
			robot.correctHeading(-90);
			robot.setDirection(true);
			robot.getPilot().forward();
			while (!suppressed) {
				Thread.yield();
			}
		} else if (right > 0.25) {
			robot.correctHeading(90);
			robot.setDirection(false);
			robot.getPilot().forward();
			while (!suppressed) {
				Thread.yield();
			}
		} else {
			robot.correctHeading(180);
			robot.setDirection(false);
			robot.setDirection(false);
			robot.getPilot().forward();
			while (!suppressed) {
				Thread.yield();
			}
		}
	}

	// When called, this should stop action()
	public void suppress() {
		suppressed = true;
	}

	public boolean takeControl() {
		if (robot.map[robot.GetX()][robot.GetY()].getOccupied() == 2 && robot.corrected == false
				&& robot.needColorCorrect == true) {
			if (!(ColorThread.leftColSample[0] <= 0.04 && ColorThread.leftColSample[1] <= 0.04
					&& ColorThread.leftColSample[2] <= 0.04)
					|| !(ColorThread.rightColSample[0] <= 0.04 && ColorThread.rightColSample[1] <= 0.04
							&& ColorThread.rightColSample[2] <= 0.04)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}