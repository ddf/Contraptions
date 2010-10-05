package net.compartmental.contraptions.sds;

import processing.core.PApplet;

public class SDSFace 
{
  public SDSVertex[] v;
  public Vertex center;
  public int    cInd;
  
  public SDSFace(int nVerts)
  {
	v = new SDSVertex[nVerts];
	cInd = -1;
  }
  
  public SDSFace(SDSVertex[] v)
  {
	this.v = v;
	float x, y, z;
	x = y = z = 0;
	int color = v[0].color;
	for(int i = 0; i < v.length; i++)
	{
	  v[i].faces.add(this);
	  x += v[i].p.x;
	  y += v[i].p.y;
	  z += v[i].p.z;
	  color = PApplet.lerpColor(color, v[i].color, 0.5f, PApplet.RGB);
	}
	x /= v.length;
	y /= v.length;
	z /= v.length;
	center = new Vertex(x, y, z, color);
	cInd = -1;
  }
  
}
