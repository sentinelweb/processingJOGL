package net.evan;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class FBOTest extends PApplet {

	PGraphicsOpenGL pgl;
	GL gl;
	GLU glu;
	 
	int[] drawTex = { 0 };
	int[] drawFBO = { 0 };
	 
	int texWidth = 320;
	int texHeight = 240;
	 
	 
	GLUquadric quadric; 
	 
	public void setup()
	{
	    size(640, 480, OPENGL);  
	   
	    pgl = (PGraphicsOpenGL) g;  // g may change
	    gl = pgl.gl;  // always use the GL object returned by beginGL    
	   
	    glu = pgl.glu;
	    
	   quadric = glu.gluNewQuadric();
	   
	    // Creating texture.
	    gl.glGenTextures(1, drawTex, 0);    
	    gl.glBindTexture(GL.GL_TEXTURE_2D, drawTex[0]);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
	    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texWidth, texHeight, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, null);  
	     
	    // Creating FBO.
	    gl.glGenFramebuffersEXT(1, drawFBO, 0);
	    gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, drawFBO[0]);
	    gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, drawTex[0], 0);
	    int stat = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
	    if (stat != GL.GL_FRAMEBUFFER_COMPLETE_EXT) System.out.println("FBO error");

	    gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	    noLoop();
	    
	}
	 
	public void draw()
	{    
	    PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;  // g may change
	    gl = pgl.beginGL();  // always use the GL object returned by beginGL
	 
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); 
	 
	    // Binding FBO.
	    gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, drawFBO[0]);
	     
	    // Drawing to the first color attachement of drawFBO (this is where drawTex is attached to).
	    gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);    
	 
	    // Setting orthographic projection.    
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPushMatrix();    
	    gl.glLoadIdentity();
	    gl.glOrtho(0.0, texWidth, 0.0, texHeight, -100.0, +100.0);
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPushMatrix();    
	    gl.glLoadIdentity();
	     
	    gl.glViewport(0, 0, texWidth, texHeight);
	 
	    // draw stuff
	    gl.glDisable(GL.GL_TEXTURE_2D);
	        
	    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
	     
	    gl.glBegin(GL.GL_QUADS);
	    gl.glVertex2f(0.0f, 0.0f);
	    gl.glVertex2f((float)texWidth, 0.0f);
	    gl.glVertex2f((float)texWidth, (float)texHeight);
	    gl.glVertex2f(0.0f, (float)texHeight);
	    gl.glEnd();
	     
	    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    
	    gl.glBegin(GL.GL_QUADS);
	    gl.glVertex2f(10.0f, 10.0f);
	    gl.glVertex2f(20.0f, 10.0f);
	    gl.glVertex2f(20.0f, 20.0f);
	    gl.glVertex2f(10.0f, 20.0f);
	    gl.glEnd();
	    
	    gl.glTranslatef((float)mouseX/(float)width*20f, 0, 0);
		  
	    displayObjects(); 
	     
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPopMatrix();  
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPopMatrix();    

	    // Unbinding drawFBO. Now drawing to screen again.
	    gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);    
	 
	    // Setting orthographic projection.  
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPushMatrix();    
	    gl.glLoadIdentity();
	    gl.glOrtho(0.0, width, 0.0, height, -100.0, +100.0);
	    gl.glMatrixMode(GL.GL_MODELVIEW);  
	    gl.glPushMatrix();    
	    gl.glLoadIdentity();   
	    gl.glViewport(0, 0, width, height);
	    gl.glEnable(GL.GL_TEXTURE_2D);
	    gl.glActiveTexture(GL.GL_TEXTURE0);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, drawTex[0]);

	   for (int i=0; i<3; i++)
	    for (int j=0; j<3; j++)
	    {
	 
	      float wm = (float)i/3;
	      float hm = (float)j/3;
	      
	      float wmn = (float)(i+1)/3;
	      float hmn = (float)(j+1)/3;
	      
	      println("wm:" + wm*(float)width);
	      println("wmn:" + wmn*(float)width);
	      println("hm:" + hm*(float)height);
	      
	      
	    // Drawing texture to screen.

	    gl.glBegin(GL.GL_QUADS);
	  gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 gl.glTexCoord2f(0.0f, 1.0f);
	 gl.glVertex2f(wm*(float)width, hm*(float)height);
	 
	 gl.glTexCoord2f(1.0f, 1.0f);
	 gl.glVertex2f(wmn*(float)width, hm*(float)height);
	 
	 gl.glTexCoord2f(1.0f, 0.0f);
	gl.glVertex2f(wmn*(float)width, hmn*(float)height);
	 
	 gl.glTexCoord2f(0.0f, 0.0f);
	gl.glVertex2f(wm*(float)width, hmn*(float)height);


	    gl.glEnd();
	}

	    
	    
	    gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	   
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPopMatrix();  
	    
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPopMatrix();    
	 
	    pgl.endGL();
	}



	void displayObjects() 
	{ 
	  float torus_diffuse[] = { 
	    0.7f, 0.7f, 0.0f, 1.0f     }; 
	  float cube_diffuse[] = { 
	    0.0f, 0.7f, 0.7f, 1.0f     }; 
	  float sphere_diffuse[] = { 
	    0.7f, 0.0f, 0.7f, 1.0f     }; 
	  float octa_diffuse[] = { 
	    0.7f, 0.4f, 0.4f, 1.0f     }; 

	  gl.glLoadIdentity(); 
	  gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	  
	  gl.glPushMatrix();
	  gl.glTranslatef (150.0f, 50.0f, 0.0f); 
	  gl.glRotatef (45.0f, 1.0f, 0.0f, 0.0f); 
	  gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, cube_diffuse,0); 
	 
	  float sc = 50f;
	  
	  gl.glBegin(GL.GL_QUADS);
	  
	  gl.glVertex3f(0.0f, 0.0f, 0.0f);
	  gl.glVertex3f(sc, 0.0f, 0.0f);
	  gl.glVertex3f(sc, sc, 0.0f);
	  gl.glVertex3f(0.0f, sc, 0.0f);
	  
	  gl.glVertex3f(sc, 0.0f, 0.0f);
	  gl.glVertex3f(sc, 0.0f, sc);
	  gl.glVertex3f(sc, sc, sc);
	  gl.glVertex3f(sc, sc, 0.0f);
	  
	  gl.glEnd();
	  gl.glPopMatrix (); 
	  
	  gl.glPushMatrix (); 
	  gl.glTranslatef (50.0f, 50.0f, 0.0f); 
	  gl.glRotatef (30.0f, 1.0f, 0.0f, 0.0f); 
	  gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, sphere_diffuse,0); 
	  gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	  glu.gluSphere(quadric, 100f, 80, 20);
	  gl.glPopMatrix (); 
	  
	} 
}
