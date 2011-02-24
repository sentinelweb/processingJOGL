package net.processing.examples.particle.simple;

import java.util.ArrayList;

import net.processing.examples.particle.Vector3D;

import processing.core.PApplet;

public class SimpleParticleSystem extends PApplet {
	/**
	 * Simple Particle System
	 * by Daniel Shiffman.  
	 * 
	 * Particles are generated each cycle through draw(),
	 * fall with gravity and fade out over time
	 * A ParticleSystem object manages a variable size (ArrayList) 
	 * list of particles. 
	 */
	 
	ParticleSystem ps;

	public void setup() {
	  size(800,600,P3D);
	  frameRate(30);
	  colorMode(RGB,255,255,255,100);
	  ps = new ParticleSystem(1,new Vector3D(width/2,height/2,0));
	  //smooth();
	}

	public void draw() {
	  background(0);
	  ps.run();
	  ps.addParticle();
	}


	// A simple Particle class

	class Particle {
	  Vector3D loc;
	  Vector3D vel;
	  Vector3D acc;
	  float r;
	  float timer;

	  // One constructor
	  Particle(Vector3D a, Vector3D v, Vector3D l, float r_) {
	    acc = a.copy();
	    vel = v.copy();
	    loc = l.copy();
	    r = r_;
	    timer = 100.0f;
	  }
	  
	  // Another constructor (the one we are using here)
	  Particle(Vector3D l) {
	    acc = new Vector3D(0f,0.05f,0f);
	    vel = new Vector3D(random(-1,1),random(-2,0),0);
	    loc = l.copy();
	    r = 10.0f;
	    timer = 100.0f;
	  }


	  void run() {
	    update();
	    render();
	  }

	  // Method to update location
	  void update() {
	    vel.add(acc);
	    loc.add(vel);
	    timer -= 1.0;
	  }

	  // Method to display
	  void render() {
	    ellipseMode(CENTER);
	    noStroke();
	    fill(255,timer);
	    ellipse(loc.x,loc.y,r,r);
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

	  ParticleSystem(int num, Vector3D v) {
	    particles = new ArrayList<Particle>();              // Initialize the arraylist
	    origin = v.copy();                        // Store the origin point
	    for (int i = 0; i < num; i++) {
	      particles.add(new Particle(origin));    // Add "num" amount of particles to the arraylist
	    }
	  }

	  void run() {
	    ru1();
	  }

	private void ru1() {
		// Cycle through the ArrayList backwards b/c we are deleting
	    for (int i = particles.size()-1; i >= 0; i--) {
	      Particle p = (Particle) particles.get(i);
	      p.run();
	      if (p.dead()) {
	        particles.remove(i);
	      }
	    }
	}

	  void addParticle() {
	    particles.add(new Particle(origin));
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
