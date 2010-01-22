package net.compartmental.contraptions;


/**
 * An Entity is simply a list of Components. It provides you an interface for getting at 
 * the individual Components it contains and also generically setting Properties that 
 * those Components might have.
 * 
 * @author ddf
 *
 */
public class Entity
{
  ComponentManager m_manager;
  
  public EntityComponent getComponentByClass( Class componentClass )
  {
    return m_manager.getComponentByClass(this, componentClass);
  }
} 
