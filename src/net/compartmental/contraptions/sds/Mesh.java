package net.compartmental.contraptions.sds;

import javax.vecmath.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Mesh
{
	public static final int QUAD = PApplet.QUADS;
	public static final int TRI = PApplet.TRIANGLES;
	public static final int HYBRID = 2;
	  
    public Vertex[] vertices;
    public Vertex[] transVerts;
    public int[]	indices;
    public int      type;
    public Vector3f center;
    public float    scale;
    public float    rx, ry, rz;
    public boolean  wireframe;
    protected PImage  tex;
    protected int[]   uv;
    
    
    public Mesh()
    {
      tex = null;
      center = new Vector3f();
      scale = 1;
      rx = ry = rz = 0;
      wireframe = false;
      vertices = new Vertex[0];
      transVerts = new Vertex[0];
    }
    
    public Mesh(Vertex[] vertices, int[] indices, int type)
    {
      this.type = type;
      if ( type == QUAD && indices.length % 4 != 0 )
      {
    	throw new IllegalArgumentException("Number of vertices is not a multiple of four.");
      }
      else if ( type == TRI && indices.length % 3 != 0 )
      {
    	throw new IllegalArgumentException("Number of vertices is not a multiple of three.");
      }
      else
      {
        this.vertices = vertices;
        this.indices  = indices;
        transVerts = new Vertex[vertices.length];
        System.arraycopy(vertices, 0, transVerts, 0, vertices.length);
      }
      tex = null;
      tex = null;
      center = new Vector3f();
      scale = 1;
      rx = ry = rz = 0;
      wireframe = false;
    }
    
    public int getType()
    {
    	return type;
    }
    
	public void scale(float s) 
	{
	  scale *= s;
	  transformVertices();
	}
	
	public float getScale()
	{
	    return scale;
	}
	
	public void translate(float x, float y, float z)
	{
	  Vector3f t = new Vector3f(x, y, z);
	  center.add(t);
	  transformVertices();
	}
	
	public void rotateX(float r)
	{
	  rx += r;
	  transformVertices();
	}
	
	public void rotateY(float r)
	{
	  ry += r;
	  transformVertices();
	}
	
	public void rotateZ(float r)
	{
	  rz += r;
	  transformVertices();
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
	  if ( type == QUAD )
	    uv = new int[] { 0, 0, 0, 1, 1, 1, 1, 0 };
	  else if ( type == TRI )
		uv = new int[] { 0, 0, 0, 1, 1, 1 };
	  else uv = new int[] { 0, 1 };
	}
	
	protected void transformVertices()
	{
		Matrix4f transform = new Matrix4f();
		Matrix3f rotScale = new Matrix3f();
		rotScale.setScale(scale);
		Matrix3f rX = new Matrix3f();
		Matrix3f rY = new Matrix3f();
		Matrix3f rZ = new Matrix3f();
		rX.rotX(rx);
		rY.rotY(ry);
		rZ.rotZ(rz);
		PApplet.println("rX: " + rX.toString());
		PApplet.println("rY: " + rY.toString());
		PApplet.println("rZ: " + rZ.toString());
		rotScale.mul(rX);
		rotScale.mul(rY);
		rotScale.mul(rZ);
		PApplet.println("rotScale: " + rotScale.toString());
		transform.setRotationScale(rotScale);
	    transform.setTranslation(center);
	    PApplet.println(transform.toString());
		Vertex v;
		Point3f vt;
		for (int i = 0; i < vertices.length; i++)
		{
			v = vertices[i];
			vt = new Point3f();
			transform.transform(v.p, vt);
			transVerts[i] = new Vertex(vt.x, vt.y, vt.z, v.n.x, v.n.y, v.n.z, v.color);
		}
	}
	
	public void calculateNormals()
	{
	    // zero out the normals to prepare for recalcutation
	    for(int i = 0; i < transVerts.length; ++i)
	    {
	        transVerts[i].n.set( 0,0,0 );
	    }
	    
	    int step = type == QUAD ? 4 : 3;
	    // find the normal of each face and add it into the normal of each vert on the face
	    for(int i = 0; i < indices.length; i += step)
	    {
	        Vertex A = transVerts[ indices[i] ];
	        Vertex B = transVerts[ indices[i+1] ];
	        Vertex C = transVerts[ indices[i+2] ];

	        Vector3f axis1 = new Vector3f();
	        axis1.sub( B.p, A.p );
	        Vector3f axis2 = new Vector3f();
	        axis2.sub( C.p, A.p );
	        
	        Vector3f faceNormal = new Vector3f();
	        faceNormal.cross( axis1, axis2 );
	        faceNormal.normalize();
	        
	        A.n.add( faceNormal );
	        B.n.add( faceNormal );
	        C.n.add( faceNormal );
	        
	        if ( type == QUAD )
	        {
	            transVerts[ indices[i+3] ].n.add( faceNormal );
	        }
	    }
	    
	    // normalize all the normals
	    for(int i = 0; i < transVerts.length; ++i)
	    {
	        transVerts[i].n.normalize();
	    }
	}
    
	public void draw(PApplet p) 
	{
	  p.textureMode(PApplet.NORMAL);
	  p.beginShape(type);
	  for (int i = 0; i < indices.length; i++) 
	  {
		  if ( tex != null) 
		  {
			  int uvi = i%(uv.length/2);
			  if ( uvi == 0 ) p.texture(tex);
			  Point3f vp = transVerts[indices[i]].p;
			  p.vertex(vp.x, vp.y, vp.z, uv[uvi*2], uv[(uvi*2)+1]);
		  }
		  else
		  {
		    Vertex v = transVerts[indices[i]];
		    p.fill(v.color);
		    p.normal(v.n.x, v.n.y, v.n.z);
		    p.vertex(v.p.x, v.p.y, v.p.z);
		  }
	  }
	  p.endShape();
	}
	
	public void renderNormals( PApplet p, float length, int color )
	{
	    p.stroke( color );
	    for(int i = 0; i < transVerts.length; ++i)
	    {
	        Vertex v = transVerts[i];
	        p.line( v.p.x, v.p.y, v.p.z, v.p.x + v.n.x * length, v.p.y + v.n.y * length, v.p.z + v.n.z * length );
	    }
	}
	
	public void drawWireFrame(PApplet p, int c)
	{
	  p.noFill();
	  p.stroke(c);
	  p.beginShape(type);
	  for (int i = 0; i < indices.length; i++)
	  {
		  Point3f v = transVerts[indices[i]].p;
		  p.vertex(v.x, v.y, v.z);
	  }
	  p.endShape();
	}
}
