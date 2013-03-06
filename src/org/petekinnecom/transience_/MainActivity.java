package org.petekinnecom.transience_;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	private static final String TAG = "MainActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up click listeners for all the buttons
		findViewById(R.id.options_button).setOnClickListener(this);
		findViewById(R.id.start_button).setOnClickListener(this);
		Log.d(TAG, "blue: "+Color.red(Color.BLUE)+", "+Color.green(Color.BLUE)+", "+Color.blue(Color.BLUE));
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.options_button:
			Intent i = new Intent(this, Options.class);
			startActivity(i);
			break;
		case R.id.start_button:
			startActivity(new Intent(this, LevelSelector.class));
			break;
		}

	}
}