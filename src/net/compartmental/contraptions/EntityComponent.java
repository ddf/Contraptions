package net.compartmental.contraptions;

public abstract class EntityComponent
{
  ComponentManager m_manager;
  Entity m_entity;
  
  public void update() {}
  
  public Entity getEntity()
  {
    return m_entity;
  }
  
  public ComponentManager getManager()
  {
	  return m_manager;
  }
  
  protected float deltaTime()
  {
    return m_manager.deltaTime();
  }
}
