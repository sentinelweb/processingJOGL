package net.processing.examples.particle.multi;

import java.util.ArrayList;

import net.processing.examples.particle.Vector3D;

import processing.core.PApplet;

public class MultipleParticleSystems extends PApplet {
	/**
	 * Multiple Particle Systems
	 * by Daniel Shiffman.  
	 * 
	 * Click the mouse to generate a burst of particles
	 * at mouse location. 
	 * 
	 * Each burst is one instance of a particle system
	 * with Particles and CrazyParticles (a subclass of Particle)
	 * Note use of Inheritance and Polymorphism here. 
	 */
	 
	ArrayList<ParticleSystem> psystems;

	public void setup() {
	  size(800,600,P3D);
	  frameRate(20);
	  colorMode(RGB,255,255,255,100);
	  psystems = new ArrayList();
	 //smooth();
	}

	public void draw() {
	  background(0);

	  // Cycle through all particle systems, run them and delete old ones
	  for (int i = psystems.size()-1; i >= 0; i--) {
	    ParticleSystem psys = (ParticleSystem) psystems.get(i);
	    psys.run();
	    if (psys.dead()) {
	      psystems.remove(i);
	    }
	  }

	}

	// When the mouse is pressed, add a new particle system
	public void mousePressed() {
	  psystems.add(new ParticleSystem((int)random(5,25f), new Vector3D(mouseX,mouseY)));
	}


	// A subclass of Particle

	// Created 2 May 2005

	class CrazyParticle extends Particle {

	  // Just adding one new variable to a CrazyParticle
	  // It inherits all other fields from "Particle", and we don't have to retype them!
	  float theta;

	  // The CrazyParticle constructor can call the parent class (super class) constructor
	  CrazyParticle(Vector3D l) {
	    // "super" means do everything from the constructor in Particle
	    super(l);
	    // One more line of code to deal with the new variable, theta
	    theta = 0.0f;

	  }

	  // Notice we don't have the method run() here; it is inherited from Particle

	  // This update() method overrides the parent class update() method
	  void update() {
	    super.update();
	    // Increment rotation based on horizontal velocity
	    float theta_vel = (vel.x * vel.magnitude()) / 10.0f;
	    theta += theta_vel;
	  }

	  // Override timer
	  void timer() {
	    timer -= 0.5;
	  }
	  
	  // Method to display
	  void render() {
	    // Render the ellipse just like in a regular particle
	    super.render();

	    // Then add a rotating line
	    pushMatrix();
	    translate(loc.x,loc.y);
	    rotate(theta);
	    stroke(255,timer);
	    line(0,0,25,0);
	    popMatrix();
	  }
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
	    acc = new Vector3D(0.002f,0.00005f,0f);
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

	  ArrayList particles;    // An arraylist for all the particles
	  Vector3D origin;        // An origin point for where particles are birthed

	  ParticleSystem(int num, Vector3D v) {
	    particles = new ArrayList();              // Initialize the arraylist
	    origin = v.copy();                        // Store the origin point
	    for (int i = 0; i < num; i++) {
	      particles.add(new CrazyParticle(origin));    // Add "num" amount of particles to the arraylist
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
