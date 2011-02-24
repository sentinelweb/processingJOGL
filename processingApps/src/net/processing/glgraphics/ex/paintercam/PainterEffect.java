package net.processing.glgraphics.ex.paintercam;

import processing.core.PApplet;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import codeanticode.glgraphics.GLTextureFilterParameters;
import codeanticode.glgraphics.GLTextureParameters;
import codeanticode.glgraphics.GLTexturePingPong;

public class PainterEffect {
	 PainterEffect(PApplet parent, int n, int w, int h)
	    {
	        this.parent = parent;
	        numParticles = n;
	        canvasWidth = w;
	        canvasHeight = h;
	          
	        initParameters();
	        createTextures();
	        initTextures();    
	        createFilters();          
	    }
	    
	    void apply(GLTexture srcTex, GLTexture brushTex, GLTexture destTex, boolean clear, boolean change, float changeTime)
	    {
	        if (clear) destTex.clear(0, 0, 0, 255);
	        updateBrushes(srcTex, change, changeTime);
	        drawBrushes(brushTex, destTex);      
	    }
	    
	    void updateBrushes(GLTexture srcTex, boolean change, float changeTime)
	    {
	        moveFilterSrcTex[0] = posTex.getReadTex();
	        moveFilterSrcTex[1] = gradTex.getReadTex();
	        moveFilterSrcTex[2] = texfpVel;
	        moveFilterSrcTex[3] = texfpNoise;
	    
	        moveFilterParams.setVec21(canvasWidth, canvasHeight);
	        if (followGrad) moveFilterParams.parInt1 = 1;
	        else moveFilterParams.parInt1 = 0;
	        moveFilterParams.parFlt1 = velMean;
	        moveFilterParams.parFlt2 = noiseMag;
	        moveFilter.apply(moveFilterSrcTex, posTex.getWriteTex(), moveFilterParams);
	        posTex.swap();
	        
	        currentTime = (float)(parent.millis()) / (float)(1000);
	        
	        if ((updateNoiseTime != 0) && (currentTime - lastNoiseUpdateTime >= updateNoiseTime))
	        {
	        	noiseFilter.apply(posTex.getReadTex(), texfpNoise, canvasWidth, canvasHeight, currentTime);
	         
	            lastNoiseUpdateTime = currentTime;
	        }
	        
	        if (updateColor) 
	        {
	            colorFilterSrcTex[0] = imageTex.getOldTex();
	            colorFilterSrcTex[1] = imageTex.getNewTex();
	            colorFilterSrcTex[2] = colorTex.getReadTex();
	            colorFilterSrcTex[3] = colorAuxTex.getReadTex();
	            colorFilterSrcTex[4] = posTex.getReadTex();
	            colorFilterSrcTex[5] = colorCountTex.getReadTex();

	            colorFilterDestTex[0] = colorTex.getWriteTex();
	            colorFilterDestTex[1] = colorAuxTex.getWriteTex();
	            colorFilterDestTex[2] = colorCountTex.getWriteTex();
	        
	            colorFilterParams.parInt1 = brushMaxLength;
	            colorFilterParams.parFlt1 = changeCoeff;
	            colorFilterParams.parFlt2 = brushChangeFrac;
	            colorFilterParams.parFlt3 = brushChangePow;        
	            colorFilter.apply(colorFilterSrcTex, colorFilterDestTex, colorFilterParams);
	            colorTex.swap();
	            colorAuxTex.swap();
	            colorCountTex.swap();        
	        }   
	         
	        if (change)
	        {
	            PApplet.println("Start changing image...");

	            // Preprocessing filter is applied to the source image to generate the new image.
	            imgFilter.apply(srcTex, imageTex.getNewTex());

	            // The gradient of the new image is calculated and stored in first texture of newGradTex.
	            newGradTex.setWriteTex(0);
	            gradFilterSrcTex[0] = imageTex.getNewTex();
	            gradFilterSrcTex[1] = texfpRand;
	            gradFilter.apply(gradFilterSrcTex, newGradTex.getWriteTex());

	            // Initializing variables to control transition between old and new image/gradient.
	            changeCoeff = 0.0f;       // Linear interpolation coefficient.
	            swapedImageTex = false;

	            // Used to control the averaging of the gradient of the new image during the transition
	            // period.
	            newGradTex.init();
	        
	            lastChangeTime = currentTime;
	        }

	        if ((0.0 <= changeCoeff) && (changeCoeff < 1.0))
	        {
	            // Updating linear interpolation coefficient.
	            changeCoeff = (currentTime - lastChangeTime) / changeTime;
	            if (1.0 < changeCoeff) changeCoeff = 1.0f;
	        }
	        else if (!swapedImageTex)
	        {
	            // Transition period is finished.
	        	PApplet.println("...done changing image.");
	            imageTex.swap();
	            swapedImageTex = true;
	            changeCoeff = -1.0f; // With this value, the shaders don't enter into the transition mode.
	        }

	        aveCount++;
	        if (aveCount == aveInterval)
	        {
	            // Gradient average.
	            aveCount = 0;
	            for (int n = 0; n < numAveIter; n++)
	            {
	                aveGradFilterSrcTex[0] = gradTex.getReadTex();
	                aveGradFilterSrcTex[1] = texfpRand; 
	                aveGradFilterSrcTex[2] = newGradTex.getReadTex();
	            
	                aveGradFilterParams.parFlt1 = changeCoeff;
	            
	                if (changeCoeff == -1) 
	                {
	                    aveGradFilter.apply(aveGradFilterSrcTex, gradTex.getWriteTex(), aveGradFilterParams);
	                }
	                else 
	                {
	                    aveGradFilterDestTex[0] = gradTex.getWriteTex();
	                    aveGradFilterDestTex[1] = newGradTex.getWriteTex();
	                    aveGradFilter.apply(aveGradFilterSrcTex, aveGradFilterDestTex, aveGradFilterParams);
	                }
	            
	                gradTex.swap();
	                newGradTex.swap();
	            }
	        }
	    }
	    
