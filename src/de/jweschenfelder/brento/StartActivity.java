package de.jweschenfelder.brento;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startactivity);

        Button mondayEdit= (Button)findViewById(R.id.startButton);
        mondayEdit.setOnClickListener(new OnClickListener() 
        {   public void onClick(View v) 
            {   
                Intent intent = new Intent(StartActivity.this, OpenGLActivity.class);
                    startActivity(intent);      
                    finish();
            }
        });
    
    }

}
