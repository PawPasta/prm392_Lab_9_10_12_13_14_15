package com.prm392_sp26.se182138_lab15;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.prm392_sp26.se182138_lab15.helper.TransactionDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int UNIT_PRICE = 10000;


    private EditText edtQuantity;

    private TextView txtUnitPrice;
    private ListView listHistory;
    private ArrayAdapter<String> historyAdapter;
    private TransactionDbHelper dbHelper;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        edtQuantity = findViewById(R.id.edtQuantity);

        txtUnitPrice = findViewById(R.id.txtUnitPrice);
        listHistory = findViewById(R.id.listHistory);
        dbHelper = new TransactionDbHelper(this);
        historyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>()
        );
        listHistory.setAdapter(historyAdapter);

        Button btnConfirm = findViewById(R.id.btnConfirm);


        txtUnitPrice.setText(formatCurrency(UNIT_PRICE));


        btnConfirm.setOnClickListener(v -> {

            String qtyStr = edtQuantity.getText().toString().trim();

            if (qtyStr.isEmpty()) {

                Toast.makeText(this, "Vui lòng nhập số lượng chim", Toast.LENGTH_SHORT).show();

                return;

            }

            int qty;

            try {

                qty = Integer.parseInt(qtyStr);

            } catch (NumberFormatException e) {

                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();

                return;

            }

            if (qty <= 0) {

                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();

                return;

            }


            int total = qty * UNIT_PRICE;


            Intent intent = new Intent(MainActivity.this, OrderPaymentActivity.class);

            intent.putExtra("product_name", "Chú chim chích chòe");

            intent.putExtra("quantity", qty);

            intent.putExtra("amount", total);

            startActivity(intent);

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void loadHistory() {
        if (dbHelper == null || historyAdapter == null) {
            return;
        }
        historyAdapter.clear();
        historyAdapter.addAll(dbHelper.getTransactionSummaries());
        historyAdapter.notifyDataSetChanged();
    }


    private String formatCurrency(int value) {

        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));

        return nf.format(value) + " VND";

    }

}