	    void drawBrushes(GLTexture brushTex, GLTexture destTex)
	    {
	        brushesFilterSrcTex[0] = gradTex.getReadTex();
	        brushesFilterSrcTex[1] = brushTex;
	        brushesFilterSrcTex[2] = colorTex.getReadTex();
	        brushesFilterSrcTex[3] = posTex.getReadTex();
	        brushesFilterParams.blend = blendBrushes;
	        brushesFilterParams.blendMode = blendMode;
	        brushesFilterParams.parFlt1 = brushSize;
	        brushesFilter.apply(brushesFilterSrcTex, destTex, brushesFilterParams);    
	    }

	    void initParameters()
	    {
	        setDefParameters();
	 
	        aveCount = 0;
	        lastChangeTime = -1;
	        changeCoeff = -1.0f;
	        lastNoiseUpdateTime = -1;

	        startClock = parent.millis();    
	    }
	    
	    void createTextures()
	    {
	        GLTextureParameters floatTexParams = new GLTextureParameters();
	        floatTexParams.format = GLTexture.FLOAT4;
	    
	        imageTex = new GLTexturePingPong(new GLTexture(parent, canvasWidth, canvasHeight), 
	                                         new GLTexture(parent, canvasWidth, canvasHeight));    
	    
	        posTex = new GLTexturePingPong(new GLTexture(parent, numParticles, floatTexParams), 
	                                       new GLTexture(parent, numParticles, floatTexParams));        
	    
	        gradTex = new GLTexturePingPong(new GLTexture(parent, canvasWidth, canvasHeight, floatTexParams), 
	                                        new GLTexture(parent, canvasWidth, canvasHeight, floatTexParams));         
	    
	        newGradTex = new GLTexturePingPong(new GLTexture(parent, canvasWidth, canvasHeight, floatTexParams), 
	                                           new GLTexture(parent, canvasWidth, canvasHeight, floatTexParams));     
	    
	        colorTex = new GLTexturePingPong(new GLTexture(parent, numParticles, floatTexParams), 
	                                         new GLTexture(parent, numParticles, floatTexParams));

	        colorAuxTex = new GLTexturePingPong(new GLTexture(parent, numParticles, floatTexParams), 
	                                            new GLTexture(parent, numParticles, floatTexParams));
	    
	        colorCountTex = new GLTexturePingPong(new GLTexture(parent, numParticles, floatTexParams), 
	                                              new GLTexture(parent, numParticles, floatTexParams));

	        int w = posTex.getReadTex().width;
	        int h = posTex.getReadTex().height;

	        texfpVel = new GLTexture(parent, w, h, floatTexParams);
	        texfpRand = new GLTexture(parent, canvasWidth, canvasHeight, floatTexParams);
	        texfpNoise = new GLTexture(parent, w, h, floatTexParams);
	    
	        moveFilterSrcTex = new GLTexture[4];
	        colorFilterSrcTex = new GLTexture[6];
	        colorFilterDestTex = new GLTexture[3];
	        gradFilterSrcTex = new GLTexture[2];
	        aveGradFilterSrcTex = new GLTexture[3];
	        aveGradFilterDestTex = new GLTexture[2];
	        brushesFilterSrcTex = new GLTexture[4];
	    
	        PApplet.println("Size of particles box: " + w + "x" + h);
	        PApplet.println("Number of particles: " + w * h);    
	    }

