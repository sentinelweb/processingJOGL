package net.robmunro.test;
import javax.media.opengl.GL;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.MotionBlur;
import processing.opengl.*;

import processing.core.PApplet;

public class SpaceJunk2 extends PApplet {
	
	//cube count-lower/raise to test P3D/OPENGL performance
	int limit =200;

	//array for all cubes
	Cube[] cubes = new Cube[limit];
	//Sphere[] cubes = new Sphere[limit];
	GL gl;
	OpenGL ogl ;
	MotionBlur mblur=null;
	public void setup(){
	  //try substituting P3D for OPENGL 
	  //argument to test performance
	  size(1200, 800, OPENGL); 
	  background(0); 

	  //instantiate cubes, passing in random vals for size and postion
	  ogl=new OpenGL(this);
	  this.gl=ogl.gl;
	  mblur=new MotionBlur(gl);
	  mblur.clearAccum();
	  gl.glEnable(GL.GL_BLEND);
	  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	  for (int i = 0; i< cubes.length; i++){
	    cubes[i] = new Cube(
	    		Math.round(random(-100, 100)), 
	    		Math.round(random(-100, 100)), 
	    		5,//Math.round(random(-100, 100)),
	    		Math.round(random(-10, 10)), 
	   			Math.round(random(-10, 10)), 
				Math.round( random(-10, 10))
	    );
	    cubes[i].rFact = (float)Math.random()/10f;
		  /*
		  cubes[i] = new Sphere( Math.round( random(-100, 100)),
				  Math.round(random(-10, 10)), 
		   			Math.round(random(-10, 10)), 
					Math.round( random(-10, 10)));
		*/
	  }
	  
	  try {
		ogl.makeProgram(
					"glass",
					new String[] {},
					new String[] {"SpecularColor1","SpecularColor2","SpecularFactor1","SpecularFactor2","LightPosition"}, //"GlassColor",
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glass_c.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glass_c.frag"	)
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
	 // gl.glClearAccum(0f,0f,0f,0f); // for moion blur
		
	  GLSLProgram gProgram = ogl.getProgram("glass");
	  boolean vertexShaderEnabled =(gProgram!=null);// false;//
	  if (vertexShaderEnabled) {
		  // gl.glDeleteObjectARB(wavesProgram.getProgramObject());

		     gl.glUseProgramObjectARB(gProgram.getProgramObject());
		    // gl.glUseProgramObjectARB(gfProgram.getProgramObject());
		     
		     gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 0f, 0f,  5f);
		     // unconmment for orig glass func
		    // gl.glUniform4fARB(gProgram.getUniformId("GlassColor"), mouseX/(float)width, mouseY/(float)height, 0.6f, 0.15f);
		     gl.glUniform4fARB(gProgram.getUniformId("SpecularColor1"),  .1f, 0.1f, 0.1f, 1f);
		    gl.glUniform4fARB(gProgram.getUniformId("SpecularColor2"),  .1f, 0.1f, 0.1f, 1f);
		    gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor1"),2f) ;
		     gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor2"),2f);
		   }
	  
	  /*center geometry in display windwow.
	   you can change 3rd argument ('0')
	   to move block group closer(+)/further(-)*/
	  gl.glPushMatrix();
	  
	 // gl.glTranslatef(width/2, height/2,-100);
	  gl.glTranslatef(0, 0 ,-1000);
	  //rotate around y and x axes
	  //rotateY(radians(ang));
	 //rotateY( mouseX/240f );
	  gl.glRotatef( mouseX, mouseY, 0, 0);
	  stroke(color(255,255,255,50));
	  //draw cubes
	  
	  for (int i = 0; i< cubes.length; i++){
		  gl.glPushMatrix();
			 
		  gl.glTranslatef( cubes[i].shiftX*(float)mouseX,  cubes[i].shiftY*(float)mouseY, cubes[i].shiftZ*(float)mouseX);
		  gl.glColor4f(cubes[i].shiftX*.5f+.5f,  cubes[i].shiftY*.5f+.5f, cubes[i].shiftZ*.5f+.5f,.1f);
		  cubes[i].drawCube();
		  gl.glPopMatrix();
	  }
	  gl.glPopMatrix();
	  
	  mblur.blur(0.9f);
	    
	}
	
	
	//simple Cube class, based on Quads
	class Cube {

	  //properties
	  int w, h, d;
	  int shiftX, shiftY, shiftZ;

	  float rotate=0f;
	  float rFact=5f;
	  int listId=0;
	  int sDetail=50;
	  //constructor
	  Cube(int w, int h, int d, int shiftX, int shiftY, int shiftZ){
	    this.w = w;
	    this.h = h;
	    this.d = d;
	    this.shiftX = shiftX;
	    this.shiftY = shiftY;
	    this.shiftZ = shiftZ;
	    initCube();
	    rFact=50f+(float)Math.random()*20f;
	  }

	  void drawCube(){

		  rotate+=rFact*10f;
		   gl.glPushMatrix();
		  gl.glRotatef( rotate, 0,10,0);
		  
			 
		  gl.glBegin(GL.GL_QUADS);
		  		gl.glCallList(this.listId);
		  gl.glEnd();
		 
		  gl.glPopMatrix();
	  }
	  void initCube(){
		  this.listId = gl.glGenLists(1);
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
	
	/*
	 * This doesnt seem to translate - but it draws, ... might be useful later
	 * 
	 */
	class Sphere {
		int listId=0;
		int sDetail=50;
		float[] cx,cz,sphereX,sphereY,sphereZ;
		float sinLUT[];
		float cosLUT[];
		float SINCOS_PRECISION = 0.5f;
		int SINCOS_LENGTH = (int) (360.0f / SINCOS_PRECISION);
		int r;
		
		float rotate=0f;
		  float rFact=5f;
		  int shiftX, shiftY, shiftZ;
		Sphere (int r,int shiftX, int shiftY, int shiftZ) {
			this.r=r;
			init();
			initSphere();
		}
		void init() {
			
			  sinLUT = new float[SINCOS_LENGTH];
			  cosLUT = new float[SINCOS_LENGTH];

			  for (int i = 0; i < SINCOS_LENGTH; i++) {
			    sinLUT[i] = (float) Math.sin(i * PApplet.DEG_TO_RAD * SINCOS_PRECISION);
			    cosLUT[i] = (float) Math.cos(i * PApplet.DEG_TO_RAD * SINCOS_PRECISION);
			  }

			  float delta = (float)SINCOS_LENGTH/sDetail;
			  float[] cx = new float[sDetail];
			  float[] cz = new float[sDetail];
			  
			  // Calc unit circle in XZ plane
			  for (int i = 0; i < sDetail; i++) {
			    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
			    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
			  }
			  
			  // Computing vertexlist vertexlist starts at south pole
			  int vertCount = sDetail * (sDetail-1) + 2;
			  int currVert = 0;
			  
			  // Re-init arrays to store vertices
			  sphereX = new float[vertCount];
			  sphereY = new float[vertCount];
			  sphereZ = new float[vertCount];
			  float angle_step = (SINCOS_LENGTH*0.5f)/sDetail;
			  float angle = angle_step;
			  
			  // Step along Y axis
			  for (int i = 1; i < sDetail; i++) {
			    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
			    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
			    for (int j = 0; j < sDetail; j++) {
			      sphereX[currVert] = cx[j] * curradius;
			      sphereY[currVert] = currY;
			      sphereZ[currVert++] = cz[j] * curradius;
			    }
			    angle += angle_step;
			  }
			
		}
		void initSphere(){
			 int v1,v11,v2;
			  this.listId = gl.glGenLists(1);
			  gl.glNewList(this.listId, GL.GL_COMPILE_AND_EXECUTE);
				  float iu=(float)(width-1)/(sDetail);
				  float iv=(float)(height-1)/(sDetail);
				  float u=0,v=iv;
				  for (int i = 0; i < sDetail; i++) {
				    gl.glVertex3f(0, -r, 0);
				    gl.glVertex3f(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r);
				    u+=iu;
				  }
				  gl.glVertex3f(0, -r, 0);
				  gl.glVertex3f(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r);
				 // pa.endShape();   
				  
				  // Middle rings
				  int voff = 0;
				  for(int i = 2; i < sDetail; i++) {
				    v1=v11=voff;
				    voff += sDetail;
				    v2=voff;
				    u=0;
				   // pa.beginShape(PApplethis.TRIANGLE_STRIP);
				   // pa.texture(t);
				    for (int j = 0; j < sDetail; j++) {
				    	gl.glVertex3f(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r);
				    	gl.glVertex3f(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r);
				      u+=iu;
				    }
				  
				    // Close each ring
				    v1=v11;
				    v2=voff;
				    gl.glVertex3f(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r);
				    gl.glVertex3f(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r);
				    v+=iv;
				  }
				  u=0;
				  
				  // Add the northern cap
				  //pa.beginShape(PApplethis.TRIANGLE_STRIP);
				  //pa.texture(t);
				  for (int i = 0; i < sDetail; i++) {
				    v2 = voff + i;
				    gl.glVertex3f(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r);
				    gl.glVertex3f(0, r, 0);    
				    u+=iu;
				  }
				  gl.glVertex3f(0, r, 0);
				  gl.glVertex3f(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r);
			  gl.glEndList();
		  }
		
		void drawCube(){

			  gl.glPushMatrix();
			  gl.glRotatef( rotate, 0,10,0);
			  
			  
			  gl.glBegin(GL.GL_TRIANGLE_STRIP);
			  		gl.glCallList(this.listId);
			  gl.glEnd();
			    gl.glPopMatrix();
		  }
	}

}
