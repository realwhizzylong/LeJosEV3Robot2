import java.util.ArrayList;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class PilotRobot {
	public Brick myEV3;
	public GraphicsLCD lcd;
	public int xGoal = 0, yGoal = 0;
	public int nonVictimNum = 0; // n means non-critical;
	public int victimNum = 0; // this for all possible position of victim
	public int RunMode = 0; // 0 for communicate mode, 1 for travelAndSearch mode, 2 for findWaitingVictim,
							// mode 3 for rescue mode
	public boolean isRobotWaiting = false;
	public boolean needColorCorrect = false;
	public Cell[][] map = new Cell[6][6];
	public static boolean corrected = false;
	public static boolean bump = false;
	public static int[][] probability = new int[8][8];
	public ArrayList<ProPoint> listOfPro= new ArrayList();
	private EV3ColorSensor leftColSensor, rightColSensor; // can be initialise by thread
	private EV3UltrasonicSensor usSensor; // can be initialise by thread
	private EV3GyroSensor gyroSensor;
	private EV3LargeRegulatedMotor motorL, motorR;
	private EV3MediumRegulatedMotor motorM;
	private SampleProvider gyroSP, usSP;
	private float[] gyroSample, usSample;
	private int xPos;
	private int yPos;
	private int direction; // 0 means north, 1 means east, 2 means south, 3 means west
	private int width = 6;
	private int height = 6;
	private MovePilot pilot;
	private float objectDis = 0;
	private boolean isVictim = false;
	private boolean pathFinished = false;

	public PilotRobot() {
		myEV3 = BrickFinder.getDefault();
		lcd = myEV3.getGraphicsLCD();

		// Setting up Motor Ports
		motorL = new EV3LargeRegulatedMotor(myEV3.getPort("B"));
		motorR = new EV3LargeRegulatedMotor(myEV3.getPort("D"));
		motorM = new EV3MediumRegulatedMotor(myEV3.getPort("C"));

		// Setting up Chassis and Pilot
		Wheel leftWheel = WheeledChassis.modelWheel(motorL, 4.05).offset(-4.9);
		Wheel rightWheel = WheeledChassis.modelWheel(motorR, 4.05).offset(4.9);
		Chassis chassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(chassis);

		// initialise position of the robot
		xPos = 0;
		yPos = 0;
		direction = 0;

		// initialise the map
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				map[i][j] = new Cell();
			}
		}
	}

	public void closeRobot() {
		leftColSensor.close();
		rightColSensor.close();
		usSensor.close();
		gyroSensor.close();
	}

	public EV3LargeRegulatedMotor getmotorL() {
		return motorL;
	}

	public EV3LargeRegulatedMotor getmotorR() {
		return motorR;
	}

	public GraphicsLCD getLCD() {
		return lcd;
	}

	public MovePilot getPilot() {
		return pilot;
	}

	public void setmotorM(int a) {
		motorM.rotate(a);
	}

	public int GetX() {
		return xPos;
	}

	public int GetY() {
		return yPos;
	}

	public void updatePosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(boolean left) {
		if (left) {
			if (direction == 0) {
				direction = 3;
			} else {
				direction -= 1;
			}
		} else {
			if (direction == 3) {
				direction = 0;
			} else {
				direction += 1;
			}
		}
	}

	public void correctHeading(float wantdegree) {
		pilot.setAngularSpeed(30);
		pilot.rotate(wantdegree);
		Delay.msDelay(100);
		while ((GyroThread.gyroSample[0] != wantdegree)) {
			pilot.rotate(wantdegree - GyroThread.gyroSample[0]);
			Delay.msDelay(100);
		}
		GyroThread.reset();
		Delay.msDelay(100);
	}

	public void longIsFront() {
		if (getDirection() == 0) {
			correctHeading(180);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-180);
		} else if (getDirection() == 1) {
			correctHeading(90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-90);
		} else if (getDirection() == 2) {
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
		} else {
			correctHeading(-90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(90);
		}
	}

	public void longIsLeft() {
		if (getDirection() == 0) {
			correctHeading(90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-90);
		} else if (getDirection() == 1) {
			pilot.travel(-20);
			pilot.travel(3);
		} else if (getDirection() == 2) {
			correctHeading(-90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(90);
		} else {
			correctHeading(180);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-180);
		}
	}

	public void longIsRight() {
		if (getDirection() == 0) {
			correctHeading(-90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(90);
		} else if (getDirection() == 1) {
			correctHeading(180);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-180);
		} else if (getDirection() == 2) {
			correctHeading(90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-90);
		} else {
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
		}
	}

	public void longIsBehind() {
		if (getDirection() == 0) {
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
		} else if (getDirection() == 1) {
			correctHeading(-90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(90);
		} else if (getDirection() == 2) {
			correctHeading(180);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-180);
		} else {
			correctHeading(90);
			pilot.travel(-20);
			GyroThread.reset();
			pilot.travel(3);
			correctHeading(-90);
		}
	}

	public void checkByLong() {
		pilot.setAngularSpeed(50);
		if (GetX() + 1 < width && !bump) {
			if (map[GetX() + 1][GetY()].getOccupied() == 1) {
				longIsRight();
				bump = true;
			}
		}
		if (GetX() - 1 >= 0 && !bump) {
			if (map[GetX() - 1][GetY()].getOccupied() == 1) {
				longIsLeft();
				bump = true;
			}
		}
		if (GetY() + 1 < height && !bump) {
			if (map[GetX()][GetY() + 1].getOccupied() == 1) {
				longIsFront();
				bump = true;
			}
		}
		if (GetY() - 1 >= 0 && !bump) {
			if (map[GetX()][GetY() - 1].getOccupied() == 1) {
				longIsBehind();
				bump = true;
			}
		}
		bump = false;
	}

	public void findVictim(int type) {
		map[xPos][yPos].setValue(0);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (map[i][j].getOccupied() == 1) {
					map[i][j].setValue(100);
				}
			}
		}

		// TravelAndSearch victim
		if (type == 1) {
			int n = 0;
			while (!isVictim) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (map[x][y].getValue() == n) {
							if ((x - 1) >= 0) {
								if (map[x - 1][y].getValue() == -1) {
									map[x - 1][y].setValue(n + 1);
								}
							}
							if ((x + 1) < width) {
								if (map[x + 1][y].getValue() == -1) {
									map[x + 1][y].setValue(n + 1);
								}
							}
							if ((y - 1) >= 0) {
								if (map[x][y - 1].getValue() == -1) {
									map[x][y - 1].setValue(n + 1);
								}
							}
							if ((y + 1) < height) {
								if (map[x][y + 1].getValue() == -1) {
									map[x][y + 1].setValue(n + 1);
								}
							}
							if (map[x][y].getOccupied() == 2 && !map[x][y].getFound()) {
								xGoal = x;
								yGoal = y;
								isVictim = true;
							}
						}
					}
				}
				n++;
			}
		}

		// FindWaitingVictim
		if (type == 2) {
			int n = 0;
			while (!isVictim) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (map[x][y].getValue() == n) {
							if ((x - 1) >= 0) {
								if (map[x - 1][y].getValue() == -1) {
									map[x - 1][y].setValue(n + 1);
								}
							}
							if ((x + 1) < width) {
								if (map[x + 1][y].getValue() == -1) {
									map[x + 1][y].setValue(n + 1);
								}
							}
							if ((y - 1) >= 0) {
								if (map[x][y - 1].getValue() == -1) {
									map[x][y - 1].setValue(n + 1);
								}
							}
							if ((y + 1) < height) {
								if (map[x][y + 1].getValue() == -1) {
									map[x][y + 1].setValue(n + 1);
								}
							}
							if (map[x][y].getOccupied() == 2 && map[x][y].getCritical() == 1) {
								xGoal = x;
								yGoal = y;
								isVictim = true;
							}
						}
					}
				}
				n++;
			}
		}

		// Rescue victim to hospital
		if (type == 3) {
			int n = 0;
			Delay.msDelay(1000);
			while (map[0][0].getValue() == -1) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (map[x][y].getValue() == n) {
							if ((x - 1) >= 0) {
								if (map[x - 1][y].getValue() == -1) {
									map[x - 1][y].setValue(n + 1);
								}
							}
							if ((x + 1) < width) {
								if (map[x + 1][y].getValue() == -1) {
									map[x + 1][y].setValue(n + 1);
								}
							}
							if ((y - 1) >= 0) {
								if (map[x][y - 1].getValue() == -1) {
									map[x][y - 1].setValue(n + 1);
								}
							}
							if ((y + 1) < height) {
								if (map[x][y + 1].getValue() == -1) {
									map[x][y + 1].setValue(n + 1);
								}
							}
						}
					}
				}
				n++;
			}
			xGoal = 0;
			yGoal = 0;
		}
	}

	public void findPath() {
		map[xGoal][yGoal].setPath(map[xGoal][yGoal].getValue());
		pathFinished = false;

		int a = map[xGoal][yGoal].getValue();
		int preX = xGoal;
		int preY = yGoal;

		while (!pathFinished) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					lcd.drawString(Integer.toString(a), 0, 100, 0);
					if (a > 0) {
						if ((map[x][y].getValue() == a - 1)
								&& ((preX - x) * (preX - x) + (preY - y) * (preY - y) == 1)) {
							map[x][y].setPath(a - 1);
							Sound.beep();
							Delay.msDelay(2000);
							a--;
							preX = x;
							preY = y;
						}
					}
				}
			}
			if (a == 0) {
				pathFinished = true;
			}
		}
	}

	public void restoreDefault() {
		for (int p = 0; p < width; p++) {
			for (int q = 0; q < height; q++) {
				map[p][q].setValue(-1);
				map[p][q].setPath(-1);
			}
		}
		isVictim = false;
	}

}