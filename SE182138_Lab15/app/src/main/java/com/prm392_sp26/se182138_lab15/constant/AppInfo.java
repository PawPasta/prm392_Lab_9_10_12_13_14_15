package com.prm392_sp26.se182138_lab15.constant;

public final class AppInfo {
    // Replace with your ZaloPay sandbox app credentials.
    public static final int APP_ID = 2553;
    public static final String APP_USER = "user123";
    public static final String KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL";
    public static final String KEY2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz";
    public static final String URL_CREATE_ORDER = "https://sb-openapi.zalopay.vn/v2/create";

    // Replace with your MoMo sandbox credentials.
    public static final String MOMO_PARTNER_CODE = "MOMO";
    public static final String MOMO_ACCESS_KEY = "F8BBA842ECF85";
    public static final String MOMO_SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    public static final String MOMO_STORE_ID = "MOMO_STORE_ID";
    public static final String MOMO_STORE_NAME = "MoMo Sandbox";
    public static final String MOMO_REQUEST_TYPE = "captureWallet";
    public static final String MOMO_REDIRECT_URL = "se182138momo://payment";
    public static final String MOMO_IPN_URL = "https://example.com/ipn";
    public static final String MOMO_CREATE_URL = "https://test-payment.momo.vn/v2/gateway/api/create";

    private AppInfo() {
    }
}
