
package net.robmunro.video;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import processing.core.PApplet;
import codeanticode.gsvideo.GSMovie;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import fullscreen.FullScreen;

public class Video extends PApplet {
	private VideoPlay player;
	GL gl;
	OpenGL ogl ;
	GLU glu;
	ArrayList<Texture> textures = new ArrayList<Texture>();
	int currInsertPos = 0;
	int currReadPos = 0;
	int MAX_LENGTH = 300;
	
	
	public void setup() {
		 //size(1200, 800, OPENGL);
		size(800,600, OPENGL);
		 background(0);
		 ogl=new OpenGL(this);
		 this.gl=ogl.gl;
		 glu = new GLU();

		 player = new VideoPlay(this, "resources/robmunro/chandeleir_cpk_ky5.avi");
		 //player = new VideoPlay(this, "/home/robm/media/video/60_com/avi/TONYTIGER.avi");
		 //player = new VideoPlay(this, "resources/robmunro/TONYTIGER.avi");
		 
		 player.loop();
		 //startFullscreen();
		 
	}
	
	void movieEvent(GSMovie myMovie) {
		//System.out.println("hello:");
	}
	float ang=0;
	Texture videoTex=null;
	
	public void draw() {
		//System.out.println("hello1:");
		background(0);
		ang+=5;
		if (player.videoData!=null) {
			//gl.glTranslatef(-100,-60,-100);
			gl.glTranslatef(-200,-100,-300);
			gl.glColor4f(1f, 1f, 1, 1f);
			if (videoTex!=null) {videoTex.dispose();}
			videoTex = TextureIO.newTexture( player.videoData);
			
			videoTex.bind();
			videoTex.enable();
			int row = 10;
			for (int i=0;i<row;i++) {
				float x1 = (float)mouseX/(float)width*i*row/0.5f;
				//System.out.println(x1);
				gl.glTranslatef(x1,0,0);
				gl.glPushMatrix();
				for (int j=0;j<row;j++) {
					 float y1 = (float)mouseY/(float)height*j*row/0.5f;
					gl.glTranslatef(0,y1,0);
					 drawSquare(Math.round(x1),Math.round(y1));
				}
				gl.glPopMatrix();
				gl.glFlush();
				gl.glFinish();
				
			}
		}
		
		if (mousePressed && mouseButton == RIGHT) {  
		    stopFullscreen();  
		  }  
	}
	
	
	
	private void drawSquare(int w,int h) {
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

	//public static void main(String args[]) {
	//  PApplet.main(new String[] { "--display=1", "--present",  "--present-stop-color=#000000", Video.class.getCanonicalName() });
	//}
	
	void startFullscreen() {
		  frame = new Frame(); 
		  //frame.dispose();  
		  frame.setUndecorated(true);  
		  frame.add(this);
		  //GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice  ();  
		  GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1];  // the second screen
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
