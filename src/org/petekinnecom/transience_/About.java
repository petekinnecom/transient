package org.petekinnecom.transience_;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        TextView t=(TextView)findViewById(R.id.about_content); 
        if(C.PAID)
        {
        	String s = "Thank you for purchasing this game.  I hope very much that you enjoy it. " +
        			"\n\n" +
        			"Please direct comments/questions to pete.kinnecom@gmail.com " +
        			"\n\n" +
        			"Please direct all hatemail to klasdfafaadsjadfs@adfsadfsfasdfasdfkj.com" +
        			"\n\n" +
        			"Every level included has been beaten at least once, I promise.";
        	t.setText(s);
        }
        else
        {
        	String s = "Thank you for trying this game.  I hope very much that you enjoy it." +
        			"\n\n" +
			"The full version includes more levels and a level editor!  Check out the details here: http://google.com" +
			"\n\n" +
			"Please direct comments/questions to pete.kinnecom@gmail.com " +
			"\n\n" +
			"Please direct all hatemail to klasdfafaadsjadfs@adfsadfsfasdfasdfkj.com" +
			"\n\n" +
			"Every level included has been beaten at least once, I promise.";
        	t.setText(s);
        }
    }
}

