package net.robmunro.perform.ol5;

import javax.media.opengl.GL;

import processing.core.PApplet;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;

public class SpaceJunk {
	PApplet p;
	OpenGL ogl;
    GL gl;
    Shape s;
    int size = 300;
    Shape.Cube[]cubes;
    Vector3D[] cubRotationFactor ;
    Vector3D[] cubRotation ;
    Vector3D[] cubColor ;
    Vector3D globalRotation;
    Vector3D expansion;
    float selectRangeStart=-1;;
	public SpaceJunk(PApplet p, OpenGL ogl,int size) {
		super();
		this.p = p;
		this.ogl = ogl;
		 this.gl=ogl.gl;
		 this.s = new Shape(gl,p);
		 this.size=size;
		 this.cubes = new Shape.Cube[size];
		 this.cubRotationFactor = new Vector3D[size];
		 this.cubRotation = new Vector3D[size];
		 this.cubColor = new Vector3D[size];
		 globalRotation = new Vector3D();
		 expansion = new Vector3D();
	}

	public Vector3D getRotation() {
		return globalRotation;
	}

	public void setRotation(Vector3D rotation) {
		this.globalRotation = rotation;
	}

	public Vector3D getExpansion() {
		return expansion;
	}

	public void setExpansion(Vector3D expansion) {
		this.expansion = expansion;
	}

	public void setup(){
			//instantiate cubes, passing in random vals for size and postion
		  gl.glEnable(GL.GL_BLEND);
		  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		  for (int i = 0; i< cubes.length; i++){
		    cubes[i] = this.s.new Cube(
		    		Math.round(p.random(-100, 100)), 
		    		Math.round(p.random(-100, 100)), 
		    		5,//Math.round(random(-100, 100)),
		    		Math.round(p.random(-10, 10)), 
		   			Math.round(p.random(-10, 10)), 
					Math.round( p.random(-10, 10))
		    );
		    cubRotation[i]=new Vector3D();
		    cubRotationFactor[i]=new Vector3D();
		    cubRotationFactor[i].x = (float)Math.random()/2f;
		    cubRotationFactor[i].y = (float)Math.random()/2f;
		    cubRotationFactor[i].z = (float)Math.random()/2f;
		    
		    cubColor[i]=new Vector3D();
		    cubColor[i].x = (float)Math.random();
		    cubColor[i].y = (float)Math.random();
		    cubColor[i].z = (float)Math.random();
		  }
		  
		  try {
			ogl.makeProgram(
						"glass",
						new String[] {},
						new String[] {"SpecularColor1","SpecularColor2","SpecularFactor1","SpecularFactor2","LightPosition"}, //"GlassColor",
						ogl.loadGLSLShaderVObject(	"resources/robmunro/perform/ol5/glass_c.vert"  ), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/perform/ol5/glass_c.frag"	)
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void draw(){
		  GLSLProgram gProgram = ogl.getProgram("glass");
		  boolean vertexShaderEnabled =(gProgram!=null);// 
		  if (vertexShaderEnabled) {
			  gl.glUseProgramObjectARB(gProgram.getProgramObject());
			  // gl.glUseProgramObjectARB(gfProgram.getProgramObject());
			  gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 0f, 0f,  5f);
			     // unconmment for orig glass func
			    // gl.glUniform4fARB(gProgram.getUniformId("GlassColor"), mouseX/(float)width, mouseY/(float)height, 0.6f, 0.15f);
			  gl.glUniform4fARB(gProgram.getUniformId("SpecularColor1"),  .1f, 1f, 0.1f, 0.9f);
			  gl.glUniform4fARB(gProgram.getUniformId("SpecularColor2"),  .5f, 0.1f, 1f, 0.9f);
			  gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor1"),3f) ;
			  gl.glUniform1fARB(gProgram.getUniformId("SpecularFactor2"),5f);
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
		  gl.glRotatef( globalRotation.x, 1, 0, 0);
		  gl.glRotatef( globalRotation.y, 0, 1, 0);
		  gl.glRotatef( globalRotation.z, 0, 0, 1);
		  
		  p.stroke(p.color(255,255,255,50));
		  
		  //draw cubes
		  //gl.glLineWidth(20);
		  for (int i = 0; i< cubes.length; i++){
			  gl.glPushMatrix();
				 gl.glTranslatef( cubes[i].shiftX*expansion.x,  cubes[i].shiftY*expansion.y, cubes[i].shiftZ*expansion.z );
				  gl.glColor4f( cubColor[i].x,  cubColor[i].z, cubColor[i].z, .1f );
				  gl.glPushMatrix();
				  		cubRotation[i].add(cubRotationFactor[i]);
					  	gl.glRotatef( cubRotation[i].x, 1, 0, 0 );
					  	gl.glRotatef( cubRotation[i].y, 0, 1, 0 );
					  	gl.glRotatef( cubRotation[i].z, 0, 0, 1 );
					  	 if (selectRangeStart>0 && cubColor[i].x>selectRangeStart && cubColor[i].x<selectRangeStart+0.033 ) {
							  gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
						 } else {
							  gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
						 }
					  	cubes[i].draw();
					  	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				  gl.glPopMatrix();
			  gl.glPopMatrix();
		  }
		  gl.glPopMatrix();
		}

	public void setWireFrameColourSelect(float selRangeStart) {
		selectRangeStart=selRangeStart;
		
	}
	
}
