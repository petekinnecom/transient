package org.petekinnecom.transience_;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

public class GameRender
{
	private static final String TAG = "GameRender";

	private int BRICK_SIZE;
	private int NUM_TRAILERS = 100;

	private Rect screenRect, laneRect, buttonRect, infoRect;
	private int laneHeight, quadWidth;

	public GameRender()
	{
		Log.d(TAG, "constructor");
	}

	/*
	 * Set the rectangle sizes in which we draw. Must be outside of the
	 * constructor, because the constructor is called before the surface is
	 * created.
	 */
	public void setDimensions(View gameView, float gridHeight, Button[] buttons)
	{
		Log.d(TAG, "setting RECT sizes");

		screenRect = new Rect(0, 0, gameView.getWidth(), gameView.getHeight());

		/*
		 * Create lane and determine how big the bricks shall be drawn. Note
		 * that we use laneRect to calculate bricksize.
		 */
		laneHeight = (int) (gameView.getHeight() * C.LANE_VERTICAL_RATIO);

		laneRect = new Rect(0, 0, gameView.getWidth(), laneHeight);

		infoRect = new Rect(0, laneHeight, gameView.getWidth(), laneHeight + 10);

		buttonRect = new Rect(0, infoRect.bottom, gameView.getWidth(),
				gameView.getHeight());

		BRICK_SIZE = (int) (laneRect.height() / gridHeight);

		/*
		 * Now we must set-up the buttons.
		 */
		quadWidth = gameView.getWidth() / 4;
		buttons[0].setDimensions(0, buttonRect.top, quadWidth,
				buttonRect.bottom);
		buttons[1].setDimensions(3 * quadWidth, buttonRect.top,
				buttonRect.right, buttonRect.bottom);
	}

	private Level level;
	private Rect brick = new Rect();
	private int x1, x2, y1, y2;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/*
	 * HACK TO FIX
	 */
	public static boolean SHOW_GRID = false;

	public boolean fade;

	/**
	 * For an instant restart!
	 */
	public void reset()
	{
		totalTick = C.TICK_OFFSET + C.LEVEL.getStartSquare()[0];
		fade = false;
		trailers = new Circle[NUM_TRAILERS];
		for (int i = 0; i < trailers.length; i++)
		{
			trailers[i] = new Circle();
		}
	}

	/*
	 * This is the action game render call.
	 */
	private float totalTick = C.TICK_OFFSET;

	public void render(Canvas canvas, GameModel gameModel, Button[] buttons,
			float deltaTick)
	{
		totalTick += deltaTick;
		paint.setColor(Color.WHITE);
		canvas.drawRect(screenRect, paint);
		renderLevel(canvas, gameModel);
		renderButtons(canvas, buttons);
		renderBall(canvas, totalTick, gameModel.getBall());
		if (fade)
		{
			paint.setColor(Color.argb(206, 255, 255, 255));
			canvas.drawRect(screenRect, paint);
		}
	}

	// experimental outline
	private boolean[] outlines;
	int grid_i;
	int brick_i;
	float nudge;

	public void renderLevel(Canvas canvas, GameModel gameModel)
	{
		// Log.d(TAG, "render");
		paint.setColor(Color.WHITE);
		canvas.drawRect(screenRect, paint);

		level = gameModel.getLevel();

		/*
		 * On dealing with the tick and making sure we draw the correct part of
		 * the view: The data should be accessed at |_tick_| However, the first
		 * brick should be drawn at x=0. So we'll do the subtraction up front
		 * and track both as we iterate over the data.
		 * 
		 * Nudge is the factor to smooth out the transitions, so the bricks
		 * appear to soothly scroll.
		 * 
		 * Also note that, the model treats y+ as up. Fixing y+ as down will
		 * always be done while drawing to canvas.
		 */

		grid_i = (int) Math.floor(totalTick);
		brick_i = 0;
		nudge = (totalTick - grid_i);

		while ((brick_i - 1) * BRICK_SIZE <= laneRect.width())
		{
			for (int j = 0; j < level.getHeight(); j++)
			{
				paint.setColor(gameModel.getColor(grid_i, j));
				outlines = gameModel.getOutLineArray(grid_i, j);
				drawBrick(canvas, nudge, brick_i, j,
						gameModel.getColor(grid_i, j), outlines);
			}
			grid_i++;
			brick_i++;
		}

	}

	private void drawBrick(Canvas canvas, float nudge, int brick_i, int j,
			int color, boolean[] outlines)
	{
		x1 = (int) ((brick_i - nudge) * BRICK_SIZE);
		y1 = laneRect.height() - j * BRICK_SIZE;

		x2 = (int) ((brick_i + 1 - nudge) * BRICK_SIZE);
		y2 = laneRect.height() - (j + 1) * BRICK_SIZE;

		/*
		 * y1 is lower than y2 on screen because of inversion.
		 */
		if (j == 0)
		{
			y1 = screenRect.bottom;
			// Log.d(TAG, "here");
		}
		paint.setColor(Color.BLACK);
		// brick = new Rect(x1, y1, x2, y2);
		brick.set(x1, y1, x2, y2);
		canvas.drawRect(brick, paint);

		// top
		if (outlines[0])
			y2 = y2 + 2;
		// bottom
		if (outlines[1])
			y1 = y1 - 2;
		// left
		if (outlines[2])
			x1 = x1 + 2;
		// right
		if (outlines[3])
			x2 = x2 - 2;

		paint.setColor(color);
		brick.set(x1, y1, x2, y2);
		canvas.drawRect(brick, paint);

	}

