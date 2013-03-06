package org.petekinnecom.transience_;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Options extends PreferenceActivity
{
	private static final String TAG = "Options";

	private static final String SETTINGS_TRAILERS = "trailers";
	private static final boolean SETTINGS_TRAILERS_DEFAULT = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.options);
	}

	public static boolean getTrailers(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(SETTINGS_TRAILERS, SETTINGS_TRAILERS_DEFAULT);
	}
	static Integer I;
	public static float getVerticalRatio(Context context)
	{
		
		I = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
				.getString("vertical_ratio", "80"));
		float v = I.floatValue()/100.0f;
		if(v<.6 || v>0.9)
		{
			return 0.8f;
		}
		else return v;
	}
}
