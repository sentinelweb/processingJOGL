package net.robmunro.perform.ol5;

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

	void startFullscreen() {
		Frame oldFrame = p.frame;
		p.frame = new Frame(); 
		p.frame.add(p);
		p.frame.setUndecorated(true);  
		p.frame.setLocation(0, 0);
		if (oldFrame!=null) {
			oldFrame.setVisible(false);
			oldFrame.setAlwaysOnTop(false);
		}
		p.frame.setAlwaysOnTop(true);
		GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice myGraphicsDevice = screenDevices[1];  // the second screen
		for (int i=0;i<screenDevices.length;i++) {
			  System.out.println(screenDevices[i].getIDstring()+":"+screenDevices[i].getDisplayMode().getWidth()+"x"+screenDevices[i].getDisplayMode().getHeight());
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
		 
	void stopFullscreen() {  
	  GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
	  myGraphicsDevice.setFullScreenWindow(null);  
	} 
}
