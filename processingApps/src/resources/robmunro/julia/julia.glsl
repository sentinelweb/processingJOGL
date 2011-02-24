
uniform vec2 par_vec21;
uniform float par_flt1;

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
		//st = c_sub( zsq, par_vec21);
		st = c_add( zsq, par_vec21);
		if (c_mod(st)>2.0) { break; }
		loopCtr+=1;
	}
	//float alpha = 1.0;
	float alpha = par_flt1;
	//if (loopCtr==50.0 || loopCtr<5.0) {alpha=0.0;}
	gl_FragColor = vec4(1, loopCtr/50.0 ,0, alpha);
	//gl_FragColor = vec4(st.s,st.t ,0, 1.0);
	
}
