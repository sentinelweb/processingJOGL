package net.robmunro.perform.ol5;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.MotionBlur;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.VideoPlay;
import oscP5.OscIn;
import oscP5.OscP5;
import processing.core.PApplet;
import net.robmunro.perform.Beano140309;
import net.robmunro.perform.ol5.*;
import net.robmunro.test.nehe.TextureReader;
/**
 * Performed at Openlab 5 : cafe oto 25 April, 2009
 * 						Audio patches: mystery/recordMIDI.pd, OL5_composition/main.pd
 * 						Timeline patch: mystery/mystery2.xml
 * 
 * @author robm
 */
public class OpenLab5_240409 extends PApplet {
	GL gl;
	OpenGL ogl ;
	GLU glu;
	Shape shape;
	ParticleSystems ps;
	MotionBlur mblur=null;
	FullScreen fullScreen;
	// OSC 
	OscP5 oscP5_10002 =null; 
	OscP5 oscP5_10001 =null; 
	SpaceJunk sj;
	VideoManager vm ;
	int starTex=-1;
	public void setup() {
		size(800,600, OPENGL);
		//size(1280,1024, OPENGL);
		background(0);
		//fullScreen = new FullScreen(this);
		//fullScreen.startFullscreen();
		
		ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
		
		shape=new Shape(this.gl);
		
		this.sj=new SpaceJunk (this,ogl,200);
		this.sj.setup();
		
		this.vm = new VideoManager(this,ogl);
		//vm.setVideo("/home/robm/media/video/buckrogers/avi/buck_rgr_tweeky_cpk_400x300_ky1.avi");
		this.ps = new ParticleSystems(ogl,this);
		mblur = new MotionBlur(this.gl);
		mblur.clearAccum();
		// osc
		oscP5_10002= new OscP5(	this,	"localhost",	10011,	10002,	"receiveOSC"	);
		
		vm.setVideo("/home/robm/media/video/buckrogers/mpg/"+bucks[1]);
		ogl.loadTexture("star",  "/home/robm/processing/processing-1.0/processingApps/src/resources/robmunro/perform/ol5/star.bmp");
	}
	
	boolean sjExplode=true;
	boolean sjRotate=false;
	boolean sjOn=false;
	int envOscMotion=0;
	boolean rotateParticles=false;
	public void keyPressed() {
		switch (key) {
			case 'z':sjExplode=!sjExplode;break;
			case 'x':sjRotate=!sjRotate;break;
			case 'c':sjOn=!sjOn;break;
			case 'v':envOscMotion++;envOscMotion%=3;break;
			case 'b':vm.setVideo();break;
			case 'n':rotateParticles=!rotateParticles;break;
			//test code
			case 'q':addParticleSys("drm1",10) ;
		}
	}
	
	boolean mblurEnabled=false;
	private float rotate=0f;
	public void draw() {
		background(0);
		rotate+=1;
		//gl.glEnable(GL.GL_TEXTURE_2D);   
		//gl.glBindTexture(GL.GL_TEXTURE_2D,  ogl.getTex("")); 
		gl.glBindTexture(GL.GL_TEXTURE_2D,  0); 
		gl.glPushMatrix();
			if (rotateParticles){
				gl.glRotatef(rotate,1,0,0);
			}
			gl.glTranslatef(0,0,-300);
			
			/*
			 
			//gl.glRotatef(rotate, 0, 0, 1);
			
			// these are just for the HypocycloidMotion - or generally systems that move forward on the z-axis
			gl.glRotatef(-90, 1, 0, 0);
			gl.glRotatef(rotate, 0, 0, 1);
			gl.glTranslatef(0,0,300);
			*/
			
			
			// set acceleration for particles - good for simple motion.
			Vector3D acc = new Vector3D((mouseX-width/2)*0.0001f,(mouseY-height/2)*0.0001f,0);
			//ps.setAcc(acc);
			
			// render particle systems
			ps.render();
		gl.glPopMatrix();
		
		// space junk
		//this.sj.setExpansion(new Vector3D(mouseX/10,mouseY/10,(mouseX+mouseY)/20));
		
		if (sjOn) {	
			gl.glPushMatrix();
			this.sj.draw();
			gl.glPopMatrix();
		}
		
		vm.drawVideo();
		//println(vm.player0.getPosMilli()+"ms");
		if (mblurEnabled) {
			mblur.blur(0.95f);
		}
	}
	
