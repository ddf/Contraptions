package net.compartmental.contraptions;

import java.util.ArrayList;

import processing.core.PApplet;

//an animation stores a list of frames and the frame rate the animation should be played at.
public class Animation
{
  // all of the frames of this animation
  // private because no one should be able to muck with these directly
  private ArrayList<AnimationFrame> m_frames;
  // how many frames per second should this animation run at?
  private float m_fps;
  // how long is this animation, in seconds?
  private float m_animLength;
  // what is the name of this animation
  private String m_animName;
  
  Animation(String animName, float fps)
  {
    m_frames = new ArrayList<AnimationFrame>();
    m_fps = fps;
    m_animName = animName;
  }
  
  void addFrame(AnimationFrame animFrame)
  {
    m_frames.add(animFrame);
  }
  
  void calculateAnimationLength()
  {
    m_animLength = (float)m_frames.size() / m_fps;
  }
  
  public int frameCount()
  {
    return m_frames.size();
  }
  
  public float length()
  {
    return m_animLength;
  }
  
  public float fps()
  {
    return m_fps;
  }
  
  public String getName()
  {
	  return m_animName;
  }
  
  public int frameForTime(float time)
  {
    float frameFraction = PApplet.map(time, 0, m_animLength, 0, this.frameCount());
    // println("frameFraction is " + frameFraction);
    int frameNumber = PApplet.floor(frameFraction) % this.frameCount();
    // println("Drawing frame " + frameNumber);
    return frameNumber;
  }
  
  public void drawFrame(int frameNumber)
  {
    m_frames.get(frameNumber).draw();
  }
  
  // figures out which frame to draw based on the time passed in.
  public void drawFrame(float atTime)
  {
    drawFrame( frameForTime(atTime) );
  }
  
  public float frameWidth( int frameNumber )
  {
	  return m_frames.get(frameNumber).width();
  }
  
  public float frameHeight( int frameNumber )
  {
	  return m_frames.get(frameNumber).height();
  }
}
