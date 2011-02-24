package net.processing.examples.particle.smoke_psys;

import java.util.ArrayList;
import java.util.Random;

import net.processing.examples.particle.Vector3D;

import processing.core.PApplet;
import processing.core.PImage;

public class SmokeParticleSystem extends PApplet {
	/**
	 * Smoke Particle System
	 * by Daniel Shiffman.  
	 * 
	 * A basic smoke effect using a particle system. 
	 * Each particle is rendered as an alpha masked image. 
	 */
	 
	ParticleSystem ps;
	Random generator;

	public void setup() {
	  size(400,300 ,P3D);
	  frameRate(25);
	  colorMode(RGB, 255, 255, 255, 100);
	  generator = new Random();
	    
	  // Create an alpha masked image to be applied as the particle's texture
	  //PImage msk = loadImage("texture.gif");
	  //PImage img = new PImage(msk.width,msk.height);
	 // for (int i = 0; i < img.pixels.length; i++) img.pixels[i] = color(255);
	 // img.mask(msk);

	 // ps = new ParticleSystem(0,new Vector3D(width/2,height-20),img);
	  ps = new ParticleSystem(0,new Vector3D(width/2,height-20));
	  //smooth();
	}

	public void draw() {
	  background(0);
	  int boxFill = color(255, 255,255, 50);
	  fill(boxFill);
	  // Calculate a "wind" force based on mouse horizontal position
	  float dx = (mouseX - width/2f) / 1000.0f;
	  Vector3D wind = new Vector3D(dx,0,0);
	  ps.add_force(wind);
	  ps.run();
	  for (int i = 0; i < 2; i++) {
	    ps.addParticle();
	  }
	  
	  // Draw an arrow representing the wind force
	  drawVector(wind, new Vector3D(width/2,50,0),500);

	}

	// Renders a vector object 'v' as an arrow and a location 'loc'
	void drawVector(Vector3D v, Vector3D loc, float scayl) {
	  pushMatrix();
	  float arrowsize = 4;
	  // Translate to location to render vector
	  translate(loc.x,loc.y);
	  stroke(255);
	  // Call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
	  rotate(v.heading2D());
	  // Calculate length of vector & scale it to be bigger or smaller if necessary
	  float len = v.magnitude()*scayl;
	  // Draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
	  line(0,0,len,0);
	  line(len,0,len-arrowsize,+arrowsize/2);
	  line(len,0,len-arrowsize,-arrowsize/2);
	  popMatrix();
	}



	// A simple Particle class, renders the particle as an image

	class Particle {
	  Vector3D loc;
	  Vector3D vel;
	  Vector3D acc;
	  float timer;
	  //PImage img;

	  // One constructor
	  Particle(Vector3D a, Vector3D v, Vector3D l, PImage img_) {
	    acc = a.copy();
	    vel = v.copy();
	    loc = l.copy();
	    timer = 100.0f;
	   // img = img_;
	  }

	  // Another constructor (the one we are using here)
	  Particle(Vector3D l) {//,PImage img_
	    acc = new Vector3D(0.0f,0.0f,0.0f);
	    float x = (float) generator.nextGaussian()*0.3f;
	    float y = (float) generator.nextGaussian()*0.3f - 1.0f;
	    vel = new Vector3D(x,y,0);
	    loc = l.copy();
	    timer = 100.0f;
	   // img = img_;
	  }

	  void run() {
	    update();
	    render();
	  }
	  
	  // Method to apply a force vector to the Particle object
	  // Note we are ignoring "mass" here
	  void add_force(Vector3D f) {
	    acc.add(f);
	  }  

	  // Method to update location
	  void update() {
	    vel.add(acc);
	    loc.add(vel);
	    timer -= 2.5;
	    acc.setXY(0,0);
	  }

	  // Method to display
	  void render() {
	   // imageMode(CORNER);
	   // tint(255,timer);
	   // image(img,loc.x-img.width/2,loc.y-img.height/2);
		  pushMatrix();
		  translate((float)loc.x,(float)loc.y,0f);
		  int boxFill = color(255, 255,255, 50);
		  fill(boxFill);
		 
		  sphere(10f);
		  popMatrix();
	  }

	  // Is the particle still useful?
	  boolean dead() {
	    if (timer <= 0.0) {
	      return true;
	    } else {
	      return false;
	    }
	  }
	}


	// A class to describe a group of Particles
	// An ArrayList is used to manage the list of Particles 

	class ParticleSystem {

	  ArrayList<Particle> particles;    // An arraylist for all the particles
	  Vector3D origin;        // An origin point for where particles are birthed
	  //PImage img;
	  
	  ParticleSystem(int num, Vector3D v) {//, PImage img_
	    particles = new ArrayList<Particle>();              // Initialize the arraylist
	    origin = v.copy();                        // Store the origin point
	  //  img = img_;
	    for (int i = 0; i < num; i++) {
	     // particles.add(new Particle(origin, img));    // Add "num" amount of particles to the arraylist
	    	particles.add(new Particle(origin)); 
	    }
	  }

	  void run() {
	    // Cycle through the ArrayList backwards b/c we are deleting
	    for (int i = particles.size()-1; i >= 0; i--) {
	      Particle p = (Particle) particles.get(i);
	      p.run();
	      if (p.dead()) {
	        particles.remove(i);
	      }
	    }
	  }
	  
	  // Method to add a force vector to all particles currently in the system
	  void add_force(Vector3D dir) {
	    for (int i = particles.size()-1; i >= 0; i--) {
	      Particle p = (Particle) particles.get(i);
	      p.add_force(dir);
	    }
	  
	  }  

	  void addParticle() {
	    particles.add(new Particle(origin));///,img
	  }

	  void addParticle(Particle p) {
	    particles.add(p);
	  }

	  // A method to test if the particle system still has particles
	  boolean dead() {
	    if (particles.isEmpty()) {
	      return true;
	    } else {
	      return false;
	    }
	  }

	}


	



}
