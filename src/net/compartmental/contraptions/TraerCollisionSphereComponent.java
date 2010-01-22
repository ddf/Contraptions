package net.compartmental.contraptions;

import java.util.ArrayList;

import traer.physics.Vector3D;

public class TraerCollisionSphereComponent extends EntityComponent 
{
	private float m_radius;
	
	public TraerCollisionSphereComponent(float radius)
	{
		setRadius(radius);
	}
	
	public float Radius()
	{
		return m_radius;
	}
	
	public void setRadius(float radius)
	{
		m_radius = radius;
	}
	
	public void update()
	{
		// do collision restitution with all the other collision spheres in the system
		// should we really do this with forces?
		ArrayList<EntityComponent> tcsComponents = getManager().getComponentsOfClass( this.getClass() );
		for( EntityComponent comp : tcsComponents )
		{
			if ( comp != this )
			{
				TraerCollisionSphereComponent theirCollision = (TraerCollisionSphereComponent)comp;
				TraerParticleComponent ourParticle = TraerParticleComponent.Get( getEntity() );
				TraerParticleComponent theirParticle = TraerParticleComponent.Get( theirCollision.getEntity() );
				// unlikely that they'll have a collision component without a particle
				// but we'll check anyhow. we can be assured of this if we get component
				// dependencies going on.
				if ( ourParticle != null && theirParticle != null && theirParticle.Particle().isFree() )
				{
					Vector3D ourPosition = ourParticle.Particle().position();
					Vector3D theirPosition = theirParticle.Particle().position();
					float minDist = Radius() + theirCollision.Radius();
					// are we too close?
					if ( ourPosition.distanceSquaredTo(theirPosition) < minDist*minDist )
					{
						// ok, we are. give the other guy a little bump in the direction
						// we are headed, plus a little extra along the direction vector
						// from me to him
						Vector3D bump = new Vector3D( ourParticle.Particle().velocity() );
						bump.multiplyBy(0.1f);
						theirParticle.Particle().velocity().add( bump );
						
						bump.set( theirParticle.Particle().position() );
						bump.subtract( ourParticle.Particle().position() );
						// hell of a way to normalize and set length
						// TODO: the "get out of me" bump strength should prob 
						// be proportional to how overlapped they are.
						bump.multiplyBy( 1.f / bump.length() );
						bump.multiplyBy( 0.2f );
						theirParticle.Particle().velocity().add( bump );
					}
				}
			}
		}
	}
	
}
