package utils;

public enum Resolution {
	high,
	standard,
	low;
	
	public static Integer toInt(Resolution r) {
		switch(r) {
		case high: return 2;
		case standard: return 1;
		case low: return 0;
		default : return -1;
		}
	}
	
	public static Resolution convertInt(Integer r) {
		switch(r) {
		case 2: return high;
		case 1: return standard;
		case 0: return low;
		default : return low;
		}
	}
}