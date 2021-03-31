import java.util.ArrayList;
import java.util.List;

public class ProPoint {
	private int nowX;
	private int nowY;
	private int steps;
	private int nowHeading;

	public ProPoint(int nowHeading, int steps, int nowX, int nowY) {
		this.nowHeading = nowHeading;
		this.steps = steps;
		this.nowX = nowX;
		this.nowY = nowY;
	}

	public int getNowHeading() {
		return nowHeading;
	}

	public int getSteps() {
		return steps;
	}

	public int getNowX() {
		return nowX;
	}

	public int getNowY() {
		return nowY;
	}

	public void setNowHeading(int nowHeading) {
		this.nowHeading = nowHeading;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public void setNowX(int nowX) {
		this.nowX = nowX;
	}

	public void setNowY(int nowY) {
		this.nowY = nowY;
	}
}
