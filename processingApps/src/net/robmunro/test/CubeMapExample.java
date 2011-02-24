package net.robmunro.test;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.sun.opengl.util.GLUT;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.OpenGL.GLSLProgram;
import net.robmunro.lib.ogl.tools.Shape;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class CubeMapExample extends PApplet {

	PGraphicsOpenGL pgl;
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	 GLUT glut;
	 int currTex =0;
	 String[][] bases = new String[][]{
	    		{"redsky","redsky","png"},
	    		{"opensea","opensea","png"},
	    		{"night","night","png"},
	    		{"mars","mars","jpg"},
	    		{"greenhill","greenhill","png"},
	    		{"grandcanyon","grandcanyon","jpg"},
	    		{"dots","dots","png"},
//	    		{"city","city","png"},
	    		{"badmeat_nightsky","nightsky","jpg"},
	    		{"arch","arch","jpg"}
	    };
	public void setup()
	{
	    size(640, 480, OPENGL);  
	   
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
		glut = new GLUT();
	    s = new Shape(gl);
		//boolean fboOK=ogl.createFrameBufferObject( "test", 640, 480);
		//println(fboOK);
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glShadeModel(GL.GL_SMOOTH);

	    //makeImages();
	    /*
	    String base="/home/robm/download/opengl/glsl/cubemap/cubemap_opensea/";
	    ogl.loadCubeMap("envMap",  //px, nx, py, ny, pz, nz
	    		base+"opensea_positive_x.png", 
	    		base+"opensea_negative_x.png",  
	    		base+"opensea_positive_y.png",
	    		base+"opensea_negative_y.png", 
	    		base+"opensea_positive_z.png",
	    		base+"opensea_negative_z.png"
	    );
	    */
	    String cubeMapBase="/home/robm/download/opengl/glsl/cubemap/cubemap_";
	   
	    loadMaps(cubeMapBase,bases);
	    //ogl.loadTexture("test", base+"opensea_negative_x.jpg");
	    

	    
	    //gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);

	    gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
	   // gl.glEnable(GL.GL_LIGHTING);
	    //gl.glEnable(GL.GL_LIGHT0);
	    //gl.glEnable(GL.GL_AUTO_NORMAL);
	    //gl.glEnable(GL.GL_NORMALIZE);
	    
		//ogl.loadTexture( "envMap","/home/robm/download/opengl/glsl/cubemap/city_cubemap.jpeg");
	    	
		try {
			ogl.makeProgram(
						"cubeMap",
						new String[] {},
						new String[] {"LightPos","BaseColor","MixRatio","EnvMap"}, 
						ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/cubeMap.vert"), 
						ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/cubeMap.frag")
				);
			ogl.makeProgram(
					"pass",
					new String[] {},
					new String[] {}, 
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/glow/passthru.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/glow/passthru.frag"	)
			);
			ogl.makeProgram(
					"wood",
					new String[] {},
					new String[] {"LightPosition","Scale","DarkColor","spread","GrainSizeRecip"}, 
					ogl.loadGLSLShaderVObject(	"resources/robmunro/glsl/wood.vert"), 
					ogl.loadGLSLShaderFObject(	"resources/robmunro/glsl/wood.frag")
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

	float rotate = 0;
	public void draw()	{
		rotate++;
		gl.glColor4f(1, 1, 1, 0.8f);
		//ogl.clearFB();
		
		
		//ogl.setFB("test");
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		applyCubeMap();
		//applyWood();
		//applyPass();
		
		//gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		//gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S,      GL.GL_REPEAT);
		//gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T,      GL.GL_REPEAT);
		//gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_R,        GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER,        GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER,        GL.GL_NEAREST);
		    
		gl.glEnable(GL.GL_TEXTURE_CUBE_MAP); 
		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP,  ogl.getTex(bases[currTex][0])); 
		//gl.glActiveTexture(ogl.getTex("envMap"));
		
//	    gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
//	    gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
//	    gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
//	   gl.glEnable(GL.GL_TEXTURE_GEN_S);
//	    gl.glEnable(GL.GL_TEXTURE_GEN_T);
//	    gl.glEnable(GL.GL_TEXTURE_GEN_R);
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f ,-80f);
		gl.glRotatef(rotate, 1, 0, 0) ;
		//glu.gluCylinder(glu.gluNewQuadric(), 5, 10, 10 ,15,20);
		glu.gluSphere(glu.gluNewQuadric(),  10 ,15,20);
		glut.glutSolidCylinder( 2.0, 30 ,15,20);
		gl.glRotatef(rotate, 0, 1, 0) ;
		glut.glutSolidCylinder( 2.0, 30 ,15,20);
		gl.glRotatef(rotate, 0, 1, 1) ;
		glut.glutSolidCylinder( 2.0, 30 ,15,20);
		//s.drawSquare(20,20);
		gl.glPopMatrix();
		
	    gl.glFlush();       
	    
	}
	
	@Override
	public void keyPressed() {
		switch (key) {
		case 'z':currTex++ ; currTex%=bases.length;println(currTex);break;
		}
	}

	@Override
	public void keyReleased() {
		// TODO Auto-generated method stub
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
	void applyPass() {
		GLSLProgram gProgram = ogl.getProgram("pass");
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
	}
	void applyWood() {
		GLSLProgram gProgram = ogl.getProgram("wood");
        gl.glUseProgramObjectARB(gProgram.getProgramObject());
        gl.glUniform3fARB(gProgram.getUniformId("LightPosition"), 1,1,1);
        gl.glUniform1fARB(gProgram.getUniformId("Scale"),2f);
        
	    gl.glUniform3fARB(gProgram.getUniformId("DarkColor"),  .1f, 0.8f, 0.5f);
	    gl.glUniform3fARB(gProgram.getUniformId("spread"),  .1f, 0.1f, 0.5f);
	    gl.glUniform1fARB(gProgram.getUniformId("GrainSizeRecip"),5f) ;
	}
}
