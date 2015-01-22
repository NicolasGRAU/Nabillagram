package com.nabillgram.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends ActionBarActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private File file;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_intent_camera);

        Button pivoter = (Button) findViewById(R.id.rotation);
        Button btn = (Button) findViewById(R.id.intentCameraCapture);

        ImageView background = (ImageView) findViewById(R.id.intentCameraPreview);
        ImageView filtre_1 = (ImageView) findViewById(R.id.filtre_1);
        ImageView filtre_2 = (ImageView) findViewById(R.id.filtre_2);
        ImageView filtre_3 = (ImageView) findViewById(R.id.filtre_3);
        ImageView filtre_4 = (ImageView) findViewById(R.id.filtre_4);
        ImageView filtre_5 = (ImageView) findViewById(R.id.filtre_5);

        pivoter.setVisibility(ImageView.INVISIBLE);
        btn.setVisibility(ImageView.VISIBLE);

        background.setVisibility(ImageView.VISIBLE);
        filtre_1.setVisibility(ImageView.INVISIBLE);
        filtre_2.setVisibility(ImageView.INVISIBLE);
        filtre_3.setVisibility(ImageView.INVISIBLE);
        filtre_4.setVisibility(ImageView.INVISIBLE);
        filtre_5.setVisibility(ImageView.INVISIBLE);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(MainActivity.this, IntentActivity.class);
                startActivity(intent);


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
