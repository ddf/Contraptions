package net.compartmental.contraptions.sds;

import processing.core.PApplet;

public class CatmullClark implements SDSTechnique 
{
	public Mesh subdivide(Mesh m)
	{
	  SDSTopology t = new SDSTopology(m);
	  Mesh sub = subdivide(t);
	  if ( m.isTextured() ) sub.setTexture(m.getTexture());
	  return sub;
	}
	
	public Mesh subdivide(Mesh m, int n)
	{
	  Mesh sub = m;
	  for (int i = 0; i < n; i++)
	  {
		  SDSTopology t = new SDSTopology(sub);
		  sub = subdivide(t);
	  }
	  if ( m.isTextured() ) sub.setTexture(m.getTexture());
	  return sub;
	}
	
	public Mesh subdivide(SDSTopology top) 
	{
	  //PApplet.println("Number of faces = " + top.faces.size());
	  //PApplet.println("Number of verts = " + top.verts.size());
	  int numVerts = 0;
	  if ( top.meshType == Mesh.QUAD )
	  {
	    numVerts = top.faces.size()*4 + 2;
	  }
	  else if ( top.meshType == Mesh.TRI )
	  {
		// verts plus faces plus edges
		numVerts = top.verts.size() + 
		           top.faces.size() + 
		          (top.faces.size() + top.verts.size() - 2);
	  }
	  // new vertex buffer
	  Vertex[] verts = new Vertex[numVerts];
	  int      vInd  = 0;
	  // new index buffer
	  int numInd = 0;
	  if ( top.meshType == Mesh.QUAD )
	  {
	    numInd = top.faces.size()*4*4;
	  }
	  else if ( top.meshType == Mesh.TRI )
	  {
		numInd = (numVerts-2)*4;
	  }
	  int ind[] = new int[numInd];
	  // for each face, subdivide the face, add the new 
	  // vertices to verts, building a new index buffer as we go
	  for (int i = 0; i < top.faces.size(); i++)
	  {
		 SDSFace currFace = (SDSFace)top.faces.get(i);
		 // here's the easy one
		 Vertex center = currFace.center;
		 int cVert;
		 if ( currFace.cInd == -1 )
		 {
			 cVert = vInd;
			 currFace.cInd = cVert;
			 verts[vInd++] = center;
		 }
		 else
		 {
			 cVert = currFace.cInd;
		 }
		 // the index of the repositioned vertices
		 int nVerts[] = new int[currFace.v.length];
		 // the index of the new edge points
		 int eVerts[] = new int[currFace.v.length];
		 
		 for (int j = 0; j < currFace.v.length; j++) 
		 {
		   SDSVertex v = currFace.v[j];
		   if ( v.nInd == -1 )
		   {
		     // calculate new position for v
		     SDSVertex Q = new SDSVertex();
		     int n = v.faces.size();
		     for (int k = 0; k < n; k++) 
		     {
		       SDSFace aFace = (SDSFace)v.faces.get(k);
		       Vertex c = aFace.center;
		       Q.add(c);
		     }
		     Q.div(n);
		   
		     SDSVertex R = new SDSVertex();
		     for (int k = 0; k < v.verts.size(); k++) 
		     {
		  	   Vertex a = (Vertex)v.verts.get(k);
			   R.p.x += (v.p.x + a.p.x)/2;
			   R.p.y += (v.p.y + a.p.y)/2;
			   R.p.z += (v.p.z + a.p.z)/2;
		     }
		     R.div(v.verts.size());
		     R.mul(2);
		   
		     SDSVertex N = new SDSVertex(v);
		     N.mul(n - 3).add(R).add(Q).div(n);
		     nVerts[j] = vInd;
		     v.nInd = vInd;
		     Vertex p = new Vertex(N);
		     p.setNormFromPos();
		     verts[vInd++] = p;
		   }
		   else
		   {
			 nVerts[j] = v.nInd;
		   }
		   
		   // calculate the new edge point between v[j] and v[j+1]
		   int nextVert = (j+1)%currFace.v.length;
		   SDSVertex adj = currFace.v[nextVert];
		   Integer mInd = (Integer)v.edgeMids.get(adj);
		   if ( mInd.intValue() == -1 )
		   {
		     SDSVertex edgePoint = new SDSVertex();
		     // calc the midpoint of the edge
		     edgePoint.add(v).add(adj).div(2);
		     SDSFace adjFace = getAdjFace(currFace, v, adj);
		     if ( adjFace == null )
			     throw new RuntimeException("Couldn't find adjacent face in CatmullClark.");
		     Vertex adjCenter = adjFace.center;
		     float midX = (adjCenter.p.x + center.p.x)/2;
		     float midY = (adjCenter.p.y + center.p.y)/2;
		     float midZ = (adjCenter.p.z + center.p.z)/2;
		     Vertex centerMid = new Vertex(midX, midY, midZ);
		     edgePoint.add(centerMid);
		     edgePoint.div(2);
		     eVerts[j] = vInd;
		     v.edgeMids.put(adj, new Integer(vInd));
		     adj.edgeMids.put(v, new Integer(vInd));
		     Vertex ep = new Vertex(edgePoint);
		     ep.color = PApplet.lerpColor(v.color, adj.color, 0.5f, PApplet.RGB);
		     ep.setNormFromPos();
		     verts[vInd++] = ep;
		   }
		   else
		   {
			 eVerts[j] = mInd.intValue();
		   }
		 }
		 // adding indexes to ind[]
		 // writing in chunks of 4 x nVerts.length
		 for (int j = i*4*nVerts.length, k = 0; k < nVerts.length; j+=4, k++) 
		 {
		   int p = (k-1);
		   if ( p < 0 ) p = nVerts.length-1;
		   ind[j] = nVerts[k];
		   ind[j+1] = eVerts[k];
		   ind[j+2] = cVert;
		   ind[j+3] = eVerts[p];
		 }
	  }
	  return new Mesh(verts, ind, Mesh.QUAD);
	}
	
	// return the face that shares the edge v0-v1 with currFace
	private SDSFace getAdjFace(SDSFace currFace, SDSVertex v0, SDSVertex v1)
	{
	  PApplet.println("Searching v0 faces. There are: " + v0.faces.size());
      for (int i = 0; i < v0.faces.size(); i++)
      {
    	SDSFace face = (SDSFace)v0.faces.get(i);
    	// skip the face we know about
    	if ( face == currFace ) continue;
    	PApplet.println("Searching face verts. There are: " + face.v.length);
    	for (int j = 0; j < face.v.length; j++) 
    	{
		  if ( face.v[j] == v1 ) return face;	
		}
      }
	  return null;
	}
}
