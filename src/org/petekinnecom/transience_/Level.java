package org.petekinnecom.transience_;

import android.content.ContentValues;
import android.util.Log;

public class Level
{
	private static final String TAG = "Level";

	/*
	 * Game data
	 */
	private int[][] grid;
	private int length, height;

	/*
	 * Info data
	 */

	// values: 0,1. Not boolean to simplify work with DB.
	private int finished;

	private String summary;
	private String name;

	public static boolean editorMode = false;

	public String getSummary()
	{
		return summary;
	}

	public Level()
	{

	}

	public Level(int[][] grid)
	{
		this.grid = grid;
		this.length = grid.length;
		this.height = grid[0].length;
	}

	/*
	 * This is the only constructor that should ever be called from the outside.
	 */
	public Level(int[][] grid, String name, String summary, int finished)
	{
		this(grid);
		this.name = name;
		this.summary = summary;
		this.finished = finished;
		if (summary == null)
			summary = "default text";
	}

	public Level(int i, int j)
	{
		length = i;
		height = j;
		grid = new int[length][height];

		for (i = 0; i < length; i++)
		{
			for (j = 0; j < height; j++)
			{
				grid[i][j] = C.SPACE;
				if (j == 0)
				{
					grid[i][j] = C.SOLID;
				}
			}
		}
	}

	/*
	 * Testing purposes only.
	 */
	public static Level makeTestLevel()
	{

		int[][] g = new int[30][10];
		// Test level
		for (int i = 0; i < g.length; i++)
		{
			for (int j = 0; j < g[0].length; j++)
			{
				g[i][j] = 0;
				if (j==0 && i%25>0)
					g[i][j] = C.SOLID;
				else
					g[i][j] = C.SPACE;
				if (j == 1 && (i+3) % 10 < 2)
				{
					g[i][j] = C.WORLD_B;
				}
				if (j == 2 && i % 2 == 0)
				{
					g[i][j] = C.SPACE;
				}

			}
		}
		return new Level(g);
		// int[][] set = {
		// {1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
		// {1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
		// {0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
		// {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		// {0, 0, 1, 1, 1, 0, 0, 0, 0, 0},
		// {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		// {0, 0, 1, 1, 1, 0, 0, 0, 0, 0},
		// {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		// {0, 0, 1, 1, 1, 0, 0, 0, 0, 0},
		// {1, 0, 1, 0, 1, 0, 1, 0, 1, 0}
		//
		// };
		// for(int i=0;i<1000;i++)
		// {
		// for(int j=0;j<height;j++)
		// {
		// grid[i][j] = set[j][i%10];
		// }
		// }
	}

	private int brickType;

	public boolean isSolid(int i, int j, int world)
	{

		brickType = getBrick(i, j);
		if (brickType == C.SOLID)
			return true;
		if (brickType == C.SPACE_END)
			return false;
		if (brickType == world)
			return true;
		return false;
	}


	public int getHeight()
	{
		return height;
	}

	/*
	 * This method will ensure that we don't have any index out of bounds
	 * exceptions and also tack on a runway at the beginning and end of the
	 * level.
	 * 
	 * Because the game starts with a negative tick, all of the initial i values
	 * are negative.
	 */
	public int getBrick(int i, int j)
	{

		/*
		 * Check for strange situations.
		 */
		if (j >= height)
		{
/*			if (C.DEBUG)
				Log.d(TAG, "getBrick: j:" + j + " but height: " + height);
			if (C.DEBUG)
				Log.d(TAG, "Array index out of bounds.  Returning SOLID");*/
			return C.SOLID;
		}
		else if(j<0)
		{
			/**
			 * Oh, this is so clever, isn't it?
			 * We want the bottom brick (j=0) to
			 * extend downward in a column.  So
			 * We just reference the above brick.
			 */
			return getBrick(i, j+1);
		}
		/*
		 * Check beginning/end of level.
		 */
		if (i < 0 || i >= length)
		{
			if (j == 0)
				return C.SOLID;
			return C.SPACE_END;
		}

		return grid[i][j];
	}

