package net.processing.examples.cubicgrid;

import java.awt.Color;

import processing.core.PApplet;

public class CubicGrid extends PApplet{
	float boxSize = 80;
	float margin = boxSize*4;
	float depth = 1600;
	int boxFill;
	int WIDTH=800;
	int HEIGHT=600;

	@Override
	public void draw() {
		
		  background(0);
		  // center and spin grid
		  translate(width/2, height/2, -depth/2);
		  rotateY(frameCount*PI/300);
		  rotateX(frameCount*PI/300);
		  float rotation =(1f*mouseX)/(float)WIDTH*PI*2;
		  // build grid using multiple translations 
		  for (float i=-depth/2+margin; i<=depth/2-margin; i+=boxSize){
		    pushMatrix();
		    for (float j=-height+margin; j<=height-margin; j+=boxSize){
		      pushMatrix();
		      for (float k=-width+margin; k<=width-margin; k+=boxSize){
		    	  float ramdomise=(1f*mouseY)/(float)HEIGHT* (float)Math.random()*100;
		 		 // base fill color on counter values, abs function 
		        // ensures values stay within legal range
		        boxFill = color(abs(i), abs(j), abs(k), 50);
		        pushMatrix();
		        translate(k, j, i);
		        rotateX(rotation);
		        translate(ramdomise, ramdomise, ramdomise);
		        fill(boxFill);
		        box(boxSize, boxSize, boxSize);
		        popMatrix();
		      }
		      popMatrix();
		    }
		    popMatrix();
		  }
	}

	public void setup() {
		size(WIDTH, HEIGHT, P3D);
		noStroke();
	}

	
	
}
