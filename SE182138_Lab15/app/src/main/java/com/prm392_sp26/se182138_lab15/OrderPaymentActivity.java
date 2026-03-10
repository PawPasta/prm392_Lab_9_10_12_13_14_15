package com.prm392_sp26.se182138_lab15;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prm392_sp26.se182138_lab15.api.CreateOrder;
import com.prm392_sp26.se182138_lab15.integration.PaymentMethod;
import com.prm392_sp26.se182138_lab15.constant.AppInfo;

import org.json.JSONObject;
import org.json.JSONArray;

import java.nio.charset.StandardCharsets;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPaymentActivity extends AppCompatActivity {

    private static final String TAG = "OrderPayment";

    private Button btnPayZalo;
    private Button btnPayMoMo;

    private TextView txtProductName;

    private TextView txtQuantityValue;

    private TextView txtTotalValue;

    private String amountStr = null;
    private String currentProductName = "";
    private int currentQuantity = 0;
    private int currentAmount = 0;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll()

                .build();

        StrictMode.setThreadPolicy(policy);


        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_order_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;

        });


        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);


        if (getIntent() != null && getIntent().getData() != null) {

            ZaloPaySDK.getInstance().onResult(getIntent());

        }


        btnPayZalo = findViewById(R.id.btnPayZalo);
        btnPayMoMo = findViewById(R.id.btnPayMoMo);

        txtProductName = findViewById(R.id.txtProductName);

        txtQuantityValue = findViewById(R.id.txtQuantityValue);

        txtTotalValue = findViewById(R.id.txtTotalValue);


        Intent srcIntent = getIntent();

        int quantity = srcIntent.getIntExtra("quantity", 0);

        int amount = srcIntent.getIntExtra("amount", 0);

        String productName = srcIntent.getStringExtra("product_name");

        if (productName == null || productName.isEmpty()) {

            productName = "Chú chim chích chòe";

        }

        if (quantity <= 0) {

            quantity = 0;

        }


        amountStr = amount > 0 ? String.valueOf(amount) : null;
        currentProductName = productName;
        currentQuantity = quantity;
        currentAmount = amount;


        txtProductName.setText(productName);

        txtQuantityValue.setText(String.valueOf(quantity));

        txtTotalValue.setText(formatCurrency(amount));


        btnPayZalo.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                CreateOrder orderApi = new CreateOrder();

                try {

                    if (amountStr == null) {

                        Toast.makeText(OrderPaymentActivity.this, "Vui lòng quay lại và nhập số lượng trước khi thanh toán", Toast.LENGTH_SHORT).show();

                        return;

                    }

                    JSONObject data = orderApi.createOrder(amountStr);

                    if (data == null) {

                        Toast.makeText(OrderPaymentActivity.this, "Không tạo được đơn hàng (không có phản hồi)", Toast.LENGTH_SHORT).show();

                        return;

                    }

                    String returnCode = data.optString("returncode", data.optString("return_code", "-1"));

                    if ("1".equals(returnCode)) {

                        String token = data.optString("zptranstoken", data.optString("zp_trans_token", ""));

                        if (token.isEmpty()) {

                            Toast.makeText(OrderPaymentActivity.this, "Không lấy được zp_trans_token", Toast.LENGTH_SHORT).show();

                            return;

                        }


                        ZaloPaySDK.getInstance().payOrder(OrderPaymentActivity.this, token, "demozpdk://app",

                                new PayOrderListener() {

                                    @Override

                                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {

                                        openResultScreen("Thanh toán thành công");

                                    }


                                    @Override

                                    public void onPaymentCanceled(String zpTransToken, String appTransID) {

                                        openResultScreen("Thanh toán bị hủy");

                                    }


                                    @Override

                                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {

                                        openResultScreen("Thanh toán thất bại");

                                    }

                                });

                    } else {

                        String returnMessage = data.optString("returnmessage",
                                data.optString("return_message", "Khong ro nguyen nhan"));
                        String subReturnCode = data.optString("subreturncode",
                                data.optString("sub_return_code", ""));
                        String subReturnMessage = data.optString("subreturnmessage",
                                data.optString("sub_return_message", ""));
                        Log.d(TAG, "create_order_failed=" + data);
                        StringBuilder detail = new StringBuilder();
                        detail.append("Tao don hang that bai: ").append(returnCode)
                                .append(" - ").append(returnMessage);
                        if (!subReturnCode.isEmpty() || !subReturnMessage.isEmpty()) {
                            detail.append(" (")
                                    .append(subReturnCode.isEmpty() ? "?" : subReturnCode);
                            if (!subReturnMessage.isEmpty()) {
                                detail.append(": ").append(subReturnMessage);
                            }
                            detail.append(")");
                        }
                        Toast.makeText(OrderPaymentActivity.this, detail.toString(), Toast.LENGTH_LONG).show();

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    Log.d(TAG, "create_order_exception", e);
                    String error = e.getMessage();
                    if (error == null || error.isEmpty()) {
                        error = e.getClass().getSimpleName();
                    }
                    Toast.makeText(OrderPaymentActivity.this, "Loi tao don hang: " + error, Toast.LENGTH_LONG).show();

                }

            }

        });

        btnPayMoMo.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                requestMoMoPayment();

            }

        });

    }


    @Override

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        ZaloPaySDK.getInstance().onResult(intent);

    }


    private String formatCurrency(int value) {

        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.forLanguageTag("vi-VN"));

        return nf.format(value) + " VND";

    }


    private void openResultScreen(String message) {

        Intent intent = new Intent(OrderPaymentActivity.this, PaymentNotificationActivity.class);

        intent.putExtra("message", message);

        startActivity(intent);

    }


    private void requestMoMoPayment() {

        if (amountStr == null || currentAmount <= 0) {

            Toast.makeText(OrderPaymentActivity.this, "Vui long quay lai va nhap so luong truoc khi thanh toan", Toast.LENGTH_SHORT).show();

            return;

        }

        String orderId = "order_" + System.currentTimeMillis();
        String requestId = "req_" + System.currentTimeMillis();
        String orderInfo = "Thanh toan don hang " + orderId;

        try {
            JSONObject extraJson = new JSONObject();
            extraJson.put("product_name", currentProductName);
            extraJson.put("quantity", currentQuantity);
            String extraData = android.util.Base64.encodeToString(
                    extraJson.toString().getBytes(StandardCharsets.UTF_8),
                    android.util.Base64.NO_WRAP
            );

            JSONArray items = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("id", "sku_" + orderId);
            item.put("name", currentProductName);
            item.put("price", currentQuantity > 0 ? (currentAmount / currentQuantity) : currentAmount);
            item.put("currency", "VND");
            item.put("quantity", currentQuantity > 0 ? currentQuantity : 1);
            item.put("totalPrice", currentAmount);
            items.put(item);

            PaymentMethod orderApi = new PaymentMethod();
            JSONObject data = orderApi.createOrder(currentAmount, orderId, orderInfo, requestId, extraData, items);

            if (data == null) {
                Toast.makeText(OrderPaymentActivity.this, "MoMo: Khong nhan duoc phan hoi", Toast.LENGTH_SHORT).show();
                return;
            }

            int resultCode = data.optInt("resultCode", -1);
            if (resultCode == 0) {
                String deeplink = data.optString("deeplink", "");
                String payUrl = data.optString("payUrl", "");
                String targetUrl = !deeplink.isEmpty() ? deeplink : payUrl;
                if (targetUrl.isEmpty()) {
                    Toast.makeText(OrderPaymentActivity.this, "MoMo: Khong co URL thanh toan", Toast.LENGTH_SHORT).show();
                    return;
                }
                openPaymentUrl(targetUrl);
            } else {
                String message = data.optString("message", "MoMo: Tao don hang that bai");
                Toast.makeText(OrderPaymentActivity.this, message + " (" + resultCode + ")", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d(TAG, "momo_create_order_exception", e);
            String error = e.getMessage();
            if (error == null || error.isEmpty()) {
                error = e.getClass().getSimpleName();
            }
            Toast.makeText(OrderPaymentActivity.this, "MoMo: Loi tao don hang: " + error, Toast.LENGTH_LONG).show();
        }

    }


    private void openPaymentUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(OrderPaymentActivity.this, "Khong mo duoc URL thanh toan", Toast.LENGTH_SHORT).show();
        }
    }

}
