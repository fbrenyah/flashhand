package frankbrenyah.flashhand;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import java.io.IOException;

public class Off_Service extends Activity {
    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (mCamera != null){
            try {
                mCamera.unlock();
                mCamera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            finish();
        }
    }
}
