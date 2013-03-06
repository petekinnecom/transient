package org.petekinnecom.transience_;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Help extends Activity
{

	
		String s = "" +
				"We must safely lead our faithful little ball to the end of each level. " +
				" To do this, we have to abilities at our disposal:" +
				"\n\n" +
				"JUMP: (press button)\n\n" +
				"On Screen Control: Right Button.\n" +
				"Keyboard Control: Any one of the following: U J M I K , O L . (more generally, " +
				"a key on the right side of the keyboard." +
				"\n\n" +
				"SWITCH WORLDS: (hold button)\n\n" +
				"On Screen Control: Left Button.\n" +
				"Keyboard Control: Any one of the following: Q A Z W S X E D C (more generally, " +
				"a key on the left side of the keyboard." +
				"\n\n" +
				"The utility of jumping should be apparent.  Switching worlds, on the other hand, " +
				"may be a bit confusing at first.  While the ball is GREEN, it will be unable " +
				"to pass through green bricks, but will pass through blue bricks.  While the ball is BLUE, " +
				"it will be unable to pass through blue bricks, but will pass through green bricks." +
				"\n\n" +
				"You will need to switch between worlds in order to pass through barriers and/or jump onto " +
				"platforms.  While switching, the ball will briefly become GRAY.  During this short time, it " +
				"will pass through both blue and green.";
	
	
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about);
	        
	        TextView t=(TextView)findViewById(R.id.about_content); 
	        t.setText(s);
	}
}
