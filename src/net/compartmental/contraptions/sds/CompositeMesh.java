package net.compartmental.contraptions.sds;

import java.util.ArrayList;

import processing.core.PApplet;

public class CompositeMesh extends Mesh
{
  private ArrayList<Mesh> meshes;
  private ArrayList<SharedFaceList> sharedFaceLists;
  private ArrayList<FaceLink> faceLinks;
  public boolean drawSharedFaces;
  public boolean drawMixDown;
  
  public CompositeMesh(Mesh m)
  {
	super();
	type = QUAD;
	meshes = new ArrayList<Mesh>(1);
	meshes.add(m);
	sharedFaceLists = new ArrayList<SharedFaceList>(1);
	sharedFaceLists.add(new SharedFaceList());
	faceLinks = new ArrayList<FaceLink>(1);
	drawSharedFaces = true;
	drawMixDown = false;
  }
  
  public int numMesh()
  {
	  return meshes.size();
  }
  
  public Mesh getMesh(int i)
  {
    return (Mesh)meshes.get(i);
  }
  
  // find the closest mesh in meshes
  // find the closest faces of mesh and m
  // set up the face connection
  public boolean addMesh(Mesh m)
  {
	Mesh currMesh = (Mesh)meshes.get(0);
	int currInd = 0;
	float cDist = PApplet.dist(currMesh.center.x, currMesh.center.y, currMesh.center.z, 
			                   m.center.x, m.center.y, m.center.z);
	for (int i = 1; i < meshes.size(); i++) 
	{
	  Mesh nMesh = (Mesh)meshes.get(i);
	  float dist = PApplet.dist(m.center.x, m.center.y, m.center.z, 
			                    nMesh.center.x, nMesh.center.y, nMesh.center.z);
	  if ( dist < cDist )
	  {
		currInd = i;
		cDist = dist;
	  }
	}
	return addMesh(m, currInd);
  }
  
  public boolean addMesh(Mesh m, Mesh inMeshes)
  {
	int i = meshes.indexOf(inMeshes);
	if ( i != -1 ) return addMesh(m, i);
	return false;
  }

  // create a face link between m and meshes.get(cInd)
  // by finded the closest faces of the two meshes and linking them
  public boolean addMesh(Mesh m, int cInd) 
  {
	Mesh cMesh = (Mesh)meshes.get(cInd);
	FaceLink link = new FaceLink();
	link.mNum1 = cInd;
	link.fInd1 = 0;
	link.mNum2 = meshes.size();
	link.fInd2 = 0;
	float fDist = Float.MAX_VALUE;
	for(int i = 0; i < 24; i+=4)
	{
	  Vertex c1 = getCenter(cMesh, i);
	  for(int j = 0; j < 24; j+=4)
	  {
		Vertex c2 = getCenter(m, j);
		float dist = PApplet.dist(c1.p.x, c1.p.y, c1.p.z, c2.p.x, c2.p.y, c2.p.z);
		if ( dist < fDist )
		{
		  link.fInd1 = i;
		  link.fInd2 = j;
		  fDist = dist;
		}
	  }
	}
	SharedFaceList sfl = (SharedFaceList)sharedFaceLists.get(cInd);
	if ( sfl.contains(link.fInd1) )
    {
	  PApplet.println("That face is already shared.");
	  return false;
    }
	faceLinks.add(link);
	meshes.add(m);
	sfl.add(link.fInd1);
	sfl = new SharedFaceList();
	sfl.add(link.fInd2);
	sharedFaceLists.add(sfl);
	return true;
  }
  
  // builds a single quad mesh out of the component cubes
  // and puts the results in vertices[] and indices[]
  // so that SDSTopology can parse it for subdivision
  public void mixDown()
  {
    // first order of business is copy all the vertices in meshes
	// out to vertices[]
	vertices = new Vertex[meshes.size()*8];
	// there's no good way to know how many indices there will be
	// so we'll just append them one at a time.
	indices = new int[0];
	for(int i = 0; i < meshes.size(); i++)
	{
	  Mesh m = (Mesh)meshes.get(i);;
	  System.arraycopy(m.transVerts, 0, vertices, i*8, 8);
      // now remember, the numbers in m.indices can be used to point to 
	  // the correct vertex in vertices by multiplying i by 8 and 
	  // adding the number in m.indices. knowing this, we will add all of 
	  // the non-shared faces of m to the indices list
	  SharedFaceList sfl = (SharedFaceList)sharedFaceLists.get(i);
	  for(int j = 0; j < m.indices.length; j+=4)
	  {
	    if ( !sfl.contains(j) )
	    {
	    	int[] newInd = new int[] { i*8 + m.indices[j], i*8 + m.indices[j+1],
	    			                   i*8 + m.indices[j+2], i*8 + m.indices[j+3] };
	    	indices = PApplet.concat(indices, newInd);
	    }
	  }
	}
	// now to build face links, we use the same logic, again simply
	// mapping indices inside of meshes to the new location in the mixDown
	for(int i = 0; i < faceLinks.size(); i++)
	{
	  FaceLink link = (FaceLink)faceLinks.get(i);
	  Mesh m1 = (Mesh)meshes.get(link.mNum1);
	  int  f1 = link.fInd1;
	  Mesh m2 = (Mesh)meshes.get(link.mNum2);
	  int  f2 = link.fInd2;
	  int[] newInd = new int[] { link.mNum1*8 + m1.indices[f1],
					             link.mNum2*8 + m2.indices[f2],
					             link.mNum2*8 + m2.indices[f2+3],
					             link.mNum1*8 + m1.indices[f1+3],
					              
					             link.mNum1*8 + m1.indices[f1],
					             link.mNum1*8 + m1.indices[f1+1],
					             link.mNum2*8 + m2.indices[f2+1],
					             link.mNum2*8 + m2.indices[f2],
					              
					             link.mNum1*8 + m1.indices[f1+1],
					             link.mNum2*8 + m2.indices[f2+1],
					             link.mNum2*8 + m2.indices[f2+2],
					             link.mNum1*8 + m1.indices[f1+2],
					              
					             link.mNum1*8 + m1.indices[f1+3],
					             link.mNum1*8 + m1.indices[f1+2],
					             link.mNum2*8 + m2.indices[f2+2],
					             link.mNum2*8 + m2.indices[f2+3] };
	  
	  indices = PApplet.concat(indices, newInd);
	}	
  } 
  
