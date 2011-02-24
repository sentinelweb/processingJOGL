package net.processing.glgraphics.ex.psys;

import processing.core.PApplet;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;
import codeanticode.glgraphics.GLTextureParameters;
import codeanticode.glgraphics.GLTexturePingPong;

public class SimpleParticle extends PApplet {

	// Number of particles. It is actually approximated to the closes power-of-two value.
	int SYSTEM_SIZE = 20000;

	GLTexture canvasTex;              // Texture where the particles are rendered to.
	GLTexture bubbleTex;              // Texture used to draw each particle.

	// Ping-pong textures used to store the position and velocities of the particles.
	GLTexturePingPong partPosTex;
	GLTexturePingPong partVelTex;


	GLTextureFilter movePartFilter;    // Filter that contains the dynamic kernel that updates the position and velocities of the particles on the GPU.
	GLTextureFilter renderPartFilter;  // Filter that renders the particles.

	// Parameters of the filters.
	GLTextureFilterParameters movePartFilterParams;
	GLTextureFilterParameters renderPartFilterParams;

	int sec0;

	public void setup()
	{
	    size(800, 600, GLConstants.GLGRAPHICS);  
	    colorMode(RGB);
	    
	    initTextures();
	    initFilters();
	}

	public void draw()
	{
	    background(0);
	   
	    // Input parameters used to control the motion of the particles.
	    movePartFilterParams.setVec21(width, height);   // Edges of the area where the particles can move.
	    movePartFilterParams.setVec22(mouseX, mouseY);  // Position of the mouse (the particles that are closes to the mouse are the most affected by it).
	    movePartFilterParams.setVec23(mouseX - pmouseX, mouseY - pmouseY); // Displacement vector used to set the velocity of the particles.

	    // Here, the motion of the particles is updated. There are four textures involved here,
	    // two from which the old position and velocity are read from, and two to which the updated
	    // velocities and positions are write to.
	    GLTexture[] inputTex = { partPosTex.getReadTex(), partVelTex.getReadTex() };
	    GLTexture[] outputTex = { partPosTex.getWriteTex(), partVelTex.getWriteTex() };
	    movePartFilter.apply(inputTex, outputTex, movePartFilterParams); 
	    // Exchanging the role of the ping-pong particles: those used to write to will be
	    // used to read from in the next iteration.
	    partPosTex.swap();
	    partVelTex.swap();    

	    // paint() fills the texture with the specified color, and transparency can be used
	    // see through the last image.
	    //canvasTex.paint(0, 0, 0, 10);
	    
	    // clear() erases the texture, so if it has a previous image, it is lost, even if the
	    // transparency is set to less than the maximum.
	    canvasTex.clear(0, 0, 0, 255);
	    
	    // Rendering the particles using their current positions.
	    inputTex[0] = partPosTex.getReadTex();
	    inputTex[1] = bubbleTex;
	    renderPartFilter.apply(inputTex, canvasTex);
	    
	    // Drawing the texture with the image.
	    image(canvasTex, 0, 0, width, height);

	    int sec = second();
	    if (sec != sec0) println("FPS: " + frameRate);
	    sec0 = sec;
	}

	void initTextures()
	{   
	    bubbleTex = new GLTexture(this, "resources/glgraphics/ex/psys/bubble.png");

	    canvasTex = new GLTexture(this, width, height);
	    canvasTex.loadPixels();
	    for (int i = 0; i < canvasTex.width * canvasTex.height; i++) canvasTex.pixels[i] = 0xff000000;
	    canvasTex.loadTexture();
	  
	    // Creating Ping-pong textures for position and velocities.
	    GLTextureParameters floatTexParams = new GLTextureParameters();
	    floatTexParams.format = GLTexture.FLOAT4; // The ping-pong textures are float since they store position and velocity values.
	    partPosTex = new GLTexturePingPong(new GLTexture(this, SYSTEM_SIZE, floatTexParams), 
	                                       new GLTexture(this, SYSTEM_SIZE, floatTexParams));
	    
	    partVelTex = new GLTexturePingPong(new GLTexture(this, SYSTEM_SIZE, floatTexParams),
	                                       new GLTexture(this, SYSTEM_SIZE, floatTexParams));

	    partPosTex.getReadTex().setRandom(0, width, 0, height, 0, 0, 0, 0);
	    partPosTex.getWriteTex().setRandom(0, width, 0, height, 0, 0, 0, 0);

	    partVelTex.getReadTex().setZero();
	    partVelTex.getWriteTex().setZero();

	    int w = partPosTex.getReadTex().width;
	    int h = partPosTex.getReadTex().height;
	    println("Size of particles box: " + w + "x" + h);
	    println("Number of particles: " + w * h);
	}

	void initFilters()
	{
	    movePartFilter = new GLTextureFilter(this, "resources/glgraphics/ex/psys/MovePart.xml");
	    movePartFilterParams = new GLTextureFilterParameters(this);
	    
	    renderPartFilterParams = new GLTextureFilterParameters(this);
	    renderPartFilterParams.blend = true;
	    renderPartFilterParams.blendMode = BLEND;
	    renderPartFilter = new GLTextureFilter(this, "resources/glgraphics/ex/psys/RenderPart.xml", renderPartFilterParams);
	}

}
