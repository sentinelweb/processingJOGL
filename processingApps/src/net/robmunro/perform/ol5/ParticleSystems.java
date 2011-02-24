package net.robmunro.perform.ol5;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.sun.opengl.util.texture.Texture;

import processing.core.PApplet;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.Shape.Cube;
import net.robmunro.lib.tools.LookupTable;
/* ************************************************************
 * Particle system.
 ************************************************************ */
class ParticleSystems{
	ArrayList<ParticleSystem> psystems;
	GL gl;
	GLU glu;
	OpenGL ogl ;
	PApplet p;
	//Method renderMethod;
	Shape s;
	public ParticleSystems(OpenGL ogl, PApplet p) {
		super();
		psystems = new ArrayList<ParticleSystem>();
		this.ogl=ogl;
		this.gl = ogl.gl;
		this.glu = new GLU();
		this.p=p;
		this.s = new Shape(this.gl);
	}
	
	void render(){
		gl.glPushMatrix();
		
		for (int i = psystems.size()-1; i >= 0; i--) {
		    ParticleSystem psys =  psystems.get(i);
		    psys.run();
		    if (psys.dead()) {
		      psystems.remove(i);
		    }
		}
		gl.glPopMatrix();
	}
	
	void setAcc(Vector3D acc) {
		for (int i =0; i <  psystems.size(); i++) {
			ParticleSystem ps = psystems.get(i);
			for(int j =0; j <  ps.particles.size(); j++) {
				ps.particles.get(j).acc=acc.copy();
			}
		}
	}
	
//	private float random(float min,float max) {
//		return (float)Math.random()*(max-min)+min;
//	}

	void addSystem(Vector3D origin) {
		//psystems.add(new ParticleSystem((int)p.random(45f,75f), origin));
		//psystems.add(new ParticleSystem(1, origin));
		//psystems.add(new ParticleSystem(80, origin));
		//psystems.add(new ParticleSystem(20, origin,new RoseMotion(),new RibbonRenderer(1)));
		//psystems.add(new ParticleSystem(40, origin,new EpicycloidMotion(),new RibbonRenderer(1)));
		//psystems.add(new ParticleSystem(40, origin,new HypocycloidMotion(),new RibbonRenderer(10,true)));
		//psystems.add(new ParticleSystem(40, origin,new RandomAcceleratorMotion(),new RibbonRenderer(2,true)));
		psystems.add(new ParticleSystem(80, origin,new CrazyMotion(),new SquareRenderer()));
		//psystems.add(new ParticleSystem(40, origin,new EpitrochoidMotion(),new RibbonRenderer(1)));
		//psystems.add(new ParticleSystem(20, origin,new RoseMotion(),new RibbonRenderer(1)));
	}
	
	void addSystem(ParticleSystem ps) {
		psystems.add(ps);
		
	}
	
	/********* ParticleSystem ************************************************************************************************************** */
	
	class ParticleSystem {
		Motion motion;
		ParticleRenderer renderer;
	    ArrayList<Particle> particles;    // An arraylist for all the particles
	    Vector3D origin;        // An origin point for where particles are birthed
	    int trailLength = 50;
	    int timerLength =100;
	 ParticleSystem(int num, Vector3D origin) {
	    init(num, origin,null,null);
	 }
	  
	ParticleSystem(int num, Vector3D origin, Motion m,ParticleRenderer ren) {
		init(num, origin,m,ren);
	}
	ParticleSystem(int num, Vector3D origin, Motion m,ParticleRenderer ren,int trailLength,int timerLength) {
		this.trailLength=trailLength;
		this.timerLength=timerLength;
		init(num, origin,m,ren);
	}
	private void init(int num, Vector3D v, Motion m,ParticleRenderer ren) {
		particles = new ArrayList<Particle>();              // Initialize the arraylist
		this.renderer=ren;
		this.motion=m;
	    origin = v.copy();                        // Store the origin point
	    for (int i = 0; i < num; i++) {
	     	//particles.add(new Particle(origin,new RoseMotion(),new RibbonRenderer(1),i,this));
	    	//particles.add(new Particle(origin,new CrazyMotion(),new SquareRenderer(),i));
	    	//particles.add(new Particle(origin,new StandardMotion(),new SimpleRenderer(),i));
	    	//particles.add(new Particle(origin,new LissajousMotion(),new RibbonRenderer(1),i,this));
		    //particles.add(new Particle(origin));
	    	//particles.add(new CrazyParticle(origin));    // Add "num" amount of particles to the ArrayList
	    	particles.add(new Particle(origin,i,this));
	    }
	}

