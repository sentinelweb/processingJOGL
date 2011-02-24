package net.robmunro.test;

import java.awt.Color;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.Texture;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;

import processing.core.PApplet;;

public class TestFlare extends PApplet {
	GL gl;
	OpenGL ogl ;
	GLU glu;
	Shape shape;
	
	String[] files1 = {
			"blob.png",
			"circle.png",
			"crown_inv.png",
			"crown.png",
			"crown2.png",
			"square.png",
			"sun.png",
			"star.png",
			"sunhole.png"};
	String[] files = {
			"blob.png",
			"circle.png",
			"star.png"};
	Flare flare;
	
	int newFlareCounter = 0;
	@Override
	public void draw() {
		gl.glEnable(GL.GL_TEXTURE_2D);                              // Enable Texture Mapping
		gl.glEnable(GL.GL_BLEND);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_ALPHA_TEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);         
		if (newFlareCounter%45==0) {
			flare.addElement();
		}
		newFlareCounter++;
		flare.update();
		
		gl.glTranslatef(0,0,-300);
		flare.render();
	}

	@Override
	public void keyPressed() {
		// TODO Auto-generated method stub
		super.keyPressed();
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
		gl.glEnable(GL.GL_TEXTURE_2D);                              // Enable Texture Mapping
		gl.glEnable(GL.GL_BLEND);
		gl.glShadeModel(GL.GL_SMOOTH);                              // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                    // Black Background
        gl.glClearDepth(1.0f);                                      // Depth Buffer Setup
        //gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  // Really Nice Perspective Calculations
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);                  // Set The Blending Function For Translucency
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
          
        //gl.glDepthMask(false);
		
		for (int i=0;i<files.length;i++) {
			ogl.loadJTexture(files[i], "/resources/robmunro/test/flare/"+files[i], true);
		}
		flare = new Flare();
		
	}
	
	class Flare{
		Vector<FlareElement> flares;
		int length = 300;
		public Flare() {
			this.flares = new Vector<FlareElement>();
		}
		void addElement() {
			int index = (int)Math.floor(random(files.length));
			Color color = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
			//System.out.println(index+":"+color);
			Vector3D size= new Vector3D(20,20,0);
			Vector3D growth=new Vector3D(0.5f,0.5f,0);
			int type = (int)Math.floor(random(2));
			switch(type) {
				case 0:break;
				case 1:
					 size= new Vector3D(200,2,0);
					 growth=new Vector3D(0,0,0);
					 break;
				case 2:
					size= new Vector3D(200,0,0);
					 growth=new Vector3D(0,.5f,0);
					 break;
			}
			flares.add(
					new FlareElement(
							ogl.getJText( files[index] ),
							new Vector3D(0,0,-1+random(2)),
							color,
							this,
							size,
							growth
					)
			);
		}
		void update() {
			Vector<FlareElement> rem = new Vector<FlareElement>();
			for (FlareElement f: flares) {
				f.update();
				if (f.frames>length) {rem.add(f);}
			}
			flares.removeAll(rem);
		}
		void render() {
			for (FlareElement f: flares) {
				f.render();
				
			}
		}
	}
	
	class FlareElement {
		Texture t;
		Vector3D rotationvel;
		Color c;
		Vector3D rotation=new Vector3D();
		Vector3D size=new Vector3D();
		Vector3D growth=new Vector3D(0.5f,0.5f,0);
		Flare f;
		int frames = 0;
		float alpha = 0.0f;
		public FlareElement(Texture t, Vector3D rotation, Color c,Flare f,Vector3D initialSize,Vector3D growth) {
			super();
			this.t = t;
			this.rotationvel = rotation;
			this.c = c;
			this.f=f;
			this.size=initialSize;
			if (growth!=null) {this.growth=growth;}
		}
		void update() {
			rotation.add(rotationvel);
			size.add(growth);
			frames++;
		}
		void render() {
			gl.glPushMatrix();
				if (frames < 30 ) {
					alpha += 0.03;
				}
				if (frames > f.length-30 ) {
					alpha -= 0.03;
				}
				gl.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, alpha);
				t.bind();
				t.enable();
				
				gl.glRotatef(rotation.x, 1, 0, 0);
				gl.glRotatef(rotation.y, 0, 1, 0);
				gl.glRotatef(rotation.z, 0, 0, 1);
				shape.drawSquare(size.x, size.y);
			gl.glPopMatrix();
		}
	}
}
