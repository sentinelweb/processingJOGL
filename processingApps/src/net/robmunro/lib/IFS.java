package net.robmunro.lib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Generate IFS bitmaps.
 * 
 * Each row in ifs array is:-
 * xn+1=ax+by+c
 * yn+1=dx+ey+f
 * probability p
 * where 
 * row = {p,a,b,c,d,e,f};
 * 
 * @author robm
 *
 */
public class IFS {
	public static  double[][] mapleLeafIFS = {
		{0.10,  0.14,	0.01,	-0.08,  	0.00,  	0.51,   	-1.31 },
		{0.35,  0.43,	0.52 , 	1.49,	-0.45 ,	0.50, 	-0.75 },
		{0.35,  0.45, 	-0.49,	-1.62, 	0.47, 	0.47,  	-0.74 },
		{0.20,  0.49, 	0.00, 	0.02, 	0.00, 	0.51, 	1.62  }
	};
	public static double[][] fernLeafIFS = {
		{0.01, 	0.00, 	0.00, 	0.00,	0.00 ,	0.16, 	0.00 },	
		{0.85, 	0.85, 	0.04, 	0.00,	-0.04, 	0.85, 	1.60}, 	
		{0.07 ,	0.20 ,	-0.26, 	0.00,	0.23, 	0.22, 	1.60 },	
		{0.07 ,	-0.15, 	0.28, 	0.00, 	0.26, 	0.24, 	0.44 }	
	};
	public  BufferedImage doIFS(double[][] ifs,double iterations,double[][] scale,int width,int height) {
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)img.getGraphics();
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.WHITE);
		long time = System.currentTimeMillis();
		double x=0; double y=0;
		for (int i=0;i<iterations;i++) {
			double prob=0;
			double rnd = Math.random();
			plotXY(g2, x, y, scale);
			for (int j=0;j<ifs.length;j++) {
				prob+=ifs[j][0];
				if (rnd<prob) {
					x=ifs[j][1]*x+ifs[j][2]*y+ifs[j][3];
					y=ifs[j][4]*x+ifs[j][5]*y+ifs[j][6];
					break;
				}
			}
		}
		return img;
	}
	
	private void plotXY(Graphics2D g2, double x, double y,double[][] scale) {
		int xp= (int)Math.round(scale[0][0]+x*scale[0][1]);
		int yp=(int)Math.round(scale[1][0]+y*scale[1][1]);
		g2.drawLine(xp, yp, xp+1, yp+1);
	}
	
	public double[][] copyArray(double[][] ifs) {
		double[][] newArray = new double[ifs.length][ifs[0].length];
		for (int i=0;i<ifs.length;i++) {
			for (int j=0;j<ifs[i].length;j++) {
				newArray[i][j]=ifs[i][j];
			}
		}
		return newArray;
	}
}
