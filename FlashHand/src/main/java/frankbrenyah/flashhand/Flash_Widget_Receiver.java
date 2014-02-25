package frankbrenyah.flashhand;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RemoteViews;

public class Flash_Widget_Receiver extends BroadcastReceiver {
    boolean isFlashOn;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    @Override
    public void onReceive(Context context, Intent intent){

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                                                    R.layout.flashlight_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(
                new ComponentName(context, Flash_Widget_Provider.class), remoteViews);

        if(isFlashOn){
            if(mCamera != null){
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                remoteViews.setImageViewResource(R.id.widgetButton, R.drawable.button_normal_round);
                isFlashOn = false;
            }
        }else{
            //if(mCamera==null){
                try{
                    mCamera = Camera.open();
                }catch (Exception e){ //exception catch because we only want the LED
                    Log.e("Handy Flashlight", "Failed to open Camera");
                }
                mParameters = mCamera.getParameters();
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(mParameters);
                mCamera.startPreview();
                isFlashOn = true;
                remoteViews.setImageViewResource(R.id.widgetButton, R.drawable.button_touch_round);
            }
    //    }
    }
}
