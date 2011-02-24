package net.robmunro.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import processing.core.PApplet;

public class TerrainExample extends PApplet {

	
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	Terrain t;
	 
	public void setup()
	{
	    size(640, 480, OPENGL);  
	   
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
	    s = new Shape(gl);
	    t=new Terrain();
	    try {
			t.generate(100,100,2,20,this.getClass().getDeclaredMethod("terrainFunc", new Class[]{Float.class,Float.class}),this);
		} catch (Exception e) {println(e.getClass()+" : "+e.getMessage());}
	}
	
	int rotate=0;
	public void draw()	{

		background(0);
		rotate++;
		gl.glColor4f(1,1,1,0.7f);
		gl.glTranslatef(0,0,-200);
		gl.glRotatef(rotate,0,1,0);
		gl.glRotatef(10,1,0,0);
		
		t.draw();
		
	}
	//public float pow(float num,float power){return (float)Math.pow(num, power);}
	
	// its handy to use the factored polynomial def so that the x+z coords can be specified.
	public Float terrainFunc(Float x,Float z){
		
		float y = min(20,max(-20,0.0000000001f*(x-30)*(x-20)*(x-10)*(x-30)*(x-15)*(z-30)*(z-10)*(z-45)*(z-60)+random(5)));
		//println(x+":"+z+"="+y);
		return y;
	}
	class Terrain{
		int listId = 0;
		float[][] height;
		void generate(int wid,int depth,int step,int maxHeight,Method terrainFunc,Object o) {
			this.listId = gl.glGenLists(1);
			height = new float[wid][depth];
			gl.glNewList(this.listId, GL.GL_COMPILE_AND_EXECUTE);
			//gl.glTranslatef(, 0, -depth*step/2);
			for (int x=0;x<wid;x++) {
				for (int z=0;z<depth;z++) {
					try {
						Float f = (Float) terrainFunc.invoke(o, new Float[]{(float)x,(float)z});
						height[x][z] = f;
					} catch (Exception e) {println(e.getClass()+" : "+e.getMessage());}
				}
			}
			float offsetx=-wid*step/2;
			float offsetz=-depth*step/2;
			for (int x=0;x<wid-1;x++) {
				for (int z=0;z<depth-1;z++) {
					float vx = x*step;
					float vz = z*step;
					gl.glColor4f(0,0,0.5f-0.5f*height[x][z]/maxHeight,0.7f);
					gl.glVertex3f(offsetx+vx, height[x][z], offsetz+vz);
					gl.glColor4f(0,0,0.5f-0.5f*height[x+1][z]/maxHeight,0.7f);
					gl.glVertex3f(offsetx+vx+step, height[x+1][z], offsetz+vz);
					gl.glColor4f(0,0,0.5f-0.5f*height[x+1][z+1]/maxHeight,0.7f);
					gl.glVertex3f(offsetx+vx+step, height[x+1][z+1], offsetz+vz+step);
					gl.glColor4f(0,0,0.5f-0.5f*height[x][z+1]/maxHeight,0.7f);
					gl.glVertex3f(offsetx+vx, height[x][z+1], offsetz+vz+step);
				}
			}
			gl.glEndList();
		}
		public   void draw(){
			  gl.glPushMatrix();
				  gl.glBegin(GL.GL_QUADS);
				  		gl.glCallList(this.listId);
				  gl.glEnd();
			  gl.glPopMatrix();
		  }
	}
}
