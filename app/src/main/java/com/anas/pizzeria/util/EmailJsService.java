package com.anas.pizzeria.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anas.pizzeria.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class EmailJsService {

    private static final String TAG = "EmailJsService";
    private static final String API_URL = "https://api.emailjs.com/api/v1.0/email/send";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public interface EmailCallback {
        void onSuccess();
        void onError(String message);
    }

    private EmailJsService() {}

    public static boolean isConfigured(Context context) {
        String serviceId = context.getString(R.string.emailjs_service_id);
        String publicKey = context.getString(R.string.emailjs_public_key);
        return !isPlaceholder(serviceId) && !isPlaceholder(publicKey);
    }

    public static void send(Context context, String templateId, Map<String, String> templateParams,
                            EmailCallback callback) {
        if (!isConfigured(context)) {
            notifyError(callback, "EmailJS is not configured");
            return;
        }
        if (isPlaceholder(templateId)) {
            notifyError(callback, "EmailJS template is not configured");
            return;
        }

        String serviceId = context.getString(R.string.emailjs_service_id);
        String publicKey = context.getString(R.string.emailjs_public_key);
        String privateKey = context.getString(R.string.emailjs_private_key);

        EXECUTOR.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("service_id", serviceId);
                body.put("template_id", templateId);
                body.put("user_id", publicKey);
                if (!isPlaceholder(privateKey)) {
                    body.put("accessToken", privateKey);
                }
                JSONObject params = new JSONObject();
                for (Map.Entry<String, String> e : templateParams.entrySet()) {
                    params.put(e.getKey(), e.getValue());
                }
                body.put("template_params", params);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(RequestBody.create(body.toString(), JSON))
                        .build();

                CLIENT.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "EmailJS network error", e);
                        notifyError(callback, e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "EmailJS sent OK");
                            notifySuccess(callback);
                        } else {
                            String err = response.body() != null ? response.body().string() : "HTTP " + response.code();
                            Log.e(TAG, "EmailJS error: " + err);
                            notifyError(callback, err);
                        }
                        response.close();
                    }
                });
            } catch (Exception e) {
                notifyError(callback, e.getMessage());
            }
        });
    }

    private static boolean isPlaceholder(String value) {
        return value == null
                || value.trim().isEmpty()
                || value.startsWith("YOUR_")
                || value.contains("REPLACE");
    }

    private static void notifySuccess(EmailCallback callback) {
        if (callback == null) return;
        MAIN.post(callback::onSuccess);
    }

    private static void notifyError(EmailCallback callback, String message) {
        if (callback == null) return;
        MAIN.post(() -> callback.onError(message != null ? message : "Unknown error"));
    }
}
