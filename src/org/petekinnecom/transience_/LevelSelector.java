package org.petekinnecom.transience_;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LevelSelector extends Activity implements OnItemClickListener,
		OnItemLongClickListener
{
	private static final String TAG = "LevelSelector";
	private ListView listView;

	private DBHelper dbHelper;
	private String[] levelList;
	private LevelInfo[] levelInfo;

	class DisableAdapter extends ArrayAdapter
	{
		private int[] fonts = { Color.WHITE, Color.GRAY };

		public DisableAdapter(Context context, int textViewResourceId,
				Object[] o)
		{
			super(context, textViewResourceId, o);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			TextView view = (TextView) super.getView(position, convertView,
					parent);
			view.setTextColor(Color.WHITE);
			if (levelInfo[position].finished)
			{
				view.setTextColor(Color.GRAY);
			}
			return view;
		}
	}

	private void initAdapter()
	{
		if(!dbHelper.testDB())
		{
			dbHelper.readFromAssets();
		}
		levelList = dbHelper.readLevelList();
		
		/**
		 * Yes, we are duplicating data here. This could be fixed, but whatevs,
		 * let's get this shit done already.
		 */
		levelInfo = dbHelper.readInfoList();

		listView = (ListView) findViewById(R.id.ListView);
		DisableAdapter adapter = new DisableAdapter(this,
				android.R.layout.simple_list_item_1, levelList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		if (C.PAID)
		{
			listView.setOnItemLongClickListener(this);
		}
		listView.setSelection(C.LIST_POSITION);
		
	}

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		dbHelper = new DBHelper(this);

		/**
		 * Used for the String adapter.
		 */

		setContentView(R.layout.listview);

		initAdapter();

		setTitle("Choose your adventure.");

	}

	protected void alertBox(String title, String mymessage, final Intent intent)
	{
		new AlertDialog.Builder(this).setMessage(mymessage).setTitle(title)
				.setCancelable(true)
				.setNeutralButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						startActivityForResult(intent, 1);
					}
				}).show();
	}

	@Override
	public void onItemClick(AdapterView<?> whats_this, View who_knows,
			int index, long who_cares)
	{
		if (C.DEBUG)
			Log.d(TAG, "reading level: " + levelList[index]);

		
		/** ignore short click on new **/
		if (levelList[index].equals("new..."))
			return;

		
		/** Send to the about page for info */
		if(levelList[index].equals("Get more levels."))
		{
			startActivity(new Intent(this, About.class));
			return;
		}
		
		/** Send to the about page for help */
		if(levelList[index].equals("Help/Instructions"))
		{
			startActivity(new Intent(this, Help.class));
			return;
		}
		C.LIST_POSITION = index;
		
		C.LEVEL = dbHelper.readLevel(levelList[index]);

		Intent intent = new Intent(this, GameActivity.class);

		alertBox("Preparation", C.LEVEL.getSummary(), intent);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int index,
			long arg3)
	{

		Log.d(TAG, "reading level: " + levelList[index]);
		C.LEVEL = dbHelper.readLevel(levelList[index]);
		Intent intent = new Intent(this, EditorActivity.class);
		intent.putExtra("levelName", levelList[index]);
		startActivityForResult(intent, 1);

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		if (C.DEBUG)
			inflater.inflate(R.menu.develop_menu, menu);
		else
			inflater.inflate(R.menu.regular_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// case R.id.options:
		// startActivity(new Intent(this, Options.class));
		// return true;

		case R.id.backup_db:
			if (writeToSDCard())
				Toast.makeText(this, "Success.", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this, "Failed.", Toast.LENGTH_LONG).show();
			break;
		case R.id.help:
			startActivity(new Intent(this, About.class));
			break;
		case R.id.restore_db:
			readFromSDCard();
			break;
		case R.id.options:
			startActivity(new Intent(this, Options.class));
			break;
		case R.id.recreate_db:
			dbHelper.readFromAssets();
			initAdapter();
			break;

		}
		return false;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		initAdapter();
	}

	public void readFromSDCard()
	{
		Log.d(TAG, "Begin readFromSDCard.");
		File inputFile = new File(getExternalFilesDir(null), "levels_bak.db");
		File outputFile = new File(
				"/data/data/org.petekinnecom.transience_/databases",
				"levels.db");
		if (inputFile.exists())
		{
			try
			{
				// getResources().openRawResource(R.drawable.balloons);
				InputStream is = new FileInputStream(inputFile);
				OutputStream os = new FileOutputStream(outputFile);
				byte[] data = new byte[is.available()];
				is.read(data);
				os.write(data);
				is.close();
				os.close();
			} catch (IOException e)
			{
				// Unable to create file, likely because external storage is
				// not currently mounted.
				Log.w("ExternalStorage", "Error writing " + outputFile, e);
			}
		} else
		{
			Log.d(TAG, "Cannot find DB");
		}
	}

	public boolean writeToSDCard()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
		String d = df.format(System.currentTimeMillis());
		Log.d(TAG, "Begin writeToSDCard: " + d);
		File outputFile = new File(getExternalFilesDir(null), d + ".db");
		File inputFile = new File(
				"/data/data/org.petekinnecom.transience_/databases",
				"levels.db");
		if (inputFile.exists())
		{
			try
			{
				Log.d(TAG, "Copying file.");
				InputStream is = new FileInputStream(inputFile);
				OutputStream os = new FileOutputStream(outputFile);
				byte[] data = new byte[is.available()];
				is.read(data);
				os.write(data);
				is.close();
				os.close();
				return true;
			} catch (IOException e)
			{
				// Unable to create file, likely because external storage is
				// not currently mounted.
				Log.w("ExternalStorage", "Error writing " + outputFile, e);
			}
		} else
		{
			Log.d(TAG, "Cannot find DB");

		}
		return false;
	}
}
