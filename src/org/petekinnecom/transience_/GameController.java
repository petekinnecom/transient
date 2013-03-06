package org.petekinnecom.transience_;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameController extends SurfaceView implements
		SurfaceHolder.Callback
{
	private static final String TAG = "GameController";
	private GameThread thread;
	private GameActivity gameActivity;

	private GameModel gameModel;
	private GameRender gameRender;
	private Button[] buttons = new Button[2];
	private boolean waitingForNewGame = false;

	class GameThread extends Thread
	{
		/**
		 * SPEED CONTROL
		 */
		private static final float SPEED_MULTIPLIER = 4;

		/**
		 * State-tracking constants
		 */
		public static final int STATE_LOSE = 1;
		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
		public static final int STATE_WIN = 5;

		private Context context;
		/**
		 * Current state
		 */
		private int mode;

		/** Handlers for later reference */
		private SurfaceHolder surfaceHolder;

		public GameThread(SurfaceHolder surfaceHolder, Context c)
		{
			// get handles to some important objects
			this.surfaceHolder = surfaceHolder;
			context = c;

		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		public void reset()
		{
			synchronized (surfaceHolder)
			{
				/** TODO: initialize Model, Renderer here */
				gameModel = new GameModel(level);
				gameRender.reset();
				setState(STATE_RUNNING);
				running = true;
			}
			if (C.DEBUG)
				Log.d(TAG, "thread reset");
		}

		/**
		 * Pauses the physics update & animation.
		 */
		public void pause()
		{
			synchronized (surfaceHolder)
			{
				if (mode == STATE_RUNNING)
					setState(STATE_PAUSE);
			}
		}

		/**
		 * Restores game state from the indicated Bundle. Typically called when
		 * the Activity is being restored after having been previously
		 * destroyed.
		 * 
		 * @param savedState
		 *            Bundle containing the game state
		 */
		public synchronized void restoreState(Bundle savedState)
		{
			synchronized (surfaceHolder)
			{
				/**
				 * TODO: restorestate ability. Follow outline in LunarView.java
				 */
			}
		}

		/**
		 * Thread state info
		 */
		private boolean running;
		private Canvas canvas;
		private float deltaTick;
		private long lastTime = 0;
		private long thisTime;

		private long sleepTime;
		/**
		 * Debug vars
		 */
		float ticked = 0;
		float totalTicked = 0;
		StringBuilder fps = new StringBuilder("fps: ");

		@Override
		public void run()
		{
			lastTime = System.currentTimeMillis();
			while (running)
			{
				canvas = null;
				try
				{
					canvas = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder)
					{
						/**
						 * Reset to zero, so that if we skip the update loop, we
						 * render with a delta 0, so the screen stops moving.
						 */
						deltaTick = 0;
						if (mode == STATE_RUNNING)
						{

							thisTime = System.currentTimeMillis();

							/** This will keep huge skips from happening */
							deltaTick = Math.min(C.MAX_DELTA_TICK,
									(thisTime - lastTime) / 1000f);
							/**
							 * DEBUG
							 */
							ticked++;
							totalTicked += deltaTick;
							if (ticked % 30 == 0)
							{
								fps.setLength(0);
								fps.append(ticked / totalTicked);
								//Log.d(TAG, fps.toString());
								ticked = totalTicked = 0f;
							}
							sleepTime = Math.max(0,
									(long) ((C.MIN_WAIT - deltaTick) * 1000));
							// Log.d(TAG, "dT: " + deltaTick +
							// " sT: "+sleepTime);
							/**
							 * /DEBUG
							 */

							/**
							 * we must tell the switch/unswitch routines
							 * deltaTick so that we can ensure that limbo lasts
							 * the specified amount of time, regardless of
							 * framerate.
							 */
							deltaTick = deltaTick * SPEED_MULTIPLIER;
							if (buttons[0].getPressed())
							{
								gameModel.doSwitch(deltaTick);
							} else
							{
								gameModel.undoSwitch(deltaTick);
							}

							if (buttons[1].getPressed())
							{
								gameModel.jumpBall();
							}

							setState(gameModel.update(deltaTick));
							lastTime = thisTime;
						}
						gameRender
								.render(canvas, gameModel, buttons, deltaTick);

						try
						{
							Thread.sleep(sleepTime);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} finally
				{
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (canvas != null)
					{
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		/**
		 * Dump game state to the provided Bundle. Typically called when the
		 * Activity is being suspended.
		 * 
		 * @return Bundle with this view's state
		 */
		public Bundle saveState(Bundle map)
		{
			synchronized (surfaceHolder)
			{
				/**
				 * TODO: saveState stuff
				 */
			}
			return map;
		}

		/**
		 * Used to signal the thread whether it should be running or not.
		 * Passing true allows the thread to run; passing false will shut it
		 * down if it's already running. Calling start() after this was most
		 * recently called with false will result in an immediate shutdown.
		 * 
		 * @param b
		 *            true to run, false to shut down
		 */
		public void setRunning(boolean b)
		{
			running = b;
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @see #setState(int, CharSequence)
		 * @param mode
		 *            one of the STATE_* constants
		 */
		public void setState(int mode)
		{
			synchronized (surfaceHolder)
			{
				this.mode = mode;
				if (mode == STATE_RUNNING)
					return;
				if (mode == STATE_LOSE)
				{
					if (C.DEBUG)
						Log.d(TAG, "You have lost and I know it.");
					gameRender.fade = true;

					Message msg = new Message();
					msg.arg1 = STATE_LOSE;
					gameActivity.dialogHandler.sendMessage(msg);
				} else if (mode == STATE_WIN)
				{
					if (C.DEBUG)
						Log.d(TAG, "You have won!");
					gameRender.fade = true;
					thread.setRunning(false);
					Message msg = new Message();
					msg.arg1 = STATE_WIN;
					gameActivity.dialogHandler.sendMessage(msg);

				}
			}
		}

	}

	private Level level;

	public GameController(GameActivity gameActivity)
	{
		super((Context) gameActivity);
		this.gameActivity = gameActivity;
		if (C.DEBUG)
			Log.d(TAG, "constructor");
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		thread = new GameThread(holder, (Context) gameActivity);
		setFocusable(true); // make sure we get key events
		Log.d(TAG, "Constructor: init: model, render");

		buttons[0] = new Button(Button.SWITCH_BUTTON);
		buttons[1] = new Button(Button.JUMP_BUTTON);

		level = C.LEVEL;
	}

	/* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{

	}

	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	public void surfaceCreated(SurfaceHolder holder)
	{
		/*
		 * Now that we have a surface, we can set the drawable areas.
		 */

		/** these three must stay together here */
		gameModel = new GameModel(level);
		gameRender = new GameRender();
		gameRender.setDimensions(this, gameModel.getGridHeight(), buttons);

		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		if (C.DEBUG)
			Log.d(TAG, "surfaceCreated: starting thread");

		// thread.reset();
		// thread.setRunning(true);
		/** last call */
		System.gc();

		resetThread();

		// thread.start();

		if (C.DEBUG)
			Log.d(TAG, "thread started");
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "surfaceDestroyed: stopping thread");
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		thread.setRunning(false);
		while (retry)
		{
			try
			{
				thread.join();
				retry = false;
			} catch (InterruptedException e)
			{
			}
		}
		Log.d(TAG, "thread stopped");
	}

	public GameThread getThread()
	{
		return thread;
	}

	private Button[] pressed = new Button[2];
	private int pid;
	Button actionButton;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		/**
		 * First, see if we are at the losing screen. If we are, any event
		 * starts us again. clear the flag and restart.
		 * 
		 * TODO: buggy?
		 */
		if (waitingForNewGame)
		{
			waitingForNewGame = false;
			thread.reset();
		}

		pid = event.getActionIndex();
		if (C.DEBUG)
			Log.d(TAG,
					"pid: " + pid + " at: " + event.getX(pid) + ", "
							+ event.getY(pid));
		if (pid > 1)
			return true;
		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN
				|| event.getAction() == MotionEvent.ACTION_DOWN)
		{
			actionButton = getButton((int) event.getX(pid),
					(int) event.getY(pid));
			if (actionButton != null)
			{
				actionButton.press();
				pressed[pid] = actionButton;
			}
		} else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
				|| event.getAction() == MotionEvent.ACTION_UP)
		{
			// don't know why necessary, but might stop some crashes
			if (pressed[pid] != null)
				pressed[pid].release();

			/*
			 * this is an ugly hack, but it works... Say you press two fingers:
			 * L,R in this order. L DOWN : pid = 0 R DOWN : pid = 1 L UP : pid =
			 * 0 R UP : pid = 0
			 * 
			 * Notice that finger R now has index zero at the end. This could
			 * probably be avoided by using some better event management, but
			 * I'd rather spend my time adding new exciting features right
			 * now...so blah.
			 */
			if (pid == 0)
				pressed[0] = pressed[1];
		}

		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_Q:
		case KeyEvent.KEYCODE_A:
		case KeyEvent.KEYCODE_Z:
		case KeyEvent.KEYCODE_W:
		case KeyEvent.KEYCODE_S:
		case KeyEvent.KEYCODE_X:
		case KeyEvent.KEYCODE_E:
		case KeyEvent.KEYCODE_D:
		case KeyEvent.KEYCODE_C:
		{
			buttons[0].press();
			return true;
		}
		case KeyEvent.KEYCODE_U:
		case KeyEvent.KEYCODE_J:
		case KeyEvent.KEYCODE_M:
		case KeyEvent.KEYCODE_I:
		case KeyEvent.KEYCODE_K:
		case KeyEvent.KEYCODE_COMMA:
		case KeyEvent.KEYCODE_O:
		case KeyEvent.KEYCODE_L:
		case KeyEvent.KEYCODE_PERIOD:
		{
			buttons[1].press();
			return true;
		}
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_Q:
		case KeyEvent.KEYCODE_A:
		case KeyEvent.KEYCODE_Z:
		case KeyEvent.KEYCODE_W:
		case KeyEvent.KEYCODE_S:
		case KeyEvent.KEYCODE_X:
		case KeyEvent.KEYCODE_E:
		case KeyEvent.KEYCODE_D:
		case KeyEvent.KEYCODE_C:
		{
			buttons[0].release();
			return true;
		}
		case KeyEvent.KEYCODE_U:
		case KeyEvent.KEYCODE_J:
		case KeyEvent.KEYCODE_M:
		case KeyEvent.KEYCODE_I:
		case KeyEvent.KEYCODE_K:
		case KeyEvent.KEYCODE_COMMA:
		case KeyEvent.KEYCODE_O:
		case KeyEvent.KEYCODE_L:
		case KeyEvent.KEYCODE_PERIOD:
		{
			buttons[1].release();
			return true;
		}
		}
		return false;
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event)
	{
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP)
		{
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++)
		{
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	private Button getButton(int x, int y)
	{
		for (Button b : buttons)
		{
			if (b.activates(x, y))
			{
				return b;
			}
		}
		return null;
	}

	public void stopThread()
	{
		boolean retry = true;
		thread.setRunning(false);
		while (retry)
		{
			try
			{
				thread.join();
				retry = false;
			} catch (InterruptedException e)
			{
			}
		}
		Log.d(TAG, "thread stopped");
	}

	public void resetThread()
	{
		if (thread != null)
		{
			thread.setRunning(false);
		}
		thread = new GameThread(getHolder(), (Context) gameActivity);
		buttons[0].release();
		buttons[1].release();
		thread.reset();
		System.gc();
		thread.start();

	}

}