	public void mouseClicked() {
		ps.addSystem(new Vector3D(mouseX-width/2,mouseY-height/2,-100) );
		//vm.player0.setPosPC((float)mouseX/(float)width*100);
	}
	

	public void receiveOSC(OscIn oscIn){
		try{
			if (oscIn.getAddrPattern().equals("/testme")) {
				System.out.println(oscIn.getFloat(0));
			}else if (oscIn.getAddrPattern().equals("/mov/file")) {
				//System.out.println(oscIn.getString(0));
				vm.setVideo(oscIn.getString(0));
			}else if (oscIn.getAddrPattern().equals("/mov/trig")) {
				//System.out.println(oscIn.getInt(0));
				vm.trigger(oscIn.getInt(0));
			}else if (oscIn.getAddrPattern().equals("/mov/end")) {
				//System.out.println(oscIn.getInt(0));
				vm.end(oscIn.getInt(0));
			}else if (oscIn.getAddrPattern().equals("/mov/start")) {
				//System.out.println(oscIn.getFloat(0));
				try {
					vm.setStart(oscIn.getFloat(0));
				} catch (ClassCastException e) {
					vm.setStart(oscIn.getInt(0));
				}
			}else if (oscIn.getAddrPattern().equals("/mov/speed")) {
				
				
			}else if (oscIn.getAddrPattern().equals("/drm/trig")) {
				//System.out.println(oscIn.getInt(0));
				//vm.trigger(oscIn.getInt(0));
				addParticleSys("drm1", oscIn.getInt(0));
			}else if (oscIn.getAddrPattern().equals("/osc/trig")) {
				
				//vm.trigger(oscIn.getInt(0));
				addParticleSys("osc1", oscIn.getInt(0));
			}else if (oscIn.getAddrPattern().equals("/gosc/trig")) {
				
				//vm.trigger(oscIn.getInt(0));
				addParticleSys("gosc1", oscIn.getInt(0));
			}else if (oscIn.getAddrPattern().equals("/click/trig")) {
				
				//vm.trigger(oscIn.getInt(0));
				addParticleSys("click1", oscIn.getInt(0));
			} else if (oscIn.getAddrPattern().equals("/midi1o/note")) {
				if (oscIn.getInt(1)!=0) {
					//addParticleSys("click1", oscIn.getInt(0));
					
				}
			} else if (oscIn.getAddrPattern().equals("/midi2o/note")) {
				//int val = oscIn.getInt(0);
				//float selRangeStart=(val-60f)/30f;
				//sj.setWireFrameColourSelect(selRangeStart);
				if (oscIn.getInt(1)!=0) {
					addParticleSys("ping2", oscIn.getInt(0));
					if (sjRotate) {
						sj.globalRotation.add(new Vector3D(10,0,0));
					}
				}
			} else if (oscIn.getAddrPattern().equals("/midi3o/note")) {
				if (oscIn.getInt(1)!=0) {
					if (sjExplode) {
						sj.setExpansion(sj.getExpansion().add(new Vector3D(5,5,5)));
						if (sj.getExpansion().x>100) {sj.setExpansion(new Vector3D(5,5,5));}
					}
				}
				
			} else if (oscIn.getAddrPattern().equals("/midi4o/note")) {
				if (oscIn.getInt(1)!=0) {
					addParticleSys("drm2", oscIn.getInt(0));
				}
			} 
		} catch(Exception e) {
				println(e.getClass().getName()+":"+e.getMessage()+"-"+oscIn.getAddrPattern());
				e.printStackTrace();
			}
	}
	
