package net.robmunro.proc_ex;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import processing.core.PApplet;

// doesnt work :(

public class Esfera extends PApplet {
	
	int count = 8000;
	Hair[] lista ;
	float[] z = new float[count]; 
	float[] phi = new float[count]; 
	float[] largos = new float[count]; 
	float radius = 200;
	float rx = 0;
	float ry =0;
	
	GL gl;
	OpenGL ogl ;
	GLU glu;
	public void setup()	{
	  size(640, 480, OPENGL);
	  radius = height/3.5f;
	  ogl=new OpenGL(this);
	  this.gl=ogl.gl;
	  glu = new GLU();
	  
	  lista = new Hair[count];
	  for (int i=0; i<count; i++){
	    lista[i] = new Hair();
	  }
	  noiseDetail(3);
	}

	public void draw()
	{
	  background(0);
	 // translate(width/2,height/2);
	  gl.glTranslatef(0, 0 ,-100);
		 
	  float rxp = ((mouseX-(width/2))*0.005f);
	  float ryp = ((mouseY-(height/2))*0.005f);
	  rx = (rx*0.9f)+(rxp*0.1f);
	  ry = (ry*0.9f)+(ryp*0.1f);
	  gl.glRotatef(degrees(rx), 1,0,0);
	  //rotateY(rx);
	  gl.glRotatef(degrees(ry), 0,1,0);
	  //rotateX(ry);
	  //fill(0);
	  //noStroke();
	  glu.gluSphere(glu.gluNewQuadric(), 200, 20, 30);
	  //sphere(radius);

	  for (int i=0;i<count;i++){
	    lista[i].draw();

	  }
	  gl.glFlush();
	  gl.glFinish();
	}


	 class Hair
	{
	  float z = random(-radius,radius);
	  float phi = random(TWO_PI);
	  float largo = random(1.15f,1.2f);
	  float theta = asin(z/radius);

	 void draw(){

	    float off = (noise(millis() * 0.0005f,sin(phi))-0.5f) * 0.3f;
	    float offb = (noise(millis() * 0.0007f,sin(z) * 0.01f)-0.5f) * 0.3f;

	    float thetaff = theta+off;
	    float phff = phi+offb;
	    float x = radius * cos(theta) * cos(phi);
	    float y = radius * cos(theta) * sin(phi);
	    float z = radius * sin(theta);
	    float msx= screenX(x,y,z);
	    float msy= screenY(x,y,z);

	    float xo = radius * cos(thetaff) * cos(phff);
	    float yo = radius * cos(thetaff) * sin(phff);
	    float zo = radius * sin(thetaff);

	    float xb = xo * largo;
	    float yb = yo * largo;
	    float zb = zo * largo;
	    
	    //beginShape(LINES);
	    gl.glBegin(GL.GL_LINE);
	    //stroke(0);
	    gl.glColor3f(1,1,0);
	    gl.glVertex3f(x,y,z);
	    //vertex(x,y,z);
	    //stroke(200,150);
	    gl.glColor3f(1,1,1);
	    gl.glVertex3f(xb,yb,zb);
	    //vertex(xb,yb,zb);
	    gl.glEnd();
	    //endShape();
	  }
	}


}
