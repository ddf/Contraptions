package net.compartmental.contraptions;

public class RenderComponent extends EntityComponent
{
  public interface Renderer
  {
    void draw( Entity e );
  }
  
  private Renderer m_renderer;
  
  public RenderComponent( Renderer renderer )
  {
    m_renderer = renderer;
  }
  
  public static RenderComponent Get( Entity e )
  {
    return (RenderComponent)e.getComponentByClass( RenderComponent.class );
  }
  
  public void update()
  {
    m_renderer.draw( getEntity() );
  }
}