	public void addParticleSys(String ind,int value) {
		if (ind.equals("drm1")) {
			Vector3D origin = new Vector3D(random(-200,200),random(-150,150),random(-100,100));
			switch (value){
				//case 10: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new SimpleRenderer(new Vector3D(1,0,0))));break;
				//case 11: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new SimpleRenderer(new Vector3D(1,1,0))));break;
				//case 12: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new SimpleRenderer(new Vector3D(0,0,1))));break;
				//case 13: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new SimpleRenderer(new Vector3D(0.5f,0,1))));break;
				case 10: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				case 11: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				case 12: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				case 13: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				
				// bass drums
				case 20: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new CrazyMotion(0.05f,0.5f,new Vector3D(1,1,1)),ps.new SimpleRenderer(new Vector3D(0,1,0))));break;
				case 21: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new CrazyMotion(0.02f,2f,new Vector3D(1,1,1)),ps.new SimpleRenderer(new Vector3D(0.5f,1,0))));break;
				case 22: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new CrazyMotion(0.01f,3f,new Vector3D(1,1,1)),ps.new SimpleRenderer(new Vector3D(0,1,0.5f))));break;
				case 23: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new CrazyMotion(0.03f,3f,new Vector3D(1,1,1)),ps.new SimpleRenderer(new Vector3D(0.5f,0,1))));break;
			}
		} else if (ind.equals("osc1")) {
			Vector3D origin = new Vector3D(0,0,-400);
			float k = value/3.14f%8;
			//ps.addSystem(ps.new ParticleSystem(1, origin,ps.new RoseMotion(k),ps.new RibbonRenderer(1,true),400));//ps.new RibbonRenderer(1,false)
			switch (envOscMotion){
				case 0:ps.addSystem(ps.new ParticleSystem(1, origin,ps.new EpicycloidMotion(k,30),ps.new RibbonRenderer(1,true),200,400));break;
				case 1:ps.addSystem(ps.new ParticleSystem(1, origin,ps.new HypocycloidMotion(k,30),ps.new RibbonRenderer(1,true),200,400));break;
				case 2:ps.addSystem(ps.new ParticleSystem(1, origin,ps.new RoseMotion(k),ps.new RibbonRenderer(1,true),100,400));break;
				
			}
		}else if (ind.equals("gosc1")) {
			Vector3D origin = new Vector3D(0,(value-60)*5,-400);
			ps.addSystem(ps.new ParticleSystem(2, origin,ps.new RandomAcceleratorMotion(),ps.new RibbonRenderer(1,false),300,400));
		}else if (ind.equals("click1")) {
			Vector3D origin = new Vector3D(0,0,0);
			ps.addSystem(ps.new ParticleSystem(3, origin,ps.new StandardMotion(),ps.new SquareRenderer(),50,200));
		}else if (ind.equals("drm2")) {
			Vector3D origin = new Vector3D(random(-200,200),random(-150,150),random(-100,100));
			switch (value){
				//hats
				case 46: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new CubeRenderer(new Vector3D(1,0,0))));break;
				case 42: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new CubeRenderer(new Vector3D(1,1,0))));break;
				
				// bass drums.
				case 36: ps.addSystem(ps.new ParticleSystem(10, origin,ps.new CrazyMotion(0.05f,0.5f,new Vector3D(1,1,1)),ps.new CubeRenderer(new Vector3D(0,1,0))));break;
			}
		} else if (ind.equals("ping2")) {
			float selRangeStart=(value-60f);
			Vector3D origin = new Vector3D(0,selRangeStart*10-100,-20);
			ps.addSystem(ps.new ParticleSystem(1, origin,ps.new StandardMotion(),ps.new RibbonRenderer(1,false),100,200));
		}
	}
	
	/*
	 //code for loading tex with alpha channel - doesnt work ... still!!!! ahh yoo fucking bastard prick
	  loadTextNeHe("resources/robmunro/perform/ol5/star.bmp");
		 gl.glShadeModel(GL.GL_SMOOTH);   
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);                  // Set The Blending Function For Translucency
        gl.glEnable(GL.GL_BLEND);
        gl.glClearDepth(1.0f);    
	 */
	public void loadTextNeHe(String res) {
		  int textures[] = new int[1]; 
		TextureReader.Texture texture = null;// Create Storage Space For The Texture
        try {
            texture = TextureReader.readTexture(res);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glGenTextures(1, textures, 0);                           // Create One Texture

        // Create Linear Filtered Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
        this.starTex=textures[0];
	}
	public static void main (String[] args) {
		//println( OpenLab5_240409.class.getCanonicalName());
		PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=800,600",OpenLab5_240409.class.getCanonicalName() });
		//PApplet.main(new String[] { OpenLab5_240409.class.getCanonicalName() });
	}
	String[] bucks = {
			"buck_rgr_500yrs_old_cpk_400x300_ky1.mpg.avi",
			"buck_rgr_av_maachine_cpk_400x300_ky1.mpg.avi",
			"buckrogers_buck_rgr_intro_cpk_400x300_ky1.mpg.avi",
			"buckrogers_buck_rgr_ships_cpk_400x300_ky1.mpg.avi",
			"buckrogers_buck_rgr_slumber_ctr_cpk_400x300_ky1.mpg.avi",
			"buckrogers_buck_rgr_tweeky_cpk_400x300_ky1.mpg.avi"
		};
}
