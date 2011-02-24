package net.processing.examples.gl;

import processing.core.PApplet;
import processing.opengl.*;
import javax.media.opengl.*;
import java.nio.*;
import com.sun.opengl.util.BufferUtil;
public class Shader extends PApplet {

	 
	 
	float phase;
	float div;
	String shaderSource;
	GL gl;
	int programObject;
	int vsPhase;
	boolean vertexShaderEnabled;
	boolean vertexShaderSupported;
	 
	public void setup()
	{
	  size(800,600,OPENGL);
	  gl=((PGraphicsOpenGL)g).gl;
	  div=50;
	   
	  String extensions = gl.glGetString(GL.GL_EXTENSIONS);
	  vertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1;
	  vertexShaderEnabled = true;
	 
	  if (vertexShaderSupported)  
	  {
	    String[] lines=loadStrings("waves.glsl");
	    shaderSource=join(lines,"\n");
	 
	    if (shaderSource != null)  
	    {
			 int shader = gl.glCreateShaderObjectARB(GL.GL_VERTEX_SHADER_ARB);
			 
			 gl.glShaderSourceARB(shader, 1, new String[]{shaderSource},(int[]) null, 0);
			 gl.glCompileShaderARB(shader);
			 checkLogInfo(gl, shader);
			 
			 programObject = gl.glCreateProgramObjectARB();
			 
			 gl.glAttachObjectARB(programObject, shader);
			 gl.glLinkProgramARB(programObject);
			 gl.glValidateProgramARB(programObject);
			 checkLogInfo(gl, programObject);
			 
			 vsPhase=gl.glGetAttribLocationARB(programObject, "phase");
	    }
	  }
	}
	 
	void checkLogInfo(GL gl, int obj)  
	{
	  IntBuffer iVal = BufferUtil.newIntBuffer(1);
	  gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
	 
	  int length = iVal.get();
	  if (length <= 1)  
	  {
	    return;
	  }
	  ByteBuffer infoLog = BufferUtil.newByteBuffer(length);
	  iVal.flip();
	  gl.glGetInfoLogARB(obj, length, iVal, infoLog);
	  byte[] infoBytes = new byte[length];
	  infoLog.get(infoBytes);
	  println("GLSL Validation >> " + new String(infoBytes));
	}
	 
	public void draw()
	{
	  phase-=0.01;
	  if(phase<0)
	    phase+=TWO_PI;
	  if(phase>TWO_PI)
	    phase-=TWO_PI;
	   
	  stroke(0,255,30);
	//  fill(255,0,0);
	  noFill();
	  background(0);
	  camera(1800,600,600,600,0,600,0,-1,0);
	  if (vertexShaderEnabled)  
	  {
	    gl.glUseProgramObjectARB(programObject);
	    gl.glVertexAttrib1fARB(vsPhase, phase);
	  }
	   beginShape(QUADS);
	   for (int x = 0; x < div; x++)  
	   {
	     for (int z = 0; z < div; z++)  
	     {
	     
	  vertex((x)*20.0f, 0, (z)*20.0f);   // Draw Vertex
	  vertex(((x+1))*20.0f,0 , (z)*20.0f);   // Draw Vertex
	  vertex((x+1)*20.0f, 0, ((z+1f))*20.0f);   // Draw Vertex
	  vertex((x)*20.0f, 0, ((z+1f))*20.0f);   // Draw Vertex
	     }
	   }
	   endShape();
	   if (vertexShaderEnabled) {
	     gl.glUseProgramObjectARB(0);
	   }
	  if(frameCount%30==29)
	    println(frameRate);
	} 
}
