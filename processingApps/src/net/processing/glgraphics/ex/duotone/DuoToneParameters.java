package net.processing.glgraphics.ex.duotone;

import processing.core.PApplet;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTextureFilterParameters;

class DuoToneParameters extends GLTextureFilterParameters
{
    public DuoToneParameters(PApplet parent)
    {
        super(parent);
        
        parPoint0 = new float[2];
        parPoint1 = new float[2];
        parPoint2 = new float[2];
        parPoint3 = new float[2];        

        // Default Bezier control points for a straight unitary line.        
        parPoint0[0] = 0.0f;
        parPoint0[1] = 0.0f;
        
        parPoint1[0] = 1.0f / 3.0f;
        parPoint1[1] = 1.0f / 3.0f;
        
        parPoint2[0] = 2.0f / 3.0f;
        parPoint2[1] = 2.0f / 3.0f;
        
        parPoint3[0] = 1.0f;
        parPoint3[1] = 1.0f;
    }

    public void getParamIDs(GLSLShader shader)
    {
        super.getParamIDs(shader);
        
        point0Uniform = shader.getUniformLocation("point0");
        point1Uniform = shader.getUniformLocation("point1");
        point2Uniform = shader.getUniformLocation("point2");
        point3Uniform = shader.getUniformLocation("point3");
    }

    public void setParamValues()
    {
        super.setParamValues();
        
        if (-1 < point0Uniform) gl.glUniform2fARB(point0Uniform, parPoint0[0], parPoint0[1]);      
        if (-1 < point1Uniform) gl.glUniform2fARB(point1Uniform, parPoint1[0], parPoint1[1]);
        if (-1 < point2Uniform) gl.glUniform2fARB(point2Uniform, parPoint2[0], parPoint2[1]);      
        if (-1 < point3Uniform) gl.glUniform2fARB(point3Uniform, parPoint3[0], parPoint3[1]);           
    }
    
    public float[] parPoint0;
    public float[] parPoint1;
    public float[] parPoint2;
    public float[] parPoint3;    
    
    protected int point0Uniform;
    protected int point1Uniform;
    protected int point2Uniform;
    protected int point3Uniform;
}
