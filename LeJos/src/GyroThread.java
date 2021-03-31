import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;

public class GyroThread extends Thread {
	public PilotRobot robot;
	private static int delay;
	private static EV3GyroSensor gyroSensor;
	private static SampleProvider gyroSP;
	public static float[] gyroSample;

	public GyroThread(PilotRobot robot, int delay) {
		gyroSensor = new EV3GyroSensor(robot.myEV3.getPort("S2"));
		gyroSP = gyroSensor.getAngleMode();
		gyroSample = new float[gyroSP.sampleSize()];
	}

	public float getAngle() {
		return gyroSample[0];// return the angle.
	}

	public static void reset() {
		gyroSensor.reset();
	}

	public void run() {
		while (true) {
			gyroSP.fetchSample(gyroSample, 0);

		}
	}
}
