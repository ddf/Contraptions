package net.compartmental.contraptions;

//an AnimationInstance will point to a particular animation,
//but store its own state about what frame the animation should be drawing
public class AnimationInstance
{
	private Animation m_animation;
	private float m_currentTime;
	private float m_timeScale;

	AnimationInstance(Animation anim)
	{
		m_animation = anim;
		m_currentTime = 0;
		m_timeScale = 1;
	}
	
	public Animation getAnimation()
	{
		return m_animation;
	}

	// resets the animation to the first frame
	public void reset()
	{
		m_currentTime = 0;
	}

	public int currentFrame()
	{
		return m_animation.frameForTime(m_currentTime);
	}

	public void setTimeScale(float timeScale)
	{
		m_timeScale = timeScale;
	}

	// advance the animation by dt seconds
	// returns true if this advance caused the anim to loop
	public boolean advance(float dt)
	{
		// advance our time.
		m_currentTime += dt*m_timeScale;
		if ( m_currentTime > m_animation.length() )
		{
			m_currentTime %= m_animation.length();  
			return true;
		}
		return false;
	}
	
	public float getCurrentTime()
	{
		return m_currentTime;
	}
	
	public float getNormalizedCurrentTime()
	{
		return m_currentTime / m_animation.length();
	}

	// display the animation frame
	public void draw()
	{
		m_animation.drawFrame(m_currentTime);
	}
	
	public float width()
	{
		return m_animation.frameWidth( currentFrame() );
	}
	
	public float height()
	{
		return m_animation.frameHeight( currentFrame() );
	}
}
