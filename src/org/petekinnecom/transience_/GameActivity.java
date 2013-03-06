package org.petekinnecom.transience_;

import org.petekinnecom.transience_.GameController.GameThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnKeyListener;
import android.view.WindowManager;

public class GameActivity extends Activity
{
	private static final String TAG = "GameActivity";
	private DBHelper dbHelper;
	/**
	 * TODO: Deal with pop-up menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		return true;
	}

	/**
	 * TODO: Deal with pop-up menu. Follow layout in LunarLander.java
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		}
		return false;
	}

	/**
	 * Invoked when the Activity is created.
	 * 
	 * @param savedInstanceState
	 *            a Bundle containing state saved from a previous execution, or
	 *            null if this is a new execution
	 */
	private GameController gameController;
	public DialogHandler dialogHandler;
	AlertDialog.Builder alertDialog;
	

	class DialogHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.arg1 == GameThread.STATE_LOSE)
			{
				if(C.DEBUG) Log.d(TAG, "stopping thread");
				gameController.stopThread();
				if(C.DEBUG) Log.d(TAG, "thread stopped.");
				
				OnClickListener dialogListener = new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1)
					{
						// the button was clicked
						gameController.resetThread();
					}
				};
				alertDialog.setMessage(C.getLoseMessage());
				// add a neutral button to the alert box and assign a click listener
				alertDialog.setNeutralButton("Retry", dialogListener);
				alertDialog.show();

			}
			else if(msg.arg1 == GameThread.STATE_WIN)
			{
				if(C.DEBUG) Log.d(TAG, "stopping thread");
				gameController.stopThread();
				if(C.DEBUG) Log.d(TAG, "thread stopped.");
				
				OnClickListener dialogListener = new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1)
					{
						// the button was clicked
						//gameController.resetThread();
						finish();
					}
				};
				alertDialog.setMessage(C.getWinMessage());
				// add a neutral button to the alert box and assign a click listener
				

				alertDialog.setNeutralButton("ok", dialogListener);
				alertDialog.show();

				
				/** This ought to do */
				dbHelper.setLevelFinished(C.LEVEL);
				
				
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate enter");

		// could be done better
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		dialogHandler = new DialogHandler();
		alertDialog = new AlertDialog.Builder(this);
		gameController = new GameController(this);
		if (gameController.getThread() == null)
		{
			Log.d(TAG, "gameThread is null. exiting");
			System.exit(1);
		}
		if (savedInstanceState == null)
		{
			// we were just launched: set up a new game
			Log.d(TAG, "Setting up a new game.");
			gameController.getThread().setState(GameThread.STATE_READY);
		} else
		{
			// we are being restored: resume a previous game
			Log.d(TAG,
					"Restoring state: not implemented.  Defaulting to restart.");
			// gameThread.restoreState(savedInstanceState);
			gameController.getThread().setState(GameThread.STATE_READY);
		}
		this.setContentView(gameController);
		
		
		dbHelper = new DBHelper(this);
		
		
		/**
		 * SET OPTIONS.
		 */
		C.DISABLE_TRAILERS = Options.getTrailers(this);
		C.LANE_VERTICAL_RATIO = Options.getVerticalRatio(this);
		
		Log.d(TAG, "onCreate exit");

	}

	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause()
	{
		Log.d(TAG, "onPause: not really implemented");
		super.onPause();
		gameController.getThread().pause(); // pause game when Activity pauses
	}

	/**
	 * Notification that something is about to happen, to give the Activity a
	 * chance to save state.
	 * 
	 * @param outState
	 *            a Bundle into which this Activity should save its state
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.d(TAG, "onSaveInstanceState: not really implemented");
		// just have the View's thread save its state into our Bundle
		super.onSaveInstanceState(outState);
		gameController.getThread().saveState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		gameController.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		gameController.onKeyUp(keyCode, event);
		return super.onKeyUp(keyCode, event);
	}
}