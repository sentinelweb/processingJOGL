package net.robmunro.fft;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.Sphere;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;

import oscP5.OscIn;
import oscP5.OscP5;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class DispFFTOGL extends PApplet {

	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	private static final int DATA_LENGTH = 1024;
	private static final int DATA_MAX = 255;
	boolean drawSempahore = false;
	OscP5 oscP5 =null; 
	float rotateCtr=0.0f;
	PImage buffer;
	int buffWid=640;
	int buffHgt= 480;
	//GLTextureFilter fftFilter;
	//GLTexture  tex1;
	//GLTexture  buffer1;
	//Sphere s;
	float rotation = 0f;
	
	GL gl;
	OpenGL ogl ;
	GLU glu;
	@Override
	public void setup() {
		  size(WIDTH,HEIGHT, GLConstants.GLGRAPHICS);
		  noStroke();
		  frameRate(15);
		  ogl=new OpenGL(this);
		  this.gl=ogl.gl;
		  glu = new GLU();
		  oscP5= new OscP5(
					this,
					"localhost",
					10003,
					10013,
					"receiveOSC"
					
			);
		  try {
				ogl.makeProgram(
							"distort",
							new String[] {},
							new String[] {}, //"GlassColor",
							ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/distort.vert"), 
							ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/passthru.frag")
					);
			  } catch (Exception e) {
					e.printStackTrace();
			  }
	}

	@Override
	public void draw() {
		if (!drawSempahore) {	
			drawSempahore = true;
			GLSLProgram gProgram = ogl.getProgram("distort");
			//gl.glUseProgramObjectARB(gProgram.getProgramObject());
			background(0);
			rotateCtr += 0.1;
			gl.glTranslatef(-450,-100,-600);
			gl.glColor4f(0.5f, 0f,0f,0.2f);
			ArrayList<Integer> data = sndLoader.getData();
			plotData(data,900,300);
			
			//rotation+=0.01;
		}
		drawSempahore = false;
	}

	private void plotData(ArrayList<Integer> data,float dwidth, float dheight) {
		float space = dwidth/data.size();
		
		for (int i=0;i<data.size();i++) {
			float top = dheight-data.get(i)*dheight/255-(dheight/2) ;
			//buffer.set(left, top, color( 0, 255, 255, 200 ));
			//float factor = 1f;
			gl.glTranslatef(0, top, 0);
			gl.glPushMatrix();
			drawSquare(5, 5);
			gl.glPopMatrix();
			//gl.glRotatef((float)mouseX/(float)width, 0f, 1f, 0f);
			gl.glTranslatef(space, -1*top, 0);
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
	
	
	BufferLoader sndLoader=new BufferLoader();
	BufferLoader fftLoader=new BufferLoader();
	public void receiveOSC(OscIn oscIn){
		if (oscIn.getAddrPattern().equals("/fft")) {
			fftLoader.process(oscIn);
		}else if (oscIn.getAddrPattern().equals("/snd")) {
			sndLoader.process(oscIn);
		}
	}
	
	class BufferLoader {
		private ArrayList<Integer> snd_buffer = new ArrayList<Integer>(); 
		private ArrayList<Integer> snd_data  = new ArrayList<Integer>();
		private HashMap<Integer,ArrayList> tmp  = new HashMap<Integer,ArrayList>();
		
		public void process(OscIn oscIn){
			Integer index = oscIn.getInt(0);
			String data = oscIn.getString(1);
			//System.out.println(index+ " --- "+data+":");
			String[] dataSplit=data.split(":");
			ArrayList<Integer> tmp_buf = new ArrayList<Integer>();
			for (int i=0;i<dataSplit.length;i++) {
				try {
					tmp_buf.add(Integer.parseInt(dataSplit[i]));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			tmp.put( index, tmp_buf );
			if (index==0) {
				for (int i=tmp.keySet().size()-1; i>0; i--) {
					if (tmp.get( i )!=null) {
						snd_buffer.addAll(tmp.get( i ));
					}
				}
				snd_data = snd_buffer;
				snd_buffer = new ArrayList<Integer>();
				//System.out.println("rx frame:"+oscIn.getAddrPattern());
			}
		}
		public ArrayList<Integer> getData() {
			return snd_data;
		}
	}
	
	
}
