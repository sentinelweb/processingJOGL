package net.processing.glgraphics.ex.duotone;

import processing.core.PApplet;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Slider;

public class DuoTone extends PApplet {
	GLTexture srcTex, destTex;

	GLTextureFilter duoTone;

	DuoToneParameters duoToneParams;

	ExplicitBezierCurve bCurve;

	ControlP5 controlP5;
	Slider rexpSlider;
	Slider gexpSlider;
	Slider bexpSlider;

	int colors[];
	int lightColor = 1;
	int darkColor = 2;

	public void setup()
	{
	    size(400, 400, GLConstants.GLGRAPHICS);

	    duoToneParams = new DuoToneParameters(this);
	    
	    colors = new int[3];
	    colors[0] = color(100, 200, 100);
	    colors[1] = color(200, 100, 100);    
	    colors[2] = color(100, 100, 200);

	    // Color used for light areas.
	    duoToneParams.setMat3(0, 0, red(colors[lightColor]) / 255.0f);
	    duoToneParams.setMat3(0, 1, green(colors[lightColor]) / 255.0f);
	    duoToneParams.setMat3(0, 2, blue(colors[lightColor]) / 255.0f);
	    
	    // Color used for dark areas.
	    duoToneParams.setMat3(1, 0, red(colors[darkColor]) / 255.0f);
	    duoToneParams.setMat3(1, 1, red(colors[darkColor]) / 255.0f);
	    duoToneParams.setMat3(1, 2, red(colors[darkColor]) / 255.0f);
	    
	    // Exponents of the color curves.
	    duoToneParams.setMat3(2, 0, 1);
	    duoToneParams.setMat3(2, 1, 1);
	    duoToneParams.setMat3(2, 2, 1);      
	    
	    duoTone = new GLTextureFilter(this, "resources/glgraphics/ex/duotone/DuoTone.xml", duoToneParams);
	    
	    srcTex = new GLTexture(this, "resources/glgraphics/ex/duotone/milan_rubbish.jpg");
	    destTex = new GLTexture(this, srcTex.width, srcTex.height);
	    
	    bCurve = new ExplicitBezierCurve(2, 210.0f, 210.0f, 180.0f, 180.0f,this);
	    
	    bCurve.setPointAsFirst(0);
	    bCurve.setPointCoords(0, 0.0f, 0.0f);
	    bCurve.setCPoint1Coords(0, 60.0f, 60.0f);  
	    bCurve.setPointConstrains(0, 0.0f, 0.0f, 0.0f, 180.0f);
	    bCurve.setCPoint1Constrains(0, 0.0f, 180.0f, 0.0f, 180.0f);

	    bCurve.setPointAsLast(1);
	    bCurve.setPointCoords(1, 180.0f, 180.0f);
	    bCurve.setCPoint0Coords(1, 120.0f, 120.0f);
	    bCurve.setPointConstrains(1, 180.0f, 180.0f, 0.0f, 180.0f);
	    bCurve.setCPoint0Constrains(1, 0.0f, 180.0f, 0.0f, 180.0f);   
	 
	    controlP5 = new ControlP5(this);
	    controlP5.setAutoDraw(false); 
	    rexpSlider = controlP5.addSlider("redExponent", 
	                                         0, 1, 1, // Minimum, maximum, initial value
	                                         10, 220, 46, 20); // x0, y0, x1, y1   
	    rexpSlider.setLabel("");    
	    rexpSlider.update();    
	    
	    rexpSlider = controlP5.addSlider("greenExponent", 
	                                         0, 1, 1, // Minimum, maximum, initial value
	                                         70, 220, 46, 20); // x0, y0, x1, y1   
	    rexpSlider.setLabel("");    
	    rexpSlider.update();    
	    
	    rexpSlider = controlP5.addSlider("greenExponent", 
	                                         0, 1, 1, // Minimum, maximum, initial value
	                                         130, 220, 46, 20); // x0, y0, x1, y1   
	    rexpSlider.setLabel("");    
	    rexpSlider.update();   
	}

