package com.prm392_sp26.se182138_lab15.integration;

import android.util.Log;

import com.prm392_sp26.se182138_lab15.constant.AppInfo;
import com.prm392_sp26.se182138_lab15.helper.HMacUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentMethod {
    private static final String TAG = "MomoCreateOrder";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public JSONObject createOrder(long amount, String orderId, String orderInfo, String requestId,
                                  String extraData, JSONArray items) throws Exception {
        String rawSignature = "accessKey=" + AppInfo.MOMO_ACCESS_KEY
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + AppInfo.MOMO_IPN_URL
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + AppInfo.MOMO_PARTNER_CODE
                + "&redirectUrl=" + AppInfo.MOMO_REDIRECT_URL
                + "&requestId=" + requestId
                + "&requestType=" + AppInfo.MOMO_REQUEST_TYPE;

        String signature = HMacUtil.hmacSHA256(rawSignature, AppInfo.MOMO_SECRET_KEY);

        JSONObject payload = new JSONObject();
        payload.put("partnerCode", AppInfo.MOMO_PARTNER_CODE);
        payload.put("accessKey", AppInfo.MOMO_ACCESS_KEY);
        payload.put("requestId", requestId);
        payload.put("amount", amount);
        payload.put("orderId", orderId);
        payload.put("orderInfo", orderInfo);
        payload.put("redirectUrl", AppInfo.MOMO_REDIRECT_URL);
        payload.put("ipnUrl", AppInfo.MOMO_IPN_URL);
        payload.put("requestType", AppInfo.MOMO_REQUEST_TYPE);
        payload.put("extraData", extraData);
        payload.put("signature", signature);
        payload.put("lang", "vi");

        if (AppInfo.MOMO_STORE_ID != null && !AppInfo.MOMO_STORE_ID.isEmpty()) {
            payload.put("storeId", AppInfo.MOMO_STORE_ID);
        }
        if (AppInfo.MOMO_STORE_NAME != null && !AppInfo.MOMO_STORE_NAME.isEmpty()) {
            payload.put("storeName", AppInfo.MOMO_STORE_NAME);
        }
        if (items != null) {
            payload.put("items", items);
        }

        RequestBody body = RequestBody.create(payload.toString(), JSON);
        Request request = new Request.Builder()
                .url(AppInfo.MOMO_CREATE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            String responseBody = response.body().string();
            Log.d(TAG, "response=" + responseBody);
            return new JSONObject(responseBody);
        }
    }
}
