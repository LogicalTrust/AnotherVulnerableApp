package org.mmm.anothervulnerableapp;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkActivity extends AppCompatActivity {

    private WebView webView;
    private static final String HOST = "example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        webView = (WebView) findViewById(R.id.webView);
        final OkHttpClient client = new OkHttpClient();
        final OkHttpClient clientNoCert = disableCertVerification(new OkHttpClient.Builder()).build();
        final OkHttpClient clientCertPinning = enableCertPinning(new OkHttpClient.Builder()).build();

        ((Button) findViewById(R.id.httpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWebView("");
                new MyTask(client).execute("http://" + HOST);
            }
        });
        ((Button) findViewById(R.id.httpsNoCertButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWebView("");
                new MyTask(clientNoCert).execute("https://" + HOST);
            }
        });
        ((Button) findViewById(R.id.httpsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWebView("");
                new MyTask(client).execute("https://" + HOST);
            }
        });
        ((Button) findViewById(R.id.certPinningButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWebView("");
                new MyTask(clientCertPinning).execute("https://" + HOST);
            }
        });
    }

    private void updateWebView(final String html) {
        webView.loadData(html, null, null);
    }

    private static OkHttpClient.Builder enableCertPinning(OkHttpClient.Builder builder) {
        CertificatePinner certificatePinner = new CertificatePinner
                .Builder()
                .add(HOST, "sha256/xmvvalwaPni4IBbhPzFPPMX6JbHlKqua257FmJsWWto=")
                .build();
        builder.certificatePinner(certificatePinner);
        return builder;
    }

    private static OkHttpClient.Builder disableCertVerification(OkHttpClient.Builder builder) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {

        }
        return builder;
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

        private final OkHttpClient client;

        public MyTask(OkHttpClient client) {
            this.client = client;
        }

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
