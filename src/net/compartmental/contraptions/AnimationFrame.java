package net.compartmental.contraptions;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class AnimationFrame 
{
	private PApplet mApp;
	private FrameData mFrame;
	
	private interface FrameData
	{
		void draw();
		float height();
		float width();
	}
	
	private class PImageFrameData implements FrameData
	{
		private PImage mImage;
		
		public PImageFrameData( PImage image )
		{
			mImage = image;
		}
		
		public void draw()
		{
			mApp.image(mImage, 0, 0);
		}
		
		public float height()
		{
			return mImage.height;
		}
		
		public float width()
		{
			return mImage.width;
		}
	}
	
	private class PShapeFrameData implements FrameData
	{
		private PShape mShape;
		
		public PShapeFrameData( PShape shape )
		{
			mShape = shape;
		}
		
		public void draw()
		{
			mApp.shape(mShape);
		}
		
		public float height()
		{
			return mShape.height;
		}
		
		public float width()
		{
			return mShape.width;
		}
	}
	
	public AnimationFrame( PApplet app, String fileName )
	{
		mApp = app;

		if ( fileName.endsWith(".svg") )
		{
			mFrame = new PShapeFrameData( app.loadShape(fileName) );
		}
		else
		{
			mFrame = new PImageFrameData( app.loadImage(fileName) );
		}
	}

	public void draw()
	{
		mFrame.draw();
	}
	
	public float height()
	{
		return mFrame.height();
	}
	
	public float width()
	{
		return mFrame.width();
	}
}
