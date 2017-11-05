package com.example.paginate;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.paginate.adapter.RecyclerPersonAdapter;
import com.example.paginate.data.DataProvider;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class RecyclerViewExampleActivity extends BaseActivity implements Paginate.Callbacks {

    private static final int GRID_SPAN = 3;

    private RecyclerView recyclerView;
    private RecyclerPersonAdapter adapter;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater.from(this).inflate(R.layout.recycler_layout, getContainer(), true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        handler = new Handler();
        setupPagination();
    }

    @Override
    protected void setupPagination() {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate.unbind();
        }
        handler.removeCallbacks(fakeCallback);
        adapter = new RecyclerPersonAdapter(DataProvider.getRandomData(20));
        loading = false;
        page = 0;

        int layoutOrientation;
        switch (orientation) {
            case VERTICAL:
                layoutOrientation = OrientationHelper.VERTICAL;
                break;
            case HORIZONTAL:
                layoutOrientation = OrientationHelper.HORIZONTAL;
                break;
            default:
                layoutOrientation = OrientationHelper.VERTICAL;
        }

        RecyclerView.LayoutManager layoutManager = null;
        switch (layoutManagerEnum) {
            case LINEAR:
                layoutManager = new LinearLayoutManager(this, layoutOrientation, false);
                break;
            case GRID:
                layoutManager = new GridLayoutManager(this, GRID_SPAN, layoutOrientation, false);
                break;
            case STAGGERED:
                layoutManager = new StaggeredGridLayoutManager(GRID_SPAN, layoutOrientation);
                break;
        }

        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        } else {
            ((StaggeredGridLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(adapter);

        paginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(threshold)
                .addLoadingListItem(addLoadingRow)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return GRID_SPAN;
                    }
                })
                .build();
    }

    @Override
    public synchronized void onLoadMore() {
        Log.d("Paginate", "onLoadMore");
        loading = true;
        // Fake asynchronous loading that will generate page of random data after some delay
        handler.postDelayed(fakeCallback, networkDelay);
    }

    @Override
    public synchronized boolean isLoading() {
        return loading; // Return boolean weather data is already loading or not
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == totalPages; // If all pages are loaded return true
    }

    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {
            page++;
            adapter.add(DataProvider.getRandomData(itemsPerPage));
            loading = false;
        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        VH vh;
        Paginate.Callbacks callbacks = null;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }

        @Override
        public void setFailureMode() {
            if(vh.circularProgressbar!=null) vh.circularProgressbar.setVisibility(View.GONE);
            if(vh.tvLoading!=null) vh.tvLoading.setText("Loading failed.");
            if(vh.buttonRetry!=null) {
                vh.buttonRetry.setVisibility(View.VISIBLE);
                vh.buttonRetry.setOnClickListener(new View.OnClickListener() {
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
            if(vh.circularProgressbar!=null) vh.circularProgressbar.setVisibility(View.VISIBLE);
            if(vh.buttonRetry!=null) vh.buttonRetry.setVisibility(View.GONE);
            if(vh.tvLoading!=null) vh.tvLoading.setText("Loading...");
        }

        @Override
        public void setCallbacks(Paginate.Callbacks callbacks) {
            this.callbacks = callbacks;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;
        Button buttonRetry;
        CircularProgressBar circularProgressbar;
        public VH(View itemView) {
            super(itemView);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading_text);
            buttonRetry = (Button) itemView.findViewById(R.id.button_retry);
            circularProgressbar = (CircularProgressBar) itemView.findViewById(R.id.circular_progressbar);
        }
    }

}