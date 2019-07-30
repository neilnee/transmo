package com.tinycold.transmo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tinycold.transmo.R;

public class HorizontalMoreView extends FrameLayout {

    // TODO 1、增加加载状态变化；
    public HorizontalMoreView(Context context) {
        super(context);
        initView(context);
    }

    public HorizontalMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(@NonNull Context context) {
        LayoutInflater.from(context).inflate(R.layout.moreview, this);
    }

}
