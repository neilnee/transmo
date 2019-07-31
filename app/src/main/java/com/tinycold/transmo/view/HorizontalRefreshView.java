package com.tinycold.transmo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinycold.transmo.R;

public class HorizontalRefreshView extends ViewGroup {

    public interface IHorizontalRefreshListener {
        void onLoadMore();
    }

    private final int mMoreViewID = 11;
    private MoreView mMoreView;
    private RecyclerView mInnerChild = null;
    private float mPosPreX;
    private float mPosTouchX;
    @SuppressWarnings("FieldCanBeLocal")
    private float mDisScroll;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mMaxMoreWidth = 0;
    private int mGap = 0;
    private int mScreenWidth = 1080;
    private boolean mLoadMore = false;
    private IHorizontalRefreshListener mListener = null;

    public HorizontalRefreshView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public HorizontalRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalRefreshView);
            mGap = a.getDimensionPixelSize(R.styleable.HorizontalRefreshView_inner_gap, 0);
            mMaxMoreWidth = a.getDimensionPixelSize(R.styleable.HorizontalRefreshView_max_more, 0);
            a.recycle();
        }
        if (getResources() != null) {
            if (mGap == 0) {
                mGap = getResources().getDimensionPixelSize(R.dimen.gap);
            }
            if (mMaxMoreWidth == 0) {
                mMaxMoreWidth = getResources().getDimensionPixelSize(R.dimen.max_more);
            }
            mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        }
        mMoreView = new MoreView(context);
        mMoreView.setId(mMoreViewID);
        addView(mMoreView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heigth = (int) (mScreenWidth / 2.15);
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
                child.layout(mWidth - mGap, getPaddingTop(), mWidth, mHeight);
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
        return direction > 0;
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
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mPosPreX = 0;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
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
                    mLoadMore = mDisScroll >= mMaxMoreWidth;
                    float disScroll = (mLoadMore ? mMaxMoreWidth : mDisScroll) - mGap;
                    mInnerChild.layout((int)-disScroll <= 0 ? (int)-disScroll : 0, 0, (int) (mWidth - disScroll), mHeight);
                    mMoreView.layout((int) (mWidth - mGap - disScroll), getPaddingTop(), mWidth, mHeight);
                    mMoreView.showLoadMord(mLoadMore);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mMoreView != null && mInnerChild != null) {
                    mInnerChild.layout(0, 0, mWidth, mHeight);
                    mMoreView.layout(mWidth - mGap, getPaddingTop(), mWidth, mHeight);
                    if (mInnerChild.getLayoutManager() != null) {
                        mInnerChild.smoothScrollToPosition(mInnerChild.getLayoutManager().getItemCount() - 1);
                    }
                    if (mLoadMore) {
                        if (mListener != null) {
                            mListener.onLoadMore();
                        }
                    }
                }
                mPosTouchX = 0;
                mDisScroll = 0;
                mLoadMore = false;
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    public void setListener(IHorizontalRefreshListener lis) {
        mListener = lis;
    }

    public void carouselNext() {
        if (mInnerChild != null && mInnerChild.getLayoutManager() != null
                && mInnerChild.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) mInnerChild.getLayoutManager();
            int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
            pos = pos >= layoutManager.getItemCount() - 1 ? 0 : pos + 1;
            mInnerChild.smoothScrollToPosition(pos);
        }
    }

    private final class MoreView extends FrameLayout {

        private TextView mMoreText;

        public MoreView(@NonNull Context context) {
            super(context);
            initView(context);
        }

        public MoreView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initView(context);
        }

        private void initView(@NonNull Context context) {
            View root = LayoutInflater.from(context).inflate(R.layout.moreview, this);
            mMoreText = root.findViewById(R.id.more_textview);
        }

        public void showLoadMord(boolean show) {
            if (mMoreText != null) {
                mMoreText.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        }

    }

}
