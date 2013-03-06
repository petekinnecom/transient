package org.petekinnecom.transience_;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "levels.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = "DBHelper";

	private Context context;

	public DBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

	public void resetDB()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			db.execSQL("DROP TABLE levels");
			db.execSQL("DROP TABLE bricks");
			db.execSQL("CREATE TABLE levels ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT NOT NULL, " + "length INT NOT NULL, "
					+ "height INT NOT NULL, "
					+ "finished TINYINT NOT NULL DEFAULT 0 " + ");");

			db.execSQL("CREATE TABLE bricks ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT NOT NULL, " + "x TINYINT NOT NULL, "
					+ "y TINYINT NOT NULL, " + "type TINYINT NOT NULL " + ");");
		} catch (Exception e)
		{
			Log.d(TAG, e.getMessage());
		} finally
		{
			db.close();
		}
	}

	public void newColumn()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			db.execSQL("ALTER TABLE levels ADD num TINYINT");
			db.execSQL("ALTER TABLE levels ADD available TINYINT");
		} catch (Exception e)
		{
			Log.d(TAG, "newColumn: " + e.getMessage());
		} finally
		{
			db.endTransaction();
			db.close();
		}

	}

	public void writeLevel(Level level, String name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		if (name == null)
		{
			Log.d(TAG, "Null string name.");
			return;
		}
		ContentValues cv = new ContentValues();
		if (name.equals("new..."))
		{
			name = "level_" + System.currentTimeMillis();

		} else
		{
			db.execSQL("DELETE FROM levels WHERE name='" + name + "'");
			db.execSQL("DELETE FROM bricks WHERE name='" + name + "'");
		}
		// db.execSQL("DROP * FROM levels WHERE name="+name);
		try
		{

			level.dbContentValues(cv);
			cv.put("name", name);
			db.insert("levels", null, cv);
			db.beginTransaction();
			try
			{

				for (int i = 0; i < level.getLength(); i++)
				{
					for (int j = 0; j < level.getHeight(); j++)
					{
						if (level.getBrick(i, j) != C.SPACE)
						{
							cv.clear();
							cv.put("name", name);
							cv.put("x", i);
							cv.put("y", j);
							cv.put("type", level.getBrick(i, j));
							db.insert("bricks", null, cv);
						}
					}
				}
				db.setTransactionSuccessful();
			} finally
			{
				db.endTransaction();
			}
		} catch (Exception e)
		{
			Log.d(TAG, "writeLevel: " + e.getMessage());
		} finally
		{
			db.close();
		}

		Log.d(TAG, "Wrote " + name);

	}

	public void deleteLevel(String levelName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			db.execSQL("DELETE FROM levels WHERE name='" + levelName + "'");
			db.execSQL("DELETE FROM bricks WHERE name='" + levelName + "'");

		} catch (Exception e)
		{
			Log.d(TAG, e.getMessage());
		} finally
		{
			db.close();
		}
	}

	public void renameLevel(String oldName, String newName, Level level)
	{
		if (C.DEBUG)
			Log.d(TAG, "deleting level: " + oldName);
		deleteLevel(oldName);
		if (C.DEBUG)
			Log.d(TAG, "writing level: " + newName);
		writeLevel(level, newName);
	}

	public void setLevelFinished(Level level)
	{
		if (C.DEBUG)
			Log.d(TAG, "setLevelFinished for level: " + level.getName());
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("UPDATE levels SET finished='1' WHERE name='"
				+ level.getName() + "'");
		db.close();

	}

	public Level readLevel(String levelName)
	{
		Level level = null;
		SQLiteDatabase db = this.getReadableDatabase();
		try
		{

			Log.d(TAG, "Reading level data for level: " + levelName);

			int length, height;
			Cursor c = db.rawQuery("SELECT * FROM levels WHERE name=?",
					new String[] { levelName });
			c.moveToFirst();
			length = c.getInt(c.getColumnIndex("length"));
			height = c.getInt(c.getColumnIndex("height"));
			String summary = c.getString(c.getColumnIndex("summary"));
			int finished = c.getInt(c.getColumnIndex("finished"));
			String name = c.getString(c.getColumnIndex("name"));

			Log.d(TAG, "Finished reading level data for level: " + levelName);
			Log.d(TAG, "Reading bricks");

			int x, y, type;
			int[][] grid = new int[length][height];
			for (int i = 0; i < length; i++)
			{
				for (int j = 0; j < height; j++)
				{
					grid[i][j] = C.SPACE;
				}
			}

			c = db.rawQuery("SELECT * FROM bricks where name=?",
					new String[] { levelName });

			while (c.moveToNext())
			{
				x = c.getInt(c.getColumnIndex("x"));
				y = c.getInt(c.getColumnIndex("y"));
				type = c.getInt(c.getColumnIndex("type"));

				grid[x][y] = type;
			}
			Log.d(TAG, "Finished reading bricks.");
			level = new Level(grid, name, summary, finished);
		} catch (Exception e)
		{
			Log.d(TAG, e.getMessage());

		} finally
		{
			if (db != null)
				db.close();
		}

		return level;
	}

	/*
	 * Grab every levelName from the level Table. Would like to take more data,
	 * but name enough for now.
	 */
	public String[] readLevelList()
	{
		ArrayList<String> levelList = new ArrayList<String>();
		
		/** Ghetto ass 'help' hack */
		levelList.add("Help/Instructions");
		
		SQLiteDatabase db = this.getReadableDatabase();
		try
		{

			Log.d(TAG, "Reading levelList");

			Cursor c = db.rawQuery("SELECT * FROM levels ORDER BY name",
					new String[] {});
			while (c.moveToNext())
			{
				levelList.add(c.getString(c.getColumnIndex("name")));
			}

			Log.d(TAG, "Finished reading levelList.");
		} catch (Exception e)
		{
			Log.d(TAG, e.getMessage());
			// resetDB();

		} finally
		{
			if (db != null)
				db.close();
		}

		/*
		 * We'll add in the menu item new here for now.
		 */
		int size = levelList.size() + 1;
		String[] s = new String[size];
		int i = 0;
		for (String l : levelList)
		{
			s[i] = l;
			i++;
		}
		if (C.PAID)
		{
			s[i] = "new...";
		}
		else
		{
			s[i] = "Get more levels.";
		}
		return s;

	}

	public LevelInfo[] readInfoList()
	{
		ArrayList<LevelInfo> levelList = new ArrayList<LevelInfo>();
		
		/** Ghetto ass 'help' hack */
		levelList.add(new LevelInfo("Help/Instructions", 0));
		
		SQLiteDatabase db = this.getReadableDatabase();
		try
		{

			Log.d(TAG, "Reading levelInfo");

			Cursor c = db.rawQuery("SELECT * FROM levels ORDER BY name",
					new String[] {});
			while (c.moveToNext())
			{
				levelList.add(new LevelInfo(c.getString(c
						.getColumnIndex("name")), c.getInt(c
						.getColumnIndex("finished"))));
			}
			db.close();
			Log.d(TAG, "Finished reading levelInfo.");
		} catch (Exception e)
		{
			Log.d(TAG, e.getMessage());
			// resetDB();

		} finally
		{
			if (db != null)
				db.close();
		}

		/*
		 * We'll add in the menu item new here for now. TODO: fix before
		 * deployment.
		 */
		int size = levelList.size() + 1;
		LevelInfo[] li = new LevelInfo[size];
		int i = 0;
		for (LevelInfo l : levelList)
		{
			li[i] = l;
			i++;
		}
		if (C.PAID)
		{
			li[i] = new LevelInfo("new...", 0);
		}
		else
		{
			li[i] = new LevelInfo("Get more levels.",0);
		}
		return li;

	}

	public void editLevelSummary(String levelName, String summary)
	{
		Log.d(TAG, "setting summary to : " + summary);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try
		{
			cv.put("summary", summary);
			db.update("levels", cv, "name='" + levelName + "'", null);
		} catch (Exception e)
		{
			Log.d(TAG, "editLevelSummary: " + e.getMessage());
		} finally
		{
			db.close();
		}

	}

	public void readFromAssets()
	{
		if(C.DEBUG)
			Log.d(TAG, "Begin readFromAssets.");

		String dbName;
		if(C.PAID)
			dbName = "levels_paid.db";
		else
			dbName = "levels_free.db";
		
		File outputFile = new File(
				"/data/data/org.petekinnecom.transience_/databases",
				"levels.db");
		try
		{
			// getResources().openRawResource(R.drawable.balloons);
			InputStream is = context.getAssets().open(dbName);
			OutputStream os = new FileOutputStream(outputFile);
			byte[] data = new byte[is.available()];
			is.read(data);
			os.write(data);
			is.close();
			os.close();
			if(C.DEBUG)
				Log.d(TAG, "Success it seems.");
		} catch (IOException e)
		{
			// Unable to create file, likely because external storage is
			// not currently mounted.
			if(C.DEBUG)
				Log.d("ExternalStorage", "Error writing " + outputFile, e);
		}

	}

	public boolean testDB()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		try
		{
			if(C.DEBUG)
				Log.d(TAG, "Verifying that DB is present.");

			Cursor c = db.rawQuery("SELECT * FROM levels ORDER BY name",
					new String[] {});
			int i = 0;
			while (c.moveToNext())
			{
				i++;
			}
			if(C.DEBUG)
				Log.d(TAG, "DB found.");
			if(i<20)
			{
				/** ghetto motherfucking hack */
				/** The db is there, but it doesn't have all the levels.
				 * they must be updating, so let's rebuild.*/
				return false;
			}
		} catch (Exception e)
		{
			if(C.DEBUG)
				Log.d(TAG, "DB not found.");
			return false;

		} finally
		{
			if (db != null)
				db.close();
		}
		return true;
	}
}
