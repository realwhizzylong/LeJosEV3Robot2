import java.awt.Robot;
import javax.management.loading.PrivateClassLoader;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class ColorCorrectStep2 implements Behavior {

	public boolean suppressed;
	private PilotRobot robot;
	private MovePilot pilot;

	// Constructor - store a reference to the robot
	public ColorCorrectStep2(PilotRobot robot) {
		this.robot = robot;
		pilot = robot.getPilot();

	}

	public void action() {
		suppressed = false;
		robot.isRobotWaiting = false;
		robot.getPilot().setAngularSpeed(12);
		if ((ColorThread.leftColSample[0] <= 0.04 && ColorThread.leftColSample[1] <= 0.04
				&& ColorThread.leftColSample[2] <= 0.04)
				&& !(ColorThread.rightColSample[0] <= 0.04 && ColorThread.rightColSample[1] <= 0.04
						&& ColorThread.rightColSample[2] <= 0.04)) {
			// left is black, right is not.
			while (!((ColorThread.leftColSample[0] <= 0.04 && ColorThread.leftColSample[1] <= 0.04
					&& ColorThread.leftColSample[2] <= 0.04)
					&& (ColorThread.rightColSample[0] <= 0.04 && ColorThread.rightColSample[1] <= 0.04
							&& ColorThread.rightColSample[2] <= 0.04))) {
				robot.getPilot().rotate(-1);
			}
			GyroThread.reset();
		} else if (!(ColorThread.leftColSample[0] <= 0.04 && ColorThread.leftColSample[1] <= 0.04
				&& ColorThread.leftColSample[2] <= 0.04)
				&& (ColorThread.rightColSample[0] <= 0.04 && ColorThread.rightColSample[1] <= 0.04
						&& ColorThread.rightColSample[2] <= 0.04)) {
			// right is black, left is not.
			while (!((ColorThread.leftColSample[0] <= 0.04 && ColorThread.leftColSample[1] <= 0.04
					&& ColorThread.leftColSample[2] <= 0.04)
					&& (ColorThread.rightColSample[0] <= 0.04 && ColorThread.rightColSample[1] <= 0.04
							&& ColorThread.rightColSample[2] <= 0.04))) {
				robot.getPilot().rotate(1);
			}
			GyroThread.reset();
		}
		robot.getPilot().setLinearSpeed(5);
		robot.getPilot().travel(-7.5);
//		int a = robot.getDirection();
//		if (a == 0) {
//			robot.updatePosition(robot.GetX(), robot.GetY() - 1);
//		} else if (a == 1) {
//			robot.updatePosition(robot.GetX() - 1, robot.GetY());
//		} else if (a == 2) {
//			robot.updatePosition(robot.GetX(), robot.GetY() + 1);
//		} else {
//			robot.updatePosition(robot.GetX() + 1, robot.GetY());
//		}
		robot.RunMode = 0;
		robot.isRobotWaiting = true;
		robot.corrected = true;
		robot.needColorCorrect = false;
		Network.robotWaiting();
	}

	// When called, this should stop action()
	public void suppress() {
		suppressed = true;
	}

	public boolean takeControl() {
		if (robot.map[robot.GetX()][robot.GetY()].getOccupied() == 2) {
			if ((ColorThread.leftColSample[0] <= 0.04 && ColorThread.leftColSample[1] <= 0.04
					&& ColorThread.leftColSample[2] <= 0.04)
					|| (ColorThread.rightColSample[0] <= 0.04 && ColorThread.rightColSample[1] <= 0.04
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