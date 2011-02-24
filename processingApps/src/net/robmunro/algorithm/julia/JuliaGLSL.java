package net.robmunro.algorithm.julia;

import net.robmunro.lib.Sphere;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter ;
import codeanticode.glgraphics.GLTextureFilterParameters;

public class JuliaGLSL extends PApplet{
	GLTextureFilter juliaFilter;
	GLTexture tex0, tex1;
	PImage img;
	PFont font;
	Sphere s;
	  
	float fade;
	private static final int WIDTH = 800;
	private static final int HEIGHT = WIDTH*3/4;
	
	float rotation=0f;
	public void setup()
	{
	    size(WIDTH, HEIGHT, GLConstants.GLGRAPHICS);
	    
	    // A filter is defined in an xml file where the glsl shaders and grid are specified.
	    juliaFilter = new GLTextureFilter(this, "resources/robmunro/julia/julia.xml");
	    tex0 = new GLTexture(this,WIDTH,HEIGHT);
	    tex1 = new GLTexture(this,WIDTH,HEIGHT);
	    
	    font = loadFont("resources/robmunro/julia/EstrangeloEdessa-24.vlw");
	    textFont(font, 15);     
	    s = new Sphere();
	    s.initializeSphere(15);
	}

	public void draw()
	{
	    background(0); 
	    noStroke();
	    GLTextureFilterParameters params = new GLTextureFilterParameters(this);
	    float re = ((float)mouseX-(WIDTH/2f))/(WIDTH/4f);
		float im = ((float)mouseY-(HEIGHT/2f))/(HEIGHT/4f);
		params.parVec21 = new float[]{re, im};//point
		params.parFlt1 = 1f;// alpha
	    tex0.filter(juliaFilter, tex1, params);
	    texture(tex1);
	    translate(WIDTH/2f,HEIGHT/2f,0);
	    rotateY(rotation);
	    rotation+=0.01;
	    
	    s.texturedSphere(200, tex1, this);
	    //image(tex1,0,0);
	    fill(255);
	    text(re +" : "+ im,10,15);
	}
	
}
