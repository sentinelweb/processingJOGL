package net.robmunro.test;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class FBOExample extends PApplet {

	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	 
	 
	public void setup()
	{
	    size(640, 480, OPENGL);  
	   
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
	    s = new Shape(gl);
		boolean fboOK=ogl.createFrameBufferObject( "test", 640, 480);
		println(fboOK);
		ogl.createTexture( "test", 640, 480);
		
		try {
			ogl.makeProgram(
						"wood",
						new String[] {},
						new String[] {"LightPosition","Scale","DarkColor","spread","GrainSizeRecip"}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/wood.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/wood.frag")
				);
			ogl.makeProgram(
					"pass",
					new String[] {},
					new String[] {}, 
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glow/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glow/passthru.frag"	)
			);
			ogl.makeProgram(
					"blur",
					new String[] {},
					new String[] {"src_tex_unit0","src_tex_offset0"}, 
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glow/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/glgraphics/ex/multiFilter/Blur.glsl"	)
			);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	float rotate = 0;
	public void draw()	{
		rotate++;
		gl.glColor4f(1, 0, 0, 0.8f);
		//ogl.clearFB();
		
		
		ogl.setFB("test");
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		
		//gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f ,-60f);
		gl.glRotatef(rotate, 1, 0, 0) ;
		glu.gluCylinder(glu.gluNewQuadric(), 5, 10, 10 ,15,20);
		gl.glPopMatrix();
		
		gl.glViewport(0, 0, 640, 480);
		gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("test"));
	    gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_LUMINANCE, 0, 0, 640, 480, 0);
	    
	    gl.glUseProgramObjectARB(0);
	    ogl.clearFB();// use main buffer
	    
		gl.glEnable(GL.GL_TEXTURE_2D);   
		gl.glBindTexture(GL.GL_TEXTURE_2D,  ogl.getTex("test")); 
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
        gl.glEnable(GL.GL_BLEND); 
        
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glTranslatef(0f, 0f ,-600f);
	    s.drawSquare(640, 480);


	    
	    gl.glDisable(GL.GL_TEXTURE_2D);                         
        gl.glDisable(GL.GL_BLEND);  
        
	    gl.glBindTexture(GL.GL_TEXTURE_2D, 0); 
	    gl.glFlush();       
	    
	}
	
	void applyWood() {
		GLSLProgram gProgram = ogl.getProgram("wood");
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 1,1,1);
        gl.glUniform1fARB(gProgram.getUniformId("Scale"),2f);
        
	    gl.glUniform3fARB(gProgram.getUniformId("DarkColor"),  .1f, 0.8f, 0.5f);
	    gl.glUniform3fARB(gProgram.getUniformId("spread"),  .1f, 0.1f, 0.5f);
	    gl.glUniform1fARB(gProgram.getUniformId("GrainSizeRecip"),0.5f) ;
        
	
	}
	void applyBlur(int tex) {
		GLSLProgram gProgram = ogl.getProgram("blur");
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1fARB(gProgram.getUniformId("src_tex_unit0"), tex);
        gl.glUniform2fARB(gProgram.getUniformId("src_tex_offset0"),2,2);
	}
	
	void applyPass() {
		GLSLProgram gProgram = ogl.getProgram("pass");
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
	}
}
