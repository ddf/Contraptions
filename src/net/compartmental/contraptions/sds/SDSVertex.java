package net.compartmental.contraptions.sds;

import java.util.ArrayList;
import java.util.HashMap;

public class SDSVertex extends Vertex
{
  public ArrayList<SDSFace> faces;
  public ArrayList<SDSVertex> verts;
  public int       nInd;
  public HashMap<SDSVertex, Integer>   edgeMids;

  public SDSVertex()
  {
	super();
	init();
  }
  
  public SDSVertex(float x, float y, float z)
  {
	super(x, y, z);
	init();
  }
  
  public SDSVertex(float x, float y, float z, int color)
  {
	super(x, y, z, color);
	init();
  }
  
  public SDSVertex(float x, float y, float z, float nx, float ny, float nz)
  {
	super(x, y, z, nx, ny, nz);
	init();
  }
  
  public SDSVertex(float x, float y, float z, float nx, float ny, float nz, int color)
  {
	super(x, y, z, nx, ny, nz, color);
	init();
  }
  
  public SDSVertex(Vertex v)
  {
	super(v);
	init();
  }
  
  private void init()
  {
	faces = new ArrayList<SDSFace>(4);
	verts = new ArrayList<SDSVertex>(4);
	nInd = -1;
	edgeMids = new HashMap<SDSVertex, Integer>(4);
  }
  
  // add v to this vertex in place, returns itself
  public SDSVertex add(Vertex v)
  {
    p.x += v.p.x;
    p.y += v.p.y;
    p.z += v.p.z;
    return this;
  }
  
  public SDSVertex div(float n)
  {
    p.x /= n;
    p.y /= n;
    p.z /= n;
    return this;
  }
  
  public SDSVertex mul(float n)
  {
    p.x *= n;
    p.y *= n;
    p.z *= n;
    return this;
  }
}