	private void drawBrick(Canvas canvas, float nudge, int brick_i, int j)
	{
		x1 = (int) ((brick_i - nudge) * BRICK_SIZE);
		y1 = laneRect.height() - j * BRICK_SIZE;

		x2 = (int) ((brick_i + 1 - nudge) * BRICK_SIZE);
		y2 = laneRect.height() - (j + 1) * BRICK_SIZE;

		/*
		 * y1 is lower than y2 on screen because of inversion.
		 */
		if (j == 0)
		{
			y1 = screenRect.bottom;
			Log.d(TAG, "here");
		}
		brick = new Rect(x1, y1, x2, y2);
		canvas.drawRect(brick, paint);

	}

	private int rr, gg, bb;

	/*
	 * Rather than use an arraylist and cause more GC we'll make a static array
	 * and just edit the circles inside of it. It will be FIFO.
	 * 
	 * initTrailers must be called before using the gameRender.
	 */
	private Circle[] trailers;
	private int trailers_i = 0;
	private Circle c_t;

	private void renderBall(Canvas canvas, float tick, Ball ball)
	{

		/* Experimental trailers stuff */
		if (!C.DISABLE_TRAILERS)
		{
			for (int i = 0; i < trailers.length; i++)
			{
				c_t = trailers[i];
				rr = Color.red(c_t.color);
				gg = Color.green(c_t.color);
				bb = Color.blue(c_t.color);

				paint.setColor(Color.argb(75, rr, gg, bb));
				canvas.drawCircle((c_t.x - tick) * BRICK_SIZE,
						(laneRect.height() - c_t.y * BRICK_SIZE), c_t.r
								* BRICK_SIZE, paint);
			}
		}

		trailers[trailers_i].set(ball.x, ball.y, ball.r * 0.75f,
				ball.getColor());
		trailers_i = (trailers_i + 1) % trailers.length;

		/* Draw ball */
		x1 = (int) ((ball.x - tick) * BRICK_SIZE);
		y1 = (int) (ball.y * BRICK_SIZE);
		paint.setColor(ball.getColor());
		canvas.drawCircle((float) x1, (float) (laneRect.height() - y1),
				(float) ball.r * BRICK_SIZE, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawCircle((float) x1, (float) (laneRect.height() - y1),
				(float) ball.r * BRICK_SIZE, paint);
		paint.setStyle(Paint.Style.FILL);

	}

	private Path path = new Path();
	private Button b;
	private int b_i;

	public void renderButtons(Canvas canvas, Button[] buttons)
	{

		// paint.setColor(Color.BLACK);
		// canvas.drawRect(buttonRect, paint);
		for (b_i = 0; b_i < buttons.length; b_i++)
		{
			b = buttons[b_i];
			if (b.getPressed())
			{
				paint.setColor(b.getColor());
				path.reset();
				path.addRoundRect(b.getRect(), 50f, 50f, Direction.CCW);
				canvas.drawPath(path, paint);

			} else
			{
				paint.setColor(Color.DKGRAY);
				path.reset();
				path.addRoundRect(b.getRect(), 50f, 50f, Direction.CCW);
				canvas.drawPath(path, paint);
				paint.setColor(b.getColor());
				path.reset();
				path.addRoundRect(b.getRect(), 50f, 50f, Direction.CCW);
				canvas.drawPath(path, paint);
				// Log.d(TAG, b.getRect().toString());
			}
		}
	}

	/*
	 * For editor.
	 */

	public void renderEditor(Canvas canvas, Level level, Button[] buttons,
			float tick)
	{
		paint.setColor(Color.BLACK);
		canvas.drawRect(buttonRect, paint);
		renderEditableLevel(canvas, level, tick);
		renderButtons(canvas, buttons);
	}

	private void renderEditableLevel(Canvas canvas, Level level, float tick)
	{
		/*
		 * just for the editor. Lot's of copy and paste here probably could have
		 * done better, but oh wells.
		 */
		// Log.d(TAG, "render");
		paint.setColor(Color.WHITE);
		canvas.drawRect(screenRect, paint);

		int grid_i = (int) Math.floor(tick);
		int brick_i = 0;
		float nudge = (tick - grid_i);

		while ((brick_i - 1) * BRICK_SIZE <= laneRect.width())
		{
			for (int j = 0; j < level.getHeight(); j++)
			{

				paint.setColor(C.COLOR[level.getBrick(grid_i, j)]);

				// drawBrick(canvas, nudge, brick_i, j);

				GameModel g = new GameModel(level);
				outlines = g.getOutLineArray(grid_i, j);
				drawBrick(canvas, nudge, brick_i, j,
						C.COLOR[level.getBrick(grid_i, j)], outlines);

			}
			grid_i++;
			brick_i++;
		}

		/*
		 * grid lines:
		 */
		if (SHOW_GRID)
		{
			int gx;
			gx = 0;
			while (gx < laneRect.right)
			{
				paint.setColor(Color.LTGRAY);
				canvas.drawLine(gx, 0, gx, laneRect.bottom, paint);
				canvas.drawLine(0, gx + 2f, laneRect.right, gx + 2f, paint);
				gx += BRICK_SIZE;
			}
			Log.d(TAG, "drew grid lines");
		}
	}

	public Rect getButtonRect()
	{
		return buttonRect;
	}

	public int[] getGridCoords(float x, float y, float tick)
	{
		if (!screenRect.contains((int) x, (int) y))
			return null;
		return new int[] { (int) (x / BRICK_SIZE) + 15,
				(int) (y / BRICK_SIZE) + 1 };
	}

}
