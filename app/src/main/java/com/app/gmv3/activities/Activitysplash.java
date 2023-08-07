package com.app.gmv3.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.gmv3.BuildConfig;
import com.app.gmv3.Config;
import com.app.gmv3.R;

public class Activitysplash extends AppCompatActivity {

    Boolean isCancelled = false;
    private ProgressBar progressBar;
    long id = 0;
    String url = "";
    MyApplication MyApp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_v2);
        MyApp = MyApplication.getInstance();
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }



        progressBar = findViewById(R.id.progressBar);


        ((TextView) findViewById(R.id.app_version)).setText(("v")+BuildConfig.VERSION_NAME);

        if (getIntent().hasExtra("nid")) {
            id = getIntent().getLongExtra("nid", 0);
            url = getIntent().getStringExtra("external_link");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(View.GONE);

                 if (MyApp.getIsLogin()){
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                  }else{
                    startActivity(new Intent(getBaseContext(), ActivityLogin.class));
                  }





              /*  if (!isCancelled) {
                    if (id == 0) {
                        if (url.equals("") || url.equals("no_url")) {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent a = new Intent(getApplicationContext(), Login.class);
                            startActivity(a);

                            Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(b);

                            finish();
                        }
                    } else if (id == 1010101010) {

                        Intent a = new Intent(getApplicationContext(), Login.class);
                        startActivity(a);

                        Intent b = new Intent(getApplicationContext(), ActivityHistory.class);
                        startActivity(b);

                        finish();

                    } else {

                        Intent a = new Intent(getApplicationContext(), Login.class);
                        startActivity(a);

                        Intent b = new Intent(getApplicationContext(), ActivityNotificationDetail.class);
                        b.putExtra("product_id", id);
                        startActivity(b);

                        finish();

                    }
                }*/
              finish();
            }
        }, Config.SPLASH_TIME);

    }

}