package com.example.paint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {
    private ImageView logoTV;
    private ImageView logoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoTV = findViewById(R.id.napislogo);
        logoIV = findViewById(R.id.logo);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        Animation rotacja = AnimationUtils.loadAnimation(this,R.anim.rotacja);
        logoTV.startAnimation(myanim);
        logoIV.startAnimation(rotacja);
        final Intent i = new Intent(this,MainActivity.class);
        Thread timer=new Thread(){
            public void run() {
                try {
                    sleep(3000);
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                }
            }
        };
        timer.start();
    }
}
