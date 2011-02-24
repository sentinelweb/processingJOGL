#extension GL_ARB_draw_buffers : enable

uniform sampler2D src_tex_unit0; // Position texture
uniform sampler2D src_tex_unit1; // Velocity texture

uniform vec2 par_vec21;
uniform vec2 par_vec22;
uniform vec2 par_vec23;

float wrap_coord(float v, float maxv)
{
    float f, fracf;
    int intf;

    f = v / maxv;
    intf = int(f);
    fracf = f - float(intf);
    if (f < 0.0) return (1.0 + fracf) * maxv;
    else if (1.0 < f) return fracf * maxv;
    else return v;
}

void main(void)
{
    vec2 tex_coord = gl_TexCoord[0].st;

    // Updating particle position.
    vec2 old_pos = texture2D(src_tex_unit0, tex_coord).xy;
    vec2 old_vel = texture2D(src_tex_unit1, tex_coord).xy;

    vec2 new_pos, new_vel;
    
    float d = distance(par_vec22, old_pos);
    if (d < 1.0) d = 1.0;
    
    old_vel += par_vec23 / d;

    new_pos = old_pos + old_vel;

    new_vel = 0.99 * old_vel;

    // Wrapping position around the edges...
	new_pos.x = wrap_coord(new_pos.x, par_vec21.x);
	new_pos.y = wrap_coord(new_pos.y, par_vec21.y); 

    gl_FragData[0] = vec4(new_pos, 0.0, 1.0);
    gl_FragData[1] = vec4(new_vel, 0.0, 1.0);
}
