package com.tinycold.transmo.holder;

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

public class HHolder extends RecyclerView.ViewHolder {

    private RecyclerView mRecyclerView;
    private IAdapter mIAdapter;
    private LinearLayoutManager mLayoutManager;

    public HHolder(@NonNull View itemView) {
        super(itemView);
        mRecyclerView = itemView.findViewById(R.id.h_recyclerview);
        mLayoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mIAdapter = new IAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mIAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
    }

    private final class IAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            switch (viewType) {
                default: {
                    holder = new IHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_i, parent, false));
                    break;
                }
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof IHolder) {
                ((IHolder) holder).setText(position);
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

    private final class IHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public IHolder(@NonNull View itemView) {
            super(itemView);
            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int gap = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.gap);
            int width = widthPixels - itemView.getPaddingLeft() - itemView.getPaddingRight() - gap * 2;
            itemView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
            mTextView = itemView.findViewById(R.id.i_textview);
        }

        public void setText(int pos) {
            mTextView.setText("Index " + pos);
        }

    }

}
