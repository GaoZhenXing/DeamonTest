package com.jason.deamontest;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
        startService(new Intent(this, JobHandleService.class));

        if (Build.MANUFACTURER.equals("Xiaomi")) {
            Intent intent = new Intent();
            intent.setAction("miui.intent.action.OP_AUTO_START");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }

    }
}
