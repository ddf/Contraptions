package net.compartmental.contraptions;

import processing.core.PVector;
import traer.physics.Particle;

public class TraerParticleComponent extends EntityComponent
{
  private Particle m_particle;
  
  public TraerParticleComponent( Particle particle )
  {
    m_particle = particle;
  }
  
  public static TraerParticleComponent Get( Entity e )
  {
    return e.getComponentByClass( TraerParticleComponent.class );
  }
  
  public Particle Particle()
  {
    return m_particle;
  }
  
  public void update()
  {
    // sync our entity's transform with the position of the particle
    EntityComponent comp = getEntity().getComponentByClass( PTransformComponent.class );
    if ( comp != null )
    {
      PTransformComponent transComp = (PTransformComponent)comp;
      PVector newPosition = new PVector( m_particle.position().x(), m_particle.position().y(), m_particle.position().z() );
      transComp.Position().set( newPosition );
    }
  }
}
