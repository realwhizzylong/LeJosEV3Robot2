import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class particleFilterStep1 implements Behavior {

	public boolean suppressed;
	private PilotRobot robot;

	private MovePilot pilot;

	// Constructor - store a reference to the robot
	public particleFilterStep1(PilotRobot robot) {
		this.robot = robot;
		pilot = robot.getPilot();

	}

	// When called, this should stop action()
	public void suppress() {
		suppressed = true;
	}

	public boolean takeControl() {
		if(robot.RunMode == 4) {
			return true;
		}else {
			return false;
		}
	}

	public void action() {
//		Allow this method to run
		suppressed = false;
		pilot.setLinearSpeed(8);
		pilot.setLinearAcceleration(4);
		ColorThread.updatePos = false;
		ColorThread.updateCritical = false;
		//create a array to save the map information
		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				robot.probability[a][b] = -1;
			}
		}
		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				if (a == 0 || a == 7 || b == 0 || b == 7) {
					robot.probability[a][b] = 100;
				}
			}
		}
		for (int a = 0; a < 6; a++) {
			for (int b = 0; b < 6; b++) {
				if (robot.map[a][b].getOccupied() == 1) {
					robot.probability[a + 1][b + 1] = 100;
				}
			}
		}
		// Step1: use ArrayList to save all of the probability situation.
		float front, left, right, back;
		front = USThread.disSample[0];
		robot.setmotorM(90);
		Delay.msDelay(1000);
		left = USThread.disSample[0];
		robot.setmotorM(-180);
		Delay.msDelay(1000);
		right = USThread.disSample[0];
		robot.setmotorM(90);
		Delay.msDelay(1000);
		robot.correctHeading(180);
		Delay.msDelay(1000);
		back = USThread.disSample[0];
		robot.correctHeading(180);
		for (int a = 1; a < 7; a++) {
			for (int b = 1; b < 7; b++) {
				if (robot.probability[a][b] == -1) {
					if (((robot.probability[a][b + 1] == 100 && front < 0.25)
							|| (robot.probability[a][b + 1] == -1 && front > 0.25))
							&& ((robot.probability[a + 1][b] == 100 && right < 0.25)
									|| (robot.probability[a + 1][b] == -1 && right > 0.25))
							&& ((robot.probability[a][b - 1] == 100 && back < 0.25)
									|| (robot.probability[a][b - 1] == -1 && back > 0.25))
							&& ((robot.probability[a - 1][b] == 100 && left < 0.25)
									|| (robot.probability[a - 1][b] == -1 && left > 0.25))) {
						//direction is north
						robot.listOfPro.add(new ProPoint(0, 0, a, b));
						//System.out.println("0 "+a+" "+b);
					}
					if (((robot.probability[a + 1][b] == 100 && front < 0.25)
							|| (robot.probability[a + 1][b] == -1 && front > 0.25))
							&& ((robot.probability[a][b - 1] == 100 && right < 0.25)
									|| (robot.probability[a][b - 1] == -1 && right > 0.25))
							&& ((robot.probability[a - 1][b] == 100 && back < 0.25)
									|| (robot.probability[a - 1][b] == -1 && back > 0.25))
							&& ((robot.probability[a][b + 1] == 100 && left < 0.25)
									|| (robot.probability[a][b + 1] == -1 && left > 0.25))) {
						// direction is east
						robot.listOfPro.add(new ProPoint(1, 0, a, b));
						//System.out.println("1 "+a+" "+b);
					}
					if (((robot.probability[a][b - 1] == 100 && front < 0.25)
							|| (robot.probability[a][b - 1] == -1 && front > 0.25))
							&& ((robot.probability[a - 1][b] == 100 && right < 0.25)
									|| (robot.probability[a - 1][b] == -1 && right > 0.25))
							&& ((robot.probability[a][b + 1] == 100 && back < 0.25)
									|| (robot.probability[a][b + 1] == -1 && back > 0.25))
							&& ((robot.probability[a + 1][b] == 100 && left < 0.25)
									|| (robot.probability[a + 1][b] == -1 && left > 0.25))) {
						// direction is sourth
						robot.listOfPro.add(new ProPoint(2, 0, a, b));
						//System.out.println("2 "+a+" "+b);
					}
					if (((robot.probability[a - 1][b] == 100 && front < 0.25)
							|| (robot.probability[a - 1][b] == -1 && front > 0.25))
							&& ((robot.probability[a][b + 1] == 100 && right < 0.25)
									|| (robot.probability[a][b + 1] == -1 && right > 0.25))
							&& ((robot.probability[a + 1][b] == 100 && back < 0.25)
									|| (robot.probability[a + 1][b] == -1 && back > 0.25))
							&& ((robot.probability[a][b - 1] == 100 && left < 0.25)
									|| (robot.probability[a][b - 1] == -1 && left > 0.25))) {
						// direction is west
						robot.listOfPro.add(new ProPoint(3, 0, a, b));
						//System.out.println("3 "+a+" "+b);
					}
				}
			}
		}
		//Step 2: use loop to take control of robot walk and check the location correction
		boolean needLoop = true;
		int loopRound = 0;
		while (needLoop) {
			// One of way to leave the loop is at the hospital
			if ((ColorThread.leftColSample[0] >= 0.2 && ColorThread.leftColSample[1] < 0.2
					&& ColorThread.leftColSample[1] >= 0.14 && ColorThread.leftColSample[2] <= 0.8)
					&& (ColorThread.rightColSample[0] >= 0.2 && ColorThread.rightColSample[1] < 0.2
							&& ColorThread.rightColSample[1] >= 0.11 && ColorThread.rightColSample[2] <= 0.08)) {
				robot.updatePosition(0, 0);
				int numOfAtYellow = 0;
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if (robot.listOfPro.get(i).getNowX() == 1 && robot.listOfPro.get(i).getNowY() == 1) {
						numOfAtYellow++;
					}
				}
				if(numOfAtYellow == 1) {
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if (robot.listOfPro.get(i).getNowX() == 1 && robot.listOfPro.get(i).getNowY() == 1) {
						if (robot.listOfPro.get(i).getNowHeading() == 1) {
							robot.setDirection(false);
						} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
							robot.setDirection(true);
							robot.setDirection(true);
						} else if (robot.listOfPro.get(i).getNowHeading() == 3) {
							robot.setDirection(true);
						} else {
							// do not to set the heading, because the default value is 0.
						}
					}
				}
				}else {
					//have two way to go to the yellow part
					front = USThread.disSample[0];
					robot.setmotorM(90);
					Delay.msDelay(1000);
					left = USThread.disSample[0];
					robot.setmotorM(-180);
					Delay.msDelay(1000);
					right = USThread.disSample[0];
					robot.setmotorM(90);
					Delay.msDelay(1000);
					robot.correctHeading(180);
					Delay.msDelay(1000);
					back = USThread.disSample[0];
					robot.correctHeading(180);
					Delay.msDelay(1000);
					if(front<0.25 && right<0.25 && left>0.25 && back>0.25) {
						robot.setDirection(true);
						robot.setDirection(true);
					}else if(front<0.25 && right>0.25 && back>0.25 && left<0.25) {
						robot.setDirection(true);
					}else if(front>0.25 && right>0.25 && back<0.25 && left<0.25) {
						// do not to set the heading, because the default value is 0.
					}else{
						robot.setDirection(false);
					}
				}
				needLoop = false;
				break;
			}
			//Another way to leave the loop is the robot arrive the green cell.
			if ((ColorThread.leftColSample[0] <= 0.09 && ColorThread.leftColSample[1] >= 0.17
					&& ColorThread.leftColSample[2] <= 0.09)
					&& (ColorThread.rightColSample[0] <= 0.09 && ColorThread.rightColSample[1] >= 0.014
							&& ColorThread.rightColSample[2] <= 0.11)) {
				robot.updatePosition(5, 0);
				int numOfAtGreen = 0;
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if (robot.listOfPro.get(i).getNowX() == 6 && robot.listOfPro.get(i).getNowY() == 1) {
						numOfAtGreen++;
					}
				}
				if(numOfAtGreen==1) {
					for (int i = 0; i < robot.listOfPro.size(); i++) {
						if (robot.listOfPro.get(i).getNowX() == 6 && robot.listOfPro.get(i).getNowY() == 1) {
							if (robot.listOfPro.get(i).getNowHeading() == 1) {
								robot.setDirection(false);
							} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
								robot.setDirection(true);
								robot.setDirection(true);
							} else if (robot.listOfPro.get(i).getNowHeading() == 3) {
								robot.setDirection(true);
							} else {
							// do not to set the heading, because the default value is 0.
							}
						}
					}
				}else {
					front = USThread.disSample[0];
					robot.setmotorM(90);
					Delay.msDelay(1000);
					left = USThread.disSample[0];
					robot.setmotorM(-180);
					Delay.msDelay(1000);
					right = USThread.disSample[0];
					robot.setmotorM(90);
					Delay.msDelay(1000);
					robot.correctHeading(180);
					Delay.msDelay(1000);
					back = USThread.disSample[0];
					robot.correctHeading(180);
					Delay.msDelay(1000);
					if(front<0.25 && right<0.25 && left>0.25 && back>0.25) {
						robot.setDirection(false);
					}else if(front<0.25 && right>0.25 && back>0.25 && left<0.25) {
						robot.setDirection(true);
						robot.setDirection(true);
					}else if(front>0.25 && right>0.25 && back<0.25 && left<0.25) {
						robot.setDirection(true);
					}else{
						// do not to set the heading, because the default value is 0.
					}
				}
				needLoop = false;
				break;
			}
			//The third way of leave the loop is the robot have already know his position and direction.
			int maxStepNumber = 0;
			int numberOfMaxSteps = 0;
			for (int i = 0; i < robot.listOfPro.size(); i++) {
				if (robot.listOfPro.get(i).getSteps() > maxStepNumber) {
					maxStepNumber = robot.listOfPro.get(i).getSteps();
				}
			}
			for (int i = 0; i < robot.listOfPro.size(); i++) {
				if (robot.listOfPro.get(i).getSteps() == maxStepNumber) {
					numberOfMaxSteps++;
				}
			}
			if (numberOfMaxSteps == 1) {
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if (robot.listOfPro.get(i).getSteps() == maxStepNumber) {
						robot.updatePosition(robot.listOfPro.get(i).getNowX() - 1,
								robot.listOfPro.get(i).getNowY() - 1);
						if (robot.listOfPro.get(i).getNowHeading() == 1) {
							robot.setDirection(false);
						} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
							robot.setDirection(true);
							robot.setDirection(true);
						} else if (robot.listOfPro.get(i).getNowHeading() == 3) {
							robot.setDirection(true);
						} else {
							// do not to set the heading, because the default value is 0.
						}
					}
				}
				needLoop = false;
				break;
			}
			//The below part are the loops.
			front = USThread.disSample[0];
			robot.setmotorM(90);
			Delay.msDelay(1000);
			left = USThread.disSample[0];
			robot.setmotorM(-180);
			Delay.msDelay(1000);
			right = USThread.disSample[0];
			robot.setmotorM(90);
			Delay.msDelay(1000);
			robot.correctHeading(180);
			Delay.msDelay(1000);
			back = USThread.disSample[0];
			robot.correctHeading(180);
			Delay.msDelay(1000);
			// move
			
			if (front > 0.25) {
				robot.getPilot().travel(25);
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if(robot.listOfPro.get(i).getSteps()==loopRound) {
						if (robot.listOfPro.get(i).getNowHeading() == 0) {
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() + 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 1) {
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() + 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() - 1);
						} else {
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() - 1);
						}
					}
				}
			} else if (left > 0.25) {
				robot.correctHeading(-90);
				robot.getPilot().travel(25);
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if(robot.listOfPro.get(i).getSteps()==loopRound) {
						if (robot.listOfPro.get(i).getNowHeading() == 0) {
							robot.listOfPro.get(i).setNowHeading(3);
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() - 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 1) {
							robot.listOfPro.get(i).setNowHeading(0);
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() + 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
							robot.listOfPro.get(i).setNowHeading(1);
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() + 1);
						} else {
							robot.listOfPro.get(i).setNowHeading(2);
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() - 1);
						}
					}
				}
			} else if (right > 0.25) {
				robot.correctHeading(90);
				robot.getPilot().travel(25);
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if(robot.listOfPro.get(i).getSteps()==loopRound) {
						if (robot.listOfPro.get(i).getNowHeading() == 0) {
							robot.listOfPro.get(i).setNowHeading(1);
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() + 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 1) {
							robot.listOfPro.get(i).setNowHeading(2);
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() - 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
							robot.listOfPro.get(i).setNowHeading(3);
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() - 1);
						} else {
							robot.listOfPro.get(i).setNowHeading(0);
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() + 1);
						}
					}
				}
			} else {
				robot.correctHeading(180);
				robot.getPilot().travel(25);
				for (int i = 0; i < robot.listOfPro.size(); i++) {
					if(robot.listOfPro.get(i).getSteps()==loopRound) {
						if (robot.listOfPro.get(i).getNowHeading() == 0) {
							robot.listOfPro.get(i).setNowHeading(2);
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() - 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 1) {
							robot.listOfPro.get(i).setNowHeading(3);
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() - 1);
						} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
							robot.listOfPro.get(i).setNowHeading(0);
							robot.listOfPro.get(i).setNowY(robot.listOfPro.get(i).getNowY() + 1);
						} else {
							robot.listOfPro.get(i).setNowHeading(1);
							robot.listOfPro.get(i).setNowX(robot.listOfPro.get(i).getNowX() + 1);
						}
					}
				}
			}
			// It time to check the around situation
			front = USThread.disSample[0];
			robot.setmotorM(90);
			Delay.msDelay(1000);
			left = USThread.disSample[0];
			robot.setmotorM(-180);
			Delay.msDelay(1000);
			right = USThread.disSample[0];
			robot.setmotorM(90);
			Delay.msDelay(1000);
			robot.correctHeading(180);
			Delay.msDelay(1000);
			back = USThread.disSample[0];
			robot.correctHeading(180);
			Delay.msDelay(1000);
			for (int i = 0; i < robot.listOfPro.size(); i++) {
				//System.out.println(robot.listOfPro.get(i).getSteps());
					if (robot.listOfPro.get(i).getNowHeading() == 0) {
						if (((front < 0.25
								&& robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro.get(i).getNowY()
										+ 1] == 100)
								|| (front > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() + 1] == -1))
										&& ((left < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												- 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (left > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() - 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((right < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												+ 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (right > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() + 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((back < 0.25 && robot.probability[robot.listOfPro.get(i)
												.getNowX()][robot.listOfPro.get(i).getNowY() - 1] == 100)
								|| (back > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() - 1] == -1))) {
							//It is correct when the robot have the same data information with estimate information
							robot.listOfPro.get(i).setSteps(robot.listOfPro.get(i).getSteps() + 1);
						}
					} else if (robot.listOfPro.get(i).getNowHeading() == 1) {
						if (((left < 0.25
								&& robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro.get(i).getNowY()
										+ 1] == 100)
								|| (left > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() + 1] == -1))
										&& ((back < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												- 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (back > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() - 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((front < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												+ 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (front > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() + 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((right < 0.25 && robot.probability[robot.listOfPro.get(i)
												.getNowX()][robot.listOfPro.get(i).getNowY() - 1] == 100)
								|| (right > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() - 1] == -1))) {
							//It is correct when the robot have the same data information with estimate information
							robot.listOfPro.get(i).setSteps(robot.listOfPro.get(i).getSteps() + 1);
						}
					} else if (robot.listOfPro.get(i).getNowHeading() == 2) {
						if (((back < 0.25
								&& robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro.get(i).getNowY()
										+ 1] == 100)
								|| (back > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() + 1] == -1))
										&& ((right < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												- 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (right > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() - 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((left < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												+ 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (left > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() + 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((front < 0.25 && robot.probability[robot.listOfPro.get(i)
												.getNowX()][robot.listOfPro.get(i).getNowY() - 1] == 100)
								|| (front > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() - 1] == -1))) {
							//It is correct when the robot have the same data information with estimate information
							robot.listOfPro.get(i).setSteps(robot.listOfPro.get(i).getSteps() + 1);
						}
					} else {
						if (((right < 0.25
								&& robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro.get(i).getNowY()
										+ 1] == 100)
								|| (right > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() + 1] == -1))
										&& ((front < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												- 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (front > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() - 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((back < 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()
												+ 1][robot.listOfPro.get(i).getNowY()] == 100)
								|| (back > 0.25
										&& robot.probability[robot.listOfPro.get(i).getNowX() + 1][robot.listOfPro
												.get(i).getNowY()] == -1))
										&& ((left < 0.25 && robot.probability[robot.listOfPro.get(i)
												.getNowX()][robot.listOfPro.get(i).getNowY() - 1] == 100)
								|| (left > 0.25 && robot.probability[robot.listOfPro.get(i).getNowX()][robot.listOfPro
										.get(i).getNowY() - 1] == -1))) {
							//It is correct when the robot have the same data information with estimate information
							robot.listOfPro.get(i).setSteps(robot.listOfPro.get(i).getSteps() + 1);
						}
					}
				
			}
			loopRound++;
		}
		// if is out the while loop, it will find the heading and the position of robot right now.
		//Step 3: It is time to use wavefront to come back to the hospital
		robot.restoreDefault();
		robot.RunMode = 1;
		ColorThread.updatePos = true;
		ColorThread.updateCritical = true;
	}
}