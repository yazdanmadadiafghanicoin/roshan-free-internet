
package com.roshan.freeinternet;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.net.*;
import android.telephony.TelephonyManager;
import android.os.Build;
import android.view.Gravity;
import android.widget.*;
import android.graphics.Color;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {

    TextView status, network, dns, vpn;
    LinearLayout layout;
    ExecutorService executor =
        Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(25, 30, 25, 30);
        layout.setBackgroundColor(Color.rgb(8,20,38));

        addText("ROSHAN FREE INTERNET 2.0", 23);
        status = addText("آماده بررسی اتصال", 19);
        network = addText("شبکه: بررسی نشده", 17);
        dns = addText("DNS: بررسی نشده", 17);
        vpn = addText("VPN: بررسی نشده", 17);

        Button check = new Button(this);
        check.setText("بررسی کامل اینترنت");
        layout.addView(check);

        check.setOnClickListener(v -> checkAll());

        Button settings = new Button(this);
        settings.setText("تنظیمات VPN گوشی");
        layout.addView(settings);
        settings.setOnClickListener(v -> {
            try {
                startActivity(new android.content.Intent(
                    android.provider.Settings.ACTION_VPN_SETTINGS));
            } catch (Exception e) {
                status.setText("تنظیمات VPN در دسترس نیست");
            }
        });

        setContentView(layout);
    }

    TextView addText(String text, int size) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setTextSize(size);
        t.setGravity(Gravity.CENTER);
        t.setPadding(5, 16, 5, 16);
        layout.addView(t);
        return t;
    }

    private void checkAll() {
        status.setText("در حال بررسی...");
        network.setText("شبکه: در حال بررسی");
        dns.setText("DNS: در حال بررسی");
        vpn.setText("VPN: در حال بررسی");

        ConnectivityManager cm =
            (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);

        Network active = cm.getActiveNetwork();
        NetworkCapabilities caps =
            active == null ? null :
            cm.getNetworkCapabilities(active);

        if (caps == null) {
            network.setText("شبکه: قطع است");
            vpn.setText("VPN: نامشخص");
            status.setText("شبکه در دسترس نیست");
            return;
        }

        String type = "نامشخص";

        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            type = "Wi-Fi";
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            type = "شبکه موبایل";
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            type = "VPN";
        }

        network.setText("شبکه: " + type);

        boolean connectedVPN =
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);

        vpn.setText(connectedVPN ?
            "VPN: متصل" : "VPN: متصل نیست");

        executor.execute(() -> {
            boolean dnsOK = false;
            boolean internetOK = false;

            try {
                InetAddress address =
                    InetAddress.getByName("example.com");
                dnsOK = address != null;
            } catch (Exception e) {}

            try {
                URL url = new URL(
                    "https://connectivitycheck.gstatic.com/generate_204");

                HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(7000);
                conn.setReadTimeout(7000);
                conn.setRequestMethod("GET");

                internetOK =
                    conn.getResponseCode() == 204;

                conn.disconnect();

            } catch (Exception e) {}

            final boolean d = dnsOK;
            final boolean i = internetOK;

            runOnUiThread(() -> {
                dns.setText(d ?
                    "DNS: موفق" : "DNS: ناموفق");

                status.setText(i ?
                    "اینترنت واقعی وصل است" :
                    "اتصال اینترنت تأیید نشد");

                status.setTextColor(i ?
                    Color.GREEN : Color.RED);
            });
        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
