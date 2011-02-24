package net.processing.examples.tree;

import processing.core.PApplet;

public class Tree extends PApplet {
	float theta;   
	float rotation=0f;
	public void setup() {
	  size(800,600, P3D);
	  noStroke();
	  frameRate(15);
		//smooth();
	}

	public void draw() {
	  background(0);
	  ///stroke(255);
	  // Let's pick an angle 0 to 90 degrees based on the mouse position
	  float a = (mouseX / (float) width) * 90f;
	  
	  float h = (mouseY / (float) height) * 300f;
	  // Convert it to radians
	  theta = radians(a);
	  // Start the tree from the bottom of the screen
	  translate(width/2,height);
	  // Draw a line 60 pixels
	//  line(0,0,0,-60);
	  // Move to the end of that line
	  translate(0,-1*h);
	  rotateY(rotation+=0.01);
	  // Start the recursive branching!
	  
	  branch(h);

	}

	void branch(float h) {
	  // Each branch will be 2/3rds the size of the previous one
	  h *= 0.66f;
	  
	  // All recursive functions must have an exit condition!!!!
	  // Here, ours is when the length of the branch is 2 pixels or less
	  float col=h*255/50;
	  float rCol=(60f+col)%255f;
	  float bCol=(175f+col)%255f;;
	  float gCol=(128f+col)%255f;
	  int boxFill = color(abs(rCol), abs(gCol), abs(bCol), 50);
	  fill(boxFill);
	  if (h > 20) {
	    pushMatrix();    // Save the current state of transformation (i.e. where are we now)
	    rotate(theta);   // Rotate by theta
	    stroke(255);
	    line(0,0,0,-h);  // Draw the branch
	    //noStroke();
	    //box(h,h,h);
	    //sphere(h*0.7f);
	    translate(0,-h); // Move to the end of the branch
	    branch(h);       // Ok, now call myself to draw two new branches!!
	    popMatrix();     // Whenever we get back here, we "pop" in order to restore the previous matrix state
	    
	    // Repeat the same thing, only branch off to the "left" this time!
	    pushMatrix();
	    rotate(-theta);
	    stroke(255);
	    line(0,0,0,-h);  // Draw the branch
	    //noStroke();
	    //sphere(h);
	    translate(0,-h);
	    branch(h);
	    popMatrix();
	  }
	}


}
