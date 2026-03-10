package com.prm392_sp26.se182138_lab15.api;

import android.util.Log;

import com.prm392_sp26.se182138_lab15.constant.AppInfo;
import com.prm392_sp26.se182138_lab15.helper.HMacUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrder {
    private static final String TAG = "CreateOrder";
    private final OkHttpClient client = new OkHttpClient();

    public JSONObject createOrder(String amount) throws Exception {
        String appUser = AppInfo.APP_USER;
        long appTime = System.currentTimeMillis();
        int randomId = new Random().nextInt(1_000_000);
        String appTransId = getCurrentTimeString("yyMMdd") + "_" + randomId;
        String embedData = new JSONObject().toString();
        JSONArray itemArray = new JSONArray();
        itemArray.put(new JSONObject());
        String items = itemArray.toString();

        String data = AppInfo.APP_ID + "|" + appTransId + "|" + appUser + "|" + amount + "|"
                + appTime + "|" + embedData + "|" + items;
        String mac = HMacUtil.hmacSHA256(data, AppInfo.KEY1);
        Log.d(TAG, "hmac_input=" + data);
        Log.d(TAG, "mac=" + mac);

        RequestBody body = new FormBody.Builder()
                .add("app_id", String.valueOf(AppInfo.APP_ID))
                .add("app_user", appUser)
                .add("app_time", String.valueOf(appTime))
                .add("amount", amount)
                .add("app_trans_id", appTransId)
                .add("embed_data", embedData)
                .add("item", items)
                .add("description", appUser + " - Thanh toan cho don hang #" + randomId)
                .add("bank_code", "zalopayapp")
                .add("mac", mac)
                .build();

        Request request = new Request.Builder()
                .url(AppInfo.URL_CREATE_ORDER)
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

    private static String getCurrentTimeString(String format) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTimeInMillis());
    }
}
