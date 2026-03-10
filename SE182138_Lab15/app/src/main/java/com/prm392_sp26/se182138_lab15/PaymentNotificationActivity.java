package com.prm392_sp26.se182138_lab15;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentNotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_notification);


        TextView txtResultMessage = findViewById(R.id.txtResultMessage);

        Button btnBackHome = findViewById(R.id.btnBackHome);


        Intent intent = getIntent();

        String message = intent.getStringExtra("message");

        if (message == null || message.isEmpty()) {

            message = "Thanh toán thành công";

        }

        txtResultMessage.setText(message);


        btnBackHome.setOnClickListener(v -> {

            Intent backIntent = new Intent(PaymentNotificationActivity.this, MainActivity.class);

            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(backIntent);

            finish();

        });

    }

}
