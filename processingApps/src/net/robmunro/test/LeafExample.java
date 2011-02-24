package net.robmunro.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.robmunro.lib.IFS;
import net.robmunro.lib.ogl.OpenGL;
import net.robmunro.lib.ogl.tools.Shape;
import processing.core.PApplet;

/**
 * @author robm
 * 
 * each rox in ifs array is:-
 * xn+1=ax+by+c
 * yn+1=dx+ey+f
 * probability p
 * where 
 * row = {p,a,b,c,d,e,f};
 * 
 * Keys:
 * 0: no ifs deform
 * 1..c: hex index deform (key)   deform indexes = {{keyUse%2 *2, keyUse/2+1},{keyUse%2 *2+1, keyUse/2+1}}
 * z: fern leaf ifs.
 * x: maple leaf ifs.
 */
public class LeafExample extends PApplet {
	GL gl;
	GLU glu;
	OpenGL ogl ;
	Shape s;
	IFS ifsGen;
	double[][] mapleLeafScale = {{320.0,1.0/8*640},{240.0,- 1.0f/12*480}};
	
	double[][] fernLeafScale = {{320.0, 1.0/8*640},{440.0, -1.0f/12*480}};
	int[][] replaceVec = {{-1,-1},{-1,-1}};
	double[][] replaceArr = IFS.fernLeafIFS;
	double[][] replaceScale = fernLeafScale;
	public void setup()
	{
	    size(640, 480, OPENGL);  
	    ogl=new OpenGL(this);
		this.gl=ogl.gl;  
		glu = new GLU();
	    s = new Shape(gl);
	    ifsGen = new IFS();
	    ogl.loadTexture("leaf", "/home/robm/leaf.gif");
	    /*
	    BufferedImage tex = ifsGen.doIFS(ifsGen.fernLeafIFS, 20000, fernLeafScale,640,480);
	    ogl.loadTexture("leaf", tex);
	    println("opaque:"+(tex.getTransparency()==Transparency.OPAQUE));
	    println("translucent:"+(tex.getTransparency()==Transparency.TRANSLUCENT));
	    println("bitmask:"+(tex.getTransparency()==Transparency.BITMASK));
	    
	    try {
			ImageIO.write(tex, "PNG", new File("/home/robm/leaf.png"));
		} catch (IOException e) {
			println("write img:"+e.getMessage());
		}
		*/
	}
	
	public void draw()	{
		background(0);
		double x=((float)mouseX)*2/width - 1;
		double y=((float)mouseY)*2/height - 1;
		double[][] ifs = ifsGen.copyArray(replaceArr);
		if (replaceVec[0][0]>-1) {
			ifs[replaceVec[0][0]][replaceVec[0][1]]=x;
			ifs[replaceVec[1][0]][replaceVec[1][1]]=y;
		}
		double[][] scale = {{320.0, 1.0/8*640},{240.0, -1.0f/25*480}};
		BufferedImage tex = ifsGen.doIFS(ifs, 100000, scale,640,480);
		gl.glColor4f(0,0.6f,0,1);
		ogl.loadTexture("leaf", tex);
		gl.glEnable(GL.GL_TEXTURE_2D);   
		gl.glBindTexture(GL.GL_TEXTURE_2D, ogl.getTex("leaf"));
		gl.glTranslatef(0f, 0f ,-800f);
		s.drawSquare(640, 480);
	}

	public void keyPressed(){
		println(key);
		int keyInt=-1;
		try{keyInt=Integer.parseInt(""+key,16);} catch(Exception e){}
		if (keyInt>=1 && keyInt<=12) {
			int keyUse=keyInt-1;
			int col = keyUse/2+1;
			int row = keyUse%2 *2;
			int[][] r={{row,col},{row+1,col}};
			replaceVec =r;
			println(col+":"+row);
		} else if (key=='0'){
			int[][] r={{-1,-1},{-1,-1}};
			replaceVec =r;
			println(-1);
		}
		if (key=='z') {replaceArr=ifsGen.fernLeafIFS;}
		if (key=='x') {replaceArr=ifsGen.mapleLeafIFS;}
	}
	
}
