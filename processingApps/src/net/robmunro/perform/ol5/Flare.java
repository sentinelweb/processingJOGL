package net.robmunro.perform.ol5;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.Texture;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import processing.core.PApplet;

public class Flare {

	GL gl;
	GLU glu;
	OpenGL ogl ;
	PApplet p;
	Shape s;
	
	static boolean initialised =false;
	int newFlareCounter=0;
	public boolean dead=false;
	static String[] files1 = {
			"blob.png",
			"circle.png",
			"crown_inv.png",
			"crown.png",
			"crown2.png",
			"square.png",
			"sun.png",
			"star.png",
			"sunhole.png"};
	static String[] files = {
			"blob.png",
			"circle.png",
			"star.png"};
	Vector<FlareElement> flares;
	int length = 300;
	
	public Flare(OpenGL ogl, PApplet p,int length) {
		this.ogl=ogl;
		this.gl = ogl.gl;
		this.glu = new GLU();
		this.p=p;
		this.s = new Shape(this.gl);
		
		this.flares = new Vector<FlareElement>();
		this.length=length;
		
		gl.glEnable(GL.GL_TEXTURE_2D);                              // Enable Texture Mapping
		gl.glEnable(GL.GL_BLEND);
		gl.glShadeModel(GL.GL_SMOOTH);                              // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                    // Black Background
        gl.glClearDepth(1.0f);                                      // Depth Buffer Setup
        //gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  // Really Nice Perspective Calculations
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);       
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);                  // Set The Blending Function For Translucency
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glDepthMask(false);
		if (!this.initialised) {
			for (int i=0;i<files.length;i++) {
				ogl.loadJTexture(files[i], "/resources/robmunro/test/flare/"+files[i], true);
			}
			this.initialised=true;
		}
	}
	
	void addElement() {
		addElement(-1);
	}
	void addElement(int type) {
		int index = (int)Math.floor(p.random(files.length));
		Color color = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
		//System.out.println(index+":"+color);
		Vector3D size= new Vector3D(10,10,0);
		Vector3D growth=new Vector3D(0.5f,0.5f,0);
		if (type==-1) {type = (int)Math.floor(p.random(2));}
		switch(type) {
			case 0:break;
			case 1:
				 size= new Vector3D(100,2,0);
				 growth=new Vector3D(0,0,0);
				 break;
		}
		flares.add(
				new FlareElement(
						ogl.getJText( files[index] ),
						new Vector3D(0,0,-1+p.random(2)),
						color,
						this,
						size,
						growth
				)
		);
	}
	void update() {
		if (newFlareCounter%20==0) {
			addElement();
		}
		newFlareCounter++;
		Vector<FlareElement> rem = new Vector<FlareElement>();
		for (FlareElement f: flares) {
			f.update();
			if (f.frames>length) {rem.add(f);}
		}
		flares.removeAll(rem);
		if (flares.size()==0) {
			dead=true;
		}
	}
	void render() {

		for (FlareElement f: flares) {
			f.render();
			
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
				if (frames < 40 && alpha<0.97) {
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
				s.drawSquare(size.x, size.y);
			gl.glPopMatrix();
		}
	}
}
