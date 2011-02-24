package net.robmunro.lib.ogl.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.media.opengl.GL;

import processing.core.PApplet;

public class KaleidoScope {
	GL gl;
	//Vector3D rotMatrix ;
	//int rot=0;
	Shape shape;
	Method distort;
	PApplet p;
	float[][] texMap = {{0,0},{1,1}};
	public KaleidoScope(GL gl, PApplet p) {//, Vector3D rotMatrix, int rot
		super();
		this.gl = gl;
		//this.rotMatrix = rotMatrix;
		//this.rot = rot;
		this.p=p;
		shape=new Shape(gl);
	}

	public void drawKal36(float w, float h) {
		drawKal4(w, h);
		gl.glTranslatef(w*2, 0, 0);
		drawKal4(w, h);
		gl.glTranslatef(0,h*2, 0);
		drawKal4(w, h);
		gl.glTranslatef(-w*2, 0, 0);
		drawKal4(w, h);
		gl.glTranslatef(w*2, 0, 0);
		drawKal4(w, h);
		gl.glTranslatef(0,-h*2, 0);
		drawKal4(w, h);
		gl.glTranslatef(0,-h*2, 0);
		drawKal4(w, h);
		gl.glTranslatef(w*2, 0, 0);
		drawKal4(w, h);
		gl.glTranslatef(-w*2, 0, 0);
		drawKal4(w, h);
	}

	public void drawKal16(float w, float h) {
		drawKal4(w, h);
		gl.glTranslatef(w*2, 0, 0);
		drawKal4(w, h);
		gl.glTranslatef(0,h*2, 0);
		drawKal4(w, h);
		gl.glTranslatef(-w*2, 0, 0);
		drawKal4(w, h);
	}

	public void drawKal4(float w, float h) {
		drawKal2(w, h);
		gl.glRotatef(180f, 0,1, 0 );
		drawKal2(w, h);
	}
	
	public void drawKal2(float w, float h) {
		gl.glPushMatrix();
		//gl.glRotatef(rot, rotMatrix.x, rotMatrix.y, rotMatrix.z);
		if (distort!=null) {
			try {distort.invoke(p, null);	} catch (Exception e) {	e.printStackTrace();	} 
		}
		shape.drawSquareZero(w, h,texMap);
		gl.glRotatef(180f ,0,0, 1);
		shape.drawSquareZero(w, h,texMap);
		gl.glPopMatrix();
	}
	public void drawKal2h(float w, float h) {
		gl.glPushMatrix();
		//gl.glRotatef(rot, rotMatrix.x, rotMatrix.y, rotMatrix.z);
		if (distort!=null) {
			try {distort.invoke(p, null);	} catch (Exception e) {	e.printStackTrace();	} 
		}
		shape.drawSquareZero(w, h,texMap);
		gl.glRotatef(180f ,1,0,0);
		gl.glTranslatef(0,0,0);
		shape.drawSquareZero(w, h,texMap);
		gl.glPopMatrix();
		
	}
	public void drawKal2v(float w, float h) {
		gl.glPushMatrix();
		//gl.glRotatef(rot, rotMatrix.x, rotMatrix.y, rotMatrix.z);
		if (distort!=null) {
			try {distort.invoke(p, null);	} catch (Exception e) {	e.printStackTrace();	} 
		}
		shape.drawSquareZero(w, h,texMap);
		gl.glRotatef(180f ,0,1,0);
		gl.glTranslatef(0,0,0);
		shape.drawSquareZero(w, h,texMap);
		gl.glPopMatrix();
		
	}
/*
	public Vector3D getRotMatrix() {
		return rotMatrix;
	}

	public void setRotMatrix(Vector3D rotMatrix) {
		this.rotMatrix = rotMatrix;
	}

	public int getRot() {
		return rot;
	}

	public void setRot(int rot) {
		this.rot = rot;
	}
*/
	public Method getDistort() {
		return distort;
	}

	public void setDistort(Method distort) {
		this.distort = distort;
	}

	public float[][] getTexMap() {
		return texMap;
	}

	public void setTexMap(float[][] texMap) {
		this.texMap = texMap;
	}

}
