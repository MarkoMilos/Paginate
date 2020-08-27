package com.paginate.abslistview;

import android.database.DataSetObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;

import com.paginate.Paginate;

public final class AbsListViewPaginate extends Paginate implements EndScrollListener.Callback {

    private final AbsListView absListView;
    private final Callbacks callbacks;
    private EndScrollListener scrollListener;
    private WrapperAdapter wrapperAdapter;

    AbsListViewPaginate(
            AbsListView absListView,
            Paginate.Callbacks callbacks,
            int loadingTriggerThreshold,
            AbsListView.OnScrollListener onScrollListener,
            boolean addLoadingListItem,
            LoadingListItemCreator loadingListItemCreator
    ) {
        this.absListView = absListView;
        this.callbacks = callbacks;

        // Attach scrolling listener in order to perform end offset check on each scroll event
        scrollListener = new EndScrollListener(this);
        scrollListener.setThreshold(loadingTriggerThreshold);
        scrollListener.setDelegate(onScrollListener);
        absListView.setOnScrollListener(scrollListener);

        if (addLoadingListItem) {
            BaseAdapter adapter;
            if (absListView.getAdapter() instanceof BaseAdapter) {
                adapter = (BaseAdapter) absListView.getAdapter();
            } else if (absListView.getAdapter() instanceof HeaderViewListAdapter) {
                adapter = (BaseAdapter) ((HeaderViewListAdapter) absListView.getAdapter()).getWrappedAdapter();
            } else {
                throw new IllegalStateException("Adapter needs to be subclass of BaseAdapter");
            }

            // Wrap existing adapter with new adapter that will add loading row
            wrapperAdapter = new WrapperAdapter(adapter, loadingListItemCreator);
            adapter.registerDataSetObserver(dataSetObserver);
            ((AdapterView) absListView).setAdapter(wrapperAdapter);
        }
    }

    @Override
    public void setHasMoreDataToLoad(boolean hasMoreDataToLoad) {
        if (wrapperAdapter != null) {
            wrapperAdapter.displayLoadingRow(hasMoreDataToLoad);
        }
    }

    @Override
    public void onEndReached() {
        if (!callbacks.isLoading() && !callbacks.hasLoadedAllItems()) {
            callbacks.onLoadMore();
        }
    }

    @Override
    public void unbind() {
        // Swap back original scroll listener
        absListView.setOnScrollListener(scrollListener.getDelegateScrollListener());

        // Swap back source adapter
        if (absListView.getAdapter() instanceof WrapperAdapter) {
            WrapperAdapter wrapperAdapter = (WrapperAdapter) absListView.getAdapter();
            BaseAdapter adapter = (BaseAdapter) wrapperAdapter.getWrappedAdapter();
            adapter.unregisterDataSetObserver(dataSetObserver);
            ((AdapterView) absListView).setAdapter(adapter);
        }
    }

    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            wrapperAdapter.displayLoadingRow(!callbacks.hasLoadedAllItems());
            wrapperAdapter.notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            wrapperAdapter.displayLoadingRow(!callbacks.hasLoadedAllItems());
            wrapperAdapter.notifyDataSetInvalidated();
        }
    };

    public static class Builder {
        private final AbsListView absListView;
        private final Paginate.Callbacks callbacks;

        private int loadingTriggerThreshold = 5;
        private AbsListView.OnScrollListener onScrollListener;
        private boolean addLoadingListItem = true;
        private LoadingListItemCreator loadingListItemCreator;

        public Builder(AbsListView absListView, Paginate.Callbacks callbacks) {
            this.absListView = absListView;
            this.callbacks = callbacks;
        }

        /**
         * Set the offset from the end of the list at which the load more event needs to be triggered. Default offset
         * if 5.
         *
         * @param threshold number of items from the end of the list.
         * @return {@link com.paginate.abslistview.AbsListViewPaginate.Builder}
         */
        public Builder setLoadingTriggerThreshold(int threshold) {
            this.loadingTriggerThreshold = threshold;
            return this;
        }

        /**
         * Paginate is using OnScrollListener in order to detect when list is scrolled near the end. That means that
         * internal listener is attached on AbsListView. Since AbsListView can have only one OnScrollListener it is
         * needed to use this method to add additional OnScrollListener (as delegate).
         *
         * @param onScrollListener that will be called when list is scrolled.
         * @return {@link com.paginate.abslistview.AbsListViewPaginate.Builder}
         */
        public Builder setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
            this.onScrollListener = onScrollListener;
            return this;
        }

        /**
         * Setup loading row. If loading row is used original adapter set on AbsListView will be wrapped with
         * internal adapter that will add loading row as the last item in the list. Paginate will observer the
         * changes upon original adapter and remove loading row if there is no more data to load. By default loading
         * row will be added.
         *
         * @param addLoadingListItem true if loading row needs to be added, false otherwise.
         * @return {@link com.paginate.abslistview.AbsListViewPaginate.Builder}
         * @see {@link com.paginate.Paginate.Callbacks#hasLoadedAllItems()}
         * @see {@link com.paginate.abslistview.AbsListViewPaginate.Builder#setLoadingListItemCreator(LoadingListItemCreator)}
         */
        public Builder addLoadingListItem(boolean addLoadingListItem) {
            this.addLoadingListItem = addLoadingListItem;
            return this;
        }

        /**
         * Set custom loading list item creator. If no creator is set default one will be used.
         *
         * @param loadingListItemCreator Creator that will ne called for inflating and binding loading list item.
         * @return {@link com.paginate.abslistview.AbsListViewPaginate.Builder}
         */
        public Builder setLoadingListItemCreator(LoadingListItemCreator loadingListItemCreator) {
            this.loadingListItemCreator = loadingListItemCreator;
            return this;
        }

        /**
         * Create pagination functionality upon AbsListView.
         *
         * @return {@link Paginate} instance.
         */
        public Paginate build() {
            if (absListView.getAdapter() == null) {
                throw new IllegalStateException("Adapter needs to be set!");
            }

            if (loadingListItemCreator == null) {
                loadingListItemCreator = LoadingListItemCreator.DEFAULT;
            }

            return new AbsListViewPaginate(absListView, callbacks, loadingTriggerThreshold, onScrollListener,
                    addLoadingListItem, loadingListItemCreator
            );
        }
    }

}