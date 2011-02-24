uniform vec2 point;
//uniform float alpha;

vec2 c_div(vec2 c1,vec2 c2){
	vec2 result ;
	result.x = (c1.x*c2.x + c1.y*c2.y)/(c2.x*c2.x + c2.y*c2.y);
	result.y = (c1.y*c2.x - c2.x*c2.x)/(c2.x*c2.x + c2.y*c2.y);
	return result;
}

vec2 c_mul(vec2 c1,vec2 c2){
	vec2 result ;
	result.x = c1.x*c2.x - c1.y*c2.y;
	result.y = c1.x*c2.y + c1.y*c2.x;
	return result;
}

vec2 c_sub(vec2 c1,vec2 c2){
	vec2 result ;
	result.x = c1.x - c2.x;
	result.y = c1.y - c2.y;
	return result;
}

vec2 c_add(vec2 c1,vec2 c2){
	vec2 result ;
	result.x = c1.x + c2.x;
	result.y = c1.y + c2.y;
	return result;
}

vec2 c_sq(vec2 c){
	return c_mul(c,c);
}

float c_mod(vec2 c1){
	vec2 result ;
	float mod_sq=c1.x*c1.x+c1.y*c1.y;
	return sqrt(mod_sq);
}

void main(void)
{
    vec2 st = gl_TexCoord[0].st;
	st = st*4.0-2.0;
	float loopCtr = 0.0;
	while (loopCtr<50.0) {
		vec2 zsq = c_mul(st,st);
		st = c_add( zsq, point);
		if (c_mod(st)>2.0) { break; }
		loopCtr+=1;
	}
	float a= loopCtr/50.0;
	gl_FragColor = vec4(1, loopCtr/50.0 ,0, a);
}
