package net.robmunro.perform.ol5;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

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
public class OpenNight_221009_FBO extends PApplet {
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
	//Vector<Vector3D> vec =new  Vector<Vector3D>();
	//int vecIndex=0;
	//boolean vecForward=true;
	//SpaceJunk sj;
	//VideoManager vm ;
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
		ogl.createFrameBufferObject( "particle", width, height);
		//ogl.createFrameBufferObject( "julia", width, height);
		ogl.createFrameBufferObject( "particleFlare", width, height);
		ogl.createTexture( "particle", width, height);
		ogl.createTexture( "particleBlur", width, height);
		ogl.createTexture( "particleFlare", width, height);
		//ogl.createTexture( "julia", width, height);
		/*
		try {
			ogl.makeProgram(
					"julia",
					new String[] {},
					new String[] {"point"}, //"alpha"
					ogl.loadGLSLShaderVObject(	"resources/robmunro/julia/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/julia/julia.frag"	)
			);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getPath();
		*/
		this.ps = new ParticleSystems(ogl,this);
		this.psFlare = new ParticleSystems(ogl,this);
		mblur = new MotionBlur(this.gl);
		mblur.clearAccum();
		// osc
		oscP5_10002= new OscP5(	this,	"localhost",	10012,	10002,	"receiveOSC"	);
		
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
			//case 'x':sjRotate=!sjRotate;break;
			case 'c':sjOn=!sjOn;break;
			case 'v':envOscMotion++;envOscMotion%=3;break;
			case 'n':rotateParticles=!rotateParticles;break;
			case 'm':flareOn=!flareOn;break;
			//test code
			case 'q':addParticleSys("drm1",10) ;break;
			case 'w':addParticleSys("osc1",(int)random(20,100)) ;break;
			case 'e':addParticleSys("ping2",65) ;break;
			case 'y':addParticleSys("drm1",20+(int)random(4));break;
			case 'x':System.exit(0);break;
		}
	}
	
	boolean mblurEnabled=false;
	private float rotate=0f;
	int drmCtr =0;
	int oscCtr=0;
	int speedCtr = 0;
	public void draw() {
		//background(0);
		drmCtr++;
		oscCtr++;
		speedCtr++;
		
		if (drmCtr%20==0) {addParticleSys("drm1", (int)(10+Math.round(Math.random()*3)));}
		if (oscCtr%15==0) {addParticleSys("drm1", (int)(20+Math.round(Math.random()*3)));}
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
		if (ps!=null) {
			ps.render();
		}
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
		gl.glUseProgramObjectARB(0);
		// render julia  //////////////////////////////////////////////////////////////
		/*
		ogl.setFB("julia");
		gl.glPushMatrix();
			gl.glColor4f(1, 1, 1, 1f);
			gl.glTranslatef(0, 0, -600);
			shape.drawSquare(640,480);
			applyJulia();
		gl.glPopMatrix();
		ogl.snapFBToTex("julia","julia",GL.GL_RGBA);
		gl.glUseProgramObjectARB(0);
		*/
		
		// write to main buffer //////////////////////////////////////////////////////////////
		ogl.clearFB();
		gl.glColor4f(1, 1, 1, 0.4f);
		int wid = 3;
		int z=-700;
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(-wid,-wid,z),true); 
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(wid,wid,z)); 
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(-wid,wid,z)); 
		ogl.drawFBOTextures(new String[]{"particleBlur"},new Vector3D(wid,-wid,z)); 
		   
		gl.glPopMatrix();
		ogl.drawFBOTextures(new String[]{"particle","particleFlare","particleFlare","particleFlare"/*,"julia"*/},new Vector3D(0,0,z));
		//println(vm.player0.getPosMilli()+"ms");
	    
		if (mblurEnabled) {
			mblur.blur(0.95f);
		}
	}
	/*
	void applyJulia() {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		GLSLProgram gProgram = ogl.getProgram("julia");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        Vector3D point = thePoint();
        gl.glUniform2f(gProgram.getUniformId("point"), point.x*4,point.y*4);
        //gl.glUniform1f(gProgram.getUniformId("alpha"), 1);
        
	}
	
	private Vector3D thePoint() {
		if (vec.size()==0) {
			return  getPoint();
		}
		if (vecIndex>vec.size()-2) {
			vecForward=false;
		} else if (vecIndex<1) {
			vecForward=true;
		}
		vecIndex +=vecForward?1:-1;
		return vec.get(vecIndex);
	}
	private void getPath() {
		String path ="/mnt/home/robm/patch/OpenNight221009/julia.path";
		
		File f = new File(path);
		try {
			ObjectInputStream oos = new ObjectInputStream(new FileInputStream(f));
			vec = (Vector<Vector3D> ) oos.readObject();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Vector3D getPoint() {
		Vector3D point = new Vector3D((mouseX-width/2f)/(float)width, (mouseY-height/2f)/(float)height, 0);
		return point;
}
*/
	public void mouseClicked() {
		ps.addSystem(new Vector3D(mouseX-width/2,mouseY-height/2,-100) );
		//vm.player0.setPosPC((float)mouseX/(float)width*100);
	}
	
	public void receiveOSC(OscIn oscIn){
		try{
			if (oscIn.getAddrPattern().equals("/testme")) {
				System.out.println(oscIn.getFloat(0));
			}else if (oscIn.getAddrPattern().equals("/mov/speed")) {
				
				
			}else if (oscIn.getAddrPattern().equals("/drm/trig")) {
				//System.out.println(oscIn.getInt(0));
				//vm.trigger(oscIn.getInt(0));  
				try {
					addParticleSys("drm1", oscIn.getInt(0));
				} catch (Exception e) {
					addParticleSys("drm1", Math.round(oscIn.getFloat(0)));
				}
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
	
	public static void main (String[] args) {
		//println( OpenLab5_240409.class.getCanonicalName());
		PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=1024,768",OpenNight_221009_FBO.class.getCanonicalName() });
		//PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=1280,1024",OpenLab5_240409_FBO.class.getCanonicalName() });
		//PApplet.main(new String[] { OpenLab5_240409.class.getCanonicalName() });
	}
	
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
