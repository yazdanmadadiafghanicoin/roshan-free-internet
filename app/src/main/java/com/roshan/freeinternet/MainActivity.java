
package com.roshan.freeinternet;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.*;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    TextView status;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(28, 30, 28, 30);
        layout.setBackgroundColor(Color.rgb(8, 20, 38));

        TextView title = new TextView(this);
        title.setText("ROSHAN FREE INTERNET");
        title.setTextColor(Color.WHITE);
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        status = new TextView(this);
        status.setText("وضعیت اتصال بررسی نشده است");
        status.setTextColor(Color.WHITE);
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1, 160);
        layout.addView(status, p);

        Button check = new Button(this);
        check.setText("بررسی اتصال اینترنت");
        layout.addView(check);

        check.setOnClickListener(v -> checkNetwork());

        setContentView(layout);
    }

    private void checkNetwork() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        Network network = cm.getActiveNetwork();
        NetworkCapabilities caps =
            cm.getNetworkCapabilities(network);

        if (caps != null &&
            caps.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            status.setText("شبکه موجود است؛ اتصال واقعی اینترنت هنوز تأیید نشده است.");
        } else {
            status.setText("اینترنت در دسترس نیست.");
        }
    }
}
