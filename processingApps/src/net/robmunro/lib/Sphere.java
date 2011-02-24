package net.robmunro.lib;

import processing.core.PApplet;
import processing.core.PImage;

public class Sphere {
	float[] cx,cz,sphereX,sphereY,sphereZ;
	float sinLUT[];
	float cosLUT[];
	float SINCOS_PRECISION = 0.5f;
	int SINCOS_LENGTH = (int) (360.0f / SINCOS_PRECISION);
	int sDetail = 35;  //Sphere detail setting
	
	public void initializeSphere(int res )
	{
		this.sDetail=res;
	  sinLUT = new float[SINCOS_LENGTH];
	  cosLUT = new float[SINCOS_LENGTH];

	  for (int i = 0; i < SINCOS_LENGTH; i++) {
	    sinLUT[i] = (float) Math.sin(i * PApplet.DEG_TO_RAD * SINCOS_PRECISION);
	    cosLUT[i] = (float) Math.cos(i * PApplet.DEG_TO_RAD * SINCOS_PRECISION);
	  }

	  float delta = (float)SINCOS_LENGTH/sDetail;
	  float[] cx = new float[sDetail];
	  float[] cz = new float[sDetail];
	  
	  // Calc unit circle in XZ plane
	  for (int i = 0; i < sDetail; i++) {
	    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
	    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
	  }
	  
	  // Computing vertexlist vertexlist starts at south pole
	  int vertCount = sDetail * (sDetail-1) + 2;
	  int currVert = 0;
	  
	  // Re-init arrays to store vertices
	  sphereX = new float[vertCount];
	  sphereY = new float[vertCount];
	  sphereZ = new float[vertCount];
	  float angle_step = (SINCOS_LENGTH*0.5f)/sDetail;
	  float angle = angle_step;
	  
	  // Step along Y axis
	  for (int i = 1; i < sDetail; i++) {
	    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
	    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
	    for (int j = 0; j < sDetail; j++) {
	      sphereX[currVert] = cx[j] * curradius;
	      sphereY[currVert] = currY;
	      sphereZ[currVert++] = cz[j] * curradius;
	    }
	    angle += angle_step;
	  }
	  //sDetail = res;
	}

	// Generic routine to draw textured sphere
	public void texturedSphere(float r, PImage t,PApplet pa) 
	{
	  int v1,v11,v2;
	  r = (r + 240 ) * 0.33f;
	  pa.beginShape(PApplet.TRIANGLE_STRIP);
	  pa.texture(t);
	  float iu=(float)(t.width-1)/(sDetail);
	  float iv=(float)(t.height-1)/(sDetail);
	  float u=0,v=iv;
	  for (int i = 0; i < sDetail; i++) {
	    pa.vertex(0, -r, 0,u,0);
	    pa.vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
	    u+=iu;
	  }
	  pa.vertex(0, -r, 0,u,0);
	  pa.vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
	  pa.endShape();   
	  
	  // Middle rings
	  int voff = 0;
	  for(int i = 2; i < sDetail; i++) {
	    v1=v11=voff;
	    voff += sDetail;
	    v2=voff;
	    u=0;
	    pa.beginShape(PApplet.TRIANGLE_STRIP);
	    pa.texture(t);
	    for (int j = 0; j < sDetail; j++) {
	    	pa.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
	    	pa.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
	      u+=iu;
	    }
	  
	    // Close each ring
	    v1=v11;
	    v2=voff;
	    pa.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
	    pa.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
	    pa.endShape();
	    v+=iv;
	  }
	  u=0;
	  
	  // Add the northern cap
	  pa.beginShape(PApplet.TRIANGLE_STRIP);
	  pa.texture(t);
	  for (int i = 0; i < sDetail; i++) {
	    v2 = voff + i;
	    pa.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
	    pa.vertex(0, r, 0,u,v+iv);    
	    u+=iu;
	  }
	  pa.vertex(0, r, 0,u, v+iv);
	  pa.vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
	  pa.endShape();
	  
	}
}