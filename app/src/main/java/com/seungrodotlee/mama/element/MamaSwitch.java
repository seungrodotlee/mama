package com.seungrodotlee.mama.element;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MamaSwitch extends LinearLayout implements View.OnClickListener {
    private Context context;
    private TransitionDrawable drawable;
    private ObjectAnimator toLeft, toRight;
    private LinearLayout layout;
    private ImageView thumb;

    private MamaGlobal global;

    private int dimension;

    boolean checked;

    public MamaSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);

        global = MamaGlobal.getInstance();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.switch_mama, this, true);

        layout = findViewById(R.id.switch_mama_bg);
        thumb = findViewById(R.id.switch_mama_thumb);

        dimension = global.dpToPx(14);

        toLeft = ObjectAnimator.ofFloat(thumb, "x", dimension, 0).setDuration(300);
        toRight = ObjectAnimator.ofFloat(thumb, "x", 0, dimension).setDuration(300);

        layout.setOnClickListener(this);
    }

    public MamaSwitch(Context context) {
        this(context, null);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        drawable = (TransitionDrawable) findViewById(R.id.switch_mama_bg).getBackground();

        if (drawable != null && drawable instanceof TransitionDrawable) {
            drawable.startTransition(0);
        }

            if(checked) {
                thumb.setX(dimension);
                drawable.startTransition(300);
            } else {
                thumb.setX(0);
                drawable.reverseTransition(300);
            }
    }

    public boolean getChecked() {
        return checked;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "clicked");
        checked = !checked;

        if(toLeft.isRunning() || toRight.isRunning()) {
            return;
        }

        if(checked) {
            toRight.start();
            drawable.startTransition(300);
        } else {
            toLeft.start();
            drawable.reverseTransition(300);
        }
    }
}