	  void run() {
	    // Cycle through the ArrayList backwards b/c we are deleting
		  
	    for (int i = particles.size()-1; i >= 0; i--) {
	      Particle p = particles.get(i);
	      p.run();
	      
	      if (p.dead()) {
	        particles.remove(i);
	      }
	    }
	  }

	  void addParticle() {
	    particles.add(new Particle(origin,particles.size(),this));
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
	/********* Particle ************************************************************************************************************** */
	
	// A simple Particle class
	class Particle {
		  private static final int DEF_TRAIL_LENGTH = 50;
		  private static final int DEF_TIMER_LENGTH = 50;
		  int trailLength = DEF_TRAIL_LENGTH;
		  int timerLength = DEF_TIMER_LENGTH;
		  Vector3D loc;
		  Vector3D vel;
		  Vector3D acc;
		  Vector3D rot;
		  Motion motion;
		  ParticleRenderer ren;
		  //float r;
		  int timer;
		  int counter=0;
		  RingBuffer<Vector3D> trails;
		  RingBuffer<Vector3D> trailsRot;
		  int index= 0;
		  ParticleSystem ps;
		  long startTime = System.currentTimeMillis();
		  long lastUpdateTime = startTime;
		  HashMap<String, Object> renderObjects=new HashMap<String, Object>();
		  // One constructor
		  Particle(Vector3D a, Vector3D v, Vector3D l, Motion m,ParticleRenderer ren,int index,ParticleSystem ps) {//, float r_
			  init(a,v,l,null,null,index,ps);
		  }
		  
		  // Another constructor (the one we are using here)
		  Particle(Vector3D l, Motion m,ParticleRenderer ren,int index,ParticleSystem ps) {
			  	acc = new Vector3D(0.005f,0.0008f,0f);
				vel = new Vector3D(p.random(-1,1),p.random(-1,1),p.random(-1,1));
			    init(acc,vel,l, m,ren,index,ps);
		  }
		  
		  Particle(Vector3D l,int index,ParticleSystem ps) {
			  	acc = new Vector3D(0.005f,0.0008f,0f);
				vel = new Vector3D(p.random(-1,1),p.random(-1,1),p.random(-1,1));
				init(acc,vel,l,null,null,index,ps);
				
		  }
		  
		private void init(Vector3D a, Vector3D v,Vector3D l, Motion m, ParticleRenderer ren,int index,ParticleSystem ps) {
				acc = a.copy();
			    vel = v.copy();
				loc = l.copy();
				rot = new Vector3D();
				//r = 10.0f;
				timer = timerLength;
				
				this.index=index;
				this.ps=ps;
				// motion
				if (m==null) {
					if (ps==null || ps.motion==null) {
						this.motion=new StandardMotion();
					} else {
						this.motion=null;
					}
				}else {
					this.motion=m;
				}
				// renderer
				if (ren==null) {
					if (ps==null || ps.motion==null) {
						this.ren=new SquareRenderer();
					} else {
						this.ren=null;
					}
				} else {
					this.ren=ren;
				}
				if (ps!=null) {
					trailLength=ps.trailLength;
					timerLength=ps.timerLength;
				}
				trails = new RingBuffer<Vector3D>(trailLength);
				trailsRot = new RingBuffer<Vector3D>(trailLength);
			}
			  
			public Motion getMotion() {
				return motion;
			}
	
			public void setMotion(Motion m) {
				this.motion = m;
			}
	
			void run() {
				    update();
				    render();
		   }
	
		  // Method to update location
		  void update() {
			  Motion m = this.motion!=null?this.motion:ps.motion;
			  counter=(int)(System.currentTimeMillis()-startTime)/10;
			   if (m.update(this)) {
				   timer=timerLength-counter;
			   }
			   lastUpdateTime=System.currentTimeMillis();
		  }
	
		  // Method to display
		  void render() {
			 
			  if (ren!=null) {
				  ren.render(this);
			  } else {
				  ps.renderer.render(this);
			  }
		  }
		  
		  boolean dead() {
			    if (timer <= 0.0) {
			      return true;
			    } else {
			      return false;
			    }
		  }
	}
	/********* Motion ************************************************************************************************************** */
	public interface Motion{
		boolean update (Particle p);
	}
	
	public class ResetMotion implements Motion {
		public boolean update(Particle pt) {
			pt.loc=new Vector3D(0,0,0);
			return false;
		}
	}
	
	public class StandardMotion implements Motion {
		Vector3D acc;
		
		public StandardMotion(Vector3D acc) {
			this.acc = acc;
		}
		public StandardMotion() {
		}
		public boolean update(Particle pt) {
			if (this.acc!=null) {pt.acc=this.acc;}
			pt.vel.add(pt.acc);
		    pt.loc.add(pt.vel);
		    pt.trails.enqueue(pt.loc.copy());
		    pt.trailsRot.enqueue(pt.rot.copy());
		    return true;
	  }
	}
	
	public class CrazyMotion implements Motion {
		float freq = 2;
		float amp = 2;
		Vector3D rot = new Vector3D(2,2,2);
		public CrazyMotion() {}
		public CrazyMotion(float freq, float amp,Vector3D rot) {
			super();
			this.freq = freq;
			this.amp = amp;
			this.rot = rot;
		}
		
		public boolean update(Particle pt) {
			//float theta_vel = (pt.vel.x * pt.vel.magnitude()) *9f;
			pt.rot.add(rot) ;
		    pt.vel.add(pt.acc);
		    pt.loc.add(pt.vel);
		    pt.loc.add(new Vector3D(0,freq*(float)(LookupTable.sin(pt.timer/(float)pt.timerLength*freq)),0));
		    pt.trails.enqueue(pt.loc.copy());
		    pt.trailsRot.enqueue(pt.rot.copy());
		    return true;
	  }
	}
	
	public class RandomAcceleratorMotion implements Motion {
		int lastChange = 0;
		int startIndex=0;
		public boolean update(Particle pt) {
			if (pt.counter-lastChange>40) {
				lastChange=pt.counter;
				 startIndex=0;
			}
			if (startIndex<pt.ps.particles.size()) {
				float accMax = 0.1f;
				pt.acc=new Vector3D(p.random(-accMax,accMax),p.random(-accMax,accMax),p.random(-accMax,accMax));
			}
		    pt.vel.add(pt.acc.copy().mult((System.currentTimeMillis()-pt.lastUpdateTime)/10)); 
		    pt.loc.add(pt.vel);
		    pt.trails.enqueue(pt.loc.copy());
		    pt.trailsRot.enqueue(new Vector3D(0,0,0)) ;
		    return true;
	  }
	}
	
	public class RoseMotion implements Motion {
		public  float k = 4;
		public RoseMotion() {		}
		public RoseMotion(float k) {
			super();
			this.k = k;
		}

		public boolean update (Particle pt) {
			float theta = (float) ((float)pt.counter/pt.timerLength*2*Math.PI)+pt.index*36;
			float r = (float)(200*LookupTable.sin(k*theta)+pt.index*10);
			PolarCoords p = new PolarCoords(r,theta);
			
			pt.loc=new Vector3D(p.x(),p.y(),0);
			pt.loc.add(pt.vel);
			pt.trails.enqueue(pt.loc.copy());
			
			float theta_vel = 4f;
		    pt.rot.add(new Vector3D(theta_vel,0,0)) ;
		    pt.trailsRot.enqueue(pt.rot.copy());
			return true;
		}
	}
	
	public class LissajousMotion implements Motion {
		public  float a = 4;
		public float b = 5;
		public float delta = 90;
		public float A = 100;
		public float B = 100;
		public LissajousMotion(){}
		public LissajousMotion(float a, float b, float delta, float a2, float b2) {
			super();
			this.a = a;
			this.b = b;
			this.delta = delta;
			A = a2;
			B = b2;
		}

		public boolean update (Particle pt) {
			int angle = pt.counter+pt.index*36;
			pt.loc.x=A * (float)LookupTable.sinD(a*angle+delta)+pt.index*10;
			pt.loc.y=B * (float)LookupTable.sinD(b*angle)+pt.index*10;
			
			pt.trails.enqueue(pt.loc.copy());
			pt.trailsRot.enqueue(new Vector3D(0,0,0)) ;
			return true;
		}
	}
	
	public class EpicycloidMotion implements Motion {
		public  float k = 2.1f;
		public  float r = 50;
		public EpicycloidMotion(){}
		public EpicycloidMotion(float k, float r) {
			super();
			this.k = k;
			this.r = r;
		}

		public boolean update (Particle pt) {
			int angle = pt.counter+pt.index*135;
			pt.loc.x = r*(k+1) *(float)LookupTable.cosD(angle)-r*(float)LookupTable.cosD((k+1)*angle);
			pt.loc.y = r*(k+1) *(float)LookupTable.sinD(angle)-r*(float)LookupTable.sinD((k+1)*angle);
			pt.loc.z=0;
			pt.trails.enqueue(pt.loc.copy());
			pt.trailsRot.enqueue(new Vector3D(0,0,0)) ;
			return true;
		}
	}
	
	public class HypocycloidMotion implements Motion {
		public  float k = 2.1f;
		public  float r = 30;
		public HypocycloidMotion(){}
		public HypocycloidMotion(float k, float r) {
			super();
			this.k = k;
			this.r = r;
		}

		public boolean update (Particle pt) {
			int angle = pt.counter+pt.index*135;
			pt.loc.x = r*(k-1) *(float)LookupTable.cosD(angle)+r*(float)LookupTable.cosD((k-1)*angle);
			pt.loc.y = r*(k-1) *(float)LookupTable.sinD(angle)-r*(float)LookupTable.sinD((k-1)*angle);
			pt.loc.z=0;
			pt.trails.enqueue(pt.loc.copy());
			
			float theta_vel = 4f;
		    pt.rot.add(new Vector3D(theta_vel,0,0)) ;
		    pt.trailsRot.enqueue(pt.rot.copy());
			//pt.trailsRot.enqueue(new Vector3D(0,0,0)) ;
			return true;
		}
	}
	
	public class EpitrochoidMotion implements Motion {
		public  float R = 80f;
		public  float r = 65f;
		public  float d = 90f;
		public EpitrochoidMotion(float r, float r2, float d) {
			super();
			R = r;
			r = r2;
			this.d = d;
		}
		public boolean update (Particle pt) {
			int angle = pt.counter+pt.index*70;
			//pt.loc.x = r*(k-1) *(float)LookupTable.cosD(angle)+r*(float)LookupTable.cosD((k-1)*angle);
			//pt.loc.y = r*(k-1) *(float)LookupTable.sinD(angle)-r*(float)LookupTable.sinD((k-1)*angle);
			pt.loc.x = (R+r)*(float)LookupTable.cosD(angle)-d*(float)LookupTable.cosD((R+r)/r*angle);
			pt.loc.y = (R+r)*(float)LookupTable.sinD(angle)-d*(float)LookupTable.sinD((R+r)/r*angle);
			
			//pt.loc.z -= 2; 
			pt.trails.enqueue(pt.loc.copy());
			
			float theta_vel = 4f;
		    pt.rot.add(new Vector3D(theta_vel,0,0)) ;
		    pt.trailsRot.enqueue(pt.rot.copy());
			//pt.trailsRot.enqueue(new Vector3D(0,0,0)) ;
			return false;
		}
	}
	public class PolarCoords {
		float r,theta =0;
		PolarCoords(float r,float theta){
			this.r=r;
			this.theta=theta;
		}
		
		public float x() {
			return (float)(r*LookupTable.cos(theta));
		}
		public float y() {
			return (float)(r*LookupTable.sin(theta));
		}
	}
	
	
	/********* ParticleRenderer ************************************************************************************************************** */
	public interface ParticleRenderer {
		public void render (Particle pt);
	}
	
	public class SimpleRenderer implements ParticleRenderer {
		Vector3D col = new Vector3D(1f, 0.5f,0);
		public SimpleRenderer() {	}
		public SimpleRenderer(Vector3D col) {
			this.col = col;
		}

		public void render (Particle pt) {
				 float alpha=0.6f;
				 float fadeOutTime = 30f;
				 if (pt.timer<fadeOutTime) {
						alpha=0.6f*(pt.timer/fadeOutTime);
				 }
				 gl.glPushMatrix();
				 gl.glTranslatef(pt.loc.x, pt.loc.y, pt.loc.z);
				 gl.glRotatef(pt.rot.x, 1,0,0);
				 gl.glRotatef(pt.rot.y, 0,1,0);
				 gl.glRotatef(pt.rot.y, 0,0,1);
				 gl.glColor4f(col.x, col.y,col.z,alpha);
				 s.drawSquare(5, 5);
				 gl.glPopMatrix();
		}
	}
	
	public class TextureRenderer implements ParticleRenderer {
		Vector3D col = new Vector3D(1f, 0.5f,0);
		Texture tex;
		int size=5;
		public TextureRenderer() {	}
		public TextureRenderer(Vector3D col,Texture tex,int size) {
			this.col = col;
			this.tex=tex;
			this.size=size;
		}

		public void render (Particle pt) {
				 float alpha=0.6f;
				 float fadeOutTime = 30f;
				 if (pt.timer<fadeOutTime) {
						alpha=0.6f*(pt.timer/fadeOutTime);
				 }
				 gl.glPushMatrix();
					 tex.bind();
					 tex.enable();
					 gl.glTranslatef(pt.loc.x, pt.loc.y, pt.loc.z);
					 gl.glRotatef(pt.rot.x, 1,0,0);
					 gl.glRotatef(pt.rot.y, 0,1,0);
					 gl.glRotatef(pt.rot.y, 0,0,1);
					 gl.glColor4f(col.x, col.y,col.z,alpha);
					 s.drawSquare(size, size);
				 gl.glPopMatrix();
				 gl.glBindTexture(GL.GL_TEXTURE_2D,  0); 
		}
	}
	public class CubeRenderer implements ParticleRenderer {
		Vector3D col = new Vector3D(1f, 0.5f,0);
		public CubeRenderer() {	}
		public CubeRenderer(Vector3D col) {
			this.col = col;
		}

		public void render (Particle pt) {
			Shape.Cube cube =(Shape.Cube)pt.renderObjects.get("cube");
				if (cube==null) {
					cube=s.new Cube(
				    		20 ,
				    		5, 
				    		5,//Math.round(random(-100, 100)),
				    		0, 
				   			0, 
							0
				    );
					cube.initCube();
					pt.renderObjects.put("cube",cube);
				}
				 float alpha=0.6f;
				 float fadeOutTime = 30f;
				 if (pt.timer<fadeOutTime) {
						alpha=0.6f*(pt.timer/fadeOutTime);
				 }
				 gl.glPushMatrix();
				 gl.glTranslatef(pt.loc.x, pt.loc.y, pt.loc.z);
				 gl.glRotatef(pt.rot.x, 1,0,0);
				 gl.glRotatef(pt.rot.y, 0,1,0);
				 gl.glRotatef(pt.rot.y, 0,0,1);
				 gl.glColor4f(col.x, col.y,col.z,alpha);
				 cube.draw();
				 gl.glPopMatrix();
		}

	}
	
	public class SquareRenderer implements ParticleRenderer {
		public void render (Particle pt) {
			 float alpha=0.6f;
			 float fadeOutTime = 30f;
			 if (pt.timer<fadeOutTime) {
					alpha=0.6f*(pt.timer/fadeOutTime);
			 }
			 gl.glColor4f(0,1f, (float)pt.index/pt.ps.particles.size(),  alpha);
			for (int i=0;i<pt.trails.size();i+=10) {
				 gl.glPushMatrix();
				 Vector3D theLoc = pt.trails.get(i);
				 gl.glTranslatef(theLoc.x, theLoc.y, theLoc.z);
				 
				 Vector3D theRot =pt.trailsRot.get(i);
				 if (theRot!=null) {
					 gl.glRotatef(theRot.x, 1,0, 0);
					 gl.glRotatef(theRot.x, 0,1, 0);
					 gl.glRotatef(theRot.x, 0,0, 1);
					 
					 s.drawSquare(5, 5);
				 }
				 gl.glPopMatrix();
			  }
		}


	}
	
	public class RibbonRenderer implements ParticleRenderer {
		int width = 10;
		int res = 3;
		boolean head = false;
		Vector3D colouriserMax = null;
		Vector3D colouriserMin = null;
		Vector3D colour = null;
		public RibbonRenderer(int res, boolean head) {
			this.res=res;
			this.head=head;
			int maxPos=(int)p.random(3);
			colouriserMax=new Vector3D(maxPos==0?1:0,maxPos==1?1:0,maxPos==2?1:0);
			int minPos=(int)p.random(3);
			while (minPos==maxPos) { minPos=(int)p.random(3);}
			colouriserMin=new Vector3D(minPos==0?1:0,minPos==1?1:0,minPos==2?1:0);
		}
		
		public void render (Particle pt) {
			Vector3D last = null;
			Vector3D lastRot = null;
			float alpha=0.8f;
			float fadeOutTime = 50f;
			if (pt.timer<fadeOutTime) {
				alpha=0.8f*(pt.timer/fadeOutTime);
			}
			//gl.glColor4f(1f, (float)pt.index/pt.ps.particles.size(), 0, alpha);
			Vector3D col=colouriserMax.copy().add(colouriserMin.copy().mult((float)pt.index/pt.ps.particles.size()));
			gl.glColor4f(col.x,col.y,col.z, alpha);
			if (head){
				gl.glPushMatrix();
					gl.glTranslatef(pt.loc.x, pt.loc.y, pt.loc.z);
					glu.gluSphere(glu.gluNewQuadric(), 10, 10, 8);
				gl.glPopMatrix();
			}
			float alphaFactor=1.0f/pt.trails.size();
			float alphaMultiplier = 1.0f;
			for (int i=0;i<pt.trails.size();i+=res) {
				 gl.glPushMatrix();
				 Vector3D theLoc = pt.trails.get(i);
				 Vector3D theRot = pt.trailsRot.get(i);
				 gl.glColor4f(col.x,col.y,col.z, alpha*alphaMultiplier);
					
				 if (last!=null && i+10<pt.trails.size()) {
					 gl.glBegin(GL.GL_TRIANGLE_STRIP);
						 gl.glVertex3f(theLoc.x+width*(float)LookupTable.sinD(theRot.x), theLoc.y+width*(float)LookupTable.cosD(theRot.x), theLoc.z);
						 gl.glVertex3f(theLoc.x, theLoc.y, theLoc.z);
						 gl.glVertex3f(last.x+width*(float)LookupTable.sinD(lastRot.x), last.y+width*(float)LookupTable.cosD(lastRot.x), last.z);
						 gl.glVertex3f(last.x, last.y, last.z);
					gl.glEnd();
				}
				last=theLoc;
				lastRot=theRot;
				gl.glPopMatrix();
				alphaMultiplier-=alphaFactor;
			  }
		}


	}
	
	public class  FlareRenderer implements ParticleRenderer {
		
		float alpha=0.8f;
		public FlareRenderer() {
			
		}
		
		public void render(Particle pt) {
			Flare f = (Flare)pt.renderObjects.get("flare");
			if (f==null) {
				f=new Flare(ogl,p,50);
				pt.renderObjects.put("flare",f);
				f.addElement(0);
				f.addElement(1);
				f.addElement(1);
				f.addElement(1);
			}
			f.update();
			gl.glEnable(GL.GL_TEXTURE_2D);                              // Enable Texture Mapping
			gl.glEnable(GL.GL_BLEND);
			//gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glEnable(GL.GL_ALPHA_TEST);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);   
			gl.glPushMatrix();
				gl.glTranslatef(pt.loc.x, pt.loc.y, pt.loc.z);
				f.render();
			gl.glPopMatrix();	
		}
		
	}
}
