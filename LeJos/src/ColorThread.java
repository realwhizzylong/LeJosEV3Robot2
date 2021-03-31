import java.awt.Robot;
import java.text.DecimalFormat;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.hardware.Sound;
import lejos.hardware.lcd.Font;
import lejos.hardware.motor.MotorRegulator;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorThread extends Thread {
	public PilotRobot robot;
	private int delay;
	private static EV3ColorSensor leftColSensor, rightColSensor;
	private static SampleProvider leftColSP, rightColSP;
	public static float[] rightColSample, leftColSample;
	public static boolean updatePos = true;;
	public static boolean updateCritical = true;

	public ColorThread(PilotRobot robot, int delay) {
		this.robot = robot;
		this.delay = delay;
		leftColSensor = new EV3ColorSensor(robot.myEV3.getPort("S1"));
		rightColSensor = new EV3ColorSensor(robot.myEV3.getPort("S4"));

		leftColSP = leftColSensor.getRGBMode();
		rightColSP = rightColSensor.getRGBMode();
		leftColSample = new float[leftColSP.sampleSize()];
		rightColSample = new float[rightColSP.sampleSize()];
	}

	public void run() {
		while (true) {
			leftColSP.fetchSample(leftColSample, 0);
			rightColSP.fetchSample(rightColSample, 0);
			if (rightColSample[0] <= 0.06 && rightColSample[1] <= 0.06 && rightColSample[1] <= 0.06
					&& leftColSample[0] <= 0.06 && leftColSample[1] <= 0.06 && leftColSample[2] <= 0.06 && updatePos) {
				Sound.beep();
				switch (robot.getDirection()) {
				case (0):
					robot.updatePosition(robot.GetX(), robot.GetY() + 1);
					break;
				case (1):
					robot.updatePosition(robot.GetX() + 1, robot.GetY());
					break;
				case (2):
					robot.updatePosition(robot.GetX(), robot.GetY() - 1);
					break;
				case (3):
					robot.updatePosition(robot.GetX() - 1, robot.GetY());
					break;
				}
				updatePos = false;
				updateCritical = true;
			}
			if (ColorThread.leftColSample[0] > 0.060 && ColorThread.leftColSample[0] < 0.085
					&& ColorThread.leftColSample[1] > 0.015 && ColorThread.leftColSample[1] < 0.050
					&& ColorThread.leftColSample[2] > 0.020 && ColorThread.leftColSample[2] < 0.051
					&& ColorThread.rightColSample[0] > 0.053 && ColorThread.rightColSample[0] < 0.078
					&& ColorThread.rightColSample[1] > 0.013 && ColorThread.rightColSample[1] < 0.040
					&& ColorThread.rightColSample[2] > 0.020 && ColorThread.rightColSample[2] < 0.070) {
				if(updateCritical) {
					robot.map[robot.GetX()][robot.GetY()].setCritical(2);
					updateCritical = false;
				}
			} else if (ColorThread.leftColSample[0] > 0.036 && ColorThread.leftColSample[0] < 0.062
					&& ColorThread.leftColSample[1] > 0.165 && ColorThread.leftColSample[1] < 0.218
					&& ColorThread.leftColSample[2] > 0.145 && ColorThread.leftColSample[2] < 0.210
					&& ColorThread.rightColSample[0] > 0.026 && ColorThread.rightColSample[0] < 0.053
					&& ColorThread.rightColSample[1] > 0.149 && ColorThread.rightColSample[1] < 0.185
					&& ColorThread.rightColSample[2] > 0.160 && ColorThread.rightColSample[2] < 0.200) {
				if(updateCritical) {
					robot.map[robot.GetX()][robot.GetY()].setCritical(1);
					updateCritical = false;
				}
			} else {
				if(updateCritical) {
					robot.map[robot.GetX()][robot.GetY()].setCritical(0);
					updateCritical = false;
				}	
			}
		}

	}
}
