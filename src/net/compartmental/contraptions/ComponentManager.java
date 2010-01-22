package net.compartmental.contraptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ComponentManager
{
  private HashMap<Class, ArrayList<EntityComponent>> m_componentLists;
  private long m_lastUpdate;
  private float m_dt;
  
  public ComponentManager()
  {
    m_componentLists = new HashMap<Class, ArrayList<EntityComponent>>();
    m_lastUpdate = System.currentTimeMillis();
    m_dt = 0.f;
  }
  
  public void update()
  {
    long currTime = System.currentTimeMillis();
    long longDT = currTime - m_lastUpdate;
    m_dt = (float)longDT / 1000.0f;
    m_lastUpdate = currTime;
    
    Collection<ArrayList<EntityComponent>> componentLists = m_componentLists.values();    
    for( ArrayList<EntityComponent>list : componentLists )
    {
      for( EntityComponent component : list )
      {
        component.update();
      }
    }
  }
  
  public void addComponent( Entity theEntity, EntityComponent theComponent )
  {
    ArrayList<EntityComponent> componentList = m_componentLists.get( theComponent.getClass() );
    if ( componentList == null )
    {
      componentList = new ArrayList<EntityComponent>();
      m_componentLists.put( theComponent.getClass(), componentList );
    }
    componentList.add( theComponent );
    theComponent.m_entity = theEntity;
    theComponent.m_manager = this;
    theEntity.m_manager = this;
  }
  
  public EntityComponent getComponentByClass( Entity theEntity, Class componentClass )
  {
    ArrayList<EntityComponent> componentList = m_componentLists.get( componentClass );
    if ( componentList != null )
    {
      for( EntityComponent c : componentList )
      {
        if ( c.m_entity == theEntity )
        {
          return c; 
        }
      }
    }
    
    return null;
  }
  
  public ArrayList<EntityComponent> getComponentsOfClass( Class componentClass )
  {
	  return m_componentLists.get( componentClass );
  }
  
  float deltaTime()
  {
    return m_dt;
  }
}
