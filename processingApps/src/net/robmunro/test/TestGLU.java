package net.robmunro.test;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import processing.opengl.*;

import processing.core.PApplet;

public class TestGLU extends PApplet {
	//used for oveall rotation
	
	GL gl;
	OpenGL ogl ;
	GLU glu;
	float points[][];
	public void setup(){
		  size(1200, 800, OPENGL); 
		  background(0); 

		  //instantiate cubes, passing in random vals for size and postion
		  ogl=new OpenGL(this);
		  this.gl=ogl.gl;
		  glu = new GLU();
		  try {
			ogl.makeProgram(
						"glass",
						new String[] {},
						new String[] {"GlassColor","SpecularColor1","SpecularColor2","SpecularFactor1","SpecularFactor2","LightPosition"}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glass.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glass.frag"	)
				);
		} catch (Exception e) {
			e.printStackTrace();
		}
		points=new float[200/10][2];
		 for (int i=0;i<200;i+=10){
			 points[i/10][0]=(float)Math.random()*200f-100f;
			 
			 points[i/10][1]=(float)Math.random()*200f-100f;
		 }
	}

	public void draw(){
	  background(0); 
	  fill(200);
	  GLSLProgram gProgram = ogl.getProgram("glass");
	  boolean vertexShaderEnabled =(gProgram!=null);// false;//
	  if (vertexShaderEnabled) {
		    gl.glUseProgramObjectARB(gProgram.getProgramObject());
		    gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 0f, 0f,  5f);
		    gl.glUniform4fARB(gProgram.getUniformId("GlassColor"), mouseX/(float)width, mouseY/(float)height, 0.6f, 0.15f);
		    gl.glUniform4fARB(gProgram.getUniformId("SpecularColor1"),  .1f, 0.1f, 0.1f, 1f);
		    gl.glUniform4fARB(gProgram.getUniformId("SpecularColor2"),  .1f, 0.1f, 0.1f, 1f);
		    gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor1"),2f) ;
		    gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor2"),2f);
	  }
	  
	  gl.glTranslatef(-80f, 0f ,-100f);
	 // gl.glRotatef( mouseX/(float)width*360f, 0.0f,0.0f,1.0f);
	  for (int i=0;i<200;i+=10){
		 // 	gl.glLoadIdentity();
		  gl.glPushMatrix();
		  	 gl.glRotatef( mouseY/(float)width*360f,1.0f,0.0f,0.0f);
		  	 gl.glTranslatef( points[i/10][0] , points[i/10][1],0f);
		  	 glu.gluCylinder( glu.gluNewQuadric(), 5, 10, 10 ,15,20);
		  	 gl.glTranslatef(   0f ,0f,mouseX/(float)width*100-50);
			  	
		  	 glu.gluSphere( glu.gluNewQuadric(), 5, 10, 10 );
		  	 
		  		 
		  	 
			  	
		  gl.glPopMatrix();
	  }
	}
}
