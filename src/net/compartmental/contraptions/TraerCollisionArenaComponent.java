package net.compartmental.contraptions;

import java.util.ArrayList;

import traer.physics.Vector3D;

// specify a rectangular arena, ensures that entities with collision components
// stay *inside* this rectangle.
public class TraerCollisionArenaComponent extends EntityComponent
{
  private Vector3D m_topLeft, m_bottomRight;
  
  public TraerCollisionArenaComponent(float topX, float topY, float bottomX, float bottomY)
  {
    m_topLeft = new Vector3D(topX, topY, 0.f);
    m_bottomRight = new Vector3D(bottomX, bottomY, 0.f);
  }
  
  public void update()
  {
    ArrayList<TraerCollisionSphereComponent> sphereComponents = getManager().getComponentsOfClass( TraerCollisionSphereComponent.class );
    for( TraerCollisionSphereComponent theirCollision : sphereComponents )
    {
      TraerParticleComponent theirParticleComp = theirCollision.getEntity().getComponentByClass( TraerParticleComponent.class );
      if ( theirParticleComp != null )
      {
        Vector3D position = theirParticleComp.Particle().position();
        Vector3D velocity = theirParticleComp.Particle().velocity();
        // too far left?
        if ( position.x() - m_topLeft.x() < theirCollision.Radius() )
        {
          position.setX( theirCollision.Radius() );
          velocity.setX( velocity.x() * -0.5f );
        }
        // too far up?
        if ( position.y() - m_topLeft.y() < theirCollision.Radius() )
        {
          position.setY( theirCollision.Radius() );
          velocity.setY( velocity.y() * -0.5f );
        }
        // too far right?
        if ( m_bottomRight.x() - position.x() < theirCollision.Radius() )
        {
          position.setX( m_bottomRight.x() - theirCollision.Radius() );
          velocity.setX( velocity.x() * -0.5f );
        }
        // too far down?
        if ( m_bottomRight.y() - position.y() < theirCollision.Radius() )
        {
          position.setY( m_bottomRight.y() - theirCollision.Radius() );
          velocity.setY( velocity.y() * -0.5f );
        }
      }
    }
  }
}
