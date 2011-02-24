
//
// glass.vert: Vertex shader for doing a glass-like effect
//
// author: John Kessenich
//
// Copyright (c) 2002: 3Dlabs, Inc.
//

varying vec3 LightDir;
varying vec3 EyeDir;
varying vec3 Normal;
varying vec3 Error;
varying vec4 theColor;

uniform vec3 LightPosition;

void main(void) 
{
	
    gl_Position  =  gl_ModelViewProjectionMatrix * gl_Vertex;
    EyeDir       = -1.0 * normalize(vec3(gl_ModelViewMatrix * gl_Vertex));
    Error = vec3(0.0,0.0,0.0);
    LightDir     =  normalize(LightPosition);
    Error = vec3(1.0,1.0,1.0);
    gl_TexCoord[0] =  gl_MultiTexCoord0;
    theColor = gl_Color;
    Normal       =  normalize(gl_NormalMatrix * gl_Normal);
}
