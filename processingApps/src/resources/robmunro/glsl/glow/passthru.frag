//varying vec4 theColor;
void main (void){
	//gl_FragColor = theColor;// gl_TexCoord[0];
	gl_FragColor = gl_FrontColor;
}