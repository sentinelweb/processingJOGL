package net.processing.gsvideo;

import processing.core.PApplet;
import codeanticode.gsvideo.GSMovie;

public class Loop extends PApplet {
	GSMovie myMovie;


	public void setup() {
	  size(640, 480, P3D);
	  background(0);
	  // Load and play the video in a loop
	  myMovie = new GSMovie(this, "resources/gsmovie/station.mov");
	  myMovie.loop();
	}


	public void movieEvent(GSMovie myMovie) {
	  myMovie.read();
	}


	public void draw() {
	  //tint(255, 20);
	  //image(myMovie, mouseX-myMovie.width/2, mouseY-myMovie.height/2);
		image(myMovie, 0,0,600,400);
	}

}
