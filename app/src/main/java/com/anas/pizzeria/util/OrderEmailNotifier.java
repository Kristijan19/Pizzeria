package com.anas.pizzeria.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.anas.pizzeria.R;
import com.anas.pizzeria.data.local.entity.OrderEntity;

import java.util.HashMap;
import java.util.Map;

public final class OrderEmailNotifier {

    private OrderEmailNotifier() {}

    public static void sendOrderConfirmation(Context context, OrderEntity order, String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) return;
        if (!EmailJsService.isConfigured(context)) return;

        Map<String, String> params = new HashMap<>();
        params.put("to_email", userEmail);
        params.put("to_name", order.getCustomerName());
        params.put("subject", context.getString(R.string.email_subject_order_received));
        params.put("message", context.getString(
                R.string.email_body_order_received,
                order.getCustomerName(),
                order.getDateTime(),
                order.getCustomerAddress(),
                order.getTotalCost()
        ));

        EmailJsService.send(
                context,
                context.getString(R.string.emailjs_template_order),
                params,
                emailCallback(context)
        );
    }

    public static void sendStatusUpdate(Context context, String customerEmail, String customerName,
                                        String statusCode) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) return;
        if (!EmailJsService.isConfigured(context)) return;

        String subject;
        String message;
        switch (statusCode) {
            case "PREPARING":
                subject = context.getString(R.string.email_subject_status_preparing);
                message = context.getString(R.string.email_body_status_preparing, customerName);
                break;
            case "ON_THE_WAY":
                subject = context.getString(R.string.email_subject_status_on_the_way);
                message = context.getString(R.string.email_body_status_on_the_way, customerName);
                break;
            case "DELIVERED":
                subject = context.getString(R.string.email_subject_status_delivered);
                message = context.getString(R.string.email_body_status_delivered, customerName);
                break;
            default:
                return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("to_email", customerEmail);
        params.put("to_name", customerName != null ? customerName : "");
        params.put("subject", subject);
        params.put("message", message);
        params.put("order_status", statusCode);

        EmailJsService.send(
                context,
                context.getString(R.string.emailjs_template_status),
                params,
                emailCallback(context)
        );
    }

    private static EmailJsService.EmailCallback emailCallback(Context context) {
        Handler main = new Handler(Looper.getMainLooper());
        return new EmailJsService.EmailCallback() {
            @Override
            public void onSuccess() {
                main.post(() -> Toast.makeText(
                        context.getApplicationContext(),
                        R.string.email_sent_ok,
                        Toast.LENGTH_SHORT
                ).show());
            }

            @Override
            public void onError(String message) {
                String shortMsg = message != null ? message : "";
                if (shortMsg.length() > 120) {
                    shortMsg = shortMsg.substring(0, 120) + "...";
                }
                if (shortMsg.contains("403") || shortMsg.contains("private key")) {
                    shortMsg = "Enable Private Key in strings.xml OR disable strict mode in EmailJS Security";
                }
                String finalMsg = shortMsg;
                main.post(() -> Toast.makeText(
                        context.getApplicationContext(),
                        context.getString(R.string.email_sent_failed, finalMsg),
                        Toast.LENGTH_LONG
                ).show());
            }
        };
    }
}
