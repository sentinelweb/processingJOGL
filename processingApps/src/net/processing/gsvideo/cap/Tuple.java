package net.processing.gsvideo.cap;

import processing.core.PApplet;

class Tuple {
	  float x, y, z;
	  PApplet app;
	  public Tuple(PApplet app) {this.app=app; }

	  public Tuple(PApplet app,float x, float y, float z) {
	    set(x, y, z);
	    this.app=app;
	  }

	  public void set(float x, float y, float z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	  }
	  
	  public void target(Tuple another, float amount) {
	    float amount1 = 1.0f - amount;
	    x = x*amount1 + another.x*amount;
	    y = y*amount1 + another.y*amount;
	    z = z*amount1 + another.z*amount;
	  }
	  
	  public void phil() {
	    app.fill(x, y, z);
	  }
	}