	public void draw()
	{
	    background(0);
	    
	    bCurve.draw(true);

	    image(srcTex, 0, 0, 200, 200);
	    srcTex.filter(duoTone, destTex, duoToneParams);
	    image(destTex, 200, 0, 200, 200);
	    
	    rectMode(CORNER);
	    noStroke();
	    fill(colors[0]);
	    rect(10, 260, 46, 46);
	    fill(colors[1]);
	    rect(70, 260, 46, 46);    
	    fill(colors[2]);
	    rect(130, 260, 46, 46);
	 
	    stroke(255);
	    noFill();
	    if (lightColor == 0) rect(10, 260, 46, 46);
	    else if (lightColor == 1) rect(70, 260, 46, 46);
	    else if (lightColor == 2) rect(130, 260, 46, 46); 

	    noStroke();
	    fill(colors[0]);
	    rect(10, 320, 46, 46);
	    fill(colors[1]);
	    rect(70, 320, 46, 46);    
	    fill(colors[2]);
	    rect(130, 320, 46, 46);
	 
	    stroke(255);
	    noFill();
	    if (darkColor == 0) rect(10, 320, 46, 46);
	    else if (darkColor == 1) rect(70, 320, 46, 46);
	    else if (darkColor == 2) rect(130, 320, 46, 46);
	    
	    controlP5.draw();    
	}

	public void mouseDragged()
	{
	    bCurve.updateSelPoints(mouseX, mouseY, pmouseX, pmouseY);
	    
	    duoToneParams.parPoint0[0] = bCurve.curvePoints[0].p.x / bCurve.lengthX; 
	    duoToneParams.parPoint0[1] = bCurve.curvePoints[0].p.y / bCurve.lengthY;
	    
	    duoToneParams.parPoint1[0] = bCurve.curvePoints[0].cp1.x / bCurve.lengthX;
	    duoToneParams.parPoint1[1] = bCurve.curvePoints[0].cp1.y / bCurve.lengthY;
	    
	    duoToneParams.parPoint2[0] = bCurve.curvePoints[1].cp0.x / bCurve.lengthX;
	    duoToneParams.parPoint2[1] = bCurve.curvePoints[1].cp0.y / bCurve.lengthY;
	    
	    duoToneParams.parPoint3[0] = bCurve.curvePoints[1].p.x / bCurve.lengthX;
	    duoToneParams.parPoint3[1] = bCurve.curvePoints[1].p.y / bCurve.lengthY;  
	}

	public void mouseReleased()
	{
	    bCurve.unselectAllPoints();
	    
	    boolean changedLightColor = false;
	    boolean changedDarkColor = false;    
	    if ((10 <= mouseX) && (mouseX <= 56) && (260 <= mouseY) && (mouseY <= 306))
	    {
	        lightColor = 0;
	        changedLightColor = true;
	    }
	    else if ((70 <= mouseX) && (mouseX <= 116) && (260 <= mouseY) && (mouseY <= 306)) 
	    {
	        lightColor = 1;
	        changedLightColor = true;        
	    }
	    else if ((130 <= mouseX) && (mouseX <= 176) && (260 <= mouseY) && (mouseY <= 306)) 
	    {
	        lightColor = 2;
	        changedLightColor = true;        
	    }
	    
	    if ((10 <= mouseX) && (mouseX <= 56) && (320 <= mouseY) && (mouseY <= 366))
	    {
	        darkColor = 0;
	        changedDarkColor = true;
	    }
	    else if ((70 <= mouseX) && (mouseX <= 116) && (320 <= mouseY) && (mouseY <= 366)) 
	    {
	        darkColor = 1;
	        changedDarkColor = true;        
	    }
	    else if ((130 <= mouseX) && (mouseX <= 176) && (320 <= mouseY) && (mouseY <= 366)) 
	    {
	        darkColor = 2;
	        changedDarkColor = true;        
	    }    
	    
	    if (changedLightColor)
	    {
	        duoToneParams.setMat3(0, 0, red(colors[lightColor]) / 255.0f);
	        duoToneParams.setMat3(0, 1, green(colors[lightColor]) / 255.0f);
	        duoToneParams.setMat3(0, 2, blue(colors[lightColor]) / 255.0f);  
	    }     

	    if (changedDarkColor)
	    {
	        duoToneParams.setMat3(1, 0, red(colors[darkColor]) / 255.0f);
	        duoToneParams.setMat3(1, 1, red(colors[darkColor]) / 255.0f);
	        duoToneParams.setMat3(1, 2, red(colors[darkColor]) / 255.0f);
	    }

	}

	public void controlEvent(ControlEvent event) 
	{
	    if (event.controller().name() == "redExponent")
	    {
	        float t  = event.controller().value();
	        duoToneParams.setMat3(2, 0, t);
	    } 
	    else if (event.controller().name() == "greenExponent")
	    {
	        float t  = event.controller().value();
	        duoToneParams.setMat3(2, 1, t);
	    }    
	    else if (event.controller().name() == "blueExponent")
	    {
	        float t  = event.controller().value();
	        duoToneParams.setMat3(2, 2, t);
	    }    
	}

}
