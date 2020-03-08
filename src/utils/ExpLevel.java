package utils;

public enum ExpLevel {
	high,
	medium,
	low;
	
	public static Integer toInt(ExpLevel e) {
		switch(e) {
		case high: return 2;
		case medium: return 1;
		case low: return 0;
		default : return -1;
		}
	}
	
	public static ExpLevel convertInt(Integer e) {
		switch(e) {
		case 2: return high;
		case 1: return medium;
		case 0: return low;
		default : return low;
		}
	}
}
