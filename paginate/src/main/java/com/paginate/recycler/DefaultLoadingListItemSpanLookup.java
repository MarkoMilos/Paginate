package com.paginate.recycler;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class DefaultLoadingListItemSpanLookup implements LoadingListItemSpanLookup {

    private final int loadingListItemSpan;

    public DefaultLoadingListItemSpanLookup(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            // By default full span will be used for loading list item
            loadingListItemSpan = ((GridLayoutManager) layoutManager).getSpanCount();
        } else {
            loadingListItemSpan = 1;
        }
    }

    @Override
    public int getSpanSize() {
        return loadingListItemSpan;
    }
}