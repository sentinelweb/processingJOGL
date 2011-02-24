package net.processing.examples.rotate2;

import processing.core.PApplet;

public class Rotate2 extends PApplet {
	
	float a;                          // Angle of rotation
	float offset = PI/24.0f;             // Angle offset between boxes
	int num = 12;                     // Number of boxes
	int[] colors = new int[num];  // Colors of each box
	int safecolor;

	boolean pink = true;

	public void setup() 
	{ 
	  size(800, 600, P3D);
	  noStroke();  
	  for(int i=0; i<num; i++) {
	    colors[i] = color(255 * (i+1)/num);
	  }
	  lights();
	} 
	 

	public void draw() 
	{     
	  background(0, 0, 26);
	  translate(width/2, height/2);
	  a += 0.02;   
	  for(int i=0; i<num; i++) {
	    pushMatrix();
	    fill(colors[i]);
	    rotateY(a + offset*i);
	    rotateX(a/2 + offset*i);
	    box(width * 0.45f);
	    popMatrix();
	  }
	} 

}
