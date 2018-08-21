package amata1219.mamiya.assist;

import java.util.ArrayList;
import java.util.List;

public class Stopwatch {

	private String name;
	private long start, end;
	private List<Long> splitTimes = new ArrayList<Long>();

	public Stopwatch(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public boolean hasStart(){
		return start != 0;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public boolean hasEnd(){
		return end != 0;
	}

	public List<Long> getSplitTimes() {
		return splitTimes;
	}

	public void setSplitTimes(List<Long> splitTimes) {
		this.splitTimes = splitTimes;
	}

	public String getState(){
		if(hasEnd()){
			return "計測終了";
		}else if(hasStart()){
			return "計測中";
		}
		return "待機中";
	}

	public String timeToString(long time) {
		long l = time - start;
		long hour = (l / (1000 * 60 * 60)) % 24;
		long minute = (l / (1000 * 60))  % 60;
		long second = (l / 1000)  % 60;
		long millisSec = l % 1000;
		return String.format("%02d:%02d:%02d:%03d", hour, minute, second, millisSec);
	}

}
