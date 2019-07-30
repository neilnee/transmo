package com.tinycold.transmo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tinycold.transmo.R;

public class HorizontalRefreshView extends ViewGroup {

    private final int mMoreViewID = 11;
    private HorizontalMoreView mMoreView;
    private RecyclerView mInnerChild = null;
    private float mPosPreX;
    private float mPosTouchX;
    private float mDisScroll;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mMaxMoreWidth = 144;

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
        mMaxMoreWidth = getResources().getDimensionPixelSize(R.dimen.max_more);
        addView(mMoreView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heigth = (int) (widthPixels / 2.15);
        if (mMoreView != null) {
            mMoreView.measure(
                    MeasureSpec.makeMeasureSpec(mMaxMoreWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(heigth, MeasureSpec.EXACTLY));
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heigth, MeasureSpec.EXACTLY));
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
                int gap = getResources().getDimensionPixelSize(R.dimen.gap);
                child.layout(mWidth - gap, getPaddingTop(), mWidth, mHeight);
            } else {
                if (child instanceof RecyclerView) {
                    mInnerChild = (RecyclerView) child;
                    mInnerChild.layout(left, top, right, bottom);
                }
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
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPosPreX - ev.getX() > 0) {
                    if (mInnerChild != null && !mInnerChild.canScrollHorizontally(1)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mPosPreX = 0;
                getParent().requestDisallowInterceptTouchEvent(false);
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
                if (mMoreView != null && mInnerChild != null) {
                    int gap = getResources().getDimensionPixelSize(R.dimen.gap);
                    float disScroll = (mDisScroll > mMaxMoreWidth ? mMaxMoreWidth : mDisScroll) - gap;
                    mInnerChild.layout((int)-disScroll, 0, (int) (mWidth - disScroll), mHeight);
                    mMoreView.layout((int) (mWidth - gap - disScroll), getPaddingTop(), mWidth, mHeight);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mPosTouchX = 0;
                if (mMoreView != null && mInnerChild != null) {
                    int gap = getResources().getDimensionPixelSize(R.dimen.gap);
                    mInnerChild.layout(0, 0, mWidth, mHeight);
                    mMoreView.layout(mWidth - gap, getPaddingTop(), mWidth, mHeight);
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

}
