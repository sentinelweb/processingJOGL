
uniform int parInt1;
uniform vec2 parInt2;

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
	vec2 var=vec2(0.0, 0.0);
	vec2 p=vec2(0.0, 0.0);
	//int parInt1 = 50;
	vec2 f = parInt2.yx;
	vec2 g = parInt3.yx;
	while (loopCtr<50.0) {
		var = c_mul(var, var);
		for (int ctr=0;ctr<(parInt1-1);ctr++) {
			 var = c_mul(var, var);
		}
		//var = c_div( var, c_sq(p));
		var = c_add( var, st);
		if (c_mod(var)>2.0) { break; }
		loopCtr+=1;
	}
	gl_FragColor = vec4(1, loopCtr/50.0 ,0, 1.0);
	//gl_FragColor = vec4(st.s,st.t ,0, 1.0);
}
