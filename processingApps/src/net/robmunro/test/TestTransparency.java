package net.robmunro.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import net.robmunro.lib.IFS;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.test.nehe.TextureReader;
import processing.core.PApplet;
import processing.core.PImage;


public class TestTransparency extends PApplet {
	GL gl;
	OpenGL ogl ;
	GLU glu;
	Shape shape;
	int starTex=-1;
	Texture glParticle;
	Texture glParticle1;
	Texture glParticle2;
	@Override
	public void draw() {
		
		//background(0);
		gl.glEnable(GL.GL_TEXTURE_2D);                              // Enable Texture Mapping
		gl.glEnable(GL.GL_BLEND);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_ALPHA_TEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);                
		//gl.glBindTexture(GL.GL_TEXTURE_2D,  starTex); 
		
		gl.glColor4f(1, 0, 0, 1f);
		gl.glTranslatef(0, 0, -300);
		shape.drawSquare(100, 100);
		gl.glTranslatef(20, 20, -1);
		shape.drawSquare(100, 100);
		
	}
	int tex = 0;
	@Override
	public void keyPressed() {
		++tex;
		tex%=3;
		if (tex==0) {
			glParticle.bind();
			glParticle.enable();
		} else if (tex==1) {
			glParticle1.bind();
			glParticle1.enable();
		}else if (tex==2) {
			glParticle2.bind();
			glParticle2.enable();
		}
	}

	@Override
	public void mouseMoved() {
		// TODO Auto-generated method stub
		super.mouseMoved();
	}

	@Override
	public void mousePressed() {
		// TODO Auto-generated method stub
		super.mousePressed();
	}

	@Override
	public void setup() {
		size(800,600, OPENGL);
		//size(1280,1024, OPENGL);
		background(0);
		//fullScreen = new FullScreen(this);
		//fullScreen.startFullscreen();
		
		ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
		
		shape=new Shape(this.gl);
		//ogl.loadTexture("star",  "/home/robm/processing/processing-1.0/processingApps/src/resources/robmunro/perform/ol5/star.bmp");
		gl.glEnable(GL.GL_TEXTURE_2D);                              // Enable Texture Mapping
		gl.glEnable(GL.GL_BLEND);
		gl.glShadeModel(GL.GL_SMOOTH);                              // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                    // Black Background
        gl.glClearDepth(1.0f);                                      // Depth Buffer Setup
        //gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  // Really Nice Perspective Calculations
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);                  // Set The Blending Function For Translucency
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        
        //loadTextNeHe("resources/robmunro/perform/ol5/star.bmp");
        //"/home/robm/processing/processing-1.0/processingApps/src/resources/robmunro/perform/ol5/sparkle.png"
        try { 
        	glParticle = TextureIO.newTexture(new File(getClass().getResource("/resources/robmunro/perform/ol5/sparkle.png").getPath()), true); 
        
        	glParticle1 = TextureIO.newTexture(new File(getClass().getResource("/resources/robmunro/perform/ol5/blur.png").getPath()), true); 
        	double[][] fernLeafScale = {{320.0, 1.0/8*640},{440.0, -1.0f/12*480}};
        	IFS ifsGen = new IFS();
        	BufferedImage tex = makeTransparent(ifsGen.doIFS(ifsGen.fernLeafIFS, 100000, fernLeafScale, 640, 480),Color.black);
        	glParticle2 = TextureIO.newTexture(tex, false);
        }
  	  	catch (IOException e) { exit(); }
        gl.glDepthMask(false);
	}
	
	
	/**
	 * This makes a single colour transparent - doesn't do a full alpha channel 
	 * i.e. set alpha for all pixels from 0..1
	 * 
	 * @param image
	 * @param col
	 * @return
	 */
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
	/*
	 * 
	 //code for loading tex with alpha channel - doesnt work ... still!!!! ahh yoo fucking bastard prick
	  loadTextNeHe("resources/robmunro/perform/ol5/star.bmp");
		 gl.glShadeModel(GL.GL_SMOOTH);   
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);                  // Set The Blending Function For Translucency
        gl.glEnable(GL.GL_BLEND);
        gl.glClearDepth(1.0f);    
	 */
	public void loadTextNeHe(String res) {
		int textures[] = new int[1]; 
		TextureReader.Texture texture = null;// Create Storage Space For The Texture
        try {
            texture = TextureReader.readTexture(res);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glGenTextures(1, textures, 0);                           // Create One Texture

        // Create Linear Filtered Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
        this.starTex=textures[0];
	}
}
