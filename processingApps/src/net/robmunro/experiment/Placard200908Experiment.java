package net.robmunro.experiment;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import org.gstreamer.ClockTime;
import org.gstreamer.State;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import oscP5.OscIn;
import oscP5.OscP5;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.MotionBlur;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.VideoPlay;
import net.robmunro.lib.tools.BufferLoader;
import net.robmunro.lib.tools.FullScreen;


import processing.core.PApplet;

public class Placard200908Experiment extends PApplet {
	
	//VideoPlay player;
	GL gl;
	OpenGL ogl ;
	GLU glu;
	
	Shape shape;
	Texture whiteTex ;
	MotionBlur mblur=null;
	
	OscP5 oscP5_10003 =null; 
	OscP5 oscP5_10001 =null; 
	FullScreen f = new FullScreen(this);
	
	SonicObject so ;
	RingObject ro1 ;
	SpaceJunk sj;
	Line3D sjSpace = new Line3D(new Vector3D(0,0,0));
	Line3D sjPos = new Line3D(new Vector3D(0,0,0));
	Line3D sjRotationVelocity = new Line3D(new Vector3D(0,0,0));
	VideoPanel vp;
	Line3D vpPos = new Line3D(new Vector3D(0,0,0));
	ParticleSystems ps;
	GlassGLSL gglsl;
	FFTPlot fft;
	SndData sndData =  new SndData();
	
	Vector3D start = new Vector3D(0,0,-150);
	Randomiser r = new Randomiser();
	boolean mblurEnabled = false;
	public Placard200908Experiment() {
		super();
	}
	
