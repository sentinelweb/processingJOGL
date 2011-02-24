package net.robmunro.test;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import net.robmunro.lib.IFS;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import processing.core.PApplet;

public class TreeExample extends PApplet {
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	IFS ifsGen;
	double[][] fernLeafScale = {{320.0, 1.0/8*640},{440.0, -1.0f/12*480}};
	double[][] mapleLeafScale = {{320.0,1.0/8*640},{240.0,- 1.0f/12*480}};
	float theta;   
	GLUquadric quadric;
	int listId=-1;
	public void setup()	{
	    size(640, 480, OPENGL);  
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
	    s = new Shape(gl);
	    ifsGen = new IFS();
	    
	    //gl.glEnable(GL.GL_ALPHA);
	    //BufferedImage tex = ifsGen.doIFS(ifsGen.fernLeafIFS, 100000, fernLeafScale, 640, 480);
	    BufferedImage tex = ifsGen.doIFS(ifsGen.mapleLeafIFS, 100000, mapleLeafScale, 640, 480);
	    tex = makeTransparent(tex,Color.black);
	    ogl.loadTexture("leaf", tex);
	    quadric = glu.gluNewQuadric();
	    glu.gluQuadricTexture(quadric, true);
	    //ogl.loadTexture("leaf", "/home/robm/leaf.png");
	    ogl.loadTexture("bark", this.getClass().getResource("/resources/robmunro/test/tree/bark512x512.jpg").getFile());//"/home/robm/download/opengl/texture/bark512x512.jpg"
	    drawTreeList(500,45);
	}
	
	public BufferedImage makeTransparent(BufferedImage image, Color col){
        int width = image.getWidth();
        int height = image.getHeight();
        Color temp = null;
        BufferedImage newImage = new BufferedImage(width,
              height, BufferedImage.TYPE_4BYTE_ABGR);
        int color = -1;
        if(col != null) color = col.getRGB();
        for(int i = 0; i < width; i++){
              for(int j = 0; j < height; j++){
                    int k = image.getRGB(i, j);
                    if(col != null){
                          if(k == color){      
                                newImage.setRGB(i, j, 0x00000000);
                          }
                          else{
                                newImage.setRGB(i, j, k);
                          }
                    }
                    else newImage.setRGB(i, j, k);
              }
        }      
        return newImage;
  }
	
	int rotate=0;
	public void draw()	{
		rotate++;
		background(0);
		theta = (mouseX / (float) width) * 90f;
		float h = (mouseY / (float) height) * 500f;
		gl.glColor4f(0.5f, 0.5f, 0, 0.8f);
		gl.glTranslatef(0f, 550f ,-1200f);
		//gl.glTranslatef(0f, 250f ,-80f);
		gl.glRotatef(rotate, 0, 1,  0);   // Rotate by theta
		gl.glRotatef(90, 1, 0,  0);   // Rotate by theta
		//gl.glDisable(GL.GL_TEXTURE_2D);   
		
		long st = System.currentTimeMillis();
		//drawTree(h,theta);
		//drawTree(500,theta);
		gl.glCallList(this.listId);
		println(System.currentTimeMillis()-st);
	}
	private void drawTree(float h, float theta) {
		invokeTree(h,theta);
	}
	private void drawTreeList(float h, float theta) {
		this.listId = gl.glGenLists(1);
		gl.glNewList(this.listId, GL.GL_COMPILE);
			invokeTree(h,theta);
		gl.glEndList();
		
	}
	private void invokeTree(float h, float theta) {
		gl.glEnable(GL.GL_TEXTURE_2D);   
		gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("bark"));
		glu.gluCylinder(quadric, 20, 20, h, 10, 1);
		branch(h,theta);
	}
	int depth=0;
	void branch(float h, float theta) {
		  // Each branch will be 2/3rds the size of the previous one
		  h *= 0.56f;
		  if (h > 15) {
			  int wid = 16-depth*3;
			  for (int i=0;i<4;i++) {
				  	float f = 200f/5f*i;
				  	//gl.glDisable(GL.GL_TEXTURE_2D);   
				  	gl.glEnable(GL.GL_TEXTURE_2D);   
				  	gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("bark"));
					gl.glColor4f(0.5f, 0.5f, 0, 0.8f);
				  	gl.glRotatef(f, 0, 0, 1); 
				  	//gl.glRotatef(60*i, 0, 0, 1); 
					gl.glPushMatrix(); 
					gl.glTranslatef(0, 0,h*1.8f);
					gl.glRotatef(theta, 0, 1, 0); 
					glu.gluCylinder(quadric, wid, wid, h,5, 1);
					depth++;
					branch(h,theta);
					depth--;
					gl.glPopMatrix(); 
			  }
		  	}else {
		  		/* 
		  		 * according to this:
		  			http://www.javagaming.org/index.php/topic,2928.0.html
		  			the blend func should stop the bg appearing in the square. - but it doesnt :(
		  		*/	
		  		gl.glColor4f(0,0.6f,0,0.8f);
		  		boolean drawLeaf = true;
		  		if (drawLeaf) {
					gl.glEnable(GL.GL_TEXTURE_2D);   
					gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					gl.glEnable(GL.GL_BLEND);
					gl.glEnable(GL.GL_ALPHA_TEST);
					gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("leaf"));
					gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_REPLACE, GL.GL_RGBA);
		  		} else {
		  			gl.glDisable(GL.GL_TEXTURE_2D);   
		  		}
				gl.glTranslatef(0, 0,h*1.8f);
				s.drawSquareZero(50, 50);
				
			}
	} 
}
