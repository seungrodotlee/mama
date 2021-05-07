package com.seungrodotlee.mama.util;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TypeWriter {
    private TextView target;
    private String text;
    private long typeSpeed;
    private Handler handler;

    public TypeWriter(TextView target, String text, long typeSpeed) {
        this.target = target;
        this.text = text;
        this.typeSpeed = typeSpeed;

        handler = new Handler();
    }

    public TypeWriter(TextView target, String text) {
        this.target = target;
        this.text = text;
        this.typeSpeed = 100;

        handler = new Handler();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(final String newText, int delay) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                text = newText;
            }
        };

        handler.postDelayed(runnable, delay);
    }

    public void setTarget(final TextView newTarget, int delay) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                target = newTarget;
            }
        };

        handler.postDelayed(runnable, delay);
    }

    public long getTypeSpeed() {
        return typeSpeed;
    }

    public void setTypeSpeed(int typeSpeed) {
        this.typeSpeed = typeSpeed;
    }

    public int start(long delay) {
        final int[] count = {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                target.setText(text.substring(0, count[0]));
                count[0]++;

                if(count[0] <= text.length()) {
                    handler.postDelayed(this, typeSpeed);
                }
            }
        };

        handler.postDelayed(runnable, delay);

        Log.d(TAG, "duration = " + (typeSpeed * text.length()) + delay);
        return (int) ((typeSpeed * text.length()) + delay);
    }

    public int reverse(final int delay) {
        final int[] count = {0};
        count[0] = text.length() - 1;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                target.setText(text.substring(0, count[0]));
                count[0]--;

                if(count[0] >= 0) {
                    handler.postDelayed(this, typeSpeed);
                }
            }
        };

        handler.postDelayed(runnable, delay);

        return (int) ((typeSpeed * text.length()) + delay);
    }
}
