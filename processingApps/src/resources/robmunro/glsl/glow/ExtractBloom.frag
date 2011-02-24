uniform sampler2D texture1;

// Brightness threshold.
uniform float filterThresh;

uniform float brightThreshold;

void main()
{
    vec2 st = gl_TexCoord[0].st;
    vec4 color = texture2D(texture1, st);
	
    // Calculate luminance
    float lum = dot(vec4(0.30, 0.59, 0.11, 0.0), color);
	
    // Extract very bright areas of the map.
    if (lum > filterThresh)
        gl_FragColor = color;
        
    else
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
}