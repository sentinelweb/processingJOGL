package net.robmunro.example;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import processing.opengl.*;
import javax.media.opengl.*;
import com.sun.opengl.util.texture.*
;
public class TransparencyEx extends PApplet {

/*
 * NOTES :
 * This example works fairly well except that the object must be drawn in the order of depth first (back to front)
 * Hopefully there is a way to setup the depth buffer or something.
 */
	PGraphicsOpenGL pgl;
	GL gl;
	Texture glParticle;
	int squareList; // set by initGL

	public void setup() {
	  size(500, 500, OPENGL);
	  // OpenGL
	  pgl = (PGraphicsOpenGL)g;
	  gl = pgl.gl;
	  gl.setSwapInterval(1);
	  initGL();
	  // texture
	  try { glParticle = TextureIO.newTexture(new File("/home/robm/processing/processing-1.0/processingApps/src/resources/robmunro/perform/ol5/sparkle.png"), true); }
	  catch (IOException e) { exit(); }
	  // draw some particles
	  drawSomeParticles();
	}

	public void draw() {}

	public void mouseReleased() { drawSomeParticles(); }

	void initGL() {
	  pgl.beginGL();
	  squareList = gl.glGenLists(1);
	  gl.glNewList(squareList, GL.GL_COMPILE);
	  gl.glBegin(GL.GL_POLYGON);
	  gl.glTexCoord2f(0, 0); gl.glVertex2f(-.5f, -.5f);
	  gl.glTexCoord2f(1, 0); gl.glVertex2f(.5f, -.5f);
	  gl.glTexCoord2f(1, 1); gl.glVertex2f(.5f, .5f);
	  gl.glTexCoord2f(0, 1); gl.glVertex2f(-.5f, .5f);
	  gl.glEnd();
	  gl.glEndList();
	  pgl.endGL();
	  //gl.glDepthMask(false);
	}

	void drawSomeParticles() {
	  background(0);
	  gl.glEnable(GL.GL_DEPTH_TEST);
	  pgl.beginGL();
	  glParticle.bind();
	  glParticle.enable();
	  for (int i=0; i<30; i++){
	    //drawOneParticle(random(width), random(height),  random(height)-height   , 80);//    -400 +i*13 
	    drawOneParticle(random(width), random(height),  -400 +i*13    , 80);//   
	  }
	  glParticle.disable();
	  pgl.endGL();
	}

	void drawOneParticle(float x, float y, float z, float diam) {
	  gl.glPushMatrix();
	  gl.glTranslatef(x, y, z);
	  gl.glScalef(diam, diam, diam);
	  gl.glCallList(squareList);
	  gl.glPopMatrix();
	}
	
}
