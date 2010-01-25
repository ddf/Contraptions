package net.compartmental.contraptions;

import processing.core.PVector;

public class PTransformComponent extends EntityComponent
{
  private PVector m_position;
  private PVector m_rotation;
  private PVector m_scale;
  
  public PTransformComponent()
  {
    m_position = new PVector();
    m_rotation = new PVector();
    m_scale = new PVector();
  }
  
  public static PTransformComponent Get( Entity e )
  {
    return e.getComponentByClass( PTransformComponent.class );
  }
 
  public PVector Position()
  {
    return m_position;
  }
 
  public PVector Rotation()
  {
    return m_rotation;
  }
  
  public PVector Scale()
  {
    return m_scale;
  }
}
