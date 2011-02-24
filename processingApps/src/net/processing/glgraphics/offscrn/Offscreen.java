package net.processing.glgraphics.offscrn;

import processing.core.PApplet;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphicsOffScreen;

public class Offscreen extends PApplet {
	GLGraphicsOffScreen glg1, glg2;

	int mixFactor = 127;

	public void setup()
	{
	  size(640, 480, GLConstants.GLGRAPHICS);
	    
	  // To create an off-screen drawing surface, we create an
	  // instance of the GLGraphics class, passing true as the last
	  // parameter.
	  glg1 = new GLGraphicsOffScreen(320, 240, this);
	  glg2 = new GLGraphicsOffScreen(200, 100, this);

	  // Disabling stroke lines in the first off-screen surface.
	  glg1.beginDraw();
	  glg1.noStroke();
	  glg1.endDraw();
	}

	public void draw()
	{
	  background(0);
	  
	  // In the off-screen surface 1, we draw random ellipses.
	  glg1.beginDraw();
	  glg1.fill(230, 50, 20, random(50, 200));
	  glg1.ellipse(random(0, glg1.width), random(0, glg1.height), random(10, 50), random(10, 50));
	  glg1.endDraw();   

	  // In the off-screen surface 2, we draw random rectangles.  
	  glg2.beginDraw();
	  glg2.fill(20, 50, 230, random(50, 200));
	  glg2.rect(random(0, glg1.width), random(0, glg1.height), random(10, 50), random(10, 50));
	  glg2.endDraw();   

	  // We mix the images together and scale them so the occupy the entire screen.
	  tint(255, 255 - mixFactor);
	  image(glg1.getTexture(), 0, 0, width, height); 
	  tint(255, mixFactor);
	  image(glg2.getTexture(), 0, 0, width, height);
	}

	public void mouseDragged()
	{
	    mixFactor = (int)(255 * (float)(mouseX) / width);
	}

}
