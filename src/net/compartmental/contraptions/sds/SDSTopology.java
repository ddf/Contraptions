package net.compartmental.contraptions.sds;

import java.util.ArrayList;

//import processing.core.PApplet;

public class SDSTopology 
{
  public ArrayList<SDSFace> faces;
  public ArrayList<SDSVertex> verts;
  public int meshType;
  
  public SDSTopology()
  {
  }
  
  public SDSTopology(Mesh m)
  {
	parseMesh(m);
  }
  
  public void parseMesh(Mesh m)
  {
	meshType = m.getType();
	if (meshType == Mesh.QUAD)
	{
		parseQuad(m);	
	}
	else if ( meshType == Mesh.TRI )
	{
		parseTri(m);
	}	  
  }
  
  private void parseQuad(Mesh mesh)
  {
	//PApplet.println("Parsing quad mesh.");
	faces = new ArrayList<SDSFace>(mesh.indices.length-2);
	verts = new ArrayList<SDSVertex>(mesh.indices.length);
	// convert the vertices in mesh.vertices to SDSVertex types
	for (int i = 0; i < mesh.vertices.length; i++)
	{
	  SDSVertex s = new SDSVertex(mesh.vertices[i]);
	  verts.add(s);
	}
	// now use mesh.indices to iterate through verts in chunks of four, 
	// building faces, registering faces and registering neighboring vertices
	for (int i = 0; i < mesh.indices.length; i+=4)
	{
	  SDSVertex[] v = new SDSVertex[4];
	  v[0] = getVertex(mesh.indices[i]);
	  v[1] = getVertex(mesh.indices[i+1]);
	  v[2] = getVertex(mesh.indices[i+2]);
	  v[3] = getVertex(mesh.indices[i+3]);
	  // create a face with the four verts, it will register itself with them
	  SDSFace face = new SDSFace(v);
	  faces.add(face);
	  // register neighboring vertices
	  for (int j = 0; j < v.length; j++) 
	  {
		// next and previous index, wraparound
		int n = (j+1)%v.length;
		int p = j-1;
		if ( p < 0 ) p = v.length-1;
		if ( v[j].verts.indexOf(v[n]) == -1 ) 
		{
		  v[j].verts.add(v[n]);
		  v[j].edgeMids.put(v[n], new Integer(-1));
		}
		if ( v[j].verts.indexOf(v[p]) == -1 ) 
		{
		  v[j].verts.add(v[p]);
		  v[j].edgeMids.put(v[p], new Integer(-1));
		}
	  }
	}
  }
  
  private void parseTri(Mesh mesh)
  {
	//PApplet.println("Parsing tri mesh.");
	faces = new ArrayList<SDSFace>(mesh.indices.length/3);
	verts = new ArrayList<SDSVertex>(mesh.vertices.length);
	// convert the vertices in mesh.vertices to SDSVertex types
	for (int i = 0; i < mesh.vertices.length; i++)
	{
	  verts.add(new SDSVertex(mesh.vertices[i]));
	}
	// now use mesh.indices to iterate through verts in chunks of three, 
	// building faces, registering faces and registering neighboring vertices
	for (int i = 0; i < mesh.indices.length; i+=3)
	{
	  SDSVertex[] v = new SDSVertex[3];
	  v[0] = getVertex(mesh.indices[i]);
	  v[1] = getVertex(mesh.indices[i+1]);
	  v[2] = getVertex(mesh.indices[i+2]);
	  // create a face with the four verts, it will register itself with them
	  SDSFace face = new SDSFace(v);
	  faces.add(face);
	  // register neighboring vertices
	  for (int j = 0; j < v.length; j++) 
	  {
		// next and previous index, wraparound
		int n = (j+1)%v.length;
		int p = j-1;
		if ( p < 0 ) p = v.length-1;
		if ( v[j].verts.indexOf(v[n]) == -1 ) 
		{
		  v[j].verts.add(v[n]);
		  v[j].edgeMids.put(v[n], new Integer(-1));
		}
		if ( v[j].verts.indexOf(v[p]) == -1 ) 
		{
		  v[j].verts.add(v[p]);
		  v[j].edgeMids.put(v[p], new Integer(-1));
		}
	  }
	}
  }
  
  public SDSVertex getVertex(int i)
  {
	return (SDSVertex)verts.get(i);
  }
}
