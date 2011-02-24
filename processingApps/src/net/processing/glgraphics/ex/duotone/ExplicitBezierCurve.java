package net.processing.glgraphics.ex.duotone;

import processing.core.PApplet;

public class ExplicitBezierCurve  {
	PApplet mainApplet = null;
	 ExplicitBezierCurve(int n, float x, float y, float w, float h,PApplet main)
	    {
	        npoints = n; 
	        curvePoints = new BezierPoint[npoints];
	        for (int i = 0; i < npoints; i++)
	        {
	            curvePoints[i] = new BezierPoint();
	        }
	        
	        originX = x;
	        originY = y;
	        lengthX = w;
	        lengthY = h;
	        mainApplet=main;
	    }
	    
	    void setPointAsFirst(int i)
	    {
	        curvePoints[i].first = true;  
	    }
	    void setPointAsLast(int i)
	    {
	        curvePoints[i].last = true;  
	    }
	    void setPointCoords(int i, float x, float y)
	    {
	        curvePoints[i].p.set(x, y);  
	    }
	    void setCPoint0Coords(int i, float x, float y)
	    {
	        curvePoints[i].cp0.set(x, y);  
	    }
	    void setCPoint1Coords(int i, float x, float y)
	    {
	        curvePoints[i].cp1.set(x, y);  
	    }
	    void setPointConstrains(int i, float x0, float x1, float y0, float y1)
	    {
	        curvePoints[i].pConstrain.set(x0, x1, y0, y1); 
	    }    
	    void setCPoint0Constrains(int i, float x0, float x1, float y0, float y1)
	    {
	        curvePoints[i].cp0Constrain.set(x0, x1, y0, y1); 
	    }    
	    void setCPoint1Constrains(int i, float x0, float x1, float y0, float y1)
	    {
	        curvePoints[i].cp1Constrain.set(x0, x1, y0, y1); 
	    }
	    
	    void updateSelPoints(float x, float y, float px, float py)
	    {
	        for (int i = 0; i < npoints; i++)
	            curvePoints[i].update(x - originX, y - originY, px - originX, py - originY);
	    }
	    
	    void unselectAllPoints()
	    {
	        for (int i = 0; i < npoints; i++)  
	            curvePoints[i].unselect();
	    }

	    void checkUniformity()
	    {
	        float x0, x1, l;
	        for (int i = 0; i < npoints - 1; i++)
	        {
	            x0 = curvePoints[i].p.x; 
	            x1 = curvePoints[i + 1].p.x;            
	            l = x1 - x0;
	            curvePoints[i].checkUniformity1(x0 + l / 3); 
	            curvePoints[i + 1].checkUniformity0(x1 - l / 3); 
	        }
	    }
	    
	    // For explicity Bezier curves, x = t.
	    float eval(int i, float x)
	    {
	        float t = x / (curvePoints[i + 1].p.x - curvePoints[i].p.x);
	        return mainApplet.pow(1 - t, 3) * curvePoints[i].p.y + 
	               3 * t * mainApplet.pow(1 - t, 2) * curvePoints[i].cp1.y + 
	               3 * mainApplet.sq(t) * (1 - t) * curvePoints[i + 1].cp0.y + 
	               mainApplet.pow(t, 3) * curvePoints[i + 1].p.y;
	    }

	    float eval(float x)
	    {
	        for (int i = 0; i < npoints - 1; i++)
	        {
	            if ((curvePoints[i].p.x <= x) && (x < curvePoints[i + 1].p.x))
	            {
	                return eval(i, x);  
	            }
	        }
	        return 0.0f;
	    }

	   public void draw()
	    {
	        draw(false);  
	    }
	    
	    void draw(boolean eval)
	    {
	    	mainApplet.pushMatrix();
	        
	        checkUniformity();
	        
	        mainApplet.translate(originX, originY);

	        // Drawing bounding box.        
	        mainApplet.rectMode(PApplet.CORNER);
	        mainApplet.stroke(0, 0, 255);
	        mainApplet.noFill();
	        mainApplet.rect(0.0f, 0.0f, lengthX,  lengthY);

	        // Drawing curve.
	        mainApplet.rectMode(PApplet.CENTER);    
	    
	        if (eval)
	        {
	        	mainApplet.noFill();  
	        	mainApplet.stroke(255);          
	            float y0, y, x0, x;
	            x0 = 0.0f;
	            y0 = eval(x0);
	            for (x = 0.0f; x < lengthX; x += 1.0)
	            {
	                y = eval( x);
	                mainApplet.line(x0, y0, x, y);
	                x0 = x;
	                y0 = y;
	            }
	            for (int i = 0; i < npoints; i++)
	            {
	            	mainApplet.fill(255);
	            	mainApplet.rect(curvePoints[i].p.x, curvePoints[i].p.y, 5, 5);
	            	mainApplet.noFill();               
	            }
	        }
	        else
	        {
	        	mainApplet.noFill();  
	        	mainApplet.stroke(255);
	        	mainApplet. beginShape();
	        	mainApplet. vertex(curvePoints[0].p.x, curvePoints[0].p.y);
	        	mainApplet.fill(255);
	            mainApplet.rect(curvePoints[0].p.x, curvePoints[0].p.y, 5f, 5f);
	            mainApplet.noFill();
	            for (int i = 1; i < npoints; i++)
	            {
	            	mainApplet.bezierVertex(curvePoints[i - 1].cp1.x, curvePoints[i - 1].cp1.y, curvePoints[i].cp0.x, curvePoints[i].cp0.y, curvePoints[i].p.x, curvePoints[i].p.y);
	                mainApplet.fill(255);
	                mainApplet.rect(curvePoints[i].p.x, curvePoints[i].p.y, 5, 5);
	                mainApplet.noFill();        
	            }
	            mainApplet.endShape();
	        }
	    
	        // Drawing direction vectors.
	        mainApplet. stroke(255, 0, 0);
	        for (int i = 1; i < npoints; i++)
	        {
	        	mainApplet.line(curvePoints[i - 1].p.x, curvePoints[i - 1].p.y,
	                 curvePoints[i - 1].cp1.x, curvePoints[i - 1].cp1.y);
	        	mainApplet.fill(255, 0, 0);
	        	mainApplet.rect(curvePoints[i - 1].cp1.x, curvePoints[i - 1].cp1.y, 5, 5);
	        	mainApplet.noFill();
	    
	        	mainApplet.line(curvePoints[i].p.x, curvePoints[i].p.y,
	                 curvePoints[i].cp0.x, curvePoints[i].cp0.y);
	        	mainApplet.fill(255, 0, 0);
	        	mainApplet.rect(curvePoints[i].cp0.x, curvePoints[i].cp0.y, 5, 5);
	        	mainApplet.noFill();
	        }
	        
	        mainApplet.popMatrix();        
	    }
	 
	    int npoints;
	    BezierPoint curvePoints[];
	    float originX, originY;
	    float lengthX, lengthY;
}
