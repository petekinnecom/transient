package org.petekinnecom.transience_;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class EditorView extends View
{

	private static final String TAG = null;

	DBHelper dbHelper;
	EditorActivity editorActivity;

	public boolean placingStartSquare = false;
	public boolean placingEndSquare = false;

	GameRender gameRender;
	Button[] buttons = new Button[2];
	float tick = 0;
	private Level level;

	public EditorView(Context context, String levelName, Level level)
	{
		super(context);

		editorActivity = (EditorActivity) context;
		this.level = level;
		tick = level.getStartSquare()[0];

		/*
		 * set up buttons
		 */
		buttons[0] = new Button(Button.LEFT_BUTTON);
		buttons[1] = new Button(Button.RIGHT_BUTTON);

		/** used for changing vertical ratio */
		//gameRender = new GameRender(Options.getVerticalRatio(context));
		gameRender = new GameRender();

	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if(level!=null)
		{
		gameRender.setDimensions(this, level.getHeight(), buttons);
		gameRender.renderEditor(canvas, level, buttons, (float) tick);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (gameRender.getButtonRect().contains((int) event.getX(),
				(int) event.getY()))
		{
			if (event.getX() > this.getWidth() / 2)
			{
				tick++;
			} else
			{
				tick--;
			}
		} else
		{
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				int[] coords = gameRender.getGridCoords(event.getX(),
						event.getY(), tick);
				if (coords != null)
				{
					if (placingStartSquare)
					{
						level.setStartSquare((int) tick - 15 + coords[0],
								level.getHeight() - coords[1]);
						placingStartSquare = false;
					} else if(placingEndSquare)
					{
						if(coords[0]>15)
						{
							level.setLength((int) tick - 15 + coords[0]);
						}
						placingEndSquare = false;
					}
					else
					{

						level.cycleBrick((int) tick - 15 + coords[0],
								level.getHeight() - coords[1]);
					}
				}
			}
		}
		invalidate();
		return true;
	}

}
