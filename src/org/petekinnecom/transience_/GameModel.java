package org.petekinnecom.transience_;

import org.petekinnecom.transience_.GameController.GameThread;

import android.util.Log;

public class GameModel
{
	private static final String TAG = "GameModel";

	private Level level;
	private Ball ball;
	private float totalTick;

	public GameModel(Level level)
	{
		if (level == null)
			if(C.DEBUG)
				Log.d(TAG, "null level data.");
		this.level = level;
		ball = new Ball(level.getStartSquare());
		totalTick = C.TICK_OFFSET + level.getStartSquare()[0];
		if(C.DEBUG) Log.d(TAG, "constructed ball");
	}

	public int update(float deltaTick)
	{
		ball.update(deltaTick, level);
		
		/**
		 * Now we check to see what state we are in
		 * and we report back to the thread what to
		 * do.
		 */
		totalTick += deltaTick;
		if(ball.y < -3 || ball.x<totalTick)
			return GameThread.STATE_LOSE;
		if(ball.x > level.getLength() + 5)
			return GameThread.STATE_WIN;
		return GameThread.STATE_RUNNING;
	}

	public int getGridHeight()
	{
		return level.getHeight();
	}

	public void jumpBall()
	{
		ball.jump(level);
	}

	public Ball getBall()
	{
		return ball;
	}

	public void doSwitch(float deltaTick)
	{
		ball.doSwitch(deltaTick);
	}

	public void undoSwitch(float deltaTick)
	{
		ball.undoSwitch(deltaTick);
	}

	// used for rendering
	public boolean isSolid(int brickType)
	{
		return ball.isSolid(brickType);
	}

	public Level getLevel()
	{
		return level;
	}

	public boolean isSolid(int i, int j)
	{
		return level.isSolid(i, j, ball.getWorld());
	}

	public int getColor(int i, int j)
	{
		if (level.isSolid(i, j, ball.getWorld()))
			return level.getColor(i, j, true);
		else
			return level.getColor(i, j, false);
	}

	public boolean isWon()
	{
		return (ball.x > level.getLength());
	}

	/**
	 * TODO: This seems like it should 
	 * not exist.
	 */
	public int setStartingCoords()
	{
		int[] coords = level.getStartSquare();

		ball.x = coords[0];
		ball.y = coords[1] + ball.r;
		Log.d(TAG, "set ball coords: " + coords[0] + ", " + coords[1]);
		return coords[0] - 7;
	}

	public boolean[] getOutLineArray(int i, int j)
	{
		return level.getOutLineArray(i, j, ball.getWorld());
	}

}
