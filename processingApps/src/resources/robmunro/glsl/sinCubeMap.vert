uniform float time;
uniform vec3  LightPos;

varying vec3  ReflectDir;
varying float LightIntensity;

void main(void)	{
	gl_Position    = ftransform();
	gl_FrontColor = gl_Color;
	vec4 v = vec4(gl_Vertex);
	v.y = v.z * sin(0.2*v.z + time*0.1);		
	gl_Position    = ftransform();
	gl_Position = gl_ModelViewProjectionMatrix * v;
	
	
    vec3 normal    = normalize(gl_NormalMatrix * gl_Normal);
    vec4 pos       = gl_ModelViewMatrix * v;
    vec3 eyeDir    = pos.xyz;
    ReflectDir     = reflect(eyeDir, normal);
    LightIntensity = max(dot(normalize(LightPos - eyeDir), normal),0.0);
} 
