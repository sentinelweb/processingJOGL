package net.robmunro.algorithm.julia;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter ;
import codeanticode.glgraphics.GLTextureFilterParameters;

public class MandelGLSL extends PApplet{
	GLTextureFilter mandelFilter;
	GLTexture tex0, tex1;
	PImage img;
	PFont font;

	private static final int WIDTH = 1024;
	private static final int HEIGHT = WIDTH*3/4;
	
	private int dim=1;
	
	public void setup()
	{
	    size(WIDTH, HEIGHT, GLConstants.GLGRAPHICS);
	    
	    // A filter is defined in an xml file where the glsl shaders and grid are specified.
	    mandelFilter = new GLTextureFilter(this, "resources/robmunro/julia/mandel.xml");
	    tex0 = new GLTexture(this,WIDTH,HEIGHT);
	    tex1 = new GLTexture(this,WIDTH,HEIGHT);
	    
	    font = loadFont("resources/robmunro/julia/EstrangeloEdessa-24.vlw");
	    textFont(font, 15);     
	}

	public void draw()
	{
	    background(0);
	  
	    GLTextureFilterParameters params =new GLTextureFilterParameters(this);
	    float re = (((float)mouseX-((float)WIDTH/2f)))/((float)WIDTH/4f);
		float im = (((float)mouseY-((float)HEIGHT/2f)))/((float)HEIGHT/4f);
		
		params.parVec21 = new float[]{re, im};

		params.parInt1 = dim;
		
	    tex0.filter(mandelFilter, tex1, params);

	    image(tex1,0,0);
	   
	    fill(255);
	    text("index:"+dim,10,15);
	    
	}
	
	public void mouseClicked(){
		dim = Math.round((float)mouseY/(float)HEIGHT*16f);
		
	}
}
