package com.nabillgram.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IntentActivity extends ActionBarActivity implements SensorEventListener {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private File file;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;

    private ImageView preview;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    // vitesse min pour considerer le mouvement
    private static final int SHAKE_THRESHOLD = 6000;
    // intervalle min entre deux secousses
    private static final int MIN_INTERVAL = 1000;
    // delais max entre deux valeurs pour considerer un meme mouvement
    private static final int MAX_DELAY = 100;

    private static long lastUpdate = 0;
    private static long lastShake = 0;
    private static long lastFiltre = 0;
    private static long iNumber = 0;

    private static float x = 0;
    private static float y = 0;
    private static float z = 0;
    private static float lastX = 0;
    private static float lastY = 0;
    private static float lastZ = 0;

    private long now, timeDiff;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME );



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intent_camera);
        getIntent().getStringExtra("test");

        Button pivoter = (Button) findViewById(R.id.rotation);
        Button btn = (Button) findViewById(R.id.intentCameraCapture);

        ImageView background = (ImageView) findViewById(R.id.intentCameraPreview);
        ImageView filtre_1 = (ImageView) findViewById(R.id.filtre_1);
        ImageView filtre_2 = (ImageView) findViewById(R.id.filtre_2);
        ImageView filtre_3 = (ImageView) findViewById(R.id.filtre_3);
        ImageView filtre_4 = (ImageView) findViewById(R.id.filtre_4);
        ImageView filtre_5 = (ImageView) findViewById(R.id.filtre_5);

        pivoter.setVisibility(ImageView.VISIBLE);
        btn.setVisibility(ImageView.INVISIBLE);

        background.setVisibility(ImageView.INVISIBLE);
        filtre_1.setVisibility(ImageView.INVISIBLE);
        filtre_2.setVisibility(ImageView.INVISIBLE);
        filtre_3.setVisibility(ImageView.INVISIBLE);
        filtre_4.setVisibility(ImageView.INVISIBLE);
        filtre_5.setVisibility(ImageView.INVISIBLE);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(IntentActivity.this, IntentActivity.class);
                startActivity(intent);


            }
        });

        pivoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Matrix matrix = new Matrix();
                matrix.postRotate(180);



                Bitmap bitmap = ((BitmapDrawable)preview.getDrawable()).getBitmap();



                //Bitmap bmap = preview.getDrawingCache();
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, preview.getWidth(), preview.getHeight(), matrix, true);
                preview.setImageBitmap(rotatedBitmap);
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
                preview = (ImageView) findViewById(R.id.imageView);
                Log.v("PhotoTest", "image exists");

                (new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void...voids){
                        String filepath = file.getAbsolutePath();
                        Bitmap myBitmap = BitmapFactory.decodeFile(filepath);

                        int bHeight = preview.getWidth();
                        int bWidth = preview.getHeight();

                        MediaStore.Images.Media.insertImage(getContentResolver(), myBitmap, "PhotoTest", "taken with intent camera");
                        MediaScannerConnection.scanFile(IntentActivity.this, new String[]{filepath}, null, null);
                        final Bitmap scaled = Bitmap.createScaledBitmap(myBitmap,bWidth,bHeight,false);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Matrix matrix = new Matrix();
                                matrix.postRotate(270);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(scaled , 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);


                                preview.setImageBitmap(rotatedBitmap);
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent ) {
        //
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            now = System.currentTimeMillis();

            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            if (lastUpdate == 0) {
                lastUpdate = now;
                lastShake = now;
                lastX = x;
                lastY = y;
                lastZ = z;
            } else {
                timeDiff = now - lastUpdate;
                if (timeDiff > 0 && timeDiff < MAX_DELAY) {
                    speed = Math.abs(x + y + z - lastX - lastY - lastZ)
                            / timeDiff * 10000;
                    if (speed > SHAKE_THRESHOLD) {
                        if (now - lastShake >= MIN_INTERVAL) {
                            Toast.makeText(this, "shake detected ", Toast.LENGTH_SHORT).show();
                            getRandomNumber();
                            lastShake = now;
                            now = 0;
                            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                            //preview.setImageBitmap(bitmap);
                            //storeImage(bitmap);
                        }
                    }
                }
                lastX = x;
                lastY = y;
                lastZ = z;
                lastUpdate = now;
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("fsdfds", "dsdsqdsq");
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d("error",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("error", "Error accessing file: " + e.getMessage());
        }
    }

    private void getRandomNumber() {

        while(iNumber == lastFiltre){
            Random randNumber = new Random();
            iNumber = randNumber.nextInt(5) + 1;
        }


        ImageView filtre_1 = (ImageView) findViewById(R.id.filtre_1);
        ImageView filtre_2 = (ImageView) findViewById(R.id.filtre_2);
        ImageView filtre_3 = (ImageView) findViewById(R.id.filtre_3);
        ImageView filtre_4 = (ImageView) findViewById(R.id.filtre_4);
        ImageView filtre_5 = (ImageView) findViewById(R.id.filtre_5);
        filtre_1.setVisibility(View.INVISIBLE);
        filtre_2.setVisibility(View.INVISIBLE);
        filtre_3.setVisibility(View.INVISIBLE);
        filtre_4.setVisibility(View.INVISIBLE);
        filtre_5.setVisibility(View.INVISIBLE);

        if(iNumber == 1){
            filtre_1.setVisibility(View.VISIBLE);
        }
        else if(iNumber == 2){
            filtre_2.setVisibility(View.VISIBLE);
        }
        else if(iNumber == 3){
            filtre_3.setVisibility(View.VISIBLE);
        }
        else if(iNumber == 4){
            filtre_4.setVisibility(View.VISIBLE);
        }
        else if(iNumber == 5){
            filtre_5.setVisibility(View.VISIBLE);
        }


        lastFiltre = iNumber;
    }
}
