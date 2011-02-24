package net.processing.gsvideo;

import codeanticode.gsvideo.GSMovie;
import processing.core.PApplet;

public class Scratch extends PApplet {
	GSMovie movie;

	public void setup() 
	{
	  size(640, 480);
	  background(0);

	  movie = new GSMovie(this, "resources/gsmovie/station.mov");
	  movie.play();
	}

	public void movieEvent(GSMovie movie)
	{
	  movie.read();
	}

	public void draw() 
	{
	  movie.jump(movie.duration() * mouseX / width);
	  image(movie, 0, 0, width, height);
	}

}
