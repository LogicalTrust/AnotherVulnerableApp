package org.mmm.anothervulnerableapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class Tapjacking1 extends AppCompatActivity {

    int balance = 1500;
    TextView balanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapjacking1);
        CheckBox protection = (CheckBox)findViewById(R.id.tapjackingCheckbox);
        final Button button1 = (Button) findViewById(R.id.tapjackingButton);
        final Button button2 = (Button) findViewById(R.id.tapjackingButton2);

        protection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Tapjacking1.this.getWindow().getDecorView().getRootView().setFilterTouchesWhenObscured(b);
//                button1.setFilterTouchesWhenObscured(b);
//                button2.setFilterTouchesWhenObscured(b);
            }
        });

        balanceView = (TextView) findViewById(R.id.tapjackingTextView2);
        updateBalance(0);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance(-100);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance(-50);
            }
        });
    }

    private void updateBalance(int change) {
        balance += change;
        balanceView.setText(balance + "");
    }
}
