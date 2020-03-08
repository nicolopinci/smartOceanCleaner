package utils;

public enum Validity {
	valid,
	invalid,
	undefined;
	
	
	public static Integer toInt(Validity v) {
		switch(v) {
		case valid: return 0;
		case invalid: return 1;
		default : return -1;
		}
	}
	
	public static Validity convertInt(Integer v) {
		switch(v) {
		case 0: return valid;
		case 1: return invalid;
		default : return undefined;
		}
	}
}

