package org.mmm.anothervulnerableapp;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebViewActivity extends AppCompatActivity {

    private WebView wv;
    private WebViewClient client;
    private String html;

    private void copyAsset(String name, String destination) {
        try {
            InputStream is = getAssets().open(name);
            File f = new File(destination);
            OutputStream os = new FileOutputStream(f);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFile(String name, String content) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(this.openFileOutput(name, Context.MODE_PRIVATE));
            writer.write(content);
            writer.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private String readAsset(String name) {
        BufferedReader br = null;
        try {
            InputStream is = getAssets().open("index.html");
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        html = readAsset("index.html");
        String secret = readAsset("secret.txt");
        writeFile("index.html", html);
        writeFile("secret.txt", secret);
        copyAsset("pumpkin.png", "pumpkin.png");

        client = new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.e("WVA", view + " " + request + " " + error.getDescription());
                }
            }
        };

        wv = (WebView) findViewById(R.id.webView);
        wv.setWebViewClient(client);
        final WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        ((CheckBox)findViewById(R.id.setAllowFileAccessCheckBox)).setChecked(settings.getAllowFileAccess());
        ((CheckBox)findViewById(R.id.setAllowContentAccessCheckBox)).setChecked(settings.getAllowContentAccess());
        ((CheckBox)findViewById(R.id.setAllowUniversalAccessFromFileURLsCheckBox)).setChecked(settings.getAllowUniversalAccessFromFileURLs());
        ((CheckBox)findViewById(R.id.setAllowFileAccessFromFileURLsCheckBox)).setChecked(settings.getAllowFileAccessFromFileURLs());

        ((Button)findViewById(R.id.webViewLoadHttpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.loadUrl("http://192.168.0.101:8000");
            }
        });
        ((Button)findViewById(R.id.webViewLoadFileButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.loadUrl("file:///data/data/org.mmm.anothervulnerableapp/files/index.html");
            }
        });

        ((Button)findViewById(R.id.webViewLoadDataButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.loadData(html, "text/html", "UTF-8");
            }
        });

        ((Button)findViewById(R.id.loadDataBaseUrlButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            }
        });

        ((CheckBox)findViewById(R.id.setAllowFileAccessCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setAllowFileAccess(isChecked);
            }
        });

        ((CheckBox)findViewById(R.id.setAllowContentAccessCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setAllowContentAccess(isChecked);
            }
        });

        ((CheckBox)findViewById(R.id.setAllowFileAccessFromFileURLsCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setAllowUniversalAccessFromFileURLs(isChecked);
            }
        });

        ((CheckBox)findViewById(R.id.setAllowUniversalAccessFromFileURLsCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setAllowUniversalAccessFromFileURLs(isChecked);
            }
        });
    }
}
