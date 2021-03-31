import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class USThread extends Thread {
	public PilotRobot robot;
	private static int delay;
	private static EV3UltrasonicSensor usSensor;
	private static SampleProvider disSP;
	public static float[] disSample;

	public USThread(PilotRobot robot, int delay) {
		usSensor = new EV3UltrasonicSensor(robot.myEV3.getPort("S3"));
		disSP = usSensor.getDistanceMode();
		disSample = new float[disSP.sampleSize()];
	}

	public void run() {
		while (true) {
			disSP.fetchSample(disSample, 0);
		}
	}
}
