package application;

public class MovingAverage {
	private long frame[];
	private double average;
	private int count;
	private final int frameSize;

	public MovingAverage(int frameSize) {
		this.frameSize = frameSize;
		frame = new long[frameSize];
		count = 0;
		average = 0;
	}

	public double add(long val) {
		if (count == 0) { // prázdny frame
			for (int i = 0; i < frameSize; ++i)
				frame[i] = val; // všetky val
			average = val;
		}
		int indexLast = count % frameSize;
		long lastValue = frame[indexLast];
		frame[indexLast] = val;
		average += (double)(val - lastValue) / (double)frameSize;
		count++;
		return average;
	}
	
	public int getAvg(){
		return (int)Math.round(average);
	}

}