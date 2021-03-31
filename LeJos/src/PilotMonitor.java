import java.text.DecimalFormat;

import org.jfree.data.statistics.MeanAndStandardDeviation;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.LCD;

public class PilotMonitor extends Thread {

	private int delay;
	public PilotRobot robot;
	private String msg;
	private ColorThread colt;

	// GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();

	public PilotMonitor(PilotRobot r, int d, ColorThread colt) {
		this.setDaemon(true);
		delay = d;
		robot = r;
		msg = "";
	}

	public void resetMessage() {
		this.setMessage("");
	}

	public void setMessage(String str) {
		msg = str;
	}

	public void run() {

		DecimalFormat df = new DecimalFormat("####0.000");
		robot.getLCD().setFont(Font.getSmallFont());
		// PilotRobot.lcd.drawString("Press ENTER to start!", 30, 10, 0);
		while (true) {
			robot.getLCD().clear();
			robot.getLCD().drawString("X Position: " + robot.GetX(), 0, 20, 0);
			robot.getLCD().drawString("Y Position: " + robot.GetY(), 0, 30, 0);
			robot.getLCD().drawString("Run Mode: " + robot.RunMode, 0, 40, 0);
			robot.getLCD().drawString("victimNum: " + robot.victimNum, 0, 60, 0);
			robot.getLCD().drawString("non-critical-victimNum: " + robot.nonVictimNum, 0, 70, 0);
			robot.getLCD().drawString("critical: " + robot.map[robot.GetX()][robot.GetY()].getCritical(), 0, 80, 0);
			if(robot.isRobotWaiting) {
				robot.getLCD().drawString("yes", 0, 90, 0);
			}else {
				robot.getLCD().drawString("no", 0, 90, 0);
			}
			
//			robot.getLCD()
//					.drawString("Left Color:[" + df.format(ColorThread.leftColSample[0])
//							+ df.format(ColorThread.leftColSample[1]) + df.format(ColorThread.leftColSample[2]) + "]",
//							0, 100, 0);
//			robot.getLCD()
//					.drawString("Right Color:[" + df.format(ColorThread.rightColSample[0])
//							+ df.format(ColorThread.rightColSample[1]) + df.format(ColorThread.rightColSample[2]) + "]",
//							0, 120, 0);

			try {
				sleep(delay);
			} catch (Exception e) {
			}
		}
	}
}