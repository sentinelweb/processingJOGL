uniform sampler2D src_tex_unit0; // Position texture

uniform float par_flt1;          // Brush size

void main(void)
{
	vec2 pos;
	vec4 newVertexPos;

	gl_TexCoord[0].xy = gl_MultiTexCoord0.xy;		
	gl_TexCoord[1].xy = gl_MultiTexCoord1.xy;	

	pos = texture2D(src_tex_unit0, gl_MultiTexCoord0.xy).xy;
	
	newVertexPos = vec4(pos + 10.0 * gl_Vertex.xy, 0.0, gl_Vertex.w);
	
	gl_Position = gl_ModelViewProjectionMatrix * newVertexPos;
}
