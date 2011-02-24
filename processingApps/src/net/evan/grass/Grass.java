package net.evan.grass;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import processing.core.PApplet;

public class Grass extends PApplet {
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	GLUquadric quadric;
	
	
	float noiseVal;
	float noiseScale = 0.005f;
	float noiseCount = 0.0f;
	float noiseSpeed = 0.05f;

	float xCount, xSpeed;
	float yCount, ySpeed;

	float theta;
	float angle;
	float angleDelta;
	float xv, yv;
	float speed = 10;
	
	float grassWid = 3000;
	float grassHgt = 2000;
	
	boolean lines = false;

	int noiseRes =3;

	
	
	Blade[][] vehicle;

	int xSize = 512;
	int ySize = 512;

	int xTotal = (int)(xSize / noiseRes);
	int yTotal = (int)(ySize / noiseRes);
	
	
	int WIDTH = 640;
	int HEIGHT = 480;
	public void setup()	{
	    size(WIDTH, HEIGHT, OPENGL);  
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
	    s = new Shape(gl);
	    
	    vehicle = new Blade[yTotal][xTotal];
	    for (int y=0; y<yTotal; y++){
	    	gl.glRotatef(40, 1, 0, 0);
	      for (int x=0; x<xTotal; x++){
	        vehicle[y][x] = new Blade(x, y);
	      }
	    }
	}
	
	public void draw() {
		background(0);
		gl.glTranslatef(-grassWid/2,200, -400f);
		gl.glRotatef(-80, 1, 0, 0);
		gl.glColor4f(0f, 0.6f, 0f, 0.8f);
		
		xSpeed = ((width/2) - mouseX)/10.0f;
		  ySpeed = ((height/2) - mouseY)/10.0f;

		  xCount += xSpeed;
		  yCount += ySpeed;


		  float fx = (float)mouseX / (float)width;
		  float fy = (float)mouseY / (float)height;
		  
		  noiseDetail(2, .5f);
		  noiseScale -= (noiseScale - 0.005) * .2;
			
		  noiseCount += noiseSpeed;
		  for (int y=0; y<yTotal; y++){
		    for (int x=0; x<xTotal; x++){
		      vehicle[y][x].exist();
		    }
		  }
	}
	
	class Blade {
		  float x;
		  float y;

		  float xv, yv;
		  float xf, yf;

		  float theta;
		  float angle;
		  float angleDelta;
		  float speed = random(2.0f, 10.0f);

		  Blade (int xSent, int ySent){
		    x = xSent * noiseRes;
		    y = ySent * noiseRes;
		  }

		  void exist(){
		    findVelocity();
		    move();
		    render();
		  }

		  void findVelocity(){
		    noiseVal=noise((x - xCount)*noiseScale, (y - yCount)*noiseScale, noiseCount);
		    angle -= (angle - noiseVal*720.0f) * .4f;
		    theta = -(angle * PI)/180.0f;
		    xv = cos(theta) * speed;
		    yv = sin(theta) * speed;
		  }

		  void move(){
		    x -= xv;
		    y -= yv;
/*
		    if (x < -50){
		      x += width + 100;
		    } 
		    else if (x > width + 50){
		      x -= width + 100;
		    }

		    if (y < -50){
		      y += height + 100;
		    } 
		    else if (y > height + 50){
		      y -= height + 100;
		    }
*/
		    if (x < 0){    x += grassWid ;    } 
			else if (x > grassWid ){      x -= grassWid ;    }

			 if (y < 0){     y += grassHgt ;    } 
			 else if (y > grassHgt ){     y -= grassHgt ;    }
		  }

		  void render(){
		    float l = 5f;
		    //stroke((angle - 180)/3, 100,360);
		    
		    //fill((angle - 180)/3, 300, 240, 220);
		    
		    gl.glBegin(GL.GL_TRIANGLES);
			    gl.glVertex3f(x,y,0);
			    gl.glVertex3f(x-(xv*l), y-(yv*l),-50);
			    gl.glVertex3f(x+(xv*0.5f), y-(yv*0.5f),0);
			gl.glEnd();
		    
		  }
		}
}
