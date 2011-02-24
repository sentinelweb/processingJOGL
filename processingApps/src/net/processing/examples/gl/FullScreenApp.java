package net.processing.examples.gl;

import processing.core.PApplet;

public class FullScreenApp extends PApplet {
	static public void main(String args[]) {
	  PApplet.main(new String[] { "--present", "net.processing.examples.gl.FullScreenApp" });
	}

	public 	void setup() 
	{
	  size(screen.width, screen.height, OPENGL);
	  noStroke();
	}

	public 	void draw() 
	{
	  lights();
	  background(0);
	  
	  for (int x = 0; x <= width; x += 100) {
	    for (int y = 0; y <= height; y += 100) {
	      pushMatrix();
	      translate(x, y);
	      rotateY(map(mouseX, 0, width, 0, PI));
	      rotateX(map(mouseY, 0, height, 0, PI));
	      box(90);
	      popMatrix();
	    }
	  }
	}
	
	public void keyPressed(){
		System.exit(0);
	}
}
