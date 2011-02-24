
package net.robmunro.video;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.math.BigDecimal;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.AWTGraphicsConfiguration;
import javax.media.opengl.AWTGraphicsDevice;
import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import net.robmunro.lib.ogl.tools.MotionBlur;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.VideoPlay;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.perform.Placard200908;
import processing.core.PApplet;
import processing.core.PFont;
import procontroll.ControllDevice;
import procontroll.ControllIO;
import procontroll.ControllStick;
import codeanticode.gsvideo.GSMovie;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class Video2 extends PApplet {
	private VideoPlay player;
	GL gl;
	OpenGL ogl ;
	GLU glu;
	ArrayList<Texture> textures = new ArrayList<Texture>();
	int currInsertPos = 0;
	int currReadPos = 0;
	int MAX_LENGTH = 300;
	
	PFont metaBold;
	MotionBlur m;
	ControllIO controllIO;
	ControllDevice djConsole;
	ControllStick stick1;
	ControllStick stick2;
	boolean djConsoleOn = false;
	public void setup() {
		 //size(120 0, 800, OPENGL);
		controllIO = ControllIO.getInstance(this);
		controllIO.printDevices();
		if (djConsoleOn) {
		djConsole = controllIO.getDevice("Hercules Hercules DJ Console");
		djConsole.printSticks();
		djConsole.printSliders();
		djConsole.printButtons();
		//for (int i=0 ;i<djConsole.getNumberOfButtons();i++) {
		int i=0;
		  djConsole.plug(this, "handleButton1Press", ControllIO.ON_PRESS, i);
		  djConsole.plug(this, "handleButton1Release", ControllIO.ON_RELEASE, i);
		  djConsole.plug(this, "handleMovement", ControllIO.WHILE_PRESS, i);
		//}
		  djConsole.getSlider(0).getValue();
		 for (int num=0 ;num<djConsole.getNumberOfSliders();num++) {
			 djConsole.getSlider(num).setMultiplier(256f);
		 }
		}
		//size(1280,1024, OPENGL);
		size(640,480, OPENGL);
		//startFullscreen();
		 
		 background(0);
		 ogl=new OpenGL(this);
		 this.gl=ogl.gl;  
		 glu = new GLU();
		 m = new MotionBlur(this.gl);
		 m.clearAccum();
		 setVec(0,  -66.25f,-270.0f,-990.0f, VALUE);
		 setVec(0, -1000,-1000,-1000,SCALE);
		 setVec(1, 0f,0f,0f, VALUE);
		 setVec(1, 360,360,360,SCALE);
		 setVec(2, 0.001f,0.001f,0.00f, VALUE);
		 setVec(2, 0.1f,0.1f,0.1f,SCALE);
		 setVec(3, 0f,0f,0f, VALUE);
		 setVec(3, 1,1,1,SCALE);
		 setVec(4, 0f,0f,0f, VALUE);
		 setVec(4, 10,10,10,SCALE);
		 setVec(5, 0f,0f,0f, VALUE);
		 setVec(5, 2,2,2,SCALE);
		 player = new VideoPlay(this, "/home/robm/media/video/60_com/avi/7up.avi");
			
		 //player = new VideoPlay(this, "resources/robmunro/chandeleir_cpk_ky5.avi");
		 //player = new VideoPlay(this, "/home/robm/Videos/stuff/car/motorway_drive3.avi");
		//player = new VideoPlay(this, "/home/robm/media/video/60_com/avi/TONYTIGER.avi");
		 //player = new VideoPlay(this, "resources/robmunro/TONYTIGER.avi");
		 //player = new VideoPlay(this, "/media/disk/vid/london/can_wharf/mpg/can_twr_busstop_lights.mpg");
		// player = new VideoPlay(this, "/media/disk/vid/london/can_wharf/mpg/can_twr_pan_up2.mpg");
		 //player = new VideoPlay(this, "/media/disk/vid/london/mpg/carousel_far.mpg");
					
		 
		 player.loop();
		 metaBold = loadFont("resources/gsmovie/UniversLTStd-Light-48.vlw");
		textFont(metaBold, 15); 
	}
	
	void movieEvent(GSMovie myMovie) {
		//System.out.println("hello:");
	}
	float ang=0;
	Texture videoTex=null;
	public String s2dp(float num) {return (new BigDecimal(num)).setScale(2,BigDecimal.ROUND_HALF_EVEN).toString();}
	//public String s2dpj(int num) {return s2dp(djConsole.getSlider(num).getValue());}
	//public float fj(int num) {return djConsole.getSlider(num).getValue();}
	public float fj(int num) {return 1;}
	public String s2dpj(int num) {return ""+1;}
	public void draw() {
		//System.out.println("hello1:");
		background(0);
		text(index+":"+getVec(index,VALUE)+":"+(shift?"S":" ")+":"+(ctrl?"C":" "), 10,20);
		text(s2dpj(0)+":"+s2dpj(1)+":"+s2dpj(2)+":"+s2dpj(3)+":"+s2dpj(4)+":"+s2dpj(5)+":"+s2dpj(6)+":"+s2dpj(7)+":"+s2dpj(8)+":"+s2dpj(9)+":"+s2dpj(10)+":"+s2dpj(11)+":"+s2dpj(12)+":"+s2dpj(13)+":"+s2dpj(14), 10,40);
		ang+=5;
		if (player.videoData!=null) {
			//gl.glTranslatef(-100,-60,-100);
			//gl.glTranslatef(-100,-50,-200);
			
			drawSpikes(100);
		}
		Vector3D v= getVec(5,VALUE);
		gl.glTranslatef( v.x, v.y, v.z);
		m.blur(v.x);//fj(10)
		if (mousePressed && mouseButton == RIGHT) {  
		  stopFullscreen();  
		}  
		
	}

	private void drawSpikes(int row) {
		//System.out.println("drawSpikes:");
		Vector3D r= getVec(1,VALUE);
		gl.glRotatef(r.x, 1,0,0);
		gl.glRotatef(r.y, 0,1,0);
		gl.glRotatef(r.z, 0,0,1);
		
		Vector3D v= getVec(0,VALUE);
		gl.glTranslatef( v.x, v.y, v.z);
		gl.glColor4f(1f, 1f, 0f, 0.8f);
		if (videoTex!=null) {videoTex.dispose();}
		videoTex = TextureIO.newTexture( player.videoData);
		
		videoTex.bind();
		videoTex.enable();
		
		for (int i=0;i<row;i++) {
			float x1 = (float)mouseX/(float)width*i*row/2;
			//System.out.println(x1);
			//gl.glRotatef(5,1,0,0);
			 v= getVec(4,VALUE);
			gl.glTranslatef( v.x, v.y, v.z);
			
			getVec(3,VALUE).add(getVec(2,VALUE));
			Vector3D rr=getVec(3,VALUE);
			gl.glRotatef(rr.x, 1,0,0);
			gl.glRotatef(rr.y, 0,1,0);
			gl.glRotatef(rr.z, 0,0,1);
			
			gl.glTranslatef(fj(2)*100f,0,0);
			gl.glRotatef(fj(6)*5f,0,1,0);
			
			// drawSquare(fj(4)*300f , fj(3)*300f);
			//drawSquare(fj(4)*300f , fj(3)*300f,0.5f,0f,1f,1f) ;
			drawSquare(fj(4)*300f , fj(3)*300f) ;//,0f,0f,0.5f,05f
			gl.glFlush();
			gl.glFinish();
			
		}
	}
	
	public static final int VALUE = 0;
	public static final int SCALE = 1;
	
	boolean shift = false;
	boolean ctrl = false;
	boolean alt = false;
	Vector3D[] ctlVars=new Vector3D[10];
	Vector3D[] scaleVars=new Vector3D[10];
	Vector3D[][] vars = new Vector3D[][]{ctlVars, scaleVars};
	int index=0;
	public Vector3D getVec(int i, int varsIndex){
		Vector3D[] arr = vars[varsIndex];
		
		if ( arr[i]==null) { 
			arr[i]=new Vector3D() ;
		} 
		return arr[i];
	}
	public void setVec(int i,float x , float y , float z, int varsIndex){
		Vector3D vec = getVec(i,varsIndex);
		vec.x=x;
		vec.y=y;
		vec.z=z;
	}
	public void setVec(int i,Vector3D v, int varsIndex){
		setVec( i, v.x ,  v.y ,  v.z,  varsIndex);
	}
	public void mousePressed() {
		if (mouseButton == LEFT) {
			
		} else if (mouseButton ==RIGHT) {
			
		}
	}
	
	public void mouseMoved()	{
		Vector3D vec = getVec(index,VALUE);
		if (shift &&!ctrl) {
			vec.x=(float)mouseX/(float)width*getVec(index,SCALE).x;
			vec.y=(float)mouseY/(float)height*getVec(index,SCALE).y;
			println(index+" : "+vec);
			
		} else if (shift &&ctrl) {
			vec.z=(float)mouseX/(float)width*getVec(index,SCALE).z;
			println(index+" : "+vec);
		} else if (!shift&ctrl) {
			
		}else if (!shift&alt) {
			
		}else if (!alt&ctrl) {
			
		}else if (!shift&ctrl) {
			
		}
	}
	
	public void keyPressed() {
		switch (key) {
			case '-': if (index>0)  index-- ; break;
			case '=': if (index<9)  index++; break;
			default: break;
		}
		
			  if (key == CODED) {
			    if (keyCode == SHIFT) {
			    	shift=!shift;
			    } else if (keyCode == CONTROL) {
			    	ctrl=!ctrl;
			    }else if (keyCode == ALT) {
			    	alt=!alt;
			    }
			  }
	}
	
	public void handleButton1Press() {}
	public void handleButton1Release() {}
	public void handleMovement() {}
	
	private void drawSquare(float w,float h,float sx,float sy ,float ex,float ey) {
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		gl.glTexCoord2f(sx, ey); 
		gl.glVertex2f(-w,  h);
		gl.glTexCoord2f(sx, sy); 
		gl.glVertex2f(-w, -h);
		gl.glTexCoord2f(ex, ey);
		gl.glVertex2f( w,  h);
		gl.glTexCoord2f(ex, sy); 
		gl.glVertex2f( w, -h);
		gl.glEnd();
		
	}
	private void drawSquare(float w,float h) {
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		gl.glTexCoord2f(0, 1); 
		gl.glVertex2f(-w,  h);
		gl.glTexCoord2f(0, 0); 
		gl.glVertex2f(-w, -h);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f( w,  h);
		gl.glTexCoord2f(1, 0); 
		gl.glVertex2f( w, -h);
		gl.glEnd();
	}
	
	class VideoPlay extends GSMovie{
		public TextureData videoData;
		public VideoPlay(PApplet arg0, String arg1) {
			super(arg0, arg1);
			//TextureData(int internalFormat, int width, int height, int border, int pixelFormat, int pixelType, boolean mipmap, boolean dataIsCompressed, boolean mustFlipVertically, Buffer buffer, TextureData.Flusher flusher) 
			gplayer.setVolume(0);
		}
		
		protected void invokeEvent(int w, int h, IntBuffer buffer) {
			if (videoData==null) {
				videoData = new TextureData( GL.GL_RGB, w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, false, false, false, null, null );
			}
			videoData.setBuffer(buffer);
			//System.out.print("frame:"+w+":"+h);
		}
	}

	public static void main (String[] args) {
		//GLCapabilities capabilities = new GLCapabilities();
	    //capabilities.setSampleBuffers(true);
	    //capabilities.setNumSamples(4);
		//GLDrawableFactory factory = GLDrawableFactory.getFactory();
		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
       // GraphicsDevice dev = ge.getScreenDevices()[0];
       // AWTGraphicsDevice device = new AWTGraphicsDevice(dev);
       // AWTGraphicsConfiguration agc = (AWTGraphicsConfiguration)    factory.chooseGraphicsConfiguration(capabilities, new DefaultGLCapabilitiesChooser(), device);
        PApplet.main(new String[] { "net.robmunro.video.Video2" });
		//Video2 p = new Video2();
		//p.init();
	}
	
	void startFullscreen() {
		//frame.dispose();  
		//Frame oldFrame = frame;
		
		frame = new Frame(); 
		frame.add(this);
		frame.setUndecorated(true);  
		//frame.setLocation(new Point(0,0));
		 // oldFrame.remove(this);
		 // oldFrame.setVisible(false);
		  //GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
		GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice myGraphicsDevice = screenDevices[1];  // the second screen
		for (int i=0;i<screenDevices.length;i++) {
			  System.out.println(screenDevices[i].getIDstring()+":"+screenDevices[i].getDisplayMode().getWidth()+"x"+screenDevices[i].getDisplayMode().getHeight());
		}
		myGraphicsDevice.setFullScreenWindow(frame);  
		if (myGraphicsDevice.isDisplayChangeSupported()) {  
		    DisplayMode myDisplayMode = new DisplayMode(  
		    width,  
		    height,  
		    32,  
		    DisplayMode.REFRESH_RATE_UNKNOWN);  
		    myGraphicsDevice.setDisplayMode(myDisplayMode);  
		  }  
	}  
		 
	void stopFullscreen() {  
	  GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
	  myGraphicsDevice.setFullScreenWindow(null);  
	} 
}
