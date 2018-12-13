package net.cmbt.superweb.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import net.cmbt.superweb.R;

public class SuperWebLayout extends LinearLayout {
    public SuperWebLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        layout(context);
    }

    public SuperWebLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layout(context);
    }

    private void layout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.activity_web, this);
    }

}
