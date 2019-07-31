package com.tinycold.transmo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinycold.transmo.R;
import com.tinycold.transmo.holder.HRefreshHolder;

public class LFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LAdapter mLAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static final LFragment newInstance() {
        LFragment fragment = new LFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_l, container, false);

        mRecyclerView = view.findViewById(R.id.l_recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLAdapter = new LAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mLAdapter);

        return view;
    }

    private final class LAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            switch (viewType) {
                case 0: {
                    holder = new HRefreshHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_h, parent, false));
                    break;
                }
                default: {
                    holder = new LHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_l, parent, false));
                    break;
                }
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 100;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private final class LHolder extends RecyclerView.ViewHolder {

        public LHolder(@NonNull View itemView) {
            super(itemView);
        }

    }


}
