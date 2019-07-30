package com.tinycold.transmo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HorizontalRefreshView extends FrameLayout {

    private final int mMoreViewID = 11;
    private HorizontalMoreView mMoreView;
    private View mInnerChild = null;
    private float mPosPreX;
    private float mPosTouchX;
    private float mDisScroll;
    private int mWidth = 0;
    private int mHeight = 0;

    public HorizontalRefreshView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public HorizontalRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(@NonNull Context context) {
        mMoreView = new HorizontalMoreView(context);
        mMoreView.setId(mMoreViewID);
        LayoutParams layoutParams = new LayoutParams(100, LayoutParams.MATCH_PARENT);
        addView(mMoreView, layoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int r = right - left;
        int b = bottom - top;
        mWidth = r;
        mHeight = b;
        int count = getChildCount();
        for (int i=0; i<count; i++) {
            View child = getChildAt(i);
            if (child.getId() == mMoreViewID) {
                child.layout(mWidth, 0, mWidth + 100, mHeight);
            } else {
                mInnerChild = child;
                mInnerChild.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (direction > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosPreX = ev.getX();
            case MotionEvent.ACTION_MOVE:
                if (mPosPreX - ev.getX() > 0) {
                    if (mInnerChild != null && !mInnerChild.canScrollHorizontally(1)) {
                        Log.d("transmo", "向左滑动");
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mPosPreX = 0;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                float x = ev.getX();
                if (mPosTouchX == 0) {
                    mPosTouchX = x;
                    break;
                }
                mDisScroll = mPosTouchX - x;
                Log.d("transmo", "滑动距离: " + mDisScroll);
                if (mMoreView != null && mInnerChild != null) {
                    mInnerChild.layout((int)-mDisScroll, 0, (int) (mWidth - mDisScroll), mHeight);
                    mMoreView.layout((int) (mWidth - mDisScroll), 0, mWidth, mHeight);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mPosTouchX = 0;
                if (mMoreView != null && mInnerChild != null) {
                    Log.d("transmo", "复位: " + mDisScroll);
                    mInnerChild.layout(0,0, mWidth, mHeight);
                    mMoreView.layout(mWidth, 0, mWidth + 100, mHeight);
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }
}
