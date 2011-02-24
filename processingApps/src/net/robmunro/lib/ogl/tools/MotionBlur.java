package net.robmunro.lib.ogl.tools;

import javax.media.opengl.GL;

public class MotionBlur {
	GL gl;
	public MotionBlur(GL gl) {
		super();
		this.gl = gl;
	}
	
	public void clearAccum() {
		gl.glClearAccum(0f,0f,0f,0f); // for motion blur
	    gl.glClear(GL.GL_ACCUM_BUFFER_BIT);
	}
	
	public void blur(float factor){
		 gl.glFlush();
		   // Motion blur
		    gl.glFinish();
		  	float q=factor;
		    gl.glAccum(GL.GL_MULT, q);
		    gl.glAccum(GL.GL_ACCUM, 1-q);
		    gl.glAccum(GL.GL_RETURN, 1.0f);
		    gl.glFlush();
	}
}
