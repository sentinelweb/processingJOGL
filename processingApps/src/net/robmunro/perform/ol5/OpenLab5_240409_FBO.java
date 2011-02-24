package net.robmunro.perform.ol5;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.MotionBlur;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.VideoPlay;
import oscP5.OscIn;
import oscP5.OscP5;
import processing.core.PApplet;
//import sun.swing.SwingUtilities2;
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
public class OpenLab5_240409_FBO extends PApplet {
	GL gl;
	OpenGL ogl ;
	GLU glu;
	Shape shape;
	ParticleSystems ps;
	ParticleSystems psFlare;
	MotionBlur mblur=null;
	FullScreen fullScreen;
	// OSC 
	OscP5 oscP5_10002 =null; 
	OscP5 oscP5_10001 =null; 
	SpaceJunk sj;
	VideoManager vm ;
	int starTex=-1;
	Flare f ;
	public void setup() {
		size(1024,768, OPENGL);
		//size(1280,1024, OPENGL);
		background(0);
		//fullScreen = new FullScreen(this);
		//fullScreen.startFullscreen();
		println( width+"X"+height);
		ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
		
		shape=new Shape(this.gl);
		f = new Flare(ogl,this,300);
		ogl.createFrameBufferObject( "sj", width, height);
		ogl.createFrameBufferObject( "particle", width, height);
		ogl.createFrameBufferObject( "particleFlare", width, height);
		ogl.createFrameBufferObject( "flare", width, height);
		ogl.createFrameBufferObject( "video", width, height);
		ogl.createTexture( "sj", width, height);
		ogl.createTexture( "particle", width, height);
		ogl.createTexture( "particleBlur", width, height);
		ogl.createTexture( "particleFlare", width, height);
		ogl.createTexture( "video", width, height);
		ogl.createTexture( "flare", width, height);
		
		this.sj=new SpaceJunk (this,ogl,200);
		this.sj.setup();
		
		 this.vm = new VideoManager(this,ogl);
		//vm.setVideo("/home/robm/media/video/buckrogers/avi/buck_rgr_tweeky_cpk_400x300_ky1.avi");
		this.ps = new ParticleSystems(ogl,this);
		this.psFlare = new ParticleSystems(ogl,this);
		mblur = new MotionBlur(this.gl);
		mblur.clearAccum();
		// osc
		oscP5_10002= new OscP5(	this,	"localhost",	10011,	10002,	"receiveOSC"	);
		//String base = "/home/robm/media/video/buckrogers/mpg/";
		String base = "/mnt/home/robm/media/video/buckrogers/mpg/";
		vm.setVideo(base+bucks[1]);
		ogl.loadJTexture("sun", "/resources/robmunro/test/flare/sun.png", true);
		ogl.loadJTexture("crown_inv", "/resources/robmunro/test/flare/crown_inv.png", true);
		ogl.loadJTexture("square", "/resources/robmunro/test/flare/square.png", true);
		ogl.loadJTexture("star", "/resources/robmunro/test/flare/star.png", true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				OL5ControlFrame ol5ctl = new OL5ControlFrame();
				ol5ctl.setVisible(true);
			}
		});
		
	}
	
	boolean sjExplode=true;
	boolean sjRotate=false;
	boolean sjOn=false;
	float sjAlpha=0;
	int envOscMotion=0;
	boolean rotateParticles=false;
	boolean flareOn=false;
	float flareAlpha=0;
	public void keyPressed() {
		switch (key) {
			case 'z':sjExplode=!sjExplode;break;
			case 'x':sjRotate=!sjRotate;break;
			case 'c':sjOn=!sjOn;break;
			case 'v':envOscMotion++;envOscMotion%=3;break;
			 case 'b':vm.setVideo();break;
			case 'n':rotateParticles=!rotateParticles;break;
			case 'm':flareOn=!flareOn;break;
			//test code
			case 'q':addParticleSys("drm1",10) ;break;
			case 'w':addParticleSys("osc1",(int)random(20,100)) ;break;
			case 'e':addParticleSys("ping2",65) ;break;
			 case 'r':vm.setStart(0);break;
			case 't':trigger("sjExpand",60);break;
			case 'y':addParticleSys("drm1",20+(int)random(4));break;
		}
	}
	
	boolean mblurEnabled=false;
	private float rotate=0f;
	public void draw() {
		//background(0);
		rotate+=1;
		//gl.glEnable(GL.GL_TEXTURE_2D);   
		//gl.glBindTexture(GL.GL_TEXTURE_2D,  ogl.getTex("")); 
		gl.glBindTexture(GL.GL_TEXTURE_2D,  0); 
		gl.glUseProgramObjectARB(0);
		ogl.setFB("particle");
		gl.glPushMatrix();
			
			if (rotateParticles){
				gl.glRotatef(rotate,1,0,0);
			}
			gl.glTranslatef(0,0,-600);
			/*
			//gl.glRotatef(rotate, 0, 0, 1);
			
			// these are just for the HypocycloidMotion - or generally systems that move forward on the z-axis
			gl.glRotatef(-90, 1, 0, 0);
			gl.glRotatef(rotate, 0, 0, 1);
			gl.glTranslatef(0,0,300);
			*/
			// set acceleration for particles - good for simple motion.
			//Vector3D acc = new Vector3D((mouseX-width/2)*0.0001f,(mouseY-height/2)*0.0001f,0);
			//ps.setAcc(acc);
			// render particle systems
			ps.render();
		gl.glPopMatrix();
		ogl.snapFBToTex("particle","particle",GL.GL_RGBA);
		ogl.snapFBToTex("particle","particleBlur",GL.GL_RGBA);

		// make flare particles  //////////////////////////////////////////////////////////////
		ogl.setFB("particleFlare");
		gl.glPushMatrix();
			if (rotateParticles){
				gl.glRotatef(rotate,1,0,0);
			}
			gl.glTranslatef(0,0,-600);
			psFlare.render();
		gl.glPopMatrix();
		ogl.snapFBToTex("particleFlare","particleFlare",GL.GL_RGBA);
		
		// make spacejunk  //////////////////////////////////////////////////////////////
		ogl.setFB("sj");
		if (sjAlpha>0) {	
			gl.glPushMatrix();
			this.sj.draw();
			gl.glPopMatrix();
		}
		ogl.snapFBToTex("sj","sj",GL.GL_RGBA);
		gl.glUseProgramObjectARB(0);
		// make video  //////////////////////////////////////////////////////////////
		ogl.setFB("video");
		gl.glPushMatrix();
		vm.drawVideo();
		gl.glPopMatrix();
		ogl.snapFBToTex("video","video",GL.GL_RGBA);
		gl.glBindTexture(GL.GL_TEXTURE_2D,  0); 
		// make flare  //////////////////////////////////////////////////////////////
		ogl.setFB("flare");
		if (flareAlpha>0) {
			gl.glPushMatrix();
				f.update();
				gl.glTranslatef(0,0,-300);
				f.render();
			gl.glPopMatrix();
		}
		ogl.snapFBToTex("flare","flare",GL.GL_RGBA);
		// write to main buffer //////////////////////////////////////////////////////////////
		ogl.clearFB();
		gl.glColor4f(1, 1, 1, 0.4f);
		int wid = 3;
		int z=-700;
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(-wid,-wid,z),true); 
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(wid,wid,z)); 
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(-wid,wid,z)); 
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(wid,-wid,z)); 
		gl.glColor4f(1, 1, 1, 1);
		if (sjOn && sjAlpha<1) {sjAlpha+=0.01f;} 
		if (!sjOn && sjAlpha>0) {sjAlpha-=0.01f;} 
		gl.glColor4f(1, 1, 1, sjAlpha);
		ogl.drawFBOTextures(new String[]{"sj"},new Vector3D(0,0,z*1.3f));
		
		if (flareOn && flareAlpha<1) {flareAlpha+=0.01f;} 
		if (!flareOn && flareAlpha>0) {flareAlpha-=0.01f;} 
		gl.glColor4f(1, 1, 1, flareAlpha);
		ogl.drawFBOTextures(new String[]{"flare"},new Vector3D(0,0,z));
		
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushMatrix();
		gl.glRotatef(180, 1, 0, 0);
		ogl.drawFBOTextures(new String[]{"video"},new Vector3D(0,0,-z));    
		gl.glPopMatrix();
		ogl.drawFBOTextures(new String[]{"particle","particleFlare","particleFlare","particleFlare"},new Vector3D(0,0,z));
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
				trigger("sjExpand",oscIn.getInt(0));
				
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
				case 10: psFlare.addSystem(psFlare.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				case 11: psFlare.addSystem(psFlare.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				case 12: psFlare.addSystem(psFlare.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				case 13: psFlare.addSystem(psFlare.new ParticleSystem(10, origin,ps.new StandardMotion(),ps.new FlareRenderer()));break;
				
				// bass drums
				case 20: ps.addSystem(ps.new ParticleSystem(30, origin,ps.new CrazyMotion(0.05f,0.5f,new Vector3D(1,1,1)),ps.new TextureRenderer(new Vector3D(0,1,0),ogl.getJText("sun"),20)));break;
				case 21: ps.addSystem(ps.new ParticleSystem(30, origin,ps.new CrazyMotion(0.02f,2f,new Vector3D(1,1,1)),ps.new TextureRenderer(new Vector3D(1,1,0),ogl.getJText("star"),20)));break;
				case 22: ps.addSystem(ps.new ParticleSystem(30, origin,ps.new CrazyMotion(0.01f,3f,new Vector3D(1,1,1)),ps.new TextureRenderer(new Vector3D(1,0.5f,0),ogl.getJText("square"),20)));break;
				case 23: ps.addSystem(ps.new ParticleSystem(30, origin,ps.new CrazyMotion(0.03f,3f,new Vector3D(1,1,1)),ps.new TextureRenderer(new Vector3D(0,0.5f,1),ogl.getJText("crown_inv"),20)));break;
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
			Vector3D acc=new Vector3D(random(-0.01f,0.01f),random(-0.01f,0.01f),0);
			ps.addSystem(ps.new ParticleSystem(1, origin,ps.new StandardMotion(acc),ps.new RibbonRenderer(1,true),100,200));
		} 
	}
	public void trigger(String ind,int value) {
		if (ind.equals("sjExpand")) {
			if (value!=0) {
				if (sjExplode) {
					sj.setExpansion(sj.getExpansion().add(new Vector3D(5,5,5)));
					if (sj.getExpansion().x>100) {sj.setExpansion(new Vector3D(5,5,5));}
				}
			}
		}
	}
	public static void main (String[] args) {
		//println( OpenLab5_240409.class.getCanonicalName());
		PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=1024,768",OpenLab5_240409_FBO.class.getCanonicalName() });
		//PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=1280,1024",OpenLab5_240409_FBO.class.getCanonicalName() });
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
	public class OL5ControlFrame extends JFrame {
		public OL5ControlFrame() {
			
			
			Container container = getContentPane();
			container.setLayout(new GridLayout(-1,1));
			setSize(new Dimension(240, 320));
			JButton but = new JButton("flare");
			this.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								flareOn=!flareOn;
								((JButton)e.getSource()).setText("flare :"+(flareOn?"*":""));
							} catch(Exception ex) {}
						};
					}
			);
			but = new JButton("sj");
			this.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								sjOn=!sjOn;
								((JButton)e.getSource()).setText("sj :"+(sjOn?"*":""));
							} catch(Exception ex) {}
						};
					}
			);
			but = new JButton("envOscMotion");
			this.add(but);
			but.addActionListener(
					new ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								envOscMotion++;envOscMotion%=3;
								((JButton)e.getSource()).setText("envOscMotion : "+envOscMotion);
							} catch(Exception ex) {}
						};
					}
			);
		
		but = new JButton("drm1");
		this.add(but);
		but.addMouseListener(
				new MouseListener() {
					public void mousePressed(MouseEvent e) {
						try {
							addParticleSys("drm1",10) ;
							((JButton)e.getSource()).setText("drm1 : ");
						} catch(Exception ex) {}
						
					}

					public void mouseClicked(MouseEvent e) {	}
					public void mouseEntered(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
				}
		);
		but = new JButton("osc1");
		this.add(but);
		but.addMouseListener(
				new MouseListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						
					}

					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void mousePressed(MouseEvent e) {
						try {
							addParticleSys("osc1",(int)random(20,100)) ;
							((JButton)e.getSource()).setText("osc1 : ");
						} catch(Exception ex) {}
						
					}

					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub
						
					};
				}
		);
		but = new JButton("ping2");
		this.add(but);
		but.addActionListener(
				new ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							addParticleSys("ping2",65) ;
							((JButton)e.getSource()).setText("ping2 : ");
						} catch(Exception ex) {}
					};
				}
		);but = new JButton("vm");
		this.add(but);
		but.addActionListener(
				new ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							 vm.setStart(0);
							((JButton)e.getSource()).setText("vm : ");
						} catch(Exception ex) {}
					};
				}
		);but = new JButton("sjExpand");
		this.add(but);
		but.addActionListener(
				new ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							trigger("sjExpand",60);
							((JButton)e.getSource()).setText("sjExpand : ");
						} catch(Exception ex) {}
					};
				}
		);but = new JButton("drm1");
		this.add(but);
		but.addActionListener(
				new ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							addParticleSys("drm1",20+(int)random(4));
							((JButton)e.getSource()).setText("drm1 : "+envOscMotion);
						} catch(Exception ex) {}
					};
				}
		);
		}
	}
}
