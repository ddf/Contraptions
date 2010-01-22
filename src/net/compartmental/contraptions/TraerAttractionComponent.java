package net.compartmental.contraptions;

import java.util.ArrayList;

import traer.physics.Attraction;
import traer.physics.Particle;
import traer.physics.ParticleSystem;

public class TraerAttractionComponent extends EntityComponent
{
  private ArrayList<Attraction> m_attractions;
  
  public TraerAttractionComponent()
  {
    m_attractions = new ArrayList<Attraction>();
  }
  
  public static TraerAttractionComponent Get( Entity e )
  {
    return (TraerAttractionComponent)e.getComponentByClass( TraerAttractionComponent.class );
  }
  
  public Attraction Attraction( int index )
  {
    return m_attractions.get(index);
  }
  
  public Attraction getAttractionTo( Entity e )
  {
    TraerParticleComponent particleComp = (TraerParticleComponent)e.getComponentByClass( TraerParticleComponent.class );
    if ( particleComp != null )
    {
      Particle particle = particleComp.Particle();
      for( Attraction a : m_attractions )
      {
        if ( a.getOneEnd() == particle || a.getTheOtherEnd() == particle )
        {
          return a;
        }          
      }
    }
    return null;
  }
  
  public void addAttraction( Attraction attraction )
  {
    m_attractions.add( attraction );
  }
  
  public Attraction makeAttraction( Entity e, ParticleSystem system, float strength, float minimumDistance )
  {
    TraerParticleComponent particleComponentThem = TraerParticleComponent.Get(e);
    
    if ( particleComponentThem == null )
    {
      System.out.print( e.toString() + " doesn't have a TraerParticleComponent!" );
      return null;
    }
    
    TraerParticleComponent particleComponentUs = TraerParticleComponent.Get( getEntity() );
    
    if ( particleComponentUs == null )
    {
      System.out.print( getEntity() + " doesn't have a TraerParticleComponent!" );
      return null;
    }
    
    Attraction attraction = system.makeAttraction( particleComponentThem.Particle(), 
                                                   particleComponentUs.Particle(), 
                                                   strength, 
                                                   minimumDistance
                                                 );
    addAttraction( attraction );
    // see if the other entity has an attraction component and add it to theirs?
    
    return attraction;
  }
}