	public void setup() {
		 size(1200, 800, OPENGL);
		//size(800,600, OPENGL);
		 ogl=new OpenGL(this);
		 this.gl=ogl.gl;
		 glu = new GLU();
		 shape = new Shape(gl);
		 so = new SonicObject( start, new Vector3D(0,0,0), new Vector3D(0,1,1),sndData);
		 ro1 = new RingObject(  new Vector3D(50,0,0), new Vector3D(50,80,0), new Vector3D(0,0,0));
		 sj=new SpaceJunk(200);
		 sj.enabled=true;
		 ps=new ParticleSystems();
		 gglsl = new GlassGLSL();
		 fft = new FFTPlot(sndData,new Vector3D(-40,40,-400));
		 //gglsl.enabled=true;
		 oscP5_10003= new OscP5(	this,	"localhost",	10023,	10003,	"receiveOSC"	);
		 oscP5_10001= new OscP5(	this,	"localhost",	10021,	10002,	"receiveOSC1"	);
		 mblur=new MotionBlur(gl);
		 mblur.clearAccum();
		  
		  try {
			whiteTex = TextureIO.newTexture(ImageIO.read(Placard200908Experiment.class.getResource("/resources/robmunro/white.gif")), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 f.startFullscreen(1); // 1 = my second screen
		 //player = new VideoPlay(this, "/home/robm/media/video/60_com/avi/TONYTIGER.avi");
		 //player.loop();
		 vp  = new VideoPanel(this,"/home/robm/media/video/60_com/avi/TONYTIGER.avi",new Vector3D( start.x+70, start.y, start.z-20 ));
	}
	
	int ctr = 0;
	
	public void draw() {
		ctr++;
		background(0);
		gl.glColor3f(1,1,1);
		gl.glBegin(GL.GL_LINES);
	  		gl.glVertex3f(-400, 0, -100);
	  		gl.glVertex3f(400, 0, -100);
	  	gl.glEnd();
		gl.glTranslatef(start.x,start.y,start.z);
		
		//gl.glActiveTexture(GL.GL_TEXTURE0);
		gglsl.set();
		// render sonic object
		whiteTex.bind();
		whiteTex.enable();
		so.render(ctr);
		
		// render fft
		//fft.render();
		
		ro1.render(ctr);
		// render videoplay object
		//vp.render();
		
		whiteTex.bind();
		whiteTex.enable();
		
		// render spaceJunk object
		sj.space=sjSpace.getPos();
		sj.pos=sjPos.getPos();
		sj.rotationVel=sjRotationVelocity.getPos();
		sj.render();
		
		// render particle systems
		ps.render();
		if (mblurEnabled) {
			mblur.blur(0.8f);
		}
	}
	
	class Randomiser{
		float getX() {	return (float)(Math.random()*400-200);		}
		float getY() {	return (float)(Math.random()*400-200);		}
		float getZ() {	return (float)(Math.random()*400-200);		}
		Vector3D rPoint() {return new Vector3D(this.getX(),this.getY(),this.getZ());}
	}
	public void receiveOSC1(OscIn oscIn){
		receiveOSC( oscIn);
	
	}
	public void receiveOSC(OscIn oscIn){
		try{
			if (oscIn.getAddrPattern().equals("/fft")) {
				sndData.fftLoader.process(oscIn);
			} else if (oscIn.getAddrPattern().equals("/snd")) {
				sndData.sndLoader.process(oscIn);
			} else if (oscIn.getAddrPattern().equals("/env")) {
				try {sndData.env =  oscIn.getFloat(0);}
				catch (Exception e ) {sndData.env =  oscIn.getInt(0);} 
			} else if (oscIn.getAddrPattern().equals("/video/pos")) {// the position from the start in milliseconds.
				float pos =  oscIn.getFloat(0);
				int ipos = (int)pos;
				vp.player.getPlayer().seek(ClockTime.fromMillis((long)pos));
			} else if (oscIn.getAddrPattern().equals("/video/file")) {// the position from the start in milliseconds.
				vp.player.doStop();
				vp.player.stop();
				vp.player.getPlayer().setState(State.NULL);
				vp.player.getPlayer().setInputFile(new File(oscIn.getString(0)));
				vp.player.loop();
				System.out.println("vp file:"+oscIn.getString(0));
			} else if (oscIn.getAddrPattern().equals("/video/play")) {
				vp.player.loop();
				System.out.println("vp play:");
			} else if (oscIn.getAddrPattern().equals("/video/stop")) {
				vp.player.stop();
				System.out.println("vp stop:");
			} else if (oscIn.getAddrPattern().equals("/video/pos")) {// the position from the start in milliseconds.
				float pos =  oscIn.getFloat(0);
				int ipos = (int)pos;
				vp.player.getPlayer().seek(ClockTime.fromMillis((long)pos));
			} else if (oscIn.getAddrPattern().equals("/particle/new")) {
				Vector3D p =r.rPoint();
				p.z=-200;
				ps.addSystem(p);
			} else if (oscIn.getAddrPattern().equals("/particle/sweep")) {
				Vector3D p =r.rPoint();
				p.z=-200;
				Line3D l3d = new Line3D(p);
				int count = 5;
				l3d.setTarget(r.rPoint(), count);
				for (int i=0; i < count; i++) {
					ps.addSystem(l3d.getPos());
				}
			} else if (oscIn.getAddrPattern().equals("/ro1/new")) {
				ro1.newRing();
			} else if (oscIn.getAddrPattern().equals("/ro1/rot")) {
				ro1.rotationVel=new Vector3D(OscTools.getFloat(oscIn,0),OscTools.getFloat(oscIn,1),OscTools.getFloat(oscIn,2));
			} else if (oscIn.getAddrPattern().equals("/sj/space")) {
				sjSpace.setTarget(new Vector3D(OscTools.getFloat(oscIn,0),OscTools.getFloat(oscIn,1),OscTools.getFloat(oscIn,2)), OscTools.getFloat(oscIn,3));
			}else if (oscIn.getAddrPattern().equals("/sj/pos")) {
				sjPos.setTarget(new Vector3D(OscTools.getFloat(oscIn,0),OscTools.getFloat(oscIn,1),OscTools.getFloat(oscIn,2)), OscTools.getFloat(oscIn,3));
			}else if (oscIn.getAddrPattern().equals("/sj/rotVel")) {
				sjRotationVelocity.setTarget(new Vector3D(OscTools.getFloat(oscIn,0),OscTools.getFloat(oscIn,1),OscTools.getFloat(oscIn,2)), OscTools.getFloat(oscIn,3));
			}else if (oscIn.getAddrPattern().equals("/sj/on")) {
				sj.enabled = (oscIn.getInt(0)==1);
				System.out.println("sj:"+sj.enabled);
			}else if (oscIn.getAddrPattern().equals("/glass/on")) {
				gglsl.enabled = (oscIn.getInt(0)==1);
				System.out.println("glass:"+gglsl.enabled);
			}else if (oscIn.getAddrPattern().equals("/fft/on")) {
				fft.enabled = (oscIn.getInt(0)==1);
				System.out.println("fft:"+fft.enabled);
			}else if (oscIn.getAddrPattern().equals("/mblur/on")) {
				mblurEnabled = (oscIn.getInt(0)==1);
			}else if (oscIn.getAddrPattern().equals("/vp/pos")) {
				vp.mover.setTarget(new Vector3D(OscTools.getFloat(oscIn,0),OscTools.getFloat(oscIn,1),OscTools.getFloat(oscIn,2)), OscTools.getFloat(oscIn,3));
			}else if (oscIn.getAddrPattern().equals("/vp/alpha")) {
				vp.alpha.setTarget(new Vector3D(OscTools.getFloat(oscIn,0),0,0), OscTools.getFloat(oscIn,1));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class OscTools{
		static float getFloat(OscIn oscIn,int pos){ 
			String types = oscIn.getTypetag();//oscIn.getTypes();
			if (types.length()>pos ){
				if( types.charAt(pos)=='f' ) {return oscIn.getFloat(pos);}
				if( types.charAt(pos)=='i' ) {return (float)oscIn.getInt(pos);}
			}
			return 0f;
		}
	}
	
	class SndData {
		BufferLoader sndLoader=new BufferLoader();
		BufferLoader fftLoader=new BufferLoader();
		float env = 0f;
	}
	
	class VideoPanel {
		VideoPlay player;
		Vector3D pos;
		Line3D mover;
		Line3D alpha;
		
		public VideoPanel(PApplet p, String src, Vector3D pos) {
			super();
			this.player = new VideoPlay(p, src);
			this.player.loop();
			this.pos = pos;
			mover=new Line3D(pos);
			alpha=new Line3D(new Vector3D(1,0,0));
		}
		
		public void render() {
			float alphaVal = alpha.getPos().x;
			if (alphaVal>0) {
				this.pos=mover.getPos();
				gl.glPushMatrix();
					gl.glColor4f( 1f, 1f, 1f, alphaVal );
					gl.glTranslatef( pos.x,pos.y,pos.z );
					player.setTexture(); 
					shape.drawSquare( 60, 40 );
				gl.glPopMatrix();
			}
		}
	}
	
	class Line3D {
		Vector3D step;
		Vector3D pos;
		Vector3D target;
		Line3D( Vector3D pos ) {
			this.pos=pos;
		}
		
		public Vector3D getPos() {
			update();
			return pos;
		}
		
		public  void setTarget( Vector3D target, float numSteps ) {
			this.target=target;
			this.step=new Vector3D(
					(target.x-pos.x)/numSteps,
					(target.y-pos.y)/numSteps,
					(target.z-pos.z)/numSteps
				);
		}
		
		public  void setTarget( Vector3D target, Vector3D step ) {
			this.target=target;
			this.step=step;
		}
		
		public void update() {
			if (target!=null && step !=null) {
				if ((step.x>0 && pos.x<target.x) || (step.x<0 && pos.x>target.x)) {pos.x+=step.x;} else {pos.x=target.x;}
				if ((step.y>0 && pos.y<target.y) || (step.y<0 && pos.y>target.y)) {pos.y+=step.y;} else {pos.y=target.y;}
				if ((step.z>0 && pos.z<target.z) || (step.z<0 && pos.z>target.z)) {pos.z+=step.z;} else {pos.z=target.z;}
			}
		}
		
	}
	
	class RingObject {
		Vector3D start = new Vector3D(0,0,-150);
		Vector3D rotation = new Vector3D(0,0,0);
		Vector3D rotationVel = new Vector3D(1,2,3);
		ArrayList<RingEmitter> rings= new ArrayList<RingEmitter>();
		public RingObject(Vector3D start, Vector3D rotation,	Vector3D rotationVel) {
			super();
			this.start = start;
			this.rotation = rotation;
			this.rotationVel = rotationVel;
		}
		void newRing() {
			rings.add(new RingEmitter(2f, 90f, 1f, 
					new Vector3D(0f,0f,0f), //pos
					new Vector3D(0f,0f,0f), //vel
					new Vector3D(0f,0f,0.0f), //acc
					new Vector3D(0,0,0), //rotationVel deg/step
					new Vector3D(.5f+(float)Math.random()*0.5f,0f,.5f+(float)Math.random()*0.5f), //colour
					1f, //alpha
					440 // # steps
				));
			
		}
		
		private void render(int ctr) {
			whiteTex.bind();
			whiteTex.enable();
			gl.glPushMatrix();
				gl.glTranslatef(this.start.x,this.start.y,this.start.z);
			
				rotation.add(rotationVel);
				gl.glRotatef(rotation.x,1,0,0);
				gl.glRotatef(rotation.y,0,1,0);
				gl.glRotatef(rotation.z,0,0,1);
				
				for (int i=0; i<rings.size(); i++) {
					rings.get(i).render();
					if (rings.get(i).dead()) {
						rings.remove(i);
						i--;
					}
				}
				
			gl.glPopMatrix();
		}
	}
	
	class FFTPlot{
		boolean enabled=false;
		SndData sndData;
		Vector3D start = new Vector3D(0,0,-150);
		Vector3D rotation = new Vector3D(0,0,0);
		Vector3D rotationVel = new Vector3D(0,0,0);
		FFTPlot (SndData sndData,Vector3D start) {
			this.sndData=sndData;
			this.start = start;
		}
		
		private void render() {
			if (enabled) {
				gl.glPushMatrix();
					rotation.add(rotationVel);
					gl.glRotatef(rotation.x,1,0,0);
					gl.glRotatef(rotation.y,0,1,0);
					gl.glRotatef(rotation.z,0,0,1);
					//plot
					gl.glPushMatrix();
						gl.glColor4f(1f,.5f,0f,.5f);
						gl.glTranslatef(0,-20,0);
						shape.plotData(sndData.fftLoader.getData(), 60, 40,0.5f,true);
					gl.glPopMatrix();
				gl.glPopMatrix();
			}
		}
	}
	
	class SonicObject {
		Vector3D start = new Vector3D(0,0,-150);
		Vector3D rotation = new Vector3D(0,0,0);
		Vector3D rotationVel = new Vector3D(1,2,3);
		SndData sndData;
		ArrayList<RingEmitter> rings= new ArrayList<RingEmitter>();
		
		public SonicObject(Vector3D start, Vector3D rotation,	Vector3D rotationVel,SndData sndData) {
			super();
			this.start = start;
			this.rotation = rotation;
			this.rotationVel = rotationVel;
			this.sndData = sndData;
		}

		private void render(int ctr) {
			gl.glPushMatrix();
				rotation.add(rotationVel);
				gl.glRotatef(rotation.x,1,0,0);
				gl.glRotatef(rotation.y,0,1,0);
				gl.glRotatef(rotation.z,0,0,1);
				
				if (ctr%50==0) {
					rings.add(new RingEmitter(2f, 90f*sndData.env, 1f, 
						new Vector3D(0f,0f,0f), //pos
						new Vector3D(0f,0f,0.1f), //vel
						new Vector3D(0f,0f,0.0f), //acc
						new Vector3D(0,0,0), //rotationVel deg/step
						new Vector3D(.5f+(float)Math.random()*0.5f,0f,.5f+(float)Math.random()*0.5f), //colour
						1f, //alpha
						440 // # steps
					));
				}
				for (int i=0; i<rings.size(); i++) {
					rings.get(i).render();
					if (rings.get(i).dead()) {
						rings.remove(i);
						i--;
					}
				}
				//plot
				gl.glPushMatrix();
					gl.glColor4f(1f,.5f,0f,.5f);
					gl.glTranslatef(0,-20,0);
					shape.plotData(sndData.sndLoader.getData(), 90, 40,0.5f,true);
				gl.glPopMatrix();
			gl.glPopMatrix();
		}
	}
	
	class RingEmitter{
		float initialSize;
		float endSize;
		float width;
		Vector3D pos;
		Vector3D velocity;
		Vector3D accel;
		Vector3D rotation;
		
		Vector3D colour;
		float alpha;
		private float initalAlpha;
		int lifeLength;
		private float size;
		private int step = 0;
		Vector3D rotationStep ;
		public RingEmitter(float initialSize, float endSize, float width,
				Vector3D initialPos, Vector3D velocity, Vector3D accel, Vector3D rotation,
				Vector3D colour, float alpha,int lifeLength) {
			super();
			this.initialSize = initialSize;
			this.endSize = endSize;
			this.width=width;
			this.pos = initialPos;
			this.velocity = velocity;
			this.accel = accel;
			this.rotation = rotation;
			this.colour = colour;
			this.alpha = alpha;
			this.initalAlpha = alpha;
			this.lifeLength=lifeLength;
			this.size = initialSize;
			this.rotationStep = new Vector3D(0,0,0);
			
		}
		
		void update(){
			this.pos.add(this.velocity);
			this.velocity.add(this.accel);
			this.size+=(this.endSize-this.initialSize)/this.lifeLength;
			rotationStep.add(rotation);
			this.alpha = this.initalAlpha- this.initalAlpha * ((float)step)/(float)lifeLength;
			step++;
		}
		
		void render () {
			update();
			gl.glPushMatrix();
				gl.glColor4f( this.colour.x, this.colour.y, this.colour.z, this.alpha );
				gl.glTranslatef( pos.x, pos.y, pos.z );
				gl.glPushMatrix();
					gl.glRotatef(this.rotationStep.x,1,0,0);
					gl.glRotatef(this.rotationStep.y,0,1,0);
					gl.glRotatef(this.rotationStep.z,0,0,1);
					shape.drawTorus( this.width,this.size, 20,20); //this.size+
				gl.glPopMatrix();
			gl.glPopMatrix();
		}
		boolean dead() {
			return step>lifeLength;
		}
	}
	
	class SpaceJunk {
		//array for all cubes
		Cube[] cubes ;
		Vector3D rotation;
		Vector3D rotationVel;
		boolean enabled;
		Vector3D pos=new Vector3D();
		Vector3D space=new Vector3D();
		public SpaceJunk(int limit) {
			super();
			
			this.cubes = new Cube[limit];
			for (int i = 0; i< cubes.length; i++){
			    cubes[i] = new Cube(
			    		Math.round(random(-100, 100)), 
			    		Math.round(random(-100, 100)), 
			    		5,//Math.round(random(-100, 100)),
			    		Math.round(random(-10, 10)), 
			   			Math.round(random(-10, 10)), 
						Math.round( random(-10, 10))
			    );
			    cubes[i].rFact = (float)Math.random()/10f;
			}
			this.rotation = new Vector3D(0,0,0);
			this.rotationVel = new Vector3D(0,0,1);
			enabled=false;
		}

		void render () {
			if (enabled) {
				gl.glPushMatrix();
				rotation.add(rotationVel);
				gl.glRotatef(this.rotation.x,1,0,0);
				gl.glRotatef(this.rotation.y,0,1,0);
				gl.glRotatef(this.rotation.z,0,0,1);
				gl.glTranslatef( pos.x, pos.y, pos.z );
				
				for (int i = 0; i< cubes.length; i++){
					  gl.glPushMatrix();
						  gl.glTranslatef( cubes[i].shiftX*space.x,  cubes[i].shiftY*space.y, cubes[i].shiftZ*space.z);
						  gl.glColor4f(cubes[i].shiftX*.5f+.5f,  cubes[i].shiftY*.5f+.5f, cubes[i].shiftZ*.5f+.5f,.1f);
						  cubes[i].drawCube();
					  gl.glPopMatrix();
				  }
				 gl.glPopMatrix();
			}
		}
	}
	
	//simple Cube class, based on Quads
	class Cube {

	  //properties
	  int w, h, d;
	  int shiftX, shiftY, shiftZ;

	  float rotate=0f;
	  float rFact=5f;
	  int listId=0;
	  int sDetail=50;
	  //constructor
	  Cube(int w, int h, int d, int shiftX, int shiftY, int shiftZ){
	    this.w = w;
	    this.h = h;
	    this.d = d;
	    this.shiftX = shiftX;
	    this.shiftY = shiftY;
	    this.shiftZ = shiftZ;
	    initCube();
	    rFact=50f+(float)Math.random()*20f;
	  }

	  void drawCube(){
		  rotate+=rFact*10f;
		  gl.glPushMatrix();
			  gl.glRotatef( rotate, 0,10,0);
			  gl.glBegin(GL.GL_QUADS);
			  		gl.glCallList(this.listId);
			  gl.glEnd();
		  gl.glPopMatrix();
	  }
	  
	  void initCube(){
		  this.listId = gl.glGenLists(1);
		  gl.glNewList(this.listId, GL.GL_COMPILE_AND_EXECUTE);
		 	  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, h + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 
	
			  //back face
			  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
			  gl.glVertex3i(w + shiftX, -h/2 + shiftY, d + shiftZ); 
			  gl.glVertex3i(w + shiftX, h + shiftY, d + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, h + shiftY, d + shiftZ);
	
			  //left face
			  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, h + shiftY, d + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 
	
			  //right face
			  gl.glVertex3i(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, -h/2 + shiftY, d + shiftZ); 
			  gl.glVertex3i(w + shiftX, h + shiftY, d + shiftZ); 
			  gl.glVertex3i(w + shiftX, h + shiftY, -d/2 + shiftZ); 
	
			  //top face
			  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, -h/2 + shiftY, d + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
	
			  //bottom face
			  gl.glVertex3i(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, h + shiftY, -d/2 + shiftZ); 
			  gl.glVertex3i(w + shiftX, h + shiftY, d + shiftZ); 
			  gl.glVertex3i(-w/2 + shiftX, h + shiftY, d + shiftZ); 
		  gl.glEndList();
	  }
	  
	  
	}
	
	class GlassGLSL{
		boolean enabled=false;
		Vector3D lightPos = new Vector3D( 0f, 0f,  5f);
		GlassGLSL() {
			super();
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
		}
		
		void set() {
			GLSLProgram gProgram = ogl.getProgram("glass");
			boolean vertexShaderEnabled =(gProgram!=null);// false;//
			  if (this.enabled && vertexShaderEnabled) {
			  
			     gl.glUseProgramObjectARB(gProgram.getProgramObject());
			     
			     gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), lightPos.x,lightPos.y,lightPos.z);
			     
			   // gl.glUniform4fARB(gProgram.getUniformId("GlassColor"), mouseX/(float)width, mouseY/(float)height, 0.6f, 0.35f);
			     gl.glUniform4fARB(gProgram.getUniformId("SpecularColor1"),  .1f, 0.1f, 0.1f, 1f);
			     gl.glUniform4fARB(gProgram.getUniformId("SpecularColor2"),  .1f, 0.1f, 0.1f, 1f);
			     gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor1"),2f) ;
			     gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor2"),2f);
			 }
		}
	}
	/* ************************************************************
	 * Particle system.
	 ************************************************************ */
	class ParticleSystems{
		ArrayList<ParticleSystem> psystems;

		public ParticleSystems() {
			super();
			psystems = new ArrayList<ParticleSystem>();
		}
		
		void render(){
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
		
		void addSystem(Vector3D origin) {
			psystems.add(new ParticleSystem((int)random(45f,75f), origin));//
		}
		
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
		    gl.glColor4f(1f, (float)timer/TIMER_LEN, 0, 0.5f);//(float)timer/TIMER_LEN
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
		    vel = new Vector3D(random(-1,1),random(-1,1),0);
		    loc = l.copy();
		    r = 10.0f;
		    timer = 300.0f;
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
	
	// run as app to use environment variable DISPLAY=:0.1
	// this doesnt work cos of a bug in 148 - should work in 149, set the environment variable DISPLAY=:0.1 to open on second screen.
	public static void main (String[] args) {
		//PApplet.main(new String[] { Placard200908.class.getCanonicalName() });
		Placard200908Experiment p = new Placard200908Experiment();
		//p.getGraphicsConfiguration().;
		//p.set
		//GLCapabilities capabilities = new GLCapabilities();
		//capabilities.setSampleBuffers(true);
	    //capabilities.setNumSamples(2);

		//GLDrawableFactory.getFactory().chooseGraphicsConfiguration(capabilities,null,null);
		/*
		GLCapabilities capabilities = new GLCapabilities();
		GLDrawableFactory factory = GLDrawableFactory.getFactory();
		GLDrawable drawable = factory.getGLDrawable(parent, capabilities, null);
	    GLContext  context = drawable.createContext(null);
		*/
		//p.frame.pack();
		p.init();
		//p.setup();
		
	}
}
