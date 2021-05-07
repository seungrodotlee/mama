package com.seungrodotlee.mama.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seungrodotlee.mama.MamaActivity;
import com.seungrodotlee.mama.R;

public class FirstTimeActivity extends MamaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome, R.id.welcome_layout);

        Button notFirstBtn = (Button) findViewById(R.id.welcome_not_first_time_btn);
        Button firstBtn = (Button) findViewById(R.id.welcome_first_time_btn);

        notFirstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(MamaActivity.FINISH_WITH_NEW_ACTIVITY);

                Intent intent = new Intent(getApplicationContext(), RegistryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
