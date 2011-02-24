package net.processing.glgraphics.ex.multi;

import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;

public class MultiFilter extends PApplet {
	GLTextureFilter pulseEmboss, pixelate, gaussBlur, edgeDetect, posterize;
	GLTexture tex0, tex1, tex2, tex3, tex4, tex5;

	GLTextureFilterParameters params;

	PImage img;

	public void setup()
	{
	    size(640, 480, GLConstants.GLGRAPHICS);
	    noStroke();

	    // Loading moderately big image file (1600x1200)
	    tex0 = new GLTexture(this, "resources/glgraphics/ex/multiFilter/old_house.jpg");
	    
	    // Creating destination textures for the filters.
	    tex1 = new GLTexture(this, tex0.width, tex0.height);
	    tex2 = new GLTexture(this, tex0.width, tex0.height);    
	    tex3 = new GLTexture(this, tex0.width, tex0.height);
	    tex4 = new GLTexture(this, tex0.width, tex0.height);
	    tex5 = new GLTexture(this, tex0.width, tex0.height);
	    
	    // A filter is defined in an xml file where the glsl shaders and grid are specified.
	    pulseEmboss = new GLTextureFilter(this, "resources/glgraphics/ex/multiFilter/pulsatingEmboss.xml");
	    gaussBlur = new GLTextureFilter(this, "resources/glgraphics/ex/multiFilter/gaussBlur.xml");
	    pixelate = new GLTextureFilter(this, "resources/glgraphics/ex/multiFilter/pixelate.xml");    
	    edgeDetect = new GLTextureFilter(this, "resources/glgraphics/ex/multiFilter/edgeDetect.xml");
	    posterize = new GLTextureFilter(this, "resources/glgraphics/ex/multiFilter/posterize.xml");
	    
	    // This object is used to pass parameters to the filters.
	    params = new GLTextureFilterParameters(this);
	}

	public void draw()
	{
	   background(0); 

	   // Filters can be chained, like here:   
	   tex0.filter(pulseEmboss, tex1);
	   tex1.filter(gaussBlur, tex2);

	   // The resolution of the pixelization in the pixelate filter can be 
	   // controled with the first float parameter.
	   params.parFlt1 = map(mouseX, 0, 640, 1, 100);
	   tex0.filter(pixelate, tex3, params);
	   
	   tex0.filter(edgeDetect, tex4);
	   tex0.filter(posterize, tex5);
	   
	   tex2.render(0, 0, 320, 240);
	   tex3.render(320, 0, 320, 240);
	   tex4.render(0, 240, 320, 240);   
	   tex5.render(320, 240, 320, 240);
	}

}