	public int getColor(int g_i, int g_j, boolean solid)
	{
		if (solid)
			return C.COLOR[getBrick(g_i, g_j)];
		else
			return C.COLORA[getBrick(g_i, g_j)];
	}
	/*
	 * Check the four sides of our square, returning true if there is a neighbor
	 * of similar type on that side. Check in this order:
	 * 
	 * top, bottom, left, right
	 * 
	 * Ignore SPACE
	 */
	boolean[] b = new boolean[] { false, false, false, false };

	public boolean[] getOutLineArray(int i, int j, int world)
	{

		if (getBrick(i, j) != world || world == C.SPACE_END)
		{
			b[0] = b[1] = b[2] = b[3] = false;
			return b;
		}

		b[0] = !(getBrick(i, j + 1) == world || getBrick(i, j + 1) == C.SOLID);
		b[1] = !(getBrick(i, j - 1) == world || getBrick(i, j - 1) == C.SOLID);
		b[2] = !(getBrick(i - 1, j) == world || getBrick(i - 1, j) == C.SOLID);
		b[3] = !(getBrick(i + 1, j) == world || getBrick(i + 1, j) == C.SOLID);

		return b;
	}

	public float getLength()
	{
		return length;
	}

	/**
	 * EDITOR FUNCTIONS
	 */
	
	
	public void cycleBrick(int i, int j)
	{
		if (i < 0 || i >= length || j < 0 || j >= height)
		{
			Log.d(TAG, "cycleBrick error, out of bounds: " + i + " , " + j);
			return;
		}

		/*
		 * FIX 6 to FOUR!
		 */
		grid[i][j] = (grid[i][j] + 1) % 4;

	}

	public int[] getStartSquare()
	{
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < height; j++)
			{
				if (grid[i][j] == C.START_SQUARE)
				{
					return new int[] { i, j };
				}
			}
		}
		return new int[] { 0, 1 };
	}

	public void deleteStartSquare()
	{
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < height; j++)
			{
				if (grid[i][j] == C.START_SQUARE)
				{
					grid[i][j] = C.SPACE;
				}
			}
		}
	}

	public void setStartSquare(int i, int j)
	{
		/** Error checking **/
		if(i<0 || i>=grid.length || j<1 || j>=grid[0].length)
			return;
		grid[i][j] = C.START_SQUARE;

	}

	public void increaseHeight()
	{
		int[][] g = new int[length][height + 1];
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < height + 1; j++)
			{
				if (j == height)
				{
					g[i][j] = C.SPACE;
				} else
				{
					g[i][j] = grid[i][j];
				}
			}
		}
		this.height++;
		this.grid = g;
	}

	public void decreaseHeight()
	{
		
		
		int[][] g = new int[length][height - 1];
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < height - 1; j++)
			{

				g[i][j] = grid[i][j];
			}
		}
		this.height--;
		this.grid = g;
	}


	/*
	 * Expand grid only if needed. In fact, just do a full copy to simplify
	 * things.
	 */
	public void setLength(int l)
	{
		/** Error check. */
		if(l<3)
			return;
		/** Should be good. */
		int[][] g = new int[l][height];
		for (int i = 0; i < l; i++)
		{
			for (int j = 0; j < height; j++)
			{
				if (i < length)
					g[i][j] = grid[i][j];
				else if (j == 0)
					g[i][j] = C.SOLID;
				else
					g[i][j] = C.SPACE;
			}
		}
		length = l;
		grid = g;
	}

	public void dbContentValues(ContentValues cv)
	{
		cv.put("name", name);
		cv.put("length", length);
		cv.put("height", height);
		cv.put("finished", finished);
		cv.put("summary", summary);
	}
	
	public String getName()
	{
		return name;
	}
}
