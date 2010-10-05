package net.compartmental.contraptions.sds;

import processing.core.PApplet;
import processing.core.PImage;

public class TriMesh
{
    public Vertex[] vertices;
    public int[]	indices;
    public int      type;
    private PImage   tex;
    private int[]    uv;
    
    public TriMesh(Vertex[] vertices, int[] indices)
    {
      if ( indices.length % 3 != 0 )
      {
    	throw new IllegalArgumentException("Number of vertices is not a multiple of four.");
      }
      else
      {
        this.vertices = vertices;
        this.indices  = indices;
      }
      type = Mesh.TRI;
      tex = null;
    }
    
    public int getType()
    {
    	return type;
    }

	public void scale(float s) 
	{
	  scale(s, s, s);
	}

	public void scale(float x, float y, float z) 
	{
	  for (int i = 0; i < vertices.length; i++) 
	  {
		Vertex v = vertices[i];
		v.p.x *= x;
		v.n.x *= x;
		v.p.y *= y;
		v.n.y *= y;
		v.p.z *= z;
		v.n.z *= z;
	  }
	}

	public void translate(float x, float y, float z)
	{
	  for(int i = 0; i < vertices.length; i++)
	  {
		  Vertex v = vertices[i];
		  v.p.x += x;
		  v.p.y += y;
		  v.p.z += z;
	  }
	}
	
	public boolean isTextured() 
	{
	  return tex != null;
	}
	
	public PImage getTexture() 
	{
	  return tex;
	}
	
	public void setTexture(PImage t) 
	{
	  tex = t;
	  uv = new int[] { 0, 0, 0, 1, 1, 1 };
	}
	
	public void draw(PApplet p) 
	{
	  p.textureMode(PApplet.NORMAL);
	  p.beginShape(PApplet.TRIANGLES);
	  for (int i = 0; i < indices.length; i++) 
	  {
		  if ( tex != null) 
		  {
			  int uvi = i%3;
			  if ( uvi == 0 ) p.texture(tex);
			  Vertex v = vertices[indices[i]];
			  p.vertex(v.p.x, v.p.y, v.p.z, uv[uvi*2], uv[(uvi*2)+1]);
		  }
		  else
		  {
		    Vertex v = vertices[indices[i]];
		    p.fill(v.color);
		    p.normal(v.n.x, v.n.y, v.n.z);
		    p.vertex(v.p.x, v.p.y, v.p.z);
		  }
	  }
	  p.endShape();
	}
}
