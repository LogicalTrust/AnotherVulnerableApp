package org.mmm.anothervulnerableapp;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkActivity extends AppCompatActivity {

    private WebView webView;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        client = new OkHttpClient();
        webView = (WebView) findViewById(R.id.webView);

        ((Button) findViewById(R.id.httpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWebView("");
                new MyTask().execute("http://example.com");
            }
        });
        Button httpsNoCertButton = (Button) findViewById(R.id.httpsNoCertButton);
        ((Button) findViewById(R.id.httpsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute("https://example.com");
            }
        });
        Button certPinningButton = (Button) findViewById(R.id.certPinningButton);
    }

    private void updateWebView(final String html) {
        webView.loadData(html, null, null);
    }

    private static class MyResponse {
        final boolean success;
        final String text;

        public MyResponse(boolean success, String text) {
            this.success = success;
            this.text = text;
        }
    }

    private class MyTask extends AsyncTask<String, Integer, MyResponse> {

        @Override
        protected MyResponse doInBackground(String... strings) {
            try {
                Request request = new Request.Builder().url(strings[0]).build();
                Response response = client.newCall(request).execute();
                String html = response.body().string();
                return new MyResponse(true, html);
            } catch (IOException e) {
                e.printStackTrace();
                return new MyResponse(false, e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(MyResponse response) {
            if (response.success)
                updateWebView(response.text);
            else
                new AlertDialog
                        .Builder(NetworkActivity.this)
                        .setMessage(response.text)
                        .show();
        }
    }
}
