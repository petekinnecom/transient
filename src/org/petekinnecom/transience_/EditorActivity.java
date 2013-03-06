package org.petekinnecom.transience_;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends Activity
{
	private static final String TAG = "GameActivity";
	private EditorView editorView;

	private Level level;
	private String levelName;
	private DBHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		dbHelper = new DBHelper(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		Intent sender = getIntent();
		levelName = sender.getExtras().getString("levelName");
		Log.d(TAG, "Intent sent levelName:" + levelName);
		level = parseLevel(levelName);
		Level.editorMode = true;
		editorView = new EditorView(this, levelName, level);

		this.setContentView(editorView);
	}

	private Level parseLevel(String levelName)
	{

		Log.d(TAG, "reading: " + levelName);

		if (levelName.equals("new..."))
		{
			level = new Level(300, 6);
			levelName = "level_" + System.currentTimeMillis();
		} else
		{
			level = dbHelper.readLevel(levelName);
		}
		return level;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.save:
			DBHelper db = new DBHelper(this);
			db.writeLevel(level, levelName);
			Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT);
			return true;
		case R.id.startSquare:
			level.deleteStartSquare();
			editorView.placingStartSquare = true;
			editorView.invalidate();
			return true;
		case R.id.endSquare:
			editorView.placingEndSquare = true;
			editorView.invalidate();
			return true;
		case R.id.increaseHeight:
			level.increaseHeight();
			editorView.invalidate();
			return true;
		case R.id.decreaseHeight:
			level.decreaseHeight();
			editorView.invalidate();
			return true;
		case R.id.toggleGrid:
			GameRender.SHOW_GRID = !GameRender.SHOW_GRID;
			editorView.invalidate();
			return true;
		case R.id.renameLevel:
			renameLevelDialog();
			return true;
		case R.id.editSummary:
			editSummaryDialog();
			return true;
		}
		return false;
	}

	private void renameLevelDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Rename");
		alert.setMessage("New name: ");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(levelName);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String newName = input.getText().toString();
				if(C.DEBUG) 
					Log.d(TAG, "Renaming level: " + levelName + " to level: "
						+ newName);
				dbHelper.renameLevel(levelName, newName, level);
				if(C.DEBUG)
					Log.d(TAG, "Reloading level: "+newName);
				level = dbHelper.readLevel(newName);
				levelName = newName;
			}
		});

		alert.show();
	}

	private void editSummaryDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Summary");
		alert.setMessage("Edit: ");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(level.getSummary());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String summary = input.getText().toString();
				Log.d(TAG, "Editing summary");
				dbHelper.editLevelSummary(levelName, summary);
			}
		});

		alert.show();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		DBHelper db = new DBHelper(this);
		db.writeLevel(level, levelName);
		Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		DBHelper db = new DBHelper(this);
		db.writeLevel(level, levelName);
		Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		DBHelper db = new DBHelper(this);
		db.writeLevel(level, levelName);
		Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		DBHelper db = new DBHelper(this);
		db.writeLevel(level, levelName);
		Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT);
	}
	@Override
	protected void onSaveInstanceState(Bundle bundle)
	{
		Toast.makeText(this, "saving: " + levelName, Toast.LENGTH_LONG);
		DBHelper db = new DBHelper(this);
		db.writeLevel(level, levelName);
		super.onSaveInstanceState(bundle);
	}

}
