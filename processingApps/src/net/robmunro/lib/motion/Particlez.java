package net.robmunro.lib.motion;

import java.util.ArrayList;

import net.robmunro.lib.ogl.tools.Vector3D;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import processing.core.PApplet;

public class Particlez {
	GL gl;
	PApplet p;
	GLU glu;
	public Particlez(GL gl, PApplet p) {
		super();
		this.gl = gl;
		this.glu = new GLU();
		this.p = p;
	}
	public class ParticleSystems{
		ArrayList<ParticleSystem> psystems;

		public ParticleSystems() {
			super();
			psystems = new ArrayList<ParticleSystem>();
		}
		
		public void render(){
			gl.glPushMatrix();
			gl.glTranslatef(0,0,-300);
			for (int i = psystems.size()-1; i >= 0; i--) {
			    ParticleSystem psys =  psystems.get(i);
			    psys.run();
			    if (psys.dead()) {
			      psystems.remove(i);
			    }
			}
			gl.glPopMatrix();
		}
		
		public void addSystem(Vector3D origin) {
			psystems.add(new ParticleSystem((int)p.random(45f,75f), origin));//
		}
	}
	public class CrazyParticle extends Particle {

		  // Just adding one new variable to a CrazyParticle
		  // It inherits all other fields from "Particle", and we don't have to retype them!
		  float theta;
		 // The CrazyParticle constructor can call the parent class (super class) constructor
		  CrazyParticle(Vector3D l) {
		    super(l);
		    // One more line of code to deal with the new variable, theta
		    theta = 0.0f;
		  }

		  // Notice we don't have the method run() here; it is inherited from Particle

		  // This update() method overrides the parent class update() method
		  void update() {
		    super.update();
		    // Increment rotation based on horizontal velocity
		    float theta_vel = (vel.x * vel.magnitude()) *9f;
		    theta += theta_vel;
		  }

		  // Override timer
		  void timer() {
		    timer -= 0.5;
		  }
		  
		  // Method to display
		  void render() {
			

		    gl.glPushMatrix();
		    gl.glTranslatef(loc.x, loc.y, 0f);
		    gl.glRotatef(theta, 1f, 1f, 1f);
		    gl.glColor4f(1f, (float)timer/TIMER_LEN, 0, 0.5f);//(float)timer/TIMER_LEN
		    super.render();
		    gl.glPopMatrix();

		  }
		}





		// A simple Particle class

	public class Particle {
			
		  public static final float TIMER_LEN = 200.0f;
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
		    timer = TIMER_LEN;
		  }
		  
		  // Another constructor (the one we are using here)
		  Particle(Vector3D l) {
		    acc = new Vector3D(0.005f,0.0008f,0f);
		    vel = new Vector3D(p.random(-1,1),p.random(-1,1),0);
		    loc = l.copy();
		    r = 10.0f;
		    timer = 300.0f;
		  }

		 public  void run() {
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
			  //glu.gluSphere(glu.gluNewQuadric(), 10.0,10,10); 
			  glu.gluCylinder( glu.gluNewQuadric(), 8, 0, 40 ,10,10);
			  /*
			  for (int i=0;i<3;i++) {
				  gl.glTranslatef(0f, 0f, (float)i*-1*timer/TIMER_LEN*5);
				  glu.gluSphere(glu.gluNewQuadric(), 2.0,4,4); 
			  }*/
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

	public class ParticleSystem {

		  ArrayList particles;    // An arraylist for all the particles
		  Vector3D origin;        // An origin point for where particles are birthed
		  //Vector3D color;
		  
		  ParticleSystem(int num, Vector3D v) {
		    particles = new ArrayList();              // Initialize the arraylist
		    origin = v.copy();                        // Store the origin point
		    for (int i = 0; i < num; i++) {
		      particles.add(new CrazyParticle(origin));    // Add "num" amount of particles to the arraylist
		    }
		   // color = new Vector3D((int)(Math.random()*175+80),(int)(Math.random()*175+80),((int)Math.random()*175+80));
			 
		  }

		  void run() {
		    // Cycle through the ArrayList backwards b/c we are deleting
		    for (int i = particles.size()-1; i >= 0; i--) {
		      Particle p = (Particle) particles.get(i);
		      //float factor =  (float)p.timer/Particle.TIMER_LEN;
		      //gl.glColor4f((float)color.x/255f*factor,(float)color.y/255f*factor,(float)color.z/255f*factor,1);//(float)timer/TIMER_LEN
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
