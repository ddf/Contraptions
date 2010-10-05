package net.compartmental.contraptions.sds;

public interface SDSTechnique 
{
  public Mesh subdivide(Mesh m);
  public Mesh subdivide(Mesh m, int n);
  public Mesh subdivide(SDSTopology t);
}
