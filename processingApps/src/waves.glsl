attribute float phase;
void main()  
{
  vec4 vertex = gl_Vertex;
  float val;
  float vx=vertex.x;
  float vz=vertex.z;
  val=sin(sqrt(vx*vx+vz*vz)+phase);
  vertex.y=vertex.y+30.0*val;
  gl_Position = gl_ModelViewProjectionMatrix * vertex;
  gl_FrontColor = gl_Color;
  gl_BackColor = gl_Color;
} 