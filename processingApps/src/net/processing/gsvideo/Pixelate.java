package net.processing.gsvideo;

import processing.core.PApplet;
import codeanticode.gsvideo.GSMovie;

public class Pixelate extends PApplet {
	int numPixels;
	int blockSize = 10;
	GSMovie myMovie;
	int myMovieColors[];

	public void setup() {
	  size(640, 480, P3D);
	  noStroke();
	  background(0);
	  myMovie = new GSMovie(this, "resources/gsmovie/station.mov");
	  myMovie.loop();
	  numPixels = width / blockSize;
	  myMovieColors = new int[numPixels * numPixels];
	}


	// Read new values from movie
	public void movieEvent(GSMovie m) {
	  m.read();
	  m.loadPixels();
	  
	  for (int j = 0; j < numPixels; j++) {
	    for (int i = 0; i < numPixels; i++) {
	      myMovieColors[j*numPixels + i] = m.get(i, j);
	    }
	  }
	}


	// Display values from movie
	public void draw()  {
	  for (int j = 0; j < numPixels; j++) {
	    for (int i = 0; i < numPixels; i++) {
	      fill(myMovieColors[j*numPixels + i]);
	      rect(i*blockSize, j*blockSize, blockSize-1, blockSize-1);
	    }
	  }
	}
}
