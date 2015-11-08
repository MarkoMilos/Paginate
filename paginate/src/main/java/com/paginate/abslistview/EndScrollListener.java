package com.paginate.abslistview;

import android.widget.AbsListView;

class EndScrollListener implements AbsListView.OnScrollListener {

    public interface Callback {
        void onEndReached();
    }

    private final Callback callback;
    private int visibleThreshold = 5;
    private AbsListView.OnScrollListener delegate;

    public EndScrollListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItemPosition, int visibleItemCount, int totalItemCount) {
        if ((totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + visibleThreshold)) {
            callback.onEndReached();
        }

        if (delegate != null) {
            delegate.onScroll(view, firstVisibleItemPosition, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (delegate != null) {
            delegate.onScrollStateChanged(view, scrollState);
        }
    }

    public void setThreshold(int threshold) {
        this.visibleThreshold = Math.max(0, threshold);
    }

    public void setDelegate(AbsListView.OnScrollListener delegate) {
        this.delegate = delegate;
    }

    public AbsListView.OnScrollListener getDelegateScrollListener() {
        return delegate;
    }

}