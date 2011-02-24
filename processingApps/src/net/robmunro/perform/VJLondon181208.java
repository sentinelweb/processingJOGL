	package net.robmunro.perform;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.motion.Particlez;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;
import net.robmunro.video.Video3;

import com.sun.opengl.util.GLUT;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class VJLondon181208 extends PApplet {
	int HEIGHT = 200;
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	 GLUT glut;
	 int currTex =0;
	 DisplayObject displayObject;
	 Particlez particlez; 
	 Particlez.ParticleSystems psyss;
	 String cubeMapBase="/home/robm/download/opengl/glsl/cubemap/cubemap_";
	 String[][] bases = new String[][]{
	    		{"redsky","redsky","png"},
	    		{"opensea","opensea","png"},
	    		{"night","night","png"},
	    		{"mars","mars","jpg"},
	    		{"greenhill","greenhill","png"},
	    		{"grandcanyon","grandcanyon","jpg"},
	    		{"dots","dots","png"},
	    		{"badmeat_nightsky","nightsky","jpg"},
	    		{"arch","arch","jpg"}
	    };
	 
	public void setup()
	{
	    //size(4*HEIGHT, HEIGHT, OPENGL);  
		//size(1280 ,1024, OPENGL);  
		size(640 ,480, OPENGL);  
	    //startFullscreen();
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
		glut = new GLUT();
	    s = new Shape(gl,this);
	    displayObject= new DisplayObject(Vars.numRods);
		
	    particlez = new Particlez(this.gl, this);
	    psyss = particlez.new ParticleSystems();
	    
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glShadeModel(GL.GL_SMOOTH);
	    
	    loadMaps(cubeMapBase,bases);

	    gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
	    boolean fboOK=ogl.createFrameBufferObject( "blur", 640, 480);
	    println(fboOK);
		ogl.createTexture( "blur", 640, 480);
		try {
			ogl.makeProgram(
				"cubeMap",
				new String[] {},
				new String[] {"LightPos","BaseColor","MixRatio","EnvMap"}, 
				ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/cubeMap.vert"), 
				ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/cubeMap.frag")
			);
			ogl.makeProgram(
				"deform",
				new String[] {},
				new String[] {"time","LightPos","BaseColor","MixRatio","EnvMap"}, 
				ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/sinCubeMap.vert"), 
				ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/sinCubeMap.frag")
			);
			ogl.makeProgram(
				"sin",
				new String[] {},
				new String[] {"time"}, //
				ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/sin.vert"), 
				ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/sin.frag")
			);
			ogl.makeProgram(
					"cubeMapDeform",
					new String[] {},
					new String[] {"time","LightPos","BaseColor","MixRatio","EnvMap"}, //
					new int[] {
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/sin.vert"),
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/cubeMap.vert")
							
					},
					new int[] {
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/sin.frag"),
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/cubeMap.frag")
						
					}
			);
			ogl.makeProgram(
					"blur1",
					new String[] {},
					new String[] {"tex1","offset1"}, //
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glow/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glow/Blur.frag")
			);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
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
	
	static class Vars {
		static int sphereSize = 20;
		static int numRods = 8;
		static int centreType = 0;
		static int numObjects = 1;
		static int rodLength =40;
		static int numExplodeSpheres = 8;
	}
	
	float rotate = 0;
	float wid=180;
	float time = 0;
	public void draw()	{
		rotate++;
		time++;
		gl.glColor4f(1, 1, 1, 0.8f);

		ogl.setFB("blur");
		//ogl.clearFB();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		//applyDeform2();
		//applyCubeMap();
		
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER,        GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER,        GL.GL_NEAREST);
		    
		gl.glEnable(GL.GL_TEXTURE_CUBE_MAP); 
		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP,  ogl.getTex(bases[currTex][0])); 
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f ,-200f);
		float spacing = wid/(Vars.numObjects-1);
		//if (Vars.numObjects==2) {spacing=60;}
		//else if  (Vars.numObjects==3) {spacing=40;}
		//else if  (Vars.numObjects==4) {spacing=30;}
		//else if  (Vars.numObjects==5) {spacing=20;}
		if (Vars.numObjects>1) {
			gl.glTranslatef(-1*wid/2f, 0f ,0f);
		}
		gl.glRotatef(rotate,1,0, 0);
		for (int i=0;i<Vars.numObjects;i++) {
			renderObject();
			gl.glTranslatef(spacing, 0f ,0f);
		}
		psyss.render();
		
		gl.glDisable(GL.GL_TEXTURE_CUBE_MAP); 
		gl.glPopMatrix();
	    gl.glFlush();       
	    gl.glViewport(0, 0, 640, 480);
	    gl.glActiveTexture(ogl.getTex("blur"));
		gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("blur"));
	    gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 0, 0, 640, 480, 0);
	    
	    gl.glUseProgramObjectARB(0);
	    //gl.glBindTexture(GL.GL_TEXTURE_2D, 0); 
	    ogl.clearFB(); // use main buffer
	    //applyBlur();
	    gl.glEnable(GL.GL_TEXTURE_2D);   
		//gl.glBindTexture(GL.GL_TEXTURE_2D,  ogl.getTex("blur")); 
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
        gl.glEnable(GL.GL_BLEND); 
        
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glTranslatef(0f, 0f ,-600f);
		//gl.glColor4f(0,0,1,1f);
		s.drawSquare(640, 480);
		noApply();
		//gl.glColor4f(1,1,1,0.8f);
		s.drawSquare(640, 480);
		
	    gl.glFlush();      
	}
	
	private void renderObject() {
		displayObject.render();
	}
	
	class DisplayObject {
		int numRods = 0;
		int explodeRotation = 0;
		ArrayList<Explode> explodeArr= new ArrayList<Explode>();
		ArrayList<Vector3D> rodVectors= new ArrayList<Vector3D>();
		Shape.RandomShape rnd;
		Shape.Cube cube;
		DisplayObject(int numRods) {
			this.numRods=numRods;
			rnd = s.new RandomShape(50,20);
			cube= s.new Cube();
			for (int i=0;i<numRods;i++) {
				explodeArr.add(new Explode());
				rodVectors.add(new Vector3D(random (-1,1),random (-1,1),random (-1,1)));
			}
		}
		class Explode{
			int step = 0;
			int num = 8;
			boolean render() {
				step++;
				for (int i=0;i<num;i++) {
					gl.glRotatef(360f/num*i, 0, 0, 1) ;
					gl.glPushMatrix();
						gl.glRotatef(explodeRotation, 1,0, 0) ;
						gl.glTranslatef(10,0,0) ;
						s.drawSquare(3,3);
					gl.glPopMatrix();
				}
				return step<50;
			}
		}
		void render() {
			gl.glPushMatrix();
			glu.gluSphere(glu.gluNewQuadric(),  10 ,15,20);
			explodeRotation++;
			for (int i=0;i<numRods;i++) {
				gl.glPushMatrix();
				Vector3D rodRot = rodVectors.get(i);
				gl.glRotatef(rotate, rodRot.x, rodRot.y, rodRot.z) ;
				glu.gluCylinder( glu.gluNewQuadric(),2.0, 2.0,Vars.rodLength ,15,20);
				gl.glTranslatef(0,0,Vars.rodLength) ;
				glu.gluSphere(glu.gluNewQuadric(),  2 ,15,20);
				
				for (int j=0;j<explodeArr.size();j++) {
					explodeArr.get(j).render();
				}
				gl.glPopMatrix();
			}
			gl.glTranslatef(0, 30, 0);
			gl.glRotatef(rotate, 0,1,0) ;
			
			rnd.draw();
			//cube.drawCube();
			gl.glPopMatrix();
		}
	}
	@Override
	public void keyPressed() {
		switch (key) {
			case 'z':currTex++ ; currTex%=bases.length;println(currTex);break;
			case 'x':psyss.addSystem(new Vector3D(20,20,-30));break;
			case 'c':Vars.numObjects++;if (Vars.numObjects>5)Vars.numObjects=1;println(Vars.numObjects+":"+(-1*wid/2f)+":"+wid/Vars.numObjects);break;
			case 'v':Vars.numRods++;if (Vars.numRods>10)Vars.numRods=1;break;
		}
	}

	@Override
	public void keyReleased() {
		super.keyReleased();
	}

	void applyCubeMap() {
		GLSLProgram gProgram = ogl.getProgram("cubeMap");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform3fARB(gProgram.getUniformId("LightPos"), 1,1,1);
        gl.glUniform3fARB(gProgram.getUniformId("BaseColor"), 1, 1, 1);
	    gl.glUniform1fARB(gProgram.getUniformId("MixRatio"),  0);
	    gl.glUniform1fARB(gProgram.getUniformId("EnvMap"),  ogl.getTex(bases[currTex][0]));
	}
	void applyBlur() {
		GLSLProgram gProgram = ogl.getProgram("blur1");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform2fARB(gProgram.getUniformId("offset1"), 0.003f,0.003f);
        gl.glUniform1fARB(gProgram.getUniformId("tex1"),  ogl.getTex("blur"));
	}
	void applyDeform() {
		GLSLProgram gProgram = ogl.getProgram("deform");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1fARB(gProgram.getUniformId("time"), time);
        gl.glUniform3fARB(gProgram.getUniformId("LightPos"), 1,1,1);
        gl.glUniform3fARB(gProgram.getUniformId("BaseColor"), 1, 1, 1);
	    gl.glUniform1fARB(gProgram.getUniformId("MixRatio"),  0);
	    gl.glUniform1fARB(gProgram.getUniformId("EnvMap"),  ogl.getTex(bases[currTex][0]));
	}
	void applyDeform2() {
		GLSLProgram gProgram = ogl.getProgram("cubeMapDeform");				
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform1fARB(gProgram.getUniformId("time"), time);
        gl.glUniform3fARB(gProgram.getUniformId("LightPos"), 1,1,1);
        gl.glUniform3fARB(gProgram.getUniformId("BaseColor"), 1, 1, 1);
	    gl.glUniform1fARB(gProgram.getUniformId("MixRatio"),  0);
	    gl.glUniform1fARB(gProgram.getUniformId("EnvMap"),  ogl.getTex(bases[currTex][0]));
	}
	void noApply(){  gl.glUseProgramObjectARB(0);	}
	public static void main (String[] args) {
		println( VJLondon181208.class.getCanonicalName());
		PApplet.main(new String[] { "--present" ,"--location=1920,0","--size=1280,1024", VJLondon181208.class.getCanonicalName()});
	}

	
}
