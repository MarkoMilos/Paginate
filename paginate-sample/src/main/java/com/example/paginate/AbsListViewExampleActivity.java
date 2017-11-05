package com.example.paginate;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paginate.adapter.PersonAdapter;
import com.example.paginate.data.DataProvider;
import com.paginate.Paginate;
import com.paginate.abslistview.LoadingListItemCreator;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AbsListViewExampleActivity extends BaseActivity implements
        Paginate.Callbacks,
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private PersonAdapter adapter;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setupPagination();
    }

    @Override
    protected void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        handler.removeCallbacks(fakeCallback);
        adapter = new PersonAdapter(this, DataProvider.getRandomData(20));
        loading = false;
        page = 0;

        int layoutId;
        switch (absListViewType) {
            case LIST_VIEW:
                layoutId = R.layout.listview_layout;
                break;
            case GRID_VIEW:
                layoutId = R.layout.gridview_layout;
                break;
            default:
                layoutId = R.layout.listview_layout;
                break;
        }

        getContainer().removeAllViews();
        LayoutInflater.from(this).inflate(layoutId, getContainer(), true);

        AbsListView absListView = (AbsListView) findViewById(R.id.abs_list_view);
        if ((absListView instanceof ListView) && useHeaderAndFooter) {
            ListView listView = (ListView) absListView;
            listView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_view_header, absListView, false));
            listView.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_view_footer, absListView, false));
        }

        absListView.setAdapter(adapter);
        absListView.setOnItemClickListener(this);
        absListView.setOnItemLongClickListener(this);

        paginate = Paginate.with(absListView, this)
                .setOnScrollListener(this)
                .setLoadingTriggerThreshold(threshold)
                .addLoadingListItem(addLoadingRow)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                .build();
    }

    @Override
    public void onLoadMore() {
        Log.d("Paginate", "onLoadMore");
        loading = true;
        // Fake asynchronous loading that will generate page of random data after some delay
        handler.postDelayed(fakeCallback, networkDelay);
    }

    @Override
    public boolean isLoading() {
        return loading; // Return boolean weather data is already loading or not
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page >= totalPages; // If all pages are loaded return true
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d("Paginate", "Scroll state: " + scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d("Paginate", "onScroll");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Item clicked: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Item long clicked: " + position, Toast.LENGTH_SHORT).show();
        return true;
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
        public View newView(int position, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            view.setTag(new VH(view));
            return view;
        }

        @Override
        public void bindView(int position, View view) {
            vh = (VH) view.getTag();
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getCount()));
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

    static class VH {
        TextView tvLoading;
        Button buttonRetry;
        CircularProgressBar circularProgressbar;
        public VH(View itemView) {
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading_text);
            buttonRetry = (Button) itemView.findViewById(R.id.button_retry);
            circularProgressbar = (CircularProgressBar) itemView.findViewById(R.id.circular_progressbar);
        }
    }

}