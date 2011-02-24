uniform sampler2D colorMap;
uniform sampler2D noiseMap;
uniform float timer;

void main (void){
	vec2 displacement = gl_TexCoord[0].st;

	float scaledTimer = timer*0.1;

	displacement.x += scaledTimer;
	displacement.y -= scaledTimer;

	vec3 noiseVec = normalize(texture2D(noiseMap, displacement.xy).xyz*2.0 - 1.0)*0.035;
	vec4 color = texture2D(colorMap, gl_TexCoord[0].st + noiseVec.xy).rgba;
	
	//vec4 color = texture2D(colorMap, gl_TexCoord[0].st ).rgba;
	//color.g=sin(timer);
	//color.a=1;
	
	gl_FragColor = color;
}