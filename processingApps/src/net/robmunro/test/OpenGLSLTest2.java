package net.robmunro.test;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.*;
import javax.media.opengl.*;

import net.robmunro.lib.Sphere;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;

import java.nio.*;
import java.util.HashMap;

import com.sun.opengl.util.BufferUtil;
public class OpenGLSLTest2 extends PApplet {

	GL gl;
	OpenGL ogl ;
	boolean ready=false;
	PImage img;
	
	public void setup()
	{
		size(800,600,OPENGL);
	  
	  	ogl=new OpenGL(this);
	  	this.gl=ogl.gl;
	  	try {
			ogl.makeProgram(
						"glass",
						new String[] {},
						new String[] {"GlassColor","SpecularColor1","SpecularColor2","SpecularFactor1","SpecularFactor2","LightPosition"}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glass.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glass.frag"	)
				);
			img=loadImage("resources/robmunro/glsl/00020.jpg");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	    ready=true;
	}

	public void draw()
	{
	if (!ready ) return;
	   
	 
	  noStroke();
	  fill(255,0,0);
	  background(0);
	 // pushMatrix();
	  GLSLProgram gProgram = ogl.getProgram("glass");
	  boolean vertexShaderEnabled = (gProgram!=null);
	  if (vertexShaderEnabled) {
		  // gl.glDeleteObjectARB(wavesProgram.getProgramObject());

		    gl.glUseProgramObjectARB(gProgram.getProgramObject());
		    // gl.glUseProgramObjectARB(gfProgram.getProgramObject());
		     
		    gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 0f, 0f,  5f);
		     
		    gl.glUniform4fARB(gProgram.getUniformId("GlassColor"), mouseX/(float)width, mouseY/(float)height, 0.6f, 0.15f);
		    gl.glUniform4fARB(gProgram.getUniformId("SpecularColor1"),  .1f, 0.8f, 0.5f, 1f);
		    gl.glUniform4fARB(gProgram.getUniformId("SpecularColor2"),  .1f, 0.1f, 0.5f, 1f);
		    gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor1"),2f) ;
		    gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor2"),2f);
	  }
	  for (int i=0;i<10;i++) {
		  translate(70,20+30*i, 0);
		  sphere(100);
	  }
	
	
	  if(frameCount%30==29)  println(frameRate);
	} 
}
