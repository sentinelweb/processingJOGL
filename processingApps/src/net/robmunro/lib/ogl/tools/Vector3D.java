package net.robmunro.lib.ogl.tools;

import java.io.Serializable;


public class Vector3D implements Serializable{
	public float x;
	  public float y;
	  public float z;

	  public Vector3D(float x_, float y_, float z_) {
	    x = x_; y = y_; z = z_;
	  }

	  public Vector3D(float x_, float y_) {
	    x = x_; y = y_; z = 0f;
	  }
	  
	  public Vector3D() {
	    x = 0f; y = 0f; z = 0f;
	  }

	  public void setX(float x_) {
	    x = x_;
	  }

	  public void setY(float y_) {
	    y = y_;
	  }

	  public  void setZ(float z_) {
	    z = z_;
	  }
	  
	  
	  public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	/*
	  public void setXY(float x_, float y_) {
	    x = x_;
	    y = y_;
	  }
	  */
	  public void setXYZ(float x_, float y_, float z_) {
	    x = x_;
	    y = y_;
	    z = z_;
	  }

	  public void setXYZ(Vector3D v) {
	    x = v.x;
	    y = v.y;
	    z = v.z;
	  }
	  
	  public float magnitude() {
	    return (float) Math.sqrt(x*x + y*y + z*z);
	  }

	  public Vector3D copy() {
	    return new Vector3D(x,y,z);
	  }

	  public Vector3D copy(Vector3D v) {
	    return new Vector3D(v.x, v.y,v.z);
	  }
	  
	  public Vector3D add(Vector3D v) {
	    x += v.x;
	    y += v.y;
	    z += v.z;
	    return this;
	  }

	  public Vector3D sub(Vector3D v) {
	    x -= v.x;
	    y -= v.y;
	    z -= v.z;
	    return this;
	  }
	  public Vector3D add(float n) {
		    x += n;
		    y += n;
		    z += n;
		    return this;
		  }
	  public Vector3D mult(float n) {
	    x *= n;
	    y *= n;
	    z *= n;
	    return this;
	  }

	  public Vector3D div(float n) {
	    x /= n;
	    y /= n;
	    z /= n;
	    return this;
	  }
	  
	  /*public float dot(Vector3D v) {
	    //implement DOT product
	  }*/
	  
	  /*public Vector3D cross(Vector3D v) {
	    //implement CROSS product
	  }*/

	  public void normalize() {
	    float m = magnitude();
	    if (m > 0) {
	       div(m);
	    }
	  }

	  public void limit(float max) {
	    if (magnitude() > max) {
	      normalize();
	      mult(max);
	    }
	  }

	  public float heading2D() {
	    float angle = (float) Math.atan2(-y, x);
	    return -1*angle;
	  }

	  public Vector3D add(Vector3D v1, Vector3D v2) {
	    Vector3D v = new Vector3D(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
	    return v;
	  }
	  //public Vector3D add(Vector3D v1) {		  return add(this,v1);	  }

	  public Vector3D sub(Vector3D v1, Vector3D v2) {
	    Vector3D v = new Vector3D(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
	    return v;
	  }

	  public Vector3D div(Vector3D v1, float n) {
	    Vector3D v = new Vector3D(v1.x/n,v1.y/n,v1.z/n);
	    return v;
	  }

	  public Vector3D mult(Vector3D v1, float n) {
	    Vector3D v = new Vector3D(v1.x*n,v1.y*n,v1.z*n);
	    return v;
	  }

	  public float distance (Vector3D v1, Vector3D v2) {
	    float dx = v1.x - v2.x;
	    float dy = v1.y - v2.y;
	    float dz = v1.z - v2.z;
	    return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
	  }
	  public String toString() {
		  return this.x+":"+this.y+":"+this.z;
		  
	  }
	  public Vector3D cross(Vector3D v) {
		  return new Vector3D(
				  this.y*v.z  -this.z*v.y,
				  this.z*v.x - this.x*v.z,
				  this.x*v.y - this.y*v.x
		  );
	  }
	  
	  public float dot(Vector3D v){
		  return   this.x*v.x+this.y*v.y+this.z-v.z;
	  }
	 
	
}
