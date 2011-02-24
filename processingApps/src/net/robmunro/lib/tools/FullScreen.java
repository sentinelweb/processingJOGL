package net.robmunro.lib.tools;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import processing.core.PApplet;

public class FullScreen {
	PApplet p;
	
	public FullScreen(PApplet p) {
		super();
		this.p = p;
	}

	public void startFullscreen(int screen) {
		  p.frame = new Frame(); 
		  //frame.dispose();  
		  p.frame.setUndecorated(true);  
		  p.frame.add(p);
		  //GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
		  //GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1];  // the second screen
		  GraphicsDevice myGraphicsDevice=null;
		  if (screen==-1) {
			  myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  (); 
		  } else {
			  myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen];
		  }
		  myGraphicsDevice.setFullScreenWindow(p.frame);  
		  if (myGraphicsDevice.isDisplayChangeSupported()) {  
		    DisplayMode myDisplayMode = new DisplayMode(  
		    p.width,  
		    p.height,  
		    32,  
		    DisplayMode.REFRESH_RATE_UNKNOWN);  
		    myGraphicsDevice.setDisplayMode(myDisplayMode);  
		  }  
	}  
		 
	public void stopFullscreen() {  
	  GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
	  myGraphicsDevice.setFullScreenWindow(null);  
	} 
}
