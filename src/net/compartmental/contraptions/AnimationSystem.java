package net.compartmental.contraptions;

import java.util.HashMap;

import processing.core.PApplet;
import processing.xml.XMLElement;

public class AnimationSystem 
{
	private PApplet mApp;	
	
	// all the defined animations, which we can find by name.
	private HashMap<String, Animation> mAnimations;
	// all of the animation frames we've encountered
	// so that we don't load a graphic twice.
	private HashMap<String, AnimationFrame> mFrames;
	
	public AnimationSystem( PApplet app )
	{
		mApp = app;
		mAnimations = new HashMap<String, Animation>();
		mFrames = new HashMap<String, AnimationFrame>();
	}

	// loads all the animations defined in animations.xml into Animation objects
	public void loadAnimations( String animationDefinitionFile )
	{
	  XMLElement anims = new XMLElement(mApp, animationDefinitionFile);
	  String baseDir = anims.getAttribute("baseDirectory");
	  int numAnims = anims.getChildCount();
	  for(int i = 0; i < numAnims; i++)
	  {
	    XMLElement animXML = anims.getChild(i);
	    //println(animXML.getName());
	    if ( animXML.getName().equals("animation") )
	    {
	      float fps = animXML.getFloatAttribute("fps");
	      String name = animXML.getStringAttribute("name");
	      PApplet.println("Found animation " + name);
	      Animation anim = new Animation(name, fps);
	      int numFrames = animXML.getChildCount();
	      for(int f = 0; f < numFrames; f++)
	      {
	        XMLElement frameXML = animXML.getChild(f);
	        if ( frameXML.getName().equals("frame") )
	        {
	          String frameFileName = frameXML.getContent();
	          AnimationFrame frame = loadAnimationFrame( baseDir + "/" + frameFileName);
	          anim.addFrame(frame);
	        }
	      }
	      anim.calculateAnimationLength();
	      mAnimations.put(name, anim);
	    }
	  }
	}
	
	// looks for the file in our list of already loaded frames.
	// returns that if it finds it, otherwise pulls from disk.
	private AnimationFrame loadAnimationFrame(String frameFileName)
	{
	  if ( mFrames.containsKey(frameFileName) == false )
	  {
	    PApplet.println("--- loading frame " + frameFileName + " from disk");
	    AnimationFrame frame = new AnimationFrame( mApp, frameFileName );
	    mFrames.put(frameFileName, frame);
	    return frame;
	  }
	  PApplet.println("--- returning frame " + frameFileName + " from the cache");
	  return mFrames.get(frameFileName);
	}

	// helper function to get an instance of an animation
	public AnimationInstance createAnimationInstance(String animName)
	{
	  Animation anim = mAnimations.get(animName);
	  
	  if ( anim == null )
	  {
		  throw new IllegalArgumentException("Could not find an animation named " + animName + "!");
	  }
	  
	  return new AnimationInstance(anim);
	}
}
