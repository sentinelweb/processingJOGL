package net.robmunro.test;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;

import com.sun.opengl.util.GLUT;

import processing.core.PApplet;

public class TestTextureEffect extends PApplet {
	
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	int textId=0;
	float[] offset={0f,0f};
	boolean blurOn=true;
	public void setup()
	{
	    //size(4*HEIGHT, HEIGHT, OPENGL);  
		//size(1280 ,1024, OPENGL);  
		size(640 ,480, OPENGL);  
	    //startFullscreen();
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
	    s = new Shape(gl,this);
	   // textId=ogl.loadTexture("test", "/home/robm/download/opengl/glsl/cubemap/cubemap_city/city_negative_z.png");
	    textId=ogl.loadTexture("test", "/home/robm/processing/processing-1.0/processingApps/src/resources/robmunro/line.jpg");
	    try {
			ogl.makeProgram(
					"blur1",
					new String[] {},
					new String[] {"tex1","offset1"}, //
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glow/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glow/Blur.frag")
			);
			ogl.makeProgram(
					"bloom",
					new String[] {},
					new String[] {"texture1","filterThresh"}, //,"offset1"
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glow/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glow/ExtractBloom.frag")
			);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void draw()	{

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, 640, 480);
		gl.glEnable(GL.GL_TEXTURE_2D);   
		gl.glBindTexture(GL.GL_TEXTURE_2D,  ogl.getTex("test")); 
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
        gl.glEnable(GL.GL_BLEND); 
        
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if (blurOn) applyBlur1();
		else {noApply();}
		gl.glTranslatef(0f, 0f ,-600f);
	    s.drawSquare(640, 480);
	    noApply();
	    s.drawSquare(640, 480);
	}
	void noApply(){  gl.glUseProgramObjectARB(0);	}
	void applyBlur1() {
		GLSLProgram gProgram = ogl.getProgram("blur1");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform2fARB(gProgram.getUniformId("offset1"), offset[0],offset[1]);
        gl.glUniform1fARB(gProgram.getUniformId("tex1"),  ogl.getTex("test"));
	}
	void applyBlur() {
		GLSLProgram gProgram = ogl.getProgram("bloom");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1fARB(gProgram.getUniformId("filterThresh"), offset[0]);
        gl.glUniform1fARB(gProgram.getUniformId("texture1"),  ogl.getTex("test"));
	}
	
	public void keyPressed() {
		
		switch(keyCode) {
			case UP:offset[0]+=0.001;println(""+key);break;
			case DOWN:offset[0]-=0.001;println(""+key);break;
			case LEFT:offset[1]-=0.001;println(""+key);break;
			case RIGHT:offset[1]+=0.001;println(""+key);break;
			
		}
		switch(key) {
			case 'z':blurOn=!blurOn;println(""+key+":"+blurOn);break;
		
		}
		println(offset);
	}
}
