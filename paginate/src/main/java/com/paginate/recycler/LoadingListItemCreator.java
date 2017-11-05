package com.paginate.recycler;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paginate.Paginate;
import com.paginate.R;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/** RecyclerView creator that will be called to create and bind loading list item */
public interface LoadingListItemCreator {

    /**
     * Create new loading list item {@link android.support.v7.widget.RecyclerView.ViewHolder}.
     *
     * @param parent   parent ViewGroup.
     * @param viewType type of the loading list item.
     * @return ViewHolder that will be used as loading list item.
     */
    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * Bind the loading list item.
     *
     * @param holder   loading list item ViewHolder.
     * @param position loading list item position.
     */
    void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    LoadingListItemCreator DEFAULT = new LoadingListItemCreator() {
        Paginate.Callbacks callbacks = null;
        VH vh;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_row, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            vh = (VH) holder;
        }

        @Override
        public void setFailureMode() {
            if(vh.progressBar!=null) vh.progressBar.setVisibility(View.GONE);
            if(vh.appCompactButton!=null) {
                vh.appCompactButton.setVisibility(View.VISIBLE);
                vh.appCompactButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callbacks != null)
                            callbacks.onLoadMore();
                    }
                });
            }
        }

        @Override
        public void setLoadingMode() {
            if(vh.progressBar!=null) vh.progressBar.setVisibility(View.VISIBLE);
            if(vh.appCompactButton!=null) vh.appCompactButton.setVisibility(View.GONE);
        }

        @Override
        public void setCallbacks(Paginate.Callbacks callbacks) {
            this.callbacks = callbacks;
        }

        class VH extends RecyclerView.ViewHolder {
            ProgressBar progressBar;
            AppCompatButton appCompactButton;
            public VH(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                appCompactButton = (AppCompatButton) itemView.findViewById(R.id.button);
            }
        }

    };

    void setFailureMode();

    void setLoadingMode();

    void setCallbacks(Paginate.Callbacks callbacks);
}