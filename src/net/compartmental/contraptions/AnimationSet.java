package net.compartmental.contraptions;

import java.util.HashMap;

// a set of animation instances. you can tell it to play a particular animation.
public class AnimationSet 
{	
	private HashMap<String, AnimationInstance> mAnimations;
	private AnimationInstance mCurrentAnimation;
	private AnimationInstance mNextAnimation;
	
	public AnimationSet( AnimationSystem animSystem, String[] animsForSet )
	{
		mAnimations = new HashMap<String, AnimationInstance>();
		for(int i = 0; i < animsForSet.length; i++)
		{
			mAnimations.put( animsForSet[i], animSystem.createAnimationInstance(animsForSet[i]) );
		}
		
		mCurrentAnimation = null;
		mNextAnimation = null;
	}
	
	public void update( float dt )
	{
		if ( mCurrentAnimation != null )
		{
			if ( mCurrentAnimation.advance( dt ) && mNextAnimation != null )
			{
				mCurrentAnimation = mNextAnimation;
				mNextAnimation = null;
			}
		}
	}
	
	public void draw()
	{
		if ( mCurrentAnimation != null )
		{
			mCurrentAnimation.draw();
		}
	}
	
	public void setAnimation( String animName )
	{
		// don't set if we're playing this one already
		if ( currentAnimationName().equals(animName) )
		{
			return;
		}
		
		mCurrentAnimation = mAnimations.get( animName );
		if ( mCurrentAnimation != null )
		{
			mCurrentAnimation.reset();
		}
	}
	
	public void setAnimation( String animName, String nextAnim )
	{
		setAnimation( animName );
		
		mNextAnimation = mAnimations.get( nextAnim );
		if ( mNextAnimation != null )
		{
			mNextAnimation.reset();
		}
	}
	
	public AnimationInstance currentAnimation()
	{
		return mCurrentAnimation;
	}
	
	public String currentAnimationName()
	{
		if ( mCurrentAnimation != null )
		{
			return mCurrentAnimation.getAnimation().getName();
		}
		
		return "";
	}
}
