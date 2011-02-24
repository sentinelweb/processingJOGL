package net.robmunro.test;

import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import oscP5.OscIn;
import oscP5.OscP5;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.perform.ol5.OpenLab5_240409_FBO;
import processing.core.PApplet;

public class TestPlasma extends PApplet {
	GL gl;
	OpenGL ogl ;
	GLU glu;
	Shape shape;
	Vector<Vector3D> vec =new  Vector<Vector3D>();
	boolean recording = false;
	int vecIndex=0;
	boolean vecForward=true;
	Vector3D lastMouseXY = new Vector3D();
	float time =0;
	float[] amplitudes=new float[4];
	float[] amplitudes2=new float[4];
	OscP5 oscP5_10002 =null; 
	
	
	@Override
	public void draw() {
		background (0);
		time += mouseX/(float)width*0.01f;
		//time %=30;
		applyPlasma();
		//applyJulia();
		//gl.glEnable(GL.GL_TEXTURE_2D);   
		//gl.glBindTexture(GL.GL_TEXTURE_2D,  ogl.getTex("photo")); 
		//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
       // gl.glEnable(GL.GL_BLEND); 
		//applyTexWarp();
		gl.glTranslatef(0, 0, -850);
		gl.glColor4f(1.0f,0.0f,0.0f,1f);
		shape.drawSquare(640,480);
	}

	
	@Override
	public void keyPressed() {
		if (key=='1') {amplitudes[0]=mouseX/(float)width*512f-256f;amplitudes[1]=mouseY/(float)height*512f-256f;}
		if (key=='2') {amplitudes[2]=mouseX/(float)width*512f-256f;amplitudes[3]=mouseY/(float)height*512f-256f;}
		if (key=='3') {amplitudes2[0]=mouseX/(float)width*512f-256f;amplitudes2[1]=mouseY/(float)height*512f-256f;}
		if (key=='4') {amplitudes2[2]=mouseX/(float)width*512f-256f;amplitudes2[3]=mouseY/(float)height*512f-256f;}
	}
	@Override
	public void mousePressed() {
		time=0;
	}

	@Override
	public void mouseReleased() {
	}

	@Override
	public void setup() {
		//size(640,480,OPENGL);
		size(800,600, OPENGL);
		ogl=new OpenGL(this);
	  	this.gl=ogl.gl;
	  	this.shape=new Shape(gl,this);
	  	ogl.loadTexture("photo", "/home/robm/Pictures/edits/handle.jpg");
	  	ogl.loadTexture("noise","/home/robm/Pictures/perlin_noise.jpg" );
	  	oscP5_10002= new OscP5(	this,	"localhost",	10011,	10002,	"receiveOSC"	);
	  	try {
			ogl.makeProgram(
						"plasma",
						new String[] {},
						new String[] {"sTime","amplitudes","amplitudes2"}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/lib/plasma/passthru.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/lib/plasma/plasma.frag"	)
			);
			
			ogl.makeProgram(
					"tex_warp",
					new String[] {},
					new String[] {"colorMap","noiseMap","timer"}, //
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/lib/tex_warp/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/lib/tex_warp/tex_warp.frag"	)
			);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	void applyPlasma() {
		GLSLProgram gProgram = ogl.getProgram("plasma");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1f(gProgram.getUniformId("sTime"), time);
        gl.glUniform4f(gProgram.getUniformId("amplitudes"),  amplitudes[0],amplitudes[1],amplitudes[2],amplitudes[3]);
        gl.glUniform4f(gProgram.getUniformId("amplitudes2"),  amplitudes2[0],amplitudes2[1],amplitudes2[2],amplitudes2[3]);
	}
	
	void applyTexWarp() {
		GLSLProgram gProgram = ogl.getProgram("tex_warp");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1fARB(gProgram.getUniformId("timer"),time );//time
        gl.glUniform1fARB(gProgram.getUniformId("colorMap"), ogl.getTex("photo") );
        gl.glUniform1fARB(gProgram.getUniformId("noiseMap"),ogl.getTex("noise")   );
	}
	
	public static void main (String[] args) {
		//println( OpenLab5_240409.class.getCanonicalName());
		//PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=800,600",OpenLab5_240409_FBO.class.getCanonicalName() });
		PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=800,600",TestPlasma.class.getCanonicalName() });
		//PApplet.main(new String[] { OpenLab5_240409.class.getCanonicalName() });
	}
	public void receiveOSC(OscIn oscIn){
		try{
			if (oscIn.getAddrPattern().equals("/vid/f")) {
				 amplitudes[0]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/r")) {
				System.out.println("r:"+oscIn.getFloat(0));
				amplitudes[1]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/o1")) {
				amplitudes[2]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/o2")) {
				amplitudes[3]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/mix")) {
				amplitudes2[0]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/del")) {
				amplitudes2[1]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/dela")) {
				amplitudes2[2]=oscIn.getFloat(0);
			}else if (oscIn.getAddrPattern().equals("/vid/vol")) {
				amplitudes2[3]=oscIn.getFloat(0);
			}
		} catch(Exception e) {
				println(e.getClass().getName()+":"+e.getMessage()+"-"+oscIn.getAddrPattern());
				e.printStackTrace();
			}
	}
}
