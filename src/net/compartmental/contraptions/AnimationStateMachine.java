package net.compartmental.contraptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import processing.xml.XMLElement;

/**
 * A data-driven hierarchical state machine for AnimationSystem animations, modeled after Havok Behavior.
 * 
 * @author Damien Di Fede
 *
 */
public class AnimationStateMachine 
{
	private AnimationSystem mAnimSystem;
	private Behavior mRootBehavior;
	private HashMap<String,Event> mEvents;
	private ArrayList<EventListener> mEventListeners;
	
	// special state used in transitions to indicate
	// that you can transition from anything
	private State wildCard;
	
	/**
	 * 
	 * @param animSystem The AnimationSystem we use to create animation instances.
	 * @param data The XML we load from.
	 */
	public AnimationStateMachine( AnimationSystem animSystem, XMLElement data )
	{
		mAnimSystem = animSystem;
		mEvents = new HashMap<String,Event>();
		mEventListeners = new ArrayList<EventListener>();
		wildCard = new State( "*", null );
		
		XMLElement[] events = data.getChildren("event");
		for(int i = 0; i < events.length; ++i)
		{
			String eventName = events[i].getAttribute("name");
			Event e = new Event(eventName);
			mEvents.put(eventName, e);
		}
		
		XMLElement[] behaviors = data.getChildren("behavior");
		for(int i = 0; i < behaviors.length; ++i)
		{
			String name = behaviors[i].getAttribute("name");
			if ( name.equals("root") )
			{
				mRootBehavior = new Behavior(name, null);
				mRootBehavior.loadXML( behaviors[i] );
				break;
			}
		}
		
		if ( mRootBehavior == null )
		{
			throw new IllegalArgumentException("AnimationStateMachine could not find a root behavior!");
		}
		
		mRootBehavior.enter();
	}
	
	public void update( float dt )
	{
		mRootBehavior.update( dt );
	}
	
	public void draw()
	{
		mRootBehavior.draw();
	}
	
	public AnimationInstance currentAnimation()
	{
		return mRootBehavior.currentAnimation();
	}
	
	public void sendEvent( String eventName )
	{
		sendEvent( mEvents.get(eventName) );
	}
	
	private void sendEvent( Event event )
	{
		if ( mRootBehavior.handleEvent( event ) == false )
		{
			// System.out.println( "Animation event " + event.getName() + " was not handled!" );
		}
		
		for(int i = 0; i < mEventListeners.size(); ++i)
		{
			mEventListeners.get(i).eventSent(event);
		}
	}
	
	public String getCurrentStateName()
	{
		return mRootBehavior.getCurrentStateName();
	}
	
	public void addEventListener( EventListener listener )
	{
		mEventListeners.add(listener);
	}
	
	public void removeEventListener( EventListener listener )
	{
		mEventListeners.remove(listener);
	}
	
	public class Event
	{
		private String mName;
		
		public Event( String name )
		{
			mName = name;
		}
		
		public String getName()
		{
			return mName;
		}
	}
	
	public interface EventListener
	{
		void eventSent( Event e );
	}
	
	public class State
	{
		private String mName;
		private State mParent;
		
		public State( String name, State parent )
		{
			mName = name;
			mParent = parent;
		}
		
		public String getName() 
		{ 
			return mName; 
		}
		
		public State getParentState() 
		{ 
			return mParent; 
		}
		
		public boolean hasParentState()
		{
			return mParent != null;
		}

		public boolean handleEvent( Event event ) 
		{ 
			return false; 
		}
		
		public AnimationInstance currentAnimation()
		{
			return null;
		}
		
		public void enter() {}
		public void update( float dt ) {}
		public void draw() {}
		public void exit() {}
	}
	
	public class Clip extends State
	{
		private AnimationInstance mAnimation;
		private ArrayList<Trigger> mTriggers;
		private int mNextTrigger;

		public Clip(String name, State parent) 
		{
			super(name, parent);
			mTriggers = new ArrayList<Trigger>();
			mNextTrigger = 0;
		}
		
		public void loadXML( XMLElement data )
		{
			String animName = data.getAttribute("animation");
			mAnimation = mAnimSystem.createAnimationInstance(animName);
			
			XMLElement[] triggers = data.getChildren("trigger");
			for(int i = 0; i < triggers.length; ++i)
			{
				String eventName = triggers[i].getAttribute("event");
				float  time = triggers[i].getFloatAttribute("time");
				
				Event event = mEvents.get(eventName);
				
				if ( event != null )
				{
					mTriggers.add( new Trigger(event, time) );
				}
				else
				{
					System.out.println("Couldn't find the event " + eventName + " in trigger from " + getName() );
				}
			}
				
		}
		
		private void activateTrigger( Trigger trig )
		{
			sendEvent( trig.mEvent );
			++mNextTrigger;
			if ( mNextTrigger == mTriggers.size() )
			{
				mNextTrigger = 0;
			}
		}
		
		public AnimationInstance currentAnimation()
		{
			return mAnimation;
		}

