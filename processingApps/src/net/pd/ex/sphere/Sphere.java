package net.pd.ex.sphere;

import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;
import processing.core.PApplet;

public class Sphere extends PApplet {
	GLTextureFilter pulseEmboss;
	GLTextureFilterParameters params;
	GLTexture tex0,tex1;
	public void setup() {
		size(640, 480, GLConstants.GLGRAPHICS);
	    noStroke();
	    
	    tex0 = new GLTexture(this, "eye.jpg");
	    tex1 = new GLTexture(this, tex0.width, tex0.height);
	    pulseEmboss = new GLTextureFilter(this, "resources/pd/sphere/move.xml");
	    //pulseEmboss = new GLTextureFilter(this, "resources/glgraphics/ex/multiFilter/pulsatingEmboss.xml");
		
	    params = new GLTextureFilterParameters(this);
	}
	
	public void draw() {
		background(0);
		params.parFlt1 = (params.parFlt1+1f)%20f;
		tex0.filter(pulseEmboss, tex1,params);
		pushMatrix();
		//int boxFill = color(128, 128, 255, 50);
		//fill(boxFill);
		translate( width/2, height/2 );
		beginShape();
		texture(tex1);
		//sphere(300f);
		vertex(10, 20, 0, 0);
		vertex(80, 5, 100, 0);
		vertex(95, 90, 100, 100);
		vertex(40, 95, 0, 100);

		endShape();
		popMatrix();
	}
}
