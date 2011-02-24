package net.robmunro.lib.tools;

public class LookupTable {
	private static int RESOLUTION = 1000;
	private static double sinLookup[] = null;
	private static double cosLookup[] = null;
	
	static {
		sinLookup = new double[RESOLUTION];
		cosLookup = new double[RESOLUTION];
		for (int i=0;i<RESOLUTION;i++) {
			sinLookup[i] = Math.sin(2*Math.PI / RESOLUTION*i);
			cosLookup[i] = Math.cos(2*Math.PI / RESOLUTION*i);
		}
	}
	
	
	public static double cos(double radians){
		long index = Math.round(radians*RESOLUTION/(Math.PI*2));
		index = index%RESOLUTION;
		if (index<0) {index=RESOLUTION+index;}
		return cosLookup[(int)index];
	}
	public static double cos(float radians){
		return cos((double)radians);
	}
	public static double cosD(float degrees){
		return cos(degrees*Math.PI/180);
	}
	
	public static double sin(double radians){
		long index = Math.round(radians*RESOLUTION/(Math.PI*2));
		index = index%RESOLUTION;
		if (index<0) {index=RESOLUTION+index;}
		return sinLookup[(int)index];
	}
	public static double sin(float radians){
		return sin((double)radians);
	}
	public static double sinD(float degrees){
		return sin(degrees*Math.PI/180);
	}
	
}
