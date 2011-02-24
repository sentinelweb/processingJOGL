uniform sampler2D src_tex_unit0;
uniform mat3 par_mat3;

uniform vec2 point0;
uniform vec2 point1;
uniform vec2 point2;
uniform vec2 point3;

void main(void)
{
    vec3 c0 = par_mat3[0].rgb;
	vec3 c1 = par_mat3[1].rgb;
	vec3 p = par_mat3[2].rgb;
	
	vec4 in_color = texture2D(src_tex_unit0, gl_TexCoord[0].st);
	
	float lum = dot(vec3(0.3, 0.59, 0.11), in_color.rgb);
	
	vec2 bezier_point = pow(1.0 - lum, 3.0) * point0 + 
	                    3.0 * lum * pow(1.0 - lum, 2.0) * point1 + 
						3.0 * pow(lum, 2.0) * (1.0 - lum) * point2 + 
						pow(lum, 3.0) * point3;
	
	vec3 f0 = vec3(pow(bezier_point.y, p.r), pow(bezier_point.y, p.g), pow(bezier_point.y, p.b));
	vec3 f1 = vec3(1.0 - f0.r, 1.0 - f0.g, 1.0 - f0.b);
	
	gl_FragColor = vec4(f0.r * c0.r + f1.r * c1.r, 
	                    f0.g * c0.g + f1.g * c1.g, 
						f0.b * c0.b + f1.b * c1.b, 1.0);
}
