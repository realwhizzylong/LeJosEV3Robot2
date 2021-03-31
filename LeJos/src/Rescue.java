import lejos.hardware.Sound;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class Rescue implements Behavior {

	public boolean suppressed;
	private PilotRobot robot;
	private MovePilot pilot;

	public Rescue(PilotRobot robot) {
		this.robot = robot;
		pilot = robot.getPilot();
	}

	public void action() {
		robot.isRobotWaiting = false;
		Sound.beepSequenceUp();
		pilot.setLinearSpeed(8);
		pilot.setLinearAcceleration(4);
		robot.findVictim(3);
		robot.findPath();
		for (int i = 1; i <= robot.map[0][0].getPath(); i++) {
			for (int x = 0; x < 6; x++) {
				for (int y = 0; y < 6; y++) {
					if (robot.map[x][y].getPath() == i) {
						if (x > robot.GetX()) {
							if (robot.getDirection() == 0) {
								robot.correctHeading(90);
								robot.setDirection(false);
							} else if (robot.getDirection() == 2) {
								robot.correctHeading(-90);
								robot.setDirection(true);
							} else if (robot.getDirection() == 3) {
								robot.correctHeading(90);
								robot.setDirection(false);
								robot.correctHeading(90);
								robot.setDirection(false);
							}
						}
						if (x < robot.GetX()) {
							if (robot.getDirection() == 0) {
								robot.correctHeading(-90);
								robot.setDirection(true);
							} else if (robot.getDirection() == 1) {
								robot.correctHeading(90);
								robot.setDirection(false);
								robot.correctHeading(90);
								robot.setDirection(false);
							} else if (robot.getDirection() == 2) {
								robot.correctHeading(90);
								robot.setDirection(false);
							}
						}
						if (y < robot.GetY()) {
							if (robot.getDirection() == 0) {
								robot.correctHeading(90);
								robot.setDirection(false);
								robot.correctHeading(90);
								robot.setDirection(false);
							} else if (robot.getDirection() == 1) {
								robot.correctHeading(90);
								robot.setDirection(false);
							} else if (robot.getDirection() == 3) {
								robot.correctHeading(-90);
								robot.setDirection(true);
							}
						}
						if (y > robot.GetY()) {
							if (robot.getDirection() == 1) {
								robot.correctHeading(-90);
								robot.setDirection(true);
							} else if (robot.getDirection() == 2) {
								robot.correctHeading(90);
								robot.setDirection(false);
								robot.correctHeading(90);
								robot.setDirection(false);
							} else if (robot.getDirection() == 3) {
								robot.correctHeading(90);
								robot.setDirection(false);
							}
						}
						ColorThread.updatePos = true;
						pilot.travel(25);
						ColorThread.updateCritical = true;
						robot.checkByLong();
					}
				}
			}
		}
		robot.restoreDefault();
		robot.needColorCorrect = true;
		robot.RunMode = 0;
		robot.corrected = false;
		robot.isRobotWaiting = true;
		Network.robotWaiting();
	}

	public void suppress() {
		suppressed = true;
	}

	public boolean takeControl() {
		if (robot.RunMode == 3) {
			return true;
		} else {
			return false;
		}
	}
}