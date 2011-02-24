package net.processing.glgraphics.ex.paintercam;

import processing.core.PApplet;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.gsvideo.GSCapture;

public class PainterCam extends PApplet {
	int SYSTEM_SIZE = 100000;
	int CANVAS_WIDTH = 800;
	int CANVAS_HEIGHT = 600;

	boolean clearImg = false;
	boolean changeImg = true;
	float stillTime = 1.0f;
	float changeTime = 0.5f;
	float destTexTransparency = 1.0f;
	float lastChangeTime = 0.0f;

	GLTexture srcTex, destTex, brushTex;

	PainterEffect painter;
	GSCapture cam;

	int sec0;

	public void setup()
	{
	    size(CANVAS_WIDTH, CANVAS_HEIGHT, GLConstants.GLGRAPHICS);
	    colorMode(RGB, 1.0f);
	    
	    cam = new GSCapture(this, 640, 480);
	    
	    srcTex = new GLTexture(this);
	    brushTex = new GLTexture(this, "resources/glgraphics/ex/paintercam/brush2.png");    
	    
	    destTex = new GLTexture(this, width, height);
	    destTex.loadPixels();
	    for (int i = 0; i < destTex.width * destTex.height; i++) destTex.pixels[i] = 0xff000000;
	    destTex.loadTexture();    

	    painter = new PainterEffect(this, SYSTEM_SIZE, CANVAS_WIDTH, CANVAS_HEIGHT);
	}

	void captureEvent(GSCapture cam) 
	{
	    cam.read();  
	}

	public void draw()
	{
	    background(0);
	    
	    float time = millis() / 1000.0f;
	    if (time - lastChangeTime > stillTime) {
	        srcTex.putPixelsIntoTexture(cam);
	        changeImg = true;
	        lastChangeTime = time;

	    }
	    painter.apply(srcTex, brushTex, destTex, clearImg, changeImg, changeTime);
	    if (changeImg) changeImg = false;

	    tint(1.0f, 1.0f - destTexTransparency);     
	    image(srcTex, 0, 0, width, height); 
	    tint(1.0f, destTexTransparency);
	    image(destTex, 0, 0, width, height);

	    int sec = second();
	    if (sec != sec0) println("FPS: " + frameRate);
	    sec0 = sec;
	}

	public void mouseDragged()
	{
	    destTexTransparency = (float)(mouseX) / width;
	}

	public void keyPressed()
	{
	    if (key == CODED)
	    {
	        if (keyCode == UP) painter.noiseMag = constrain(painter.noiseMag + 0.2f, 0.0f, 10.0f);
	        else if (keyCode == DOWN) painter.noiseMag = constrain(painter.noiseMag - 0.2f, 0.0f, 10.0f);
	        else if (keyCode == RIGHT) painter.brushMaxLength = constrain(painter.brushMaxLength + 1, 1, 100);
	        else if (keyCode == LEFT) painter.brushMaxLength = constrain(painter.brushMaxLength - 1, 1, 100);
	        else if (keyCode == ALT) painter.brushSize = constrain(painter.brushSize + 1.0f, 1.0f, 100.0f);
	        else if (keyCode == CONTROL) painter.brushSize = constrain(painter.brushSize - 1.0f, 1.0f, 100.0f);
	    }
	    else if (key == '+') painter.velMean = constrain(painter.velMean + 0.2f, 0.0f, 20.0f);
	    else if (key == '-') painter.velMean = constrain(painter.velMean - 0.2f, 0.0f, 20.0f);
	    else if ((key == 'F') || (key == 'f')) painter.followGrad = !painter.followGrad;
	    else if ((key == 'U') || (key == 'u')) painter.updateColor = !painter.updateColor;
	    else if ((key == 'B') || (key == 'b')) painter.blendBrushes = !painter.blendBrushes;    
	    else if ((key == 'R') || (key == 'r')) painter.setDefParameters();
	    else if ((key == 'C') || (key == 'c')) clearImg = !clearImg;
	    else if ((key == 'S') || (key == 's')) saveFrame("painter-####.tif");
	}
}
