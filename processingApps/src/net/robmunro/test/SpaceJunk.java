package net.robmunro.test;
import javax.media.opengl.GL;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import processing.opengl.*;

import processing.core.PApplet;

public class SpaceJunk extends PApplet {
	//used for oveall rotation
	float ang;

	//cube count-lower/raise to test P3D/OPENGL performance
	int limit = 200;

	//array for all cubes
	Cube[]cubes = new Cube[limit];
	GL gl;
	OpenGL ogl ;
	public void setup(){
	  //try substituting P3D for OPENGL 
	  //argument to test performance
	  size(640, 480, OPENGL); 
	  background(0); 

	  //instantiate cubes, passing in random vals for size and postion
	  ogl=new OpenGL(this);
	  this.gl=ogl.gl;
	  for (int i = 0; i< cubes.length; i++){
	    cubes[i] = new Cube(
	    		Math.round(random(-100, 100)), 
	    		Math.round(random(-100, 100)), 
	    		Math.round(random(-100, 100)),
	    		Math.round(random(-140, 140)), 
	   			Math.round(random(-140, 140)), 
				Math.round( random(-140, 140))
	    );
	    cubes[i].rFact = (float)Math.random()/10f;
	  }
	  
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
	  
	}

	public void draw(){
	  background(0); 
	  fill(200);

	  //set up some different colored lights
	  pointLight(51, 102, 255, 65, 60, 100); 
	  pointLight(200, 40, 60, -65, -60, -150);

	  //raise overall light in scene 
	  ambientLight(70, 70, 10); 
	  
	  GLSLProgram gProgram = ogl.getProgram("glass");
	  boolean vertexShaderEnabled =(gProgram!=null);
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
	  
	  /*center geometry in display windwow.
	   you can change 3rd argument ('0')
	   to move block group closer(+)/further(-)*/
	  translate(width/2, height/2,100);
	  
	  //rotate around y and x axes
	  //rotateY(radians(ang));
	  rotateY( mouseX/240f );
	  stroke(color(255,255,255,50));
	  //draw cubes
	  for (int i = 0; i< cubes.length; i++){
		  pushMatrix();
		  translate( cubes[i].shiftX*mouseX/100,  cubes[i].shiftY*mouseY/100, cubes[i].shiftZ*mouseX/100);
		  fill(color(cubes[i].shiftX*100+150,  cubes[i].shiftY*100+150, cubes[i].shiftZ*100+150,200));
		  cubes[i].drawCube();
		  popMatrix();
	  }
	  //used in rotate function calls above
	  ang++;
	}
	
	
	//simple Cube class, based on Quads
	class Cube {

	  //properties
	  int w, h, d;
	  int shiftX, shiftY, shiftZ;

	  float rotate=0f;
	  float rFact=0.01f;
	  int listId=0;
	  //constructor
	  Cube(int w, int h, int d, int shiftX, int shiftY, int shiftZ){
	    this.w = w;
	    this.h = h;
	    this.d = d;
	    this.shiftX = shiftX;
	    this.shiftY = shiftY;
	    this.shiftZ = shiftZ;
	    initCube();
	  }

	  /*main cube drawing method, which looks 
	   more confusing than it really is. It's 
	   just a bunch of rectangles drawn for 
	   each cube face*/
	  void drawCube(){
		  
		/*
	    beginShape(QUADS);
	    //front face
	    vertex(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, h + shiftY, -d/2 + shiftZ); 
		    vertex(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 

		    //back face
		    vertex(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
		    vertex(w + shiftX, -h/2 + shiftY, d + shiftZ); 
		    vertex(w + shiftX, h + shiftY, d + shiftZ); 
		    vertex(-w/2 + shiftX, h + shiftY, d + shiftZ);

		    //left face
		    vertex(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		    vertex(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
		    vertex(-w/2 + shiftX, h + shiftY, d + shiftZ); 
		    vertex(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 

		    //right face
		    vertex(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, -h/2 + shiftY, d + shiftZ); 
		    vertex(w + shiftX, h + shiftY, d + shiftZ); 
		    vertex(w + shiftX, h + shiftY, -d/2 + shiftZ); 

		    //top face
		    vertex(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, -h/2 + shiftY, d + shiftZ); 
		    vertex(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 

		    //bottom face
		    vertex(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, h + shiftY, -d/2 + shiftZ); 
		    vertex(w + shiftX, h + shiftY, d + shiftZ); 
		    vertex(-w/2 + shiftX, h + shiftY, d + shiftZ); 
	    endShape(); 
		*/
		  rotate+=rFact;
		  rotateX(rotate);
		  //System.out.println("list : "+this.listId+":"+gl.glIsList(this.listId));
		  //gl.glPushMatrix();
		 // gl.glRotatef(rotate, 0, 0, 0);
			 
		 // beginShape(QUADS);
		//  gl.glBegin(GL.GL_QUADS);
		//  	gl.glCallList(this.listId);
		//  	gl.glEnd();
		 // endShape(); 
		 // gl.glPopMatrix();
		  box(w,h,d);
		  
		  
	    //add some rotation to each box for pizazz.
	    rotateY(radians(1));
	    rotateX(radians(1));
	    rotateZ(radians(1));
	  }
	  void initCube(){
		  this.listId = gl.glGenLists(2);
		  gl.glNewList(this.listId, GL.GL_COMPILE_AND_EXECUTE);
		  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, h + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 

		  //back face
		  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
		  gl.glVertex3i(w + shiftX, -h/2 + shiftY, d + shiftZ); 
		  gl.glVertex3i(w + shiftX, h + shiftY, d + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, h + shiftY, d + shiftZ);

		  //left face
		  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, h + shiftY, d + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 

		  //right face
		  gl.glVertex3i(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, -h/2 + shiftY, d + shiftZ); 
		  gl.glVertex3i(w + shiftX, h + shiftY, d + shiftZ); 
		  gl.glVertex3i(w + shiftX, h + shiftY, -d/2 + shiftZ); 

		  //top face
		  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, -h/2 + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, -h/2 + shiftY, d + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, -h/2 + shiftY, d + shiftZ); 

		  //bottom face
		  gl.glVertex3i(-w/2 + shiftX, h + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, h + shiftY, -d/2 + shiftZ); 
		  gl.glVertex3i(w + shiftX, h + shiftY, d + shiftZ); 
		  gl.glVertex3i(-w/2 + shiftX, h + shiftY, d + shiftZ); 
		  gl.glEndList();
	  }
	}


}
