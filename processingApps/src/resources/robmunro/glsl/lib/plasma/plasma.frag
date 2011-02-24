//Simple Plasma effect fragment shader by Dave Stanley(Dr_D)

uniform float sTime;
uniform vec4 amplitudes;
uniform vec4 amplitudes2;
void main(void) {
//vec4 Color2 = vec4(0.0);
//float time = sTime;
vec4 Color1 = vec4(0.0);
vec2 tpos = gl_FragCoord.xy;
float t = sTime;
float x = tpos.x;
float y = tpos.y;

Color1.r = (0.5 + 0.49 * cos(t+x / (amplitudes2.w + 32.0 * cos(y / amplitudes.x+t))) * cos(y / (amplitudes2.x + 16.0 * sin(x / amplitudes.w ))));
Color1.g = (0.5 - 0.49 * cos(x*2 / (96.0 + 16.0 * cos(y*2.0 / (amplitudes2.y+t)))) * cos(y / (64.0 + 16.0  * sin(x / amplitudes2.z + t))));
Color1.b = (0.5 - 0.49 * sin(y / (amplitudes2.w + 8.0 * cos(x / (amplitudes.y+ t)))) * cos(x / (64.0 + 32.0  * sin(y / amplitudes.z )) ));
Color1.a = 1;

//Color1.r = (0.5 + 0.49 * cos(t+x / (128.0 + 32.0 * cos(y / 64.0+t))) * cos(y / (320.0 + 16.0 * sin(x / 64.0 ))));
//Color1.g = (0.5 - 0.49 * cos(x*2 / (96.0 + 16.0 * cos(y*2.0 / (256.0+t)))) * cos(y / (64.0 + 16.0  * sin(x / 64.0 + t))));
//Color1.b = (0.5 - 0.49 * sin(y / (128.0 + 8.0 * cos(x / (96.0 + t)))) * cos(x / (64.0 + 32.0  * sin(y / 512.0 )) ));
//Color1.a = 1;
gl_FragColor = Color1;
}