package org.petekinnecom.transience_;

import android.graphics.Color;

public class Circle
{
	public float x, y, r;
	public int color = Color.argb(127, 0, 255, 0);
	
	public Circle(){}
	public Circle(float x, float y)
	{
		this.x = x;
		this.y = y;
		this.r = 0.2f;
	}
	public Circle(float x, float y, float r)
	{
		this(x, y);
		this.r = r;
	}
	public Circle(float x, float y, float r, int color)
	{
		this.x = x;
		this.y = y;
		this.r = r;
		this.color = Color.argb(127, Color.red(color), Color.green(color), Color.blue(color));
	}
	public void set(float x, float y, float r, int color)
	{
		this.x = x;
		this.y = y;
		this.r = r;
		this.color = color;
	}
}
