package net.robmunro.lib.ogl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import net.robmunro.lib.IFS;
import net.robmunro.lib.ogl.tools.Shape;
import net.robmunro.lib.ogl.tools.Vector3D;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class OpenGL {
	PApplet p;
	public GL gl;
	Shape s ;
	HashMap<String, GLSLProgram> programObjects;
	HashMap<String, FBData> frameBufferObjects;
	HashMap<String, Integer> textures;
	HashMap<String, Texture> jtextures;
	
	public OpenGL(PApplet p) {
		this.p=p;
		this. gl=((PGraphicsOpenGL)p.g).gl;
		programObjects = new HashMap<String, GLSLProgram>();
		frameBufferObjects = new HashMap<String, FBData>();
		textures = new HashMap<String, Integer>();
		jtextures=new HashMap<String, Texture>();
		
		s = new Shape(this.gl,p);
		
	}
	public  int loadGLSLShaderVObject(String path) throws Exception {
		String extensions = gl.glGetString(GL.GL_EXTENSIONS);
		boolean vertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1;
		 if (vertexShaderSupported )   {
			 return loadGLSLShaderObject(  path, true);
		 }else {
		    	throw new Exception("vertex shader not supported");
		  }
	}
	
	public  int loadGLSLShaderFObject(String path) throws Exception {
		String extensions = gl.glGetString(GL.GL_EXTENSIONS);
		boolean fragmentShaderSupported = extensions.indexOf("GL_ARB_fragment_shader") != -1;
		 if ( fragmentShaderSupported)   {
			 return loadGLSLShaderObject(  path,  false);
		 } else {
		    	throw new Exception("fragment shader not supported");
		  }
	}
	
	public  int loadGLSLShaderObject(String path, boolean vertex) throws Exception {
	    String[] lines=p.loadStrings(path);
	    String shaderSource=PApplet.join(lines,"\n");
	    if (shaderSource != null)  {
	    	 int shader = gl.glCreateShaderObjectARB(vertex?GL.GL_VERTEX_SHADER_ARB:GL.GL_FRAGMENT_SHADER_ARB);
			 gl.glShaderSourceARB(shader, 1, new String[]{shaderSource},(int[]) null, 0);
			 gl.glCompileShaderARB(shader);
			 checkLogInfo(gl, shader,vertex?"vertex":"fragment");
			 checkShaderInfo( gl, shader) ;
			 return shader;
	    } else {
	    	throw new Exception("Invalid source : "+path);
	    }
	}
	
	public int makeProgram(String key,String[] attribs,	String[] uniforms, int vshader, int fshader) throws Exception {
		int programObject = gl.glCreateProgramObjectARB();
		if (vshader>-1){gl.glAttachObjectARB(programObject, vshader);}
		if (fshader>-1){gl.glAttachObjectARB(programObject, fshader);}
		return linkProgram(key, attribs, uniforms, programObject);
	}
	
	public int makeProgram(String key,String[] attribs,	String[] uniforms, int[] vshader, int[] fshader) throws Exception {
		int programObject = gl.glCreateProgramObjectARB();
		for (int i=0;i<vshader.length;i++) {
			if (vshader[i]>-1){gl.glAttachObjectARB(programObject, vshader[i]);}
		}
		for (int i=0;i<fshader.length;i++) {
			if (fshader[i]>-1){gl.glAttachObjectARB(programObject, fshader[i]);}
		}
		return linkProgram(key, attribs, uniforms, programObject);
	}
	
	private int linkProgram(String key, String[] attribs, String[] uniforms,int programObject) throws Exception {
		gl.glLinkProgramARB(programObject);
		IntBuffer iVal = BufferUtil.newIntBuffer(1);
		 
		gl.glValidateProgramARB(programObject);
		checkLogInfo(gl, programObject,"makeProgram");
		this.programObjects.put(key, new GLSLProgram(programObject, attribs,uniforms));
		return programObject;
	}
	
	void checkShaderInfo(GL gl, int obj)  {
		IntBuffer iVal = BufferUtil.newIntBuffer(1);
		gl.glGetShaderiv(obj, GL.GL_INFO_LOG_LENGTH, iVal);
		int length = iVal.get();
		if (length <= 1)   {   return;  }
		ByteBuffer infoLog = BufferUtil.newByteBuffer(length);
		IntBuffer numChars = BufferUtil.newIntBuffer(1);
	  //numChars.flip();
		gl.glGetShaderInfoLog(obj, length, numChars, infoLog);
		byte[] infoBytes = new byte[length];
		infoLog.get(infoBytes);
		PApplet.println("Shader Info "+numChars.get(0)+" >> " + new String(infoBytes));
	}
	
	void checkLogInfo(GL gl, int obj,String msg)  {
	  IntBuffer iVal = BufferUtil.newIntBuffer(1);
	  gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
	 
	  int length = iVal.get();
	  if (length <= 1)   {   return;  }
	  ByteBuffer infoLog = BufferUtil.newByteBuffer(length);
	  iVal.flip();
	  gl.glGetInfoLogARB(obj, length, iVal, infoLog);
	  byte[] infoBytes = new byte[length];
	  infoLog.get(infoBytes);
	  PApplet.println("GLSL Validation >> " + new String(infoBytes));
	}
	
	public GLSLProgram getProgram(String key){
		return programObjects.get(key);
	}
	
	public class GLSLProgram{
		//String path;
		 int programObject;
		 HashMap<String,Integer> attribIds = new HashMap<String, Integer>();
		 HashMap<String,Integer> uniformIds = new HashMap<String, Integer>();
		 
		public GLSLProgram(  int programObject, String[] attributes,String[] uniforms ) throws Exception {
			super();
			//this.path = path;
			this.programObject = programObject;
			for ( int i=0; i<attributes.length; i++ ) {
				Integer param = gl.glGetAttribLocationARB(programObject, attributes[i]);
				if (param==-1) {
					throw new Exception ("Invalid attrib : "+attributes[i]);
				} else {
					attribIds.put(attributes[i], param);
				}
			}
			int[] numShaders = {0};
			gl.glGetProgramiv(programObject, GL.GL_ATTACHED_SHADERS,numShaders,0);
			System.err.println("num shaders:"+numShaders[0]);
			int[] numUniforms = {0};
			gl.glGetProgramiv(programObject, GL.GL_ACTIVE_UNIFORMS, numUniforms,0);
			System.err.println("num uniforms:"+numUniforms[0]);
			for ( int i=0; i<uniforms.length; i++ ) {
				Integer param = gl.glGetUniformLocationARB(programObject, uniforms[i]);
				//glGetUniformLocationARB
				if (param==-1) {
					throw new Exception ("Invalid uniform : "+uniforms[i]);
				} else {
					uniformIds.put(uniforms[i], param);
				}
			}
		}
		public Integer getAttribId(String key){
			return attribIds.get(key);
		}
		public Integer getUniformId(String key){
			return uniformIds.get(key);
		}

		public int getProgramObject() {
			return programObject;
		}
		//public void setProgramObject(int programObject) {
		//	this.programObject = programObject;
		//}
		 
	}
	
	public boolean createFrameBufferObject( String name, int wid, int hgt) {
        // Create the FBO
        int[] frameBuffer = new int[1];
        gl.glGenFramebuffersEXT(1, frameBuffer, 0);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frameBuffer[0]);

        // Create a TEXTURE_SIZE x TEXTURE_SIZE RGBA texture that will be used as color attachment
        // for the fbo.
        int[] colorBuffer = new int[1];
        gl.glGenTextures(1, colorBuffer, 0);                 // Create 1 Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, colorBuffer[0]);  // Bind The Texture
        gl.glTexImage2D(                                     // Build Texture Using Information In data
                                                             GL.GL_TEXTURE_2D,
                                                             0,
                                                             GL.GL_RGBA,
                                                             wid,
                                                             hgt,
                                                             0,
                                                             GL.GL_RGBA,
                                                             GL.GL_UNSIGNED_BYTE,
                                                             BufferUtil.newByteBuffer(wid * hgt * 4)
        );
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        // Attach the texture to the frame buffer as the color attachment. This
        // will cause the results of rendering to the FBO to be written in the blur texture.
        gl.glFramebufferTexture2DEXT(
                GL.GL_FRAMEBUFFER_EXT,
                GL.GL_COLOR_ATTACHMENT0_EXT,
                GL.GL_TEXTURE_2D,
                colorBuffer[0],
                0
        );

        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

        // Create a 24-bit TEXTURE_SIZE x TEXTURE_SIZE depth buffer for the FBO.
        // We need this to get correct rendering results.
        int[] depthBuffer = new int[1];
        gl.glGenRenderbuffersEXT(1, depthBuffer, 0);
        gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthBuffer[0]);
        gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT24, wid, hgt);

        // Attach the newly created depth buffer to the FBO.
        gl.glFramebufferRenderbufferEXT(
                GL.GL_FRAMEBUFFER_EXT,
                GL.GL_DEPTH_ATTACHMENT_EXT,
                GL.GL_RENDERBUFFER_EXT,
                depthBuffer[0]
        );

        // Make sure the framebuffer object is complete (i.e. set up correctly)
        int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
        if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT) {
        	FBData fb = new FBData();
        	fb.width=wid;
        	fb.height=hgt;
        	fb.id=frameBuffer[0];
        	frameBufferObjects.put(name, fb);
            return true;
        } else {
            // No matter what goes wrong, we simply delete the frame buffer object
            // This switch statement simply serves to list all possible error codes
            switch(status) {
                case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                    // One of the attachments is incomplete
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                    // Not all attachments have the same size
                case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                    // The desired read buffer has no attachment
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                    // The desired draw buffer has no attachment
                case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                    // Not all color attachments have the same internal format
                case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                    // No attachments have been attached
                case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                    // The combination of internal formats is not supported
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
                    // This value is no longer in the EXT_framebuffer_object specification
                default:
                    // Delete the color buffer texture
                    gl.glDeleteTextures(1, colorBuffer, 0);
                    // Delete the depth buffer
                    gl.glDeleteRenderbuffersEXT(1, depthBuffer, 0);
                    // Delete the FBO
                    gl.glDeleteFramebuffersEXT(1, frameBuffer, 0);
                    return false;
            }
        }
    }
	class FBData{
		int width=0;
		int height=0;
		int id = 0;
	}
	public int getFB(String fbID) {	return frameBufferObjects.get(fbID).id;}
	public void setFB(String fbID) { 
		FBData fb =  frameBufferObjects.get(fbID);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT,fb.id);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, fb.width, fb.height);
	}
	public void clearFB() { gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);}
	
	public void snapFBToTex(String fbID,String texID,int mode) {
		FBData fb =  frameBufferObjects.get(fbID);
		gl.glViewport(0, 0, fb.width, fb.height);
		gl.glBindTexture(GL.GL_TEXTURE_2D, this.getTex(texID));
	    gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, mode, 0, 0, fb.width, fb.height, 0);
	}
	public void drawFBOTextures(String[] texs,Vector3D where) {
		drawFBOTextures( texs, where,false);
	}
	public void drawFBOTextures(String[] texs,Vector3D where,boolean clear) {
		if (clear) {
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		}
		gl.glEnable(GL.GL_TEXTURE_2D);   
		
		gl.glEnable(GL.GL_BLEND); 
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
        gl.glDepthMask(false);
        gl.glDisable(GL.GL_DEPTH_TEST);                          // Disable Depth Testing
        //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glPushMatrix();
			gl.glTranslatef(where.x, where.y ,where.z);
			for (int i=0;i<texs.length;i++) {
				gl.glBindTexture(GL.GL_TEXTURE_2D,  getTex(texs[i])); 
			    s.drawSquare(p.width,p.height);
			}
	    gl.glPopMatrix();
	    // restore state.
	    gl.glDisable(GL.GL_TEXTURE_2D);           
	    gl.glBindTexture(GL.GL_TEXTURE_2D, 0); 
	    gl.glEnable(GL.GL_DEPTH_TEST);
	}
	
	public int createTexture( String texID, int wid, int hgt) {                                // Create An Empty Texture
        ByteBuffer data = BufferUtil.newByteBuffer(wid * hgt*4); // Create Storage Space For Texture Data (128x128x4)
        data.limit(data.capacity());
        if (textures.get(texID)!=null) {
			int[] texIdxArr = {textures.get(texID)};
			gl.glDeleteTextures(1,texIdxArr,0);
		}
        int[] txtnumber = new int[1];
        gl.glGenTextures(1, txtnumber, 0);                                // Create 1 TextureGL_TEXTURE_CUBE_MAP
        gl.glBindTexture(GL.GL_TEXTURE_2D, txtnumber[0]);                 // Bind The Texture
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 4, wid, hgt, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, data); // Build Texture Using Information In data
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        textures.put(texID, txtnumber[0]);
        return txtnumber[0];                                              // Return The Texture ID
    }
	
	public int getTex(String texID) {		return textures.get(texID);	}
	public boolean texExist(String texID) {		return textures.get(texID)!=null;	}
	public void  unloadTexture(String texID) {
		if (textures.get(texID)!=null) {
			int[] texIdxArr = {textures.get(texID)};
			gl.glDeleteTextures(1,texIdxArr,0);
			textures.remove(texID);
		}
	}
	public int loadTexture(String texID,BufferedImage img) {
		try {
			if (textures.get(texID)!=null) {
				int[] texIdxArr = {textures.get(texID)};
				gl.glDeleteTextures(1,texIdxArr,0);
			}
			Texture t = TextureIO.newTexture(img, false);
			textures.put(texID, t.getTextureObject());
			//p.println(t.getTextureObject()+" : "+t.getTarget());
			return t.getTextureObject();
		} catch (Exception e) {
			System.out.println("Couldnt make tex:"+e.getMessage());
		}
		return -1;
	}
	
	public int loadTexture(String texID,String filepath) {
		try {
			if (textures.get(texID)!=null) {
				int[] texIdxArr = {textures.get(texID)};
				gl.glDeleteTextures(1,texIdxArr,0);
			}
			Texture t = TextureIO.newTexture(new File(filepath), false);
			textures.put(texID, t.getTextureObject());
			//p.println(t.getTextureObject()+" : "+t.getTarget());
			return t.getTextureObject();
		} catch (Exception e) {
			System.out.println("Couldnt make tex:"+e.getMessage());
		}
		return -1;
	}
	
	public void loadCubeMap(String texID, String px,String nx,String py,String ny,String pz,String nz) {
		try {
			TextureData pxb=TextureIO.newTextureData(new File(px), false, TextureIO.PNG);
			TextureData nxb=TextureIO.newTextureData(new File(nx), false, TextureIO.PNG);
			TextureData pyb=TextureIO.newTextureData(new File(py), false, TextureIO.PNG);
			TextureData nyb=TextureIO.newTextureData(new File(ny), false, TextureIO.PNG);
			TextureData pzb=TextureIO.newTextureData(new File(pz), false, TextureIO.PNG);
			TextureData nzb=TextureIO.newTextureData(new File(nz), false, TextureIO.PNG);
			
			int[] txtnumber = new int[1];
			gl.glGenTextures(1, txtnumber, 0);                               
	        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, txtnumber[0]); 
	        text2dFromTextureData(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X, pxb);
	        text2dFromTextureData(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,nxb);
	        text2dFromTextureData(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, pyb);
	        text2dFromTextureData(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, nyb);
	        text2dFromTextureData(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, pzb);
	        text2dFromTextureData(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, nzb);
	        textures.put(texID, txtnumber[0]);
	        System.out.println("bound text cube:"+texID+":"+ txtnumber[0]);
		} catch (Exception e) {
			System.out.println("Couldnt make cubmap:"+e.getMessage());
		}
	}
	private void text2dFromTextureData(int type, TextureData td){
		 gl.glTexImage2D(type, 0,td.getInternalFormat(),   td.getWidth(), td.getHeight(), td.getBorder(), td.getPixelFormat(), td.getPixelType(), td.getBuffer());
	}
	
	
	public void loadJTexture(String jTexID,String path,boolean isResource) {
		 try { 
			 	File file =null ;
			 	if (isResource) {
			 		file= new File(getClass().getResource(path).getPath());
			 	}else {
			 		file= new File(path);
			 	}
				jtextures.put(jTexID,
	        			TextureIO.newTexture(file, true)
    			);
	        }
	  	  	catch (IOException e) {System.err.print("invalid path:"+jTexID+":"+path); }
	}
	
	public Texture getJText(String jTexID) {
		return jtextures.get(jTexID	);
	}
	
	public void invoke(Method m) {
		if (m!=null) {
			try {m.invoke(this, null);	} catch (Exception e) {	e.printStackTrace();	} 
		}
	}
	public Method getMethod(Object o,String name) {
		return getMethod(o, name, new Class[]{});
	}
	
	public Method getMethod(Object o,String name, Class[] sig) {
		try{return p.getClass().getDeclaredMethod(name, sig);}catch (Exception e){return null;}	
	}
	
}
