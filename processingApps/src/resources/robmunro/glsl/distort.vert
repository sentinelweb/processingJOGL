//varying vec4 theColor;
void main(void) 
{
	//theColor = gl_Color;
	
	vec4 vertex = gl_ModelViewProjectionMatrix * gl_Vertex;
	//vertex.y = vertex.y+200.0*noise1(70.0);
    gl_Position  =   vertex;
}