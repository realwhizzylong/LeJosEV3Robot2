import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class FindWaitingVictim implements Behavior {

	public boolean suppressed;
	private PilotRobot robot;
	private MovePilot pilot;

	public FindWaitingVictim(PilotRobot robot) {
		this.robot = robot;
		pilot = robot.getPilot();
	}

	public void action() {
		robot.isRobotWaiting = false;
		pilot.setLinearSpeed(8);
		pilot.setLinearAcceleration(4);
		robot.findVictim(2);
		robot.findPath();
		int xGoal = robot.xGoal;
		int yGoal = robot.yGoal;
		for (int i = 1; i <= robot.map[xGoal][yGoal].getPath(); i++) {
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
		robot.corrected = false;
		robot.map[robot.GetX()][robot.GetY()].setCritical(0);
		robot.RunMode = 0;
//		robot.isRobotWaiting = true;
//		Network.robotWaiting();
	}

	public void suppress() {
		suppressed = true;
	}

	public boolean takeControl() {
		if (robot.RunMode == 2) {
			return true;
		} else {
			return false;
		}
	}
}