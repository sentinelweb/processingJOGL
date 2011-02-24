package net.processing.examples.explode;

import processing.core.PApplet;
import processing.core.PImage;

public class Explode extends PApplet {
	PImage img;       // The source image
	int cellsize = 4; // Dimensions of each cell in the grid
	int COLS, ROWS;   // Number of columns and rows in our system
	public void setup()
	{
	  size(800, 600, P3D); 
	  img = loadImage("eye.jpg");     // Load the image
	  COLS = width/2/cellsize;            // Calculate # of columns
	  ROWS = height/2/cellsize;           // Calculate # of rows
	  colorMode(RGB,255,255,255,100);   // Setting the colormode
	}
	public void draw()
	{
	  background(0);
	  // Begin loop for columns
	  for ( int i = 0; i < COLS;i++) {
	    // Begin loop for rows
	    for ( int j = 0; j < ROWS;j++) {
	      int x = i*cellsize + cellsize/2; // x position
	      int y = j*cellsize + cellsize/2; // y position
	      int loc = x + y*width/2;           // Pixel array location
	      int c = img.pixels[loc];       // Grab the color
	      // Calculate a z position as a function of mouseX and pixel brightness
	      float z = (mouseX / (float) width/2) * brightness(img.pixels[loc]) - 100.0f;
	      // Translate to the location, set fill and stroke, and draw the rect
	      pushMatrix();
	      translate(x*2,y*2,z*4);
	      fill(c);
	      noStroke();
	      rectMode(CENTER);
	      rect(0,0,cellsize,cellsize);
	      popMatrix();
	    }
	  }
	}

}
