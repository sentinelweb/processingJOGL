package net.robmunro.test;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;

import com.sun.opengl.util.GLUT;

import processing.core.PApplet;
/*
 * this examples has serious issues. it did work a bit at some stage though - the code should be in there somewhere
 * 
 */
public class RefractExample extends PApplet {
	String[][] bases = new String[][]{
    		{"redsky","redsky","png"},
    		{"opensea","opensea","png"},
    		{"night","night","png"},
    		{"mars","mars","jpg"},
    		{"greenhill","greenhill","png"},
    		{"grandcanyon","grandcanyon","jpg"},
    		{"dots","dots","png"},
//    		{"city","city","png"},
    		{"badmeat_nightsky","nightsky","jpg"},
    		{"arch","arch","jpg"}
    };
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	 GLUT glut;
	 GLUquadric quadric;
	 int currTex =0;
	 public void setup(){
		    size(640, 480, OPENGL);  
		   
		    ogl=new OpenGL(this);
			this.gl=ogl.gl;  
			glu = new GLU();
			glut = new GLUT();
		    s = new Shape(gl);
			//gl.glEnable(GL.GL_DEPTH_TEST);
		    //gl.glShadeModel(GL.GL_SMOOTH);
		   // gl.glEnable(GL.GL_LIGHTING);
		    //gl.glEnable(GL.GL_LIGHT0);
		    //gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {10,10,0},0);

		   // quadric = glu.gluNewQuadric();
		   String cubeMapBase="/home/robm/download/opengl/glsl/cubemap/cubemap_";
		   loadMaps(cubeMapBase,bases);
		    //gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
		    ogl.createFrameBufferObject("refract", 640, 480);
		    ogl.createTexture( "tex", 640, 480);
		    try {
		    	//ogl.loadTexture("white", this.getClass().getResource("/resources/robmunro/test/refract/white.jpg").getFile());
		    	ogl.loadTexture("bark", this.getClass().getResource("/resources/robmunro/glsl/00020.jpg").getFile());
		    //	ogl.loadTexture("bg", this.getClass().getResource("/resources/robmunro/test/refract/DSC_7979.JPG").getFile());
		    	//loadWhiteCubeMap(this.getClass().getResource("/resources/robmunro/test/refract/white.jpg").getFile());
		    	
				ogl.makeProgram(
							"refract",
							new String[] {},
							new String[] {"Texture","Environment","refraction_index"}, 
							ogl.loadGLSLShaderVObject(	this.getClass().getResource("/resources/robmunro/test/refract/water.vert").getFile()), 
							ogl.loadGLSLShaderFObject(	this.getClass().getResource("/resources/robmunro/test/refract/regular_water.frag").getFile())
					);
		    } catch (Exception e) {
		    	println(e.getMessage());
		    }
		    
	}
	 
	private void loadMaps(String cubeMapBase, String[][] bases) {
		for (int i=0;i<bases.length;i++) {
			ogl.loadCubeMap(bases[i][0], 
					cubeMapBase+bases[i][0]+"/"+bases[i][1]+"_positive_x."+bases[i][2], 
					cubeMapBase+bases[i][0]+"/"+bases[i][1]+"_negative_x."+bases[i][2],  
					cubeMapBase+bases[i][0]+"/"+bases[i][1]+"_positive_y."+bases[i][2],
					cubeMapBase+bases[i][0]+"/"+bases[i][1]+"_negative_y."+bases[i][2], 
					cubeMapBase+bases[i][0]+"/"+bases[i][1]+"_positive_z."+bases[i][2],
					cubeMapBase+bases[i][0]+"/"+bases[i][1]+"_negative_z."+bases[i][2]
		    );
		}
	}
	
	private void loadWhiteCubeMap(String path) {
		ogl.loadCubeMap("whiteCm", 	path, 	path,  	path,	path, 	path,	path  );
	}
	
	public void draw()	{
		ogl.clearFB();
		background(0);
		gl.glColor4f(1, 1, 1, 0.8f);
		//gl.glEnable(GL.GL_TEXTURE_2D);
		//gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("bark"));
		ogl.setFB("refract");
		gl.glPushMatrix();
			gl.glTranslatef(0f, 0f ,-900f);
			glu.gluSphere(glu.gluNewQuadric(),  300 ,20,20);
			ogl.snapFBToTex("refract","tex",GL.GL_RGBA);
		gl.glPopMatrix();
		ogl.clearFB();
		
		gl.glColor4f(1, 1, 1, 0.8f);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPushMatrix();
			gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("tex")); 
			gl.glTranslatef(0,0 ,-600f);
			s.drawSquare(640, 480);
		gl.glPopMatrix();
	}
	/*
	background(0);
	
	gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
	//gl.glEnable(GL.GL_TEXTURE_2D);
	ogl.setFB("refract");
	gl.glPushMatrix();
	//gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("tex")); 
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glColor4f(1, 1, 1, 0.8f);
		gl.glTranslatef(0f, 0f ,-2000f);
		//applyRefract();
		glu.gluSphere(glu.gluNewQuadric(),  10 ,20,20);
		ogl.snapFBToTex("refract","tex",GL.GL_RGBA);
	gl.glPopMatrix();
	
	ogl.clearFB();
	gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	gl.glColor4f(1, 1, 1, 0.8f);
	gl.glEnable(GL.GL_TEXTURE_2D);
	gl.glPushMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("tex")); 
		gl.glTranslatef(0,0 ,-600f);
		s.drawSquare(640, 480);
	
	gl.glPopMatrix();
	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); 
	//gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
	//gl.glDisable(GL.GL_TEXTURE_2D);
	//gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, ogl.getTex(bases[currTex][0]));
	 */
	void applyRefract() {
		GLSLProgram gProgram = ogl.getProgram("refract");
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1fARB(gProgram.getUniformId("refraction_index"),  mouseX/(float)width);
	    gl.glUniform1fARB(gProgram.getUniformId("Environment"),  ogl.getTex("redsky"));//ogl.getTex(bases[currTex][0])
	    gl.glUniform1fARB(gProgram.getUniformId("Texture"),  ogl.getTex("redsky"));
	}
}
