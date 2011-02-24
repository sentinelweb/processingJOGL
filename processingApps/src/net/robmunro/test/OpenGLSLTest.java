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
public class OpenGLSLTest extends PApplet {

	float phase;
	float div;
	String shaderSource;
	GL gl;
	
	OpenGL ogl ;
	
	Sphere s;
	boolean ready=false;
	
	PImage img;
	public void setup()
	{
		size(800,600,OPENGL);
	  
	  	div=50;
	  	ogl=new OpenGL(this);
	  	this.gl=ogl.gl;
	  	try {
	  		ogl.makeProgram(
	  				"waves",
	  				new String[] {"phase"},
	  				new String[] {},
	  				ogl.loadGLSLShaderVObject(	"waves.glsl"),
	  				-1
	  		);
			ogl.makeProgram(
						"glass",
						new String[] {},
						new String[] {"GlassColor","SpecularColor1","SpecularColor2","SpecularFactor1","SpecularFactor2","LightPosition"}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glass.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glass.frag"	)
				);
			img=loadImage("resources/robmunro/glsl/00020.jpg");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		s = new Sphere();
	    s.initializeSphere(15);
	    ready=true;
	}

	public void draw()
	{
	if (!ready ) return;
	
	  phase-=0.01;
	  if(phase<0)    phase+=TWO_PI;
	  if(phase>TWO_PI)    phase-=TWO_PI;
	   
	  GLSLProgram wavesProgram = ogl.getProgram("waves");
	  GLSLProgram gProgram = ogl.getProgram("glass");
	  //GLSLProgram gfProgram = ogl.getProgram("glass_f");
	  boolean vertexShaderEnabled = (gProgram!=null);
	  //stroke(0,255,30);
	  noStroke();
	  fill(255,0,0);
	  //noFill();
	  background(0);
	  //camera(1800,600,600,600,0,600,0,-1,0);
	  fill(255,0,0);
	 // pushMatrix();
	  //rect(0,0,width,height);
	 // image(img,0,0,width,height);
	  //popMatrix();
	  pushMatrix();
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
	//  popMatrix();
	
	//  pushMatrix();
	  if (vertexShaderEnabled)  
	  {
		gl.glUseProgramObjectARB(wavesProgram.getProgramObject());
	    gl.glVertexAttrib1fARB(wavesProgram.getAttribId("phase"), phase);
	  }
	  translate(50,500, 0);
	  //noFill();
	   //stroke(0,255,30);
	   beginShape(QUADS);
	   for (int x = 0; x < div; x++)  
	   {
	     for (int z = 0; z < div; z++)  
	     {
			  vertex((x)*20.0f, 0, (z)*20.0f);   // Draw Vertex
			  vertex(((x+1))*20.0f,0 , (z)*20.0f);   // Draw Vertex
			  vertex((x+1)*20.0f, 0, ((z+1f))*20.0f);   // Draw Vertex
			  vertex((x)*20.0f, 0, ((z+1f))*20.0f);   // Draw Vertex
	     }
	   }
	   endShape();
	 //  if (vertexShaderEnabled) {
	//     gl.glUseProgramObjectARB(wavesProgram.getProgramObject());
	 //  }
	   popMatrix();
	  
	 // pushMatrix();
	  /*
	   beginShape(QUADS);
	   for (int x = 0; x < div; x++)  
	   {
	     for (int z = 0; z < div; z++)  
	     {
			  vertex((x)*20.0f, 0, (z)*20.0f);   // Draw Vertex
			  vertex(((x+1))*20.0f,0 , (z)*20.0f);   // Draw Vertex
			  vertex((x+1)*20.0f, 0, ((z+1f))*20.0f);   // Draw Vertex
			  vertex((x)*20.0f, 0, ((z+1f))*20.0f);   // Draw Vertex
	     }
	   } */
	  
	  // popMatrix();
	  
	  if(frameCount%30==29)  println(frameRate);
	} 
}
