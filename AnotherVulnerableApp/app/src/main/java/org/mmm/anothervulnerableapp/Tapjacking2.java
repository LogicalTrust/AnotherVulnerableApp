package org.mmm.anothervulnerableapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Tapjacking2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapjacking2);

        final EditText passwordEditText = (EditText) findViewById(R.id.tapjacking2InputPassword);

        Button b = (Button) findViewById(R.id.tapjacking2ButtonLogin);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                if ("".equals(password)) {
                    Toast.makeText(Tapjacking2.this, "Empty password", Toast.LENGTH_SHORT).show();
                } else if ("test123".equals(password)) {
                    Toast.makeText(Tapjacking2.this, "Access granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Tapjacking2.this, "Access denied", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