		@Override
		public void update(float dt) 
		{
			float prevTime = mAnimation.getNormalizedCurrentTime();
			mAnimation.advance(dt);
			float newTime = mAnimation.getNormalizedCurrentTime();
			
			// see if a trigger occurred between prevTime and newTime
			while ( mNextTrigger < mTriggers.size() )
			{
				Trigger trig = mTriggers.get(mNextTrigger);
				int prevTrig = mNextTrigger;
				// trigger time is between our times
				if ( trig.mTime > prevTime && trig.mTime <= newTime )
				{
					activateTrigger( trig );
				}
				// we wrapped around and this trigger was after the previous time
				else if ( newTime < prevTime && trig.mTime > prevTime )
				{
					activateTrigger( trig );
				}
				// we wrapped around and this trigger is before the new time
				else if ( newTime < prevTime && trig.mTime <= newTime )
				{
					activateTrigger( trig );
				}
				else // we done
				{
					break;
				}
				
				if ( prevTrig == mNextTrigger )
				{
					break;
				}
			}
		}
		
		public void draw()
		{
			mAnimation.draw();
		}

		@Override
		public void enter() 
		{
			mAnimation.reset();
			mNextTrigger = 0;
		}

		@Override
		public void exit() 
		{
			
		}
		
		public class Trigger
		{
			public Event mEvent;
			public float mTime;
			
			public Trigger( Event event, float time )
			{
				mEvent = event;
				mTime = time;
			}
		}
	}
	
	public class Behavior extends State
	{
		private State mCurrentState;
		private State mDefaultState;
		
		private ArrayList<Transition> mTransitions;
		private ArrayList<State> mStates;
		
		public Behavior(String name, State parent) 
		{
			super(name, parent);
			mTransitions = new ArrayList<Transition>();
			mStates = new ArrayList<State>();
			mStates.add(wildCard);
		}
		
		public void loadXML( XMLElement data )
		{
			String defaultStateName = data.getAttribute("defaultState");
			
			XMLElement[] clips = data.getChildren("clip");
			System.out.println("Behavior " + getName() + " has " + clips.length + " child Clips.");
			for(int i = 0; i < clips.length; ++i)
			{
				Clip c = new Clip( clips[i].getAttribute("name"), this );
				c.loadXML( clips[i] );
				mStates.add(c);
				
				if ( c.getName().equals( defaultStateName ) )
				{
					mDefaultState = c;
				}
			}
			
			XMLElement[] behaviors = data.getChildren("behavior");
			System.out.println("Behavior " + getName() + " has " + behaviors.length + " child Behaviors.");
			for(int i = 0; i < behaviors.length; ++i)
			{
				Behavior b = new Behavior( behaviors[i].getAttribute("name"), this );
				b.loadXML( behaviors[i] );
				mStates.add(b);
				
				if ( b.getName().equals( defaultStateName ) )
				{
					mDefaultState = b;
				}
			}
			
			XMLElement[] transitions = data.getChildren("transition");
			System.out.println("Behavior " + getName() + " has " + transitions.length + " transitions.");
			for(int i = 0; i < transitions.length; ++i)
			{
				XMLElement elem = transitions[i];
				String fromName = elem.getAttribute("from");
				String toName = elem.getAttribute("to");
				String eventName = elem.getAttribute("event");
				
				System.out.println("Trying to create a transition from " + fromName + " to " + toName + " with event " + eventName);
				
				Event event = mEvents.get(eventName);
				
				if ( event == null )
				{
					System.out.println("The event " + eventName + "does not exist!");
				}
				else
				{
					System.out.println("Found event " + event.getName());
				}
				
				State fromState = null;
				State toState = null;
				
				Iterator<State> iter = mStates.iterator();
				while( iter.hasNext() )
				{
					State s = iter.next();
					
					if ( s.getName().equals(fromName) )
					{
						fromState = s;
					}
					
					if ( s.getName().equals(toName) )
					{
						toState = s;
					}
					
					if ( fromState != null && toState != null && event != null )
					{
						mTransitions.add( new Transition(fromState, toState, event) );
						break;
					}
				}
			}
		}
		
		public String getCurrentStateName()
		{
			return mCurrentState.getName();
		}
		
		public AnimationInstance currentAnimation()
		{
			return mCurrentState.currentAnimation();
		}
		
		public boolean handleEvent( Event event )
		{
			if ( mCurrentState.handleEvent(event) == false )
			{
				// see if we have any transitions we can perform
				Iterator<Transition> iter = mTransitions.iterator();
				while( iter.hasNext() )
				{
					Transition trans = iter.next();
					if ( (trans.mFrom == wildCard || trans.mFrom == mCurrentState) && trans.mEvent == event )
					{
						mCurrentState.exit();
						mCurrentState = trans.mTo;
						mCurrentState.enter();
						return true;
					}
				}
			}
			
			return false;
		}

		@Override
		public void enter() 
		{
			mCurrentState = mDefaultState;
			mDefaultState.enter();
		}

		@Override
		public void exit() 
		{
			mCurrentState.exit();
		}

		@Override
		public void update(float dt) 
		{
			mCurrentState.update(dt);
		}
		
		public void draw()
		{
			mCurrentState.draw();
		}
		
		public class Transition
		{
			State mFrom;
			State mTo;
			Event mEvent;
			
			public Transition( State from, State to, Event event )
			{
				mFrom = from;
				mTo = to;
				mEvent = event;
				
//				System.out.println( "Creating a transition from " + from.getName() + 
//									" to " + to.getName() + 
//									" with event " + event.getName() 
//								  );
			}
		}
		
	}
}