  public void draw(PApplet p)
  {
	if ( drawMixDown ) 
	{
		super.draw(p);
		return;
	}
	for (int i = 0; i < meshes.size(); i++)
	{
	  Mesh m = (Mesh)meshes.get(i);
	  m.draw(p);
	}
	p.fill(255);
	if ( drawSharedFaces )
	{
	  p.beginShape(QUAD);
	  for (int i = 0; i < faceLinks.size(); i++)
	  {
		FaceLink link = (FaceLink)faceLinks.get(i);
		Mesh m1 = (Mesh)meshes.get(link.mNum1);
		int  f1 = link.fInd1;
		Mesh m2 = (Mesh)meshes.get(link.mNum2);
		int  f2 = link.fInd2;
		// build a vertex array of the four faces, so we can zip
		// through it for drawing
		Vertex[] faces = new Vertex[] { m1.transVerts[m1.indices[f1]],
				                        m2.transVerts[m2.indices[f2]],
				                        m2.transVerts[m2.indices[f2+3]],
				                        m1.transVerts[m1.indices[f1+3]],
				                        
				                        m1.transVerts[m1.indices[f1]],
				                        m1.transVerts[m1.indices[f1+1]],
				                        m2.transVerts[m2.indices[f2+1]],
				                        m2.transVerts[m2.indices[f2]],
				                        
				                        m1.transVerts[m1.indices[f1+1]],
				                        m2.transVerts[m2.indices[f2+1]],
				                        m2.transVerts[m2.indices[f2+2]],
				                        m1.transVerts[m1.indices[f1+2]],
				                        
				                        m1.transVerts[m1.indices[f1+3]],
				                        m1.transVerts[m1.indices[f1+2]],
				                        m2.transVerts[m2.indices[f2+2]],
				                        m2.transVerts[m2.indices[f2+3]] };		                        
  	    for (int j = 0; j < faces.length; j++) 
	    {
		    if ( tex != null) 
		    {
			    int uvi = j%4;
			    if ( uvi == 0 ) p.texture(tex);
			    Vertex v = faces[j];
			    p.vertex(v.p.x, v.p.y, v.p.z, uv[uvi*2], uv[(uvi*2)+1]);
		    }
		    else
		    {
		      Vertex v = faces[j];
		      p.fill(v.color);
		      p.normal(v.n.x, v.n.y, v.n.z);
		      p.vertex(v.p.x, v.p.y, v.p.z);
		    }
	    }
	  }
	  p.endShape();
	}
  }
  
  // return the center of the face at fInd in mesh
  private Vertex getCenter(Mesh mesh, int fInd)
  {
	SDSVertex c = new SDSVertex();
	for(int i = 0; i < 4; i++)
	{
	  Vertex v = mesh.transVerts[ mesh.indices[fInd+i] ];
	  c.add(v);
	}
	c.div(4);
	return new Vertex(c);
  }
  
  public class FaceLink
  {
	// the index of the first mesh in meshes
	int mNum1;
	// the index of the first vertex of the face in meshes[mNum1].indices
	int fInd1;
	// the index of the second mesh in meshes
	int mNum2;
	// the index of the first vertex of the face in meshes[mNum2].indices
	int fInd2;
  }
  
  public class SharedFaceList
  {
    private int[] fNum;
    
    SharedFaceList() { fNum = new int[0]; }
    
    public void add(int i)
    {
      fNum = PApplet.append(fNum, i);
    }
    
    public boolean contains(int n)
    {
      for(int i = 0; i < fNum.length; i++)
      {
    	  if ( fNum[i] == n ) return true;
      }
      return false;
    }
  }
}
