package com.prm392_sp26.se182138_lab15;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.prm392_sp26.se182138_lab15.constant.AppInfo;
import com.prm392_sp26.se182138_lab15.helper.HMacUtil;

public class MomoCallbackActivity extends AppCompatActivity {
    private static final String TAG = "MomoCallback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Uri data = intent.getData();
        if (data == null) {
            openResultScreen("MoMo: Khong nhan duoc du lieu");
            finish();
            return;
        }

        String resultCode = getQuery(data, "resultCode");
        String message = getQuery(data, "message");
        String signature = getQuery(data, "signature");

        boolean signatureValid = true;
        if (!signature.isEmpty()) {
            try {
                String rawSignature = "accessKey=" + AppInfo.MOMO_ACCESS_KEY
                        + "&amount=" + getQuery(data, "amount")
                        + "&extraData=" + getQuery(data, "extraData")
                        + "&message=" + message
                        + "&orderId=" + getQuery(data, "orderId")
                        + "&orderInfo=" + getQuery(data, "orderInfo")
                        + "&orderType=" + getQuery(data, "orderType")
                        + "&partnerCode=" + getQuery(data, "partnerCode")
                        + "&payType=" + getQuery(data, "payType")
                        + "&requestId=" + getQuery(data, "requestId")
                        + "&responseTime=" + getQuery(data, "responseTime")
                        + "&resultCode=" + resultCode
                        + "&transId=" + getQuery(data, "transId");
                String expected = HMacUtil.hmacSHA256(rawSignature, AppInfo.MOMO_SECRET_KEY);
                signatureValid = expected.equals(signature);
            } catch (Exception e) {
                Log.d(TAG, "signature_check_error", e);
                signatureValid = false;
            }
        }

        StringBuilder detail = new StringBuilder();
        if ("0".equals(resultCode)) {
            detail.append("MoMo: Thanh toan thanh cong");
        } else {
            detail.append("MoMo: That bai");
            if (!resultCode.isEmpty()) {
                detail.append(" (").append(resultCode).append(")");
            }
        }
        if (message != null && !message.isEmpty()) {
            detail.append(" - ").append(message);
        }
        if (!signatureValid) {
            detail.append(" | Chu ky khong hop le");
        }

        openResultScreen(detail.toString());
        finish();
    }

    private String getQuery(Uri data, String key) {
        String value = data.getQueryParameter(key);
        return value == null ? "" : value;
    }

    private void openResultScreen(String message) {
        Intent intent = new Intent(MomoCallbackActivity.this, PaymentNotificationActivity.class);
        intent.putExtra("message", message);
        startActivity(intent);
    }
}
