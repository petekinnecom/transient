package org.petekinnecom.transience_;

import android.util.Log;

public class Ball
{
	private static final String TAG = "Ball";
	
	private static final float GRAVITY_CONSTANT = 3f;
	private static final float JUMP_FACTOR = 3.5f;

	/*
	 * These are kept in relation to the grid, and have nothing to do with
	 * BRICK_SIZE found in gameSurface.
	 */
	public float v_x, v_y;

	private static float STARTING_X = 7f;
	private static float STARTING_RADIUS = 0.4f;

	private int world = C.WORLD_A;
	private int switchingToWorld = C.WORLD_A;

	public float x, y, r;

	Ball(int[] coords)
	{
		r = STARTING_RADIUS;

		v_x = 1;
		v_y = 0;
		x = coords[0] + r;
		y = coords[1] + r;
		if (C.DEBUG)
			Log.d(TAG, "ball placed: " + x + ", " + y);
	}

	/** rightmost point of ball **/
	private int r_x, r_y;
	private boolean r_collide;

	/** grace point */
	private int b_g_x, b_g_y;
	private boolean b_g_collide;

	private int t_g_x, t_g_y;
	private boolean t_g_collide;
	
	/** bottom point of ball **/
	private int b_x, b_y;
	private boolean b_collide;

	/** top point of ball **/
	private int t_x, t_y;
	private boolean t_collide;

	/** future points **/
	private float next_x, next_y;

	public synchronized void update(float deltaTick, Level level)
	{
		if (switchingTime > 0)
		{
			world = C.SPACE_END;
		}
		if (switchingTime >= C.SWITCH_TIME_MAX)
		{
			switchingTime = 0;
			world = switchingToWorld;
		}
		if (level.isSolid((int) x, (int) y, world))
		{
			/**
			 * The ball is currently in a brick. It is stuck, so don't move it.
			 * Return so we don't nudge it.
			 */
			v_y = -GRAVITY_CONSTANT * deltaTick;
			return;

		}
		/**
		 * First we see where the ball will be.
		 */
		next_y = y + v_y * deltaTick;
		next_x = x + v_x * deltaTick;
		
		/**
		 * Now we check if any part of the ball will be inside of a brick.
		 */

		/** Right side **/
		r_x = (int) (next_x + r);
		r_y = (int) (next_y);

		/** Bottom side **/
		b_x = (int) (next_x);
		b_y = (int) (next_y - r);

		/** top side **/
		t_x = (int) (next_x);
		t_y = (int) (next_y + r);

		/** bottom grace point */
		b_g_x = (int) (next_x + 0.5f * r);
		b_g_y = (int) (next_y - 0.87 * r);
		
		/** top grace point */
		t_g_x = (int) (next_x + 0.5f * r);
		t_g_y = (int) (next_y + 0.87 * r);

		/**
		 * Set flags.
		 */
		b_collide = level.isSolid(b_x, b_y, world);
		r_collide = level.isSolid(r_x, r_y, world);
		t_collide = level.isSolid(t_x, t_y, world);
		b_g_collide = level.isSolid(b_g_x, b_g_y, world);
		t_g_collide = level.isSolid(t_g_x, t_g_y, world);
		

		if(!t_collide && !b_collide && r_collide && b_g_collide && !t_g_collide)
		{
			/**
			 * Must be only one brick.
			 * Must be the corner. Move 
			 * the ball up top.
			 */
			next_y = b_y + 1f + r;
			if(v_y<0)
				v_y = 0;
			
			
		}
		else if(!t_collide && !b_collide && r_collide && !b_g_collide && t_g_collide)
		{
			/**
			 * Must be only one brick.
			 * Must be the corner. Move
			 * the ball down.
			 */
			next_y = t_y - r;
			v_y = 0;
		}
		else if(!t_collide && !b_collide && !r_collide && b_g_collide && !t_g_collide)
		{
			/**
			 * Must be only one brick.
			 * Must be the corner. Move 
			 * the ball up top.
			 */
			next_y = b_y + 1f + r;
			
			/**
			 * This 'if' will keep the ball from 
			 * 'sticking' when jumping up onto
			 * a corner.
			 */
			if(v_y<0)
				v_y = 0;
		}
		else if(!t_collide && !b_collide && !r_collide && !b_g_collide && t_g_collide)
		{
			/**
			 * Must be only one brick.
			 * Must be the corner. Move
			 * the ball down.
			 */
			next_y = t_y - r;
			v_y = 0;
			
		}
		else if(!t_collide && b_collide && r_collide && b_g_collide && !t_g_collide)
		{
			
			/**
			 * This case only happens when the 
			 * ball is falling very fast.
			 */
			next_y = b_y + 1f + r;
			
			if(v_y<0)
				v_y = 0;
		}
		else
		{
			/**
			 * quick fix
			 */
			/** Right side **/
			if (r_collide)
			{
				/**
				 * The ball is stuck on the side of a brick.
				 */
				next_x = r_x - r;
			}
			
			/** Bottom side **/
			if(b_collide)
			{
				/** The ball is on top of a brick */
				next_y = b_y + 1 + r;
				v_y = 0 ;
			}
			
			/** top side **/

			if(t_collide)
			{
				/** The ball is hitting the top of a brick */
				next_y = t_y - r;
				v_y = 0;
			}
		}
		x = next_x;
		y = next_y;
		v_y -= GRAVITY_CONSTANT * deltaTick;
		//Log.d(TAG, x + ", " + y);
	}

	/** use these to check jumpability **/
	private static int yy;

	public void jump(Level level)
	{
		/**
		 * To prevent double jumps, make sure the ball is not moving upwards.
		 */
		if (v_y > 0)
			return;

		/**
		 * Special case, allow jumping if the center of the ball is inside
		 * of a brick.
		 */
		if(level.isSolid((int) x, (int)(y) , world))
		{
			v_y = JUMP_FACTOR;
			return;
		}
		/**
		 * To check whether we can jump, just check a tiny bit below the
		 * bottommost point of the circle. If it's solid, we can, else we check
		 * for 'grace periods'. This allows the ball to jump even if it's a
		 * little bit off the edge.
		 */
		yy = (int) (y - r - 0.1f);
		if (level.isSolid((int) x, yy, world))
		{
			v_y = JUMP_FACTOR;
			return;
		} else if (level.isSolid((int) (x - .5f * r), yy, world))
		{
			v_y = JUMP_FACTOR;
			return;
		} else if (level.isSolid((int) (x + 0.5f * r), yy, world))
		{
			v_y = JUMP_FACTOR;
			return;
		}
	}

	public int getColor()
	{
		return C.COLOR[world];
	}

	/*
	 * 
	 * switching should be based on the deltaTick, so that regardless of
	 * framerate, switching takes constant time.
	 */
	private float switchingTime = 0;

	public void doSwitch(float deltaTick)
	{

		if (world != C.WORLD_B)
		{
			switchingToWorld = C.WORLD_B;
			switchingTime += deltaTick;
		}
	}

	public void undoSwitch(float deltaTick)
	{

		if (world != C.WORLD_A)
		{
			switchingToWorld = C.WORLD_A;
			switchingTime += deltaTick;
		}
	}

	public int getWorld()
	{
		return world;
	}

	public boolean isSolid(int brickType)
	{
		return (brickType == C.SOLID || brickType == world);
	}
}
