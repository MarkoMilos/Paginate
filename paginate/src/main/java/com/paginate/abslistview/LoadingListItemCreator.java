package com.paginate.abslistview;

import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paginate.Paginate;
import com.paginate.R;

/** AbsListView creator that will be called to create and bind loading list item */
public interface LoadingListItemCreator {

    /**
     * Create/inflate new loading list item view.
     *
     * @param position loading list item position.
     * @param parent   parent ViewGroup.
     * @return view that will be displayed as loading list item (loading indicator).
     */
    View newView(int position, ViewGroup parent);

    /**
     * Bind the loading list item.
     *
     * @param position loading list item position.
     * @param view     loading list item view.
     */
    void bindView(int position, View view);

    LoadingListItemCreator DEFAULT = new LoadingListItemCreator() {
        Paginate.Callbacks callbacks = null;
        VH vh;
        @Override
        public View newView(int position, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.loading_row, parent, false);
            view.setTag(new VH(view));
            return view;
        }

        @Override
        public void bindView(int position, View view) {
             this.vh = (VH) view.getTag();
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

        }

         class VH {
             ProgressBar progressBar;
             AppCompatButton appCompactButton;
            public VH(View itemView) {
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                appCompactButton = (AppCompatButton) itemView.findViewById(R.id.button);
            }
        }

    };

    void setFailureMode();

    void setLoadingMode();

    void setCallbacks(Paginate.Callbacks callbacks);
}