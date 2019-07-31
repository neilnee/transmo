package com.tinycold.transmo.holder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tinycold.transmo.R;
import com.tinycold.transmo.view.HorizontalRefreshView;

public class HRefreshHolder extends RecyclerView.ViewHolder {

    private HorizontalRefreshView mRefreshView;
    private RecyclerView mRecyclerView;
    private HRefreshAdapter mRefreshAdapter;
    private LinearLayoutManager mLayoutManager;

    private final HorizontalRefreshView.IHorizontalRefreshListener mRefreshListener = new HorizontalRefreshView.IHorizontalRefreshListener() {
        @Override
        public void onLoadMore() {
            Log.e("transmo", "loadmore");
        }
    };

    public HRefreshHolder(@NonNull View itemView) {
        super(itemView);
        mRefreshView = itemView.findViewById(R.id.h_refreshview);
        mRecyclerView = itemView.findViewById(R.id.h_recyclerview);
        mRefreshView.setListener(mRefreshListener);
        mLayoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRefreshAdapter = new HRefreshAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRefreshAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
    }

    private final class HRefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            switch (viewType) {
                default: {
                    holder = new HRefreshItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_i, parent, false));
                    break;
                }
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HRefreshItemHolder) {
                ((HRefreshItemHolder) holder).setText(position);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }

    private final class HRefreshItemHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        HRefreshItemHolder(@NonNull View itemView) {
            super(itemView);
            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int gap = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.gap);
            int width = widthPixels - itemView.getPaddingLeft() - itemView.getPaddingRight() - gap * 2;
            itemView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
            mTextView = itemView.findViewById(R.id.more_textview);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRefreshView != null) {
                        mRefreshView.carouselNext();
                    }
                }
            });
        }

        void setText(int pos) {
            mTextView.setText("Index " + pos);
        }

    }

}
