package utils;

public enum Trust {
	low,
	medium,
	high;
	
	public static Integer toInt(Trust t) {
		switch(t) {
		case high: return 2;
		case medium: return 1;
		case low: return 0;
		default : return -1;
		}
	}
	
	public static Trust convertInt(Integer t) {
		switch(t) {
		case 2: return high;
		case 1: return medium;
		case 0: return low;
		default : return low;
		}
	}
}
