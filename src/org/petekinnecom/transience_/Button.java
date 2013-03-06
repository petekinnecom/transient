package org.petekinnecom.transience_;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

public class Button 
{
	public static final String TAG = "Button";
	
	public static final int JUMP_BUTTON = 0;
	public static final int SWITCH_BUTTON = 1;
	
	/** editor buttons */
	public static final int LEFT_BUTTON = 0;
	public static final int RIGHT_BUTTON = 1;

	private RectF rect;
	private boolean on = false;
	private int colorOn, colorOff;
	
	public Button(int type)
	{
		switch (type)
		{
		case JUMP_BUTTON:
			colorOn = Color.DKGRAY;
			colorOff = Color.LTGRAY;
			break;
		case SWITCH_BUTTON:
			colorOn = Color.DKGRAY;
			colorOff = Color.LTGRAY;
			break;
		}
	}
	
	public void setDimensions(float x1, float y1, float x2, float y2)
	{
		
		rect = new RectF(x1, y1, x2, y2);
		Log.d(TAG, "setDimensions: "+rect.toString());
	}

	public int getColor()
	{
		if(on)
			return colorOn;
		return colorOff;
	}

	public void press()
	{
		on = true;
	}

	public void release()
	{
		on = false;
	}
	public boolean getPressed()
	{
		return on;
	}
	
	public RectF getRect()
	{
		return rect;
	}

	public boolean activates(int x, int y)
	{
		return (rect.contains(x, y) || rect.contains(x, y+rect.height()));
	}
}
