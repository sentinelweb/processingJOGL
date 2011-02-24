package net.processing.glgraphics.ex.bloom;

import processing.core.PApplet;
import processing.core.PFont;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;
import codeanticode.glgraphics.GLTextureParameters;

public class BloomEffect extends PApplet {
	GLTexture srcTex, bloomMask, destTex;
	GLTexture tex0, tex2, tex4, tex8, tex16;

	GLTextureFilter extractBloom, blur, blend4, tonemap;

	GLTextureParameters floatTexParams;

	GLTextureFilterParameters bloomParam, toneParam;

	PFont font;

	boolean showAllTextures;
	boolean showSrcTex, showTex16, showBloomMask, showDestTex;

	public void setup()
	{
	    size(640, 480, GLConstants.GLGRAPHICS);
	    noStroke();
	    
	    // Loading required filters.
	    extractBloom = new GLTextureFilter(this, "resources/glgraphics/ex/bloom/ExtractBloom.xml");
	    blur = new GLTextureFilter(this, "resources/glgraphics/ex/bloom/Blur.xml");
	    blend4 = new GLTextureFilter(this, "resources/glgraphics/ex/bloom/Blend4.xml");  
	    tonemap = new GLTextureFilter(this, "resources/glgraphics/ex/bloom/ToneMap.xml");
	    
	    bloomParam = new GLTextureFilterParameters(this);
	    bloomParam.parFlt1 = 0.99f; // bright threshold;    
	    
	    toneParam = new GLTextureFilterParameters(this);
	    toneParam.parFlt1 = 0.86f; // exposure;
	    toneParam.parFlt2 = 0.5f;  // bloom factor;
	    toneParam.parFlt3 = 0.9f;  // bright threshold;    
	    
	    srcTex = new GLTexture(this, "resources/glgraphics/ex/bloom/lights.jpg");
	    int w = srcTex.width;
	    int h = srcTex.height;
	    destTex = new GLTexture(this, w, h);

	    // Initializing bloom mask and blur textures.
	    floatTexParams = new GLTextureParameters();
	    floatTexParams.format = GLTexture.FLOAT4;
	    floatTexParams.minFilter = GLTexture.LINEAR;
	    floatTexParams.magFilter = GLTexture.LINEAR;    
	    
	    bloomMask = new GLTexture(this, w, h, floatTexParams);
	    tex0 = new GLTexture(this, w, h, floatTexParams);
	    tex2 = new GLTexture(this, w / 2, h / 2, floatTexParams);
	    tex4 = new GLTexture(this, w / 4, h / 4, floatTexParams);
	    tex8 = new GLTexture(this, w / 8, h / 8, floatTexParams);
	    tex16 = new GLTexture(this, w / 16, h / 16, floatTexParams);
	    
	    font = loadFont("resources/glgraphics/ex/bloom/EstrangeloEdessa-24.vlw");
	    textFont(font, 24);     
	    
	    showAllTextures = true;
	    showSrcTex = false;
	    showTex16 = false;
	    showBloomMask = false;
	    showDestTex = false;
	}

	public void draw()
	{
	    background(0);
	    
	    float fx = (float)(mouseX) / width;
	    float fy = (float)(mouseY) / height;

	    bloomParam.parFlt1 = fx;
	    toneParam.parFlt1 = fy;    
	    toneParam.parFlt3 = fx;
	    
	    // Extracting the bright regions from input texture.
	    srcTex.filter(extractBloom, tex0, bloomParam);
	  
	    // Downsampling with blur.
	    tex0.filter(blur, tex2);
	    tex2.filter(blur, tex4);    
	    tex4.filter(blur, tex8);    
	    tex8.filter(blur, tex16);     
	    
	    // Blending downsampled textures.
	    blend4.apply(new GLTexture[]{tex2, tex4, tex8, tex16}, new GLTexture[]{bloomMask});
	    
	    // Final tone mapping into destination texture.
	    tonemap.apply(new GLTexture[]{srcTex, bloomMask}, new GLTexture[]{destTex}, toneParam);

	    if (showAllTextures)
	    {
	        image(srcTex, 0, 0, 320, 240);
	        image(tex16, 320, 0, 320, 240);
	        image(bloomMask, 0, 240, 320, 240);
	        image(destTex, 320, 240, 320, 240);      
	        
	        fill(220, 20, 20);
	        text("source texture", 10, 230);
	        text("downsampled texture", 330, 230);
	        text("bloom mask", 10, 470);        
	        text("final texture", 330, 470);        
	    }
	    else
	    {
	        if (showSrcTex) image(srcTex, 0, 0, width, height);
	        else if (showTex16) image(tex16, 0, 0, width, height);
	        else if (showBloomMask) image(bloomMask, 0, 0, width, height);        
	        else if (showDestTex) image(destTex, 0, 0, width, height);
	    } 
	}

	public void mousePressed()
	{
	    if (showAllTextures)
	    {
	        showAllTextures = false;
	        showSrcTex = (0 <= mouseX) && (mouseX < 320) && (0 <= mouseY) && (mouseY < 240);
	        showTex16 = (320 <= mouseX) && (mouseX <= 640) && (0 <= mouseY) && (mouseY < 240);    
	        showBloomMask = (0 <= mouseX) && (mouseX < 320) && (240 <= mouseY) && (mouseY <= 480);
	        showDestTex = (320 <= mouseX) && (mouseX <= 640) && (240 <= mouseY) && (mouseY <= 480);   
	    }
	    else
	    {
	        showAllTextures = true; 
	    }
	}
}
