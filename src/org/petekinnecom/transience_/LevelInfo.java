package org.petekinnecom.transience_;

public class LevelInfo
{
	String name;
	boolean finished;
	
	public LevelInfo(String s, boolean b)
	{
		name = s;
		finished = b;
	}
	
	public LevelInfo(String s, int i)
	{
		name = s;
		finished = (i==1);
	}
}
