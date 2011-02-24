package net.robmunro.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.perform.ol5.OpenLab5_240409_FBO;
import processing.core.PApplet;

public class TestMandel extends PApplet {
	GL gl;
	OpenGL ogl ;
	GLU glu;
	Shape shape;
	Vector<Vector3D> vec =new  Vector<Vector3D>();
	boolean recording = false;
	int vecIndex=0;
	boolean vecForward=true;
	Vector3D lastMouseXY = new Vector3D();
	@Override
	public void draw() {
		background (0);
		//applyMandel();
		applyJulia();
		gl.glTranslatef(0, 0, -600);
		shape.drawSquare(640,480);
	}


	@Override
	public void mousePressed() {
		recording=true;
		 vec =new  Vector<Vector3D>();
		 vecIndex=0;
		 vecForward=true;
	}

	@Override
	public void mouseReleased() {
		recording=false;
	}

	@Override
	public void keyPressed() {
		// TODO Auto-generated method stub
		String path ="/mnt/home/robm/patch/OpenNight221009/julia.path";
		if (key=='s') {
			File f = new File(path);
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
				oos.writeObject(vec);
				oos.flush();
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (key=='l') {
			getPath(path);
		}
	}


	private void getPath(String path) {
		File f = new File(path);
		try {
			ObjectInputStream oos = new ObjectInputStream(new FileInputStream(f));
			vec = (Vector<Vector3D> ) oos.readObject();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void setup() {
		//size(640,480,OPENGL);
		size(800,600, OPENGL);
		ogl=new OpenGL(this);
	  	this.gl=ogl.gl;
	  	this.shape=new Shape(gl,this);
	  	try {
			ogl.makeProgram(
						"mandel",
						new String[] {},
						new String[] {}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/julia/passthru.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/julia/mandel.frag"	)
			);
			ogl.makeProgram(
					"julia",
					new String[] {},
					new String[] {"point"}, //"alpha"
					ogl.loadGLSLShaderVObject(	"resources/robmunro/julia/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/julia/julia.frag"	)
			);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	void applyMandel() {
		GLSLProgram gProgram = ogl.getProgram("mandel");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        //gl.glUniform1i(gProgram.getUniformId("depth"), 50);
	}
	
	void applyJulia() {
		GLSLProgram gProgram = ogl.getProgram("julia");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        Vector3D point = thePoint();
        gl.glUniform2f(gProgram.getUniformId("point"), point.x*4,point.y*4);
        //gl.glUniform1f(gProgram.getUniformId("alpha"), 1);
	}
	
	private Vector3D getPoint() {
			Vector3D point = new Vector3D((mouseX-width/2f)/(float)width, (mouseY-height/2f)/(float)height, 0);
			return point;
	}
	
	
	private Vector3D thePoint() {
		if (recording) {
			Vector3D point = getPoint();
			if (lastMouseXY.x!=mouseX || lastMouseXY.y!=mouseY) {
				vec.add(point);
				lastMouseXY=new Vector3D(mouseX,mouseY,0);
			}
			return point;
		} else {
			if (vec.size()==0) {
				return  getPoint();
			}
			if (vecIndex>vec.size()-2) {
				vecForward=false;
			} else if (vecIndex<1) {
				vecForward=true;
			}
			vecIndex +=vecForward?1:-1;
			return vec.get(vecIndex);
		}
	}
	
	public static void main (String[] args) {
		//println( OpenLab5_240409.class.getCanonicalName());
		//PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=800,600",OpenLab5_240409_FBO.class.getCanonicalName() });
		PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=800,600",TestMandel.class.getCanonicalName() });
		//PApplet.main(new String[] { OpenLab5_240409.class.getCanonicalName() });
	}
}
