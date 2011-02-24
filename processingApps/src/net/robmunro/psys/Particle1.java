package net.robmunro.psys;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.processing.examples.particle.Vector3D;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.MotionBlur;



import processing.core.PApplet;

public class Particle1 extends PApplet {
	
	ArrayList<ParticleSystem> psystems;
	GL gl;
	OpenGL ogl ;
	GLU glu;
	MotionBlur mblur=null;
	public void setup() {
		  size(1200,800,OPENGL);
		  frameRate(20);
		  ogl=new OpenGL(this);
		  this.gl=ogl.gl;
		  glu = new GLU();
		  mblur=new MotionBlur(gl);
		  mblur.clearAccum();
		  gl.glEnable(GL.GL_DOUBLEBUFFER);
		  gl.glEnable(GL.GL_BLEND);
		  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		  try {
			ogl.makeProgram(
						"glass",
						new String[] {},
						new String[] {"SpecularColor1","SpecularColor2","SpecularFactor1","SpecularFactor2","LightPosition"}, //"GlassColor",
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glass_c.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glass_c.frag"	)
				);
		  } catch (Exception e) {
				e.printStackTrace();
		  }
		  psystems = new ArrayList();
		  gl.glClearAccum(0f,0f,0f,0f); // for motion blur
		  gl.glClear(GL.GL_ACCUM_BUFFER_BIT);
	}
	boolean drawSemaphore=false;
	public void draw() {
		if (drawSemaphore) { println("skip"); return; }
		drawSemaphore=true;
		background(0);
		// Cycle through all particle systems, run them and delete old ones
		GLSLProgram gProgram = ogl.getProgram("glass");
		  boolean vertexShaderEnabled =(gProgram!=null);// false;//
		  if (vertexShaderEnabled) {
		  
		     gl.glUseProgramObjectARB(gProgram.getProgramObject());
		     
		     gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 0f, 0f,  5f);
		     
		   // gl.glUniform4fARB(gProgram.getUniformId("GlassColor"), mouseX/(float)width, mouseY/(float)height, 0.6f, 0.35f);
		     gl.glUniform4fARB(gProgram.getUniformId("SpecularColor1"),  .1f, 0.1f, 0.1f, 1f);
		     gl.glUniform4fARB(gProgram.getUniformId("SpecularColor2"),  .1f, 0.1f, 0.1f, 1f);
		     gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor1"),2f) ;
		     gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor2"),2f);
		   }
		  
		 gl.glTranslatef(-350, -300 ,-400);
		 
		for (int i = psystems.size()-1; i >= 0; i--) {
		    ParticleSystem psys = (ParticleSystem) psystems.get(i);
		    psys.run();
		    if (psys.dead()) {
		      psystems.remove(i);
		    }
		}
		mblur.blur(0.9f);
		drawSemaphore=false;
	}
	// When the mouse is pressed, add a new particle system
	//boolean down=false;
	public void  mousePressed(){	 
		psystems.add(new ParticleSystem((int)random(45f,75f), new Vector3D(mouseX,mouseY)));//
		//down=true;
	}
	//public void  mouseReleased(){	 down=false;	}
	
	public void mouseClicked() {
	  
	}
	public void mouseMoved() {
		if (shift){
			psystems.add(new ParticleSystem((int)random(5,10f), new Vector3D(mouseX,mouseY)));
		}
	}
	boolean shift =false;
	public void keyPressed(){
		shift =(keyCode==SHIFT)&& !shift;
		
	}
	class CrazyParticle extends Particle {

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
	    gl.glColor4f(1f, (float)timer/TIMER_LEN, 0, 1);//(float)timer/TIMER_LEN
	    super.render();
	    gl.glPopMatrix();

	  }
	}





	// A simple Particle class

	class Particle {
		
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
	    vel = new Vector3D(random(-3,3),random(-5,5),0);
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

	class ParticleSystem {

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
