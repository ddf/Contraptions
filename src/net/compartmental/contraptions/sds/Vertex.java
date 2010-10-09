package net.compartmental.contraptions.sds;

import javax.vecmath.*;

public class Vertex 
{
  public Point3f p;
  public Vector3f n;
  public int   color;
  
  public Vertex()
  {
    this(0, 0, 0, 0, 0, 0, -1);
  }
  
  public Vertex(float x, float y, float z)
  {
	this(x, y, z, -1);
  }
  
  public Vertex(float x, float y, float z, int color)
  {
	this(x, y, z, 0, 0, 0, color);
	setNormFromPos();
  }
  
  public Vertex(float x, float y, float z, float nx, float ny, float nz)
  {
	this(x, y, z, nx, ny, nz, -1);
  }
  
  public Vertex(float x, float y, float z, float nx, float ny, float nz, int color)
  {
	p = new Point3f(x, y, z);
	n = new Vector3f(nx, ny, nz);
    this.color = color;
  }
  
  public Vertex(Vertex v)
  {
	this(v.p.x, v.p.y, v.p.z, v.n.x, v.n.y, v.n.z, v.color);
  }
  
  public void setNormFromPos()
  {
    n.set(p);
    n.normalize();
  }
  
  public void setColor( int color )
  {
      this.color = color;
  }
  
  public boolean equals(Object o)
  {
	Vertex v = (Vertex)o;
    return p.x == v.p.x && p.y == v.p.y && p.z == v.p.z;
  };
  
  public int hashCode()
  {
	return (int)(p.x + p.y + p.z);
  }
  
  public String toString()
  {
    return p.toString();
  }
}