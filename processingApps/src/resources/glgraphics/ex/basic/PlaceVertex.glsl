uniform sampler2D src_tex_unit0; // Position texture

void main(void)
{
   // gl_TexCoord[0].xy = gl_MultiTexCoord0.xy;	
   // gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	vec2 pos;
	vec4 newVertexPos;

	gl_TexCoord[0].xy = gl_MultiTexCoord0.xy;		

	pos = texture2D(src_tex_unit0, gl_MultiTexCoord0.xy).xy;
	
	newVertexPos = vec4(60.0 * pos + gl_Vertex.xy, 0.0, gl_Vertex.w);
	
	gl_Position = gl_ModelViewProjectionMatrix * newVertexPos;
}
