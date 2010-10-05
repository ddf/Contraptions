package net.compartmental.contraptions.sds;

import processing.core.PApplet;

public class MeshMaker 
{
  private MeshMaker() {}
  
  public static Mesh makeCube(float dim)
  {
	  return makeCube(dim, -1);
  }
  
  public static Mesh makeCube(float dim, int color)
  {
	Vertex[] verts = new Vertex[] { new Vertex(-1, 1, 1, color),
                                    new Vertex(1, 1, 1, color),
                                    new Vertex(1, -1, 1, color), 
                                    new Vertex(-1, -1, 1, color),  
                                    new Vertex(-1, 1, -1, color), 
                                    new Vertex(1, 1, -1, color),  
                                    new Vertex(1, -1, -1, color),  
                                    new Vertex(-1, -1, -1, color) }; 
	int[] inds = new int[] { 0, 1, 2, 3,
							 4, 5, 6, 7,
							 1, 5, 6, 2,
							 0, 4, 7, 3,
							 4, 5, 1, 0,
							 7, 6, 2, 3 };
	Mesh m = new Mesh(verts, inds, Mesh.QUAD);
	m.scale(dim);
	return m;
  }
  
  // this function has been swiped from the PApplet source
  // since they couldn't be bothered to make a static version
  private static int color(int x, int y, int z)
  {
    if (x > 255) x = 255; else if (x < 0) x = 0;
    if (y > 255) y = 255; else if (y < 0) y = 0;
    if (z > 255) z = 255; else if (z < 0) z = 0;
    return 0xff000000 | (x << 16) | (y << 8) | z;
  }

  public static Mesh makeColorCube(float dim)
  {
	Vertex[] verts = new Vertex[] { new Vertex(-1, 1, 1, color(0, 255, 255)),
                                    new Vertex(1, 1, 1, color(255, 255, 255)),
                                    new Vertex(1, -1, 1, color(255, 0, 255)), 
                                    new Vertex(-1, -1, 1, color(0, 0, 255)),  
                                    new Vertex(-1, 1, -1, color(0, 255, 0)), 
                                    new Vertex(1, 1, -1, color(255, 255, 0)),  
                                    new Vertex(1, -1, -1, color(255, 0, 0)),  
                                    new Vertex(-1, -1, -1, color(0, 0, 0)) }; 
	int[] inds = new int[] { 0, 1, 2, 3,
							 4, 5, 6, 7,
							 1, 5, 6, 2,
							 0, 4, 7, 3,
							 4, 5, 1, 0,
							 7, 6, 2, 3 };
	Mesh m = new Mesh(verts, inds, Mesh.QUAD);
	m.scale(dim);
	return m;
  }
  
  public static Mesh joinCubes(Mesh one, Mesh two)
  {
	if ( !(one.vertices.length == 8 && one.indices.length == 24)
	     || !(two.vertices.length == 8 && two.indices.length == 24) )
	{
	  throw new IllegalArgumentException("Mesh.joinCubes: both arguments must be cubes.");
	}
	SDSTopology oneTop = new SDSTopology(one);
	SDSTopology twoTop = new SDSTopology(two);
	int bestOne = 0;
	int bestTwo = 0;
	float bestDist = Float.MAX_VALUE;
	// compare distances of the centers of faces to find the closest faces
	for(int i = 0; i < oneTop.faces.size(); i++)
	{
	  SDSFace oneFace = (SDSFace)oneTop.faces.get(i);
	  Vertex c1 = oneFace.center;
	  for(int j = 0; j < twoTop.faces.size(); j++)
	  {
		SDSFace twoFace = (SDSFace)twoTop.faces.get(j);
		Vertex c2 = twoFace.center;
		float dist = PApplet.dist(c1.p.x, c1.p.y, c1.p.z, c2.p.x, c2.p.y, c2.p.z);
		if ( dist < bestDist )
		{
			bestOne = i*4;
			bestTwo = j*4;
			bestDist = dist;
		}
	  }
	}
	PApplet.println("Best face one starts at " + bestOne);
	PApplet.println("Best face two starts at " + bestTwo);
	// build a new mesh by connecting the two closest faces
	// note that bestOne gives the index in one.indices that
	// points to the first vertex of the face in one.vertices
	Vertex[] newVerts = new Vertex[one.vertices.length + two.vertices.length];
	System.arraycopy(one.vertices, 0, newVerts, 0, one.vertices.length);
	System.arraycopy(two.vertices, 0, newVerts, one.vertices.length, two.vertices.length);
	// new indices, there are 14 faces on the stitched mesh, times 4 is 56 indicies
	int[] newInds = new int[56];
	for(int i = 0, j = 0, n = 0; i < one.indices.length; i++, j++, n++)
	{
      if ( bestOne == i ) i+=4;
      if ( bestTwo == j ) j+=4;
      if ( i < one.indices.length) newInds[n] = one.indices[i];
      if ( j < two.indices.length) newInds[n+20] = two.indices[j] + one.vertices.length;
	}
	int[] newFace1 = new int[] { one.indices[bestOne], 
			                     two.indices[bestTwo] + one.vertices.length,
			                     two.indices[bestTwo+3] + one.vertices.length, 
			                     one.indices[bestOne+3] };
	int[] newFace2 = new int[] { one.indices[bestOne+1],
			                     two.indices[bestTwo+1] + one.vertices.length,
			                     two.indices[bestTwo] + one.vertices.length,
			                     one.indices[bestOne] };
	int[] newFace3 = new int[] { one.indices[bestOne+1],
			                     two.indices[bestTwo+1] + one.vertices.length,
			                     two.indices[bestTwo+2] + one.vertices.length,
			                     one.indices[bestOne+2] };
	int[] newFace4 = new int[] { one.indices[bestOne+2],
			                     two.indices[bestTwo+2] + one.vertices.length,
			                     two.indices[bestTwo+3] + one.vertices.length,
			                     one.indices[bestOne+3] };

    System.arraycopy(newFace1, 0, newInds, 40, 4);
    System.arraycopy(newFace2, 0, newInds, 44, 4);
    System.arraycopy(newFace3, 0, newInds, 48, 4);
    System.arraycopy(newFace4, 0, newInds, 52, 4);
	return new Mesh(newVerts, newInds, Mesh.QUAD);
  }
}
