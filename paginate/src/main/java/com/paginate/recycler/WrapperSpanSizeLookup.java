package com.paginate.recycler;

import androidx.recyclerview.widget.GridLayoutManager;

class WrapperSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final GridLayoutManager.SpanSizeLookup wrappedSpanSizeLookup;
    private final LoadingListItemSpanLookup loadingListItemSpanLookup;
    private final WrapperAdapter wrapperAdapter;

    public WrapperSpanSizeLookup(
            GridLayoutManager.SpanSizeLookup gridSpanSizeLookup,
            LoadingListItemSpanLookup loadingListItemSpanLookup,
            WrapperAdapter wrapperAdapter
    ) {
        this.wrappedSpanSizeLookup = gridSpanSizeLookup;
        this.loadingListItemSpanLookup = loadingListItemSpanLookup;
        this.wrapperAdapter = wrapperAdapter;
    }

    @Override
    public int getSpanSize(int position) {
        if (wrapperAdapter.isLoadingRow(position)) {
            return loadingListItemSpanLookup.getSpanSize();
        } else {
            return wrappedSpanSizeLookup.getSpanSize(position);
        }
    }

    public GridLayoutManager.SpanSizeLookup getWrappedSpanSizeLookup() {
        return wrappedSpanSizeLookup;
    }
}