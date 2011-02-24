package net.robmunro.perform.ol5;

import javax.media.opengl.GL;

import processing.core.PApplet;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.lib.ogl.tools.VideoPlay;

public class VideoManager {
	PApplet p;
	OpenGL ogl;
	GL gl;
	public VideoPlay player0;
	VideoPlay player1; // for switching viodeos
	String currentVideo = "";
	Shape s;
	float alpha=0.9f;
	float alphaRamp=0.05f;
	long start = 0;
	long speed =0;
	
	Vector3D pos=new Vector3D();
	Vector3D vel=new Vector3D(10,0,0);
	public VideoManager(PApplet p,OpenGL ogl) {
		this.p = p;
		this.ogl=ogl;
		this.gl=ogl.gl;
		this.s=new Shape(this.gl);
	}
	
	public void setVideo() {
		setVideo(currentVideo);
	}
	
	public void setVideo(String file) {
		currentVideo=file;
		player1 = new  VideoPlay(p,file);
		player1.loop();
		if (player0!=null) {
				player0.stop();
				player0.dispose();
		}
		player0=player1;
	}
	
	public  void drawVideo() {
		if (alpha<0.85 && alphaRamp>0) {alpha+=alphaRamp;}
		if (alpha>0.05 && alphaRamp<0) {alpha+=alphaRamp;}
		if (alpha>0.05) {
			//p.println("alpha:"+alpha);
			pos.add(vel);
			gl.glTranslatef(pos.x,pos.y,-1200);
			gl.glPushMatrix();
				gl.glColor4f(1f, 1f, 1f, alpha);
				if (player0!=null) player0.setTexture();
				s.drawSquare(400, 240*alpha);
			gl.glPopMatrix();
		}
	}
	
	public void trigger(Integer i) {
		//if (player0!=null) {
			player0.setPosMilli(this.start);
			pos.x=-600;
			//pos.y= -50;
			pos.y= p.random(-100,100);
			alphaRamp=0.05f;
			System.out.println(alphaRamp);
			player0.play();
		//}
	}
	public void end(Integer i) {
		alphaRamp=-0.05f;
		System.out.println(alphaRamp);
		player0.stop();
	}
	public void setStart(float pc){
		if (player0!=null) {
			this.start = player0.getPosPC(pc*100);
		}
	}
}
