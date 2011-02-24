package net.processing.glgraphics.ex.duotone;

import processing.core.PApplet;

class BezierPoint
{
    BezierPoint()
    {
        first = last = false;
        p = new Point();
        cp0 = new Point();
        cp1 = new Point();
        
        pConstrain = new Constrain();
        cp0Constrain = new Constrain();
        cp1Constrain = new Constrain();        
    }
    
    void update(float mX, float mY, float mX0, float mY0)
    {
        update(p, pConstrain, mX, mY, mX0, mY0);
        if (!first) update(cp0, cp0Constrain, mX, mY, mX0, mY0);
        if (!last) update(cp1, cp1Constrain, mX, mY, mX0, mY0);          
    }    

    void checkUniformity0(float x)
    {
        if (!first && (PApplet.abs(cp0.x - x) > 1e-4f )) cp0.x = x;
    }

    void checkUniformity1(float x)
    {
        if (!last && (PApplet.abs(cp1.x - x) > 1e-4 )) cp1.x = x;
    }
    
    void update(Point pt, Constrain cons, float mX, float mY, float mX0, float mY0)
    {
        if (pt.selected || (PApplet.dist(pt.x, pt.y, mX, mY) < 5.0))
        {
            pt.selected = true;
            pt.update(mX - mX0, mY - mY0, cons);
        }
    }
    
    void unselect()
    {
        p.selected = false;  
        cp0.selected = false;
        cp1.selected = false;        
    }
  
    boolean first, last;
    Point p, cp0, cp1;
    Constrain pConstrain, cp0Constrain, cp1Constrain;
    
    class Point
    {
        Point()
        {
            x = y = 0.0f;
            selected = false;
        }
    
        void set(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    
        void update(float dx, float dy, Constrain cons)
        {
            if (cons.active)
            {
                x = PApplet.constrain(x + dx, cons.x0, cons.x1);
                y = PApplet.constrain(y + dy, cons.y0, cons.y1);                
            }
            else
            {
                x += dx;  
                y += dy;                
            }
        }
    
        float x, y;
        boolean selected;
    }
    
    class Constrain
    {
        Constrain()
        {
            x0 = y0 = x1 = y0 = 0.0f;
            active = false;
        }
    
        void set(float x0, float x1, float y0, float y1)
        {
            this.x0 = x0;  
            this.x1 = x1;
            this.y0 = y0;  
            this.y1 = y1;
            active = true;  
        }
        
        void deactivate()
        {
            active = false;  
        }
    
        float x0, y0, x1, y1;
        boolean active;
    }    
}
