package com.nabillgram.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntentCameraActivity extends ActionBarActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private File file;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_camera);
        getIntent().getStringExtra("test");
        Button btn = (Button) findViewById(R.id.intentCameraCapture);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"PhotoTest");
        // create storage directory if not exist
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.d("PhotoTest","Failed to create directory");
                return null;
            }
        }
        // generate file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }else if (type == MEDIA_TYPE_VIDEO){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        }  else {
            return null;
        }

        Log.v("PhotoTest", "storing at "+mediaFile.getPath());
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                final ImageView preview = (ImageView) findViewById(R.id.intentCameraPreview);
                Log.v("PhotoTest", "image exists");
                (new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void...voids){
                        String filepath = file.getAbsolutePath();
                        Bitmap myBitmap = BitmapFactory.decodeFile(filepath);
                        MediaStore.Images.Media.insertImage(getContentResolver(), myBitmap, "PhotoTest", "taken with intent camera");
                        MediaScannerConnection.scanFile(IntentCameraActivity.this, new String[]{filepath}, null, null);
                        final Bitmap scaled = Bitmap.createScaledBitmap(myBitmap,600,600,false);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                preview.setImageBitmap(scaled);
                            }
                        });
                        return null;
                    }
                }).execute();
            }
        }
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
