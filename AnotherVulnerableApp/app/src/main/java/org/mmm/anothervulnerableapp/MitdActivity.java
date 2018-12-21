package org.mmm.anothervulnerableapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MitdActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button installButton;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitd);
        progressBar = findViewById(R.id.mitdProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        installButton = (Button) findViewById(R.id.installButton);
        installButton.setEnabled(false);
        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.parse("file://" + file), "application/vnd.android.package-archive");
                MitdActivity.this.startActivityForResult(promptInstall, 100);
            }
        });
        ((Button) findViewById(R.id.downloadButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream is = getAssets().open("app-release.apk");
                    file = new File(getExternalFilesDir(null), "app.apk");
                    DownloadTask task = new DownloadTask(is, file, MitdActivity.this);
                    task.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (file != null) {
            file.delete();
            file = null;
        }
        installButton.setEnabled(false);
    }
}

class DownloadTask extends AsyncTask<String, Integer, Boolean> {

    private final InputStream is;
    private final File file;
    private final MitdActivity activity;

    public DownloadTask(InputStream is, File file, MitdActivity activity) {
        this.is = is;
        this.file = file;
        this.activity = activity;
        System.out.println("asd");
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.progressBar.setVisibility(View.VISIBLE);
        activity.progressBar.setProgress(0);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (Boolean.TRUE.equals(aBoolean)) {
            activity.progressBar.setVisibility(View.INVISIBLE);
            activity.progressBar.setProgress(0);
            activity.installButton.setEnabled(true);
        } else {
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        activity.progressBar.setProgress(values[0]);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        boolean copied = false;
        try {
            Random r = new Random();
            for (int i = 1; i <= 100; i+= r.nextInt(15) + 1) {
                Thread.sleep(r.nextInt(300));
                onProgressUpdate(i);
                if (!copied && r.nextBoolean()) {
                    OutputStream os = new FileOutputStream(file);
                    byte[] data = new byte[is.available()];
                    is.read(data);
                    os.write(data);
                    is.close();
                    os.close();
                    copied = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return copied;
    }
}