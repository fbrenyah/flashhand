package frankbrenyah.flashhand;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.util.Log;
import android.hardware.Camera;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class Flash_Main_Activity extends Activity {

    //declare variables
    boolean hasFlash;
    ImageView indicatorLED;
    ImageView btnOnOff;
    Camera mCamera;
    Camera.Parameters mParameters;
    boolean isFlashOn;
    boolean isCancel;
    NotificationCompat.Builder notifyBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashlight_main);

        //start auto wake lock
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //check device for flash
        hasFlash = getApplicationContext()
                    .getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        //get camera
        if (!hasFlash){
            //there is no LED
            AlertDialog alert = new AlertDialog.Builder(Flash_Main_Activity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support a flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE,
                    "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //close app
                    finish();
                }
            });
            alert.show();
            return;
        }
        getCamera();

        //define indicator light
        indicatorLED = (ImageView) findViewById(R.id.switch_indicator);
        indicatorLED.setImageResource(R.drawable.flash_indicator_on);
        //define light switch and click action
        btnOnOff = (ImageView) findViewById(R.id.button_normal);
        btnOnOff.setImageResource(R.drawable.button_touch_round);
        //make isFlashOn/!isFlashOn when click, start out isFlashOn
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    turnOffFlash();
                }
                else  turnOnFlash();
            }
        });
    }//end onCreate

    private void getCamera(){ //get the camera parameters
        if(mCamera==null){
           try{
               mCamera = Camera.open();
               mParameters = mCamera.getParameters();
              }catch (Exception e){ //exception catch because we only want the LED
              Log.e(getString(R.string.app_name), "Failed to open Camera");
              e.printStackTrace();
           }
        }
    }
    private void turnOnFlash(){ //turn on the LED light
        if(!isFlashOn)
            if(mCamera==null || mParameters==null)
                return;
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        isFlashOn = true;
        isCancel = false;
        indicatorLED.setImageResource(R.drawable.flash_indicator_on);
        btnOnOff.setImageResource(R.drawable.button_touch_round);
    }
    private void turnOffFlash(){ //turn off the LED light
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParameters);
        mCamera.stopPreview();
        isFlashOn = false;
        isCancel = true;
        indicatorLED.setImageResource(R.drawable.flash_indicator_off);
        btnOnOff.setImageResource(R.drawable.button_normal_round);
    }

    //activity to open when touched
    public void notifyUser(boolean isCancel){
        NotificationManager notifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyId = 1;
        if(isCancel)
            notifyManager.cancel(notifyId);
        else{
            notifyBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.flash_launcher)
                    .setTicker("Light is on!")
                    .setContentTitle("Flashlight")
                    .setContentText("Light is on!")
                    .setOngoing(true)
                    .setAutoCancel(true);
            Intent touchIntent = new Intent(getApplicationContext(), Off_Service.class);
            PendingIntent touchPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            touchIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            notifyBuilder.setContentIntent(touchPendingIntent);
            notifyManager.notify(notifyId, notifyBuilder.build());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == R.id.menu_about){
            //if 'About Handy Flashlight' is selected
            Intent showAboutScreen = new Intent(this, Flash_Menu_Activity.class);
            startActivity(showAboutScreen);
            return true;
        }
        else
            return super.onOptionsItemSelected(menuItem);
    }

    @Override //set options for when app changes state
    protected void onStart(){
        super.onStart();
        if(mCamera != null)
            onResume();
        else{
            getCamera();
            turnOnFlash();
        }
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        onResume();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(isFlashOn)
            turnOnFlash();
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(isFlashOn && !isFinishing())
            turnOnFlash();//to keep light on
        else{
            turnOffFlash();
            mCamera.release();
            mCamera = null;
        }
        notifyUser(isCancel);
    }
}//end of Flash Hand app
