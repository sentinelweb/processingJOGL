// Render texture and bloom map
uniform sampler2D src_tex_unit0; 
uniform sampler2D src_tex_unit1;

// Control exposure with this value.
uniform float par_flt1;
// How much bloom to add.
uniform float par_flt2;
// Max bright.
uniform float par_flt3;

void main()
{
    vec2 st = gl_TexCoord[0].st;
    vec4 color = texture2D(src_tex_unit0, st);
    vec4 colorBloom = texture2D(src_tex_unit1, st);

    // Add bloom to the image
    color += colorBloom * par_flt2;

    // Perform tone-mapping.
    float Y = dot(vec4(0.30, 0.59, 0.11, 0.0), color);
    float YD = par_flt1 * (par_flt1 / par_flt3 + 1.0) / (par_flt1 + 1.0);
    color *= YD;
	
    gl_FragColor = color;
}