	    void initTextures()
	    {
	        int pix[] = new int[canvasWidth * canvasHeight];
	        for (int k = 0; k < canvasWidth * canvasHeight; k++) pix[k] = 0xff000000;

	        imageTex.getOldTex().putBuffer(pix);
	        imageTex.getNewTex().putBuffer(pix);
	    
	        posTex.getReadTex().setRandom(0, canvasWidth, 0, canvasHeight, 0, 0, 0, 0);
	        posTex.getWriteTex().setRandom(0, canvasWidth, 0, canvasHeight, 0, 0, 0, 0);  
	    
	        texfpVel.setRandom(velCoeffMin, velCoeffMax, 0, 0, 0, 0, 0, 0);
	    
	        texfpRand.setRandomDir2D(1.0f, 1.0f, 0.0f, 2*PApplet.PI);
	    
	        texfpNoise.setRandomDir2D(0.0f, 1.0f, 0.0f,  2*PApplet.PI);

	        colorTex.getReadTex().setZero();
	        colorTex.getWriteTex().setZero();
	    
	        colorAuxTex.getReadTex().setZero();
	        colorAuxTex.getWriteTex().setZero();
	    
	        colorCountTex.getReadTex().setRandom(0, 0, brushMinLengthCoeff, brushMaxLengthCoeff, 0, 0, 0, 0);
	        colorCountTex.getWriteTex().setRandom(0, 0, brushMinLengthCoeff, brushMaxLengthCoeff, 0, 0, 0, 0); 
	    
	        gradTex.getReadTex().setZero();
	        gradTex.getWriteTex().setZero();
	        newGradTex.getReadTex().setZero();
	        newGradTex.getWriteTex().setZero();
	    }

	    void createFilters()
	    {
	        moveFilterParams = new GLTextureFilterParameters(parent);
	        moveFilter = new GLTextureFilter(parent, "resources/glgraphics/ex/paintercam/MovePart.xml");
	    
	        colorFilterParams = new GLTextureFilterParameters(parent);
	        colorFilter = new GLTextureFilter(parent, "resources/glgraphics/ex/paintercam/ColorPart.xml");
	    
	        imgFilter = new GLTextureFilter(parent, "resources/glgraphics/ex/paintercam/Blur.xml");
	    
	        gradFilter = new GLTextureFilter(parent, "resources/glgraphics/ex/paintercam/RenderGrad2fp.xml");
	    
	        aveGradFilterParams = new GLTextureFilterParameters(parent);
	        aveGradFilter = new GLTextureFilter(parent, "resources/glgraphics/ex/paintercam/RenderAveGrad.xml");

	        noiseFilter = new SimplexNoiseFilter(parent, "resources/glgraphics/ex/paintercam/SimplexNoise.xml");

	        brushesFilterParams = new GLTextureFilterParameters(parent);
	        brushesFilter = new GLTextureFilter(parent, "resources/glgraphics/ex/paintercam/RenderBrushes.xml");
	    }
	        
	    void setDefParameters()
	    {
	        brushSize = 5.0f;
	        brushMaxLength = 70;
	        brushMinLengthCoeff = 0.8f;
	        brushMaxLengthCoeff = 1.2f;
	        brushChangeFrac = 3.0f;
	        brushChangePow = 1.0f;
	        velMean = 1.0f;
	        velCoeffMin = 0.8f;
	        velCoeffMax = 1.2f;
	        updateNoiseTime = 1.0f;
	        numAveIter = 1;
	        aveInterval = 2;
	        followGrad = true;
	        updateColor = true;
	        noiseMag = 0.1f;
	        blendBrushes = true;
	        blendMode = PApplet.BLEND;  
	    }    
	    
	    PApplet parent;
	    int numParticles;
	    int canvasWidth, canvasHeight;
	    float brushSize;
	    int brushMaxLength;
	    float brushMinLengthCoeff;
	    float brushMaxLengthCoeff;
	    float brushChangeFrac;
	    float brushChangePow;
	    float velMean;
	    float velCoeffMin;
	    float velCoeffMax;
	    float updateNoiseTime;
	    int numAveIter;
	    int aveInterval;
	    boolean followGrad;
	    boolean updateColor;
	    float noiseMag;
	    boolean blendBrushes;
	    int blendMode;
	    
	    float currentTime, lastChangeTime, changeCoeff, lastNoiseUpdateTime;
	    boolean swapedImageTex;
	    int aveCount;
	    int startClock;
	    
	    GLTexturePingPong imageTex, posTex, gradTex, newGradTex, colorTex, colorAuxTex, colorCountTex;
	    GLTexture texfpVel, texfpRand, texfpNoise; 

	    GLTexture[] moveFilterSrcTex;
	    GLTexture[] colorFilterSrcTex;
	    GLTexture[] colorFilterDestTex;
	    GLTexture[] gradFilterSrcTex;
	    GLTexture[] aveGradFilterSrcTex;
	    GLTexture[] aveGradFilterDestTex;
	    GLTexture[] brushesFilterSrcTex;

	    GLTextureFilter moveFilter, colorFilter, imgFilter, gradFilter, aveGradFilter, brushesFilter;
	    SimplexNoiseFilter noiseFilter;

	    GLTextureFilterParameters moveFilterParams;
	    GLTextureFilterParameters colorFilterParams;
	    GLTextureFilterParameters aveGradFilterParams;
	    GLTextureFilterParameters brushesFilterParams;    
}
