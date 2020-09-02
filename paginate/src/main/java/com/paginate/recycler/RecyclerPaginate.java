package com.paginate.recycler;

import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.paginate.Paginate;

public final class RecyclerPaginate extends Paginate {

    private final RecyclerView recyclerView;
    private final Callbacks callbacks;
    private final int loadingTriggerThreshold;
    private WrapperAdapter wrapperAdapter;
    private WrapperSpanSizeLookup wrapperSpanSizeLookup;

    RecyclerPaginate(
        RecyclerView recyclerView,
        Paginate.Callbacks callbacks,
        int loadingTriggerThreshold,
        boolean addLoadingListItem,
        LoadingListItemCreator loadingListItemCreator,
        LoadingListItemSpanLookup loadingListItemSpanLookup
    ) {
        this.recyclerView = recyclerView;
        this.callbacks = callbacks;
        this.loadingTriggerThreshold = loadingTriggerThreshold;

        // Attach scrolling listener in order to perform end offset check on each scroll event
        recyclerView.addOnScrollListener(mOnScrollListener);

        if (addLoadingListItem) {
            // Wrap existing adapter with new adapter that will add loading row
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assert adapter != null;
            wrapperAdapter = new WrapperAdapter(adapter, loadingListItemCreator);
            adapter.registerAdapterDataObserver(mDataObserver);
            recyclerView.setAdapter(wrapperAdapter);

            // For GridLayoutManager use separate/customisable span lookup for loading row
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                wrapperSpanSizeLookup = new WrapperSpanSizeLookup(
                    ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanSizeLookup(),
                    loadingListItemSpanLookup,
                    wrapperAdapter
                );
                ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(wrapperSpanSizeLookup);
            }
        }

        // Trigger initial check since adapter might not have any items initially so no scrolling events upon
        // RecyclerView (that triggers check) will occur
        checkEndOffset();
    }

    @Override
    public void setHasMoreDataToLoad(boolean hasMoreDataToLoad) {
        if (wrapperAdapter != null) {
            wrapperAdapter.displayLoadingRow(hasMoreDataToLoad);
        }
    }

    @Override
    public void unbind() {
        recyclerView.removeOnScrollListener(mOnScrollListener);   // Remove scroll listener
        if (recyclerView.getAdapter() instanceof WrapperAdapter) {
            WrapperAdapter wrapperAdapter = (WrapperAdapter) recyclerView.getAdapter();
            RecyclerView.Adapter originalAdapter = wrapperAdapter.getWrappedAdapter();
            originalAdapter.unregisterAdapterDataObserver(mDataObserver); // Remove data observer
            swapBackAdapter(originalAdapter);                             // Swap back original adapter
        }
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager && wrapperSpanSizeLookup != null) {
            // Swap back original SpanSizeLookup
            GridLayoutManager.SpanSizeLookup spanSizeLookup = wrapperSpanSizeLookup.getWrappedSpanSizeLookup();
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(spanSizeLookup);
        }
    }

    private void swapBackAdapter(final RecyclerView.Adapter adapter) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;

            // get first visible view and its position
            int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
            View firstVisibleChild = layoutManager.findViewByPosition(firstVisibleItemPosition);

            // get scroll offsets of the view
            int xPixelScroll = 0;
            int yPixelScroll = 0;
            if (firstVisibleChild != null) {
                if (!linearManager.getReverseLayout()) {
                    xPixelScroll = firstVisibleChild.getLeft();
                    yPixelScroll = firstVisibleChild.getTop();
                } else {
                    xPixelScroll = firstVisibleChild.getRight() - recyclerView.getWidth();
                    yPixelScroll = firstVisibleChild.getBottom() - recyclerView.getHeight();
                }
            }

            // set adapter and scroll to the item position that was displayed
            // when WrapperAdapter was used
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(firstVisibleItemPosition);
            recyclerView.scrollBy(-xPixelScroll, -yPixelScroll);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            recyclerView.setAdapter(adapter);
            // https://issuetracker.google.com/issues/37017287
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
                }
            });
        }
    }

    private void checkEndOffset() {
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();

        int firstVisibleItemPosition;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            // https://code.google.com/p/android/issues/detail?id=181461
            if (recyclerView.getLayoutManager().getChildCount() > 0) {
                firstVisibleItemPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
            } else {
                firstVisibleItemPosition = 0;
            }
        } else {
            throw new IllegalStateException("LayoutManager needs to subclass LinearLayoutManager or StaggeredGridLayoutManager");
        }

        // Check if end of the list is reached (counting threshold) or if there is no items at all
        if ((totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + loadingTriggerThreshold) || totalItemCount == 0) {
            // Call load more only if loading is not currently in progress and if there is more items to load
            if (!callbacks.isLoading() && !callbacks.hasLoadedAllItems()) {
                callbacks.onLoadMore();
            }
        }
    }

    private void onAdapterDataChanged() {
        wrapperAdapter.displayLoadingRow(!callbacks.hasLoadedAllItems());
        checkEndOffset();
    }

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            checkEndOffset(); // Each time when list is scrolled check if end of the list is reached
        }
    };

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            wrapperAdapter.notifyDataSetChanged();
            onAdapterDataChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeInserted(positionStart, itemCount);
            onAdapterDataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeChanged(positionStart, itemCount);
            onAdapterDataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            wrapperAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            onAdapterDataChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            wrapperAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            onAdapterDataChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            wrapperAdapter.notifyItemMoved(fromPosition, toPosition);
            onAdapterDataChanged();
        }
    };

    public static class Builder {

        private final RecyclerView recyclerView;
        private final Paginate.Callbacks callbacks;

        private int loadingTriggerThreshold = 5;
        private boolean addLoadingListItem = true;
        private LoadingListItemCreator loadingListItemCreator;
        private LoadingListItemSpanLookup loadingListItemSpanLookup;

        public Builder(RecyclerView recyclerView, Paginate.Callbacks callbacks) {
            this.recyclerView = recyclerView;
            this.callbacks = callbacks;
        }

        /**
         * Set the offset from the end of the list at which the load more event needs to be triggered.
         * Default offset if 5.
         *
         * @param threshold number of items from the end of the list.
         * @return {@link RecyclerPaginate.Builder}
         */
        public Builder setLoadingTriggerThreshold(int threshold) {
            this.loadingTriggerThreshold = threshold;
            return this;
        }

        /**
         * Setup loading row. If loading row is used original adapter set on RecyclerView will be wrapped with
         * internal adapter that will add loading row as the last item in the list. Paginate will observe the
         * changes upon original adapter and remove loading row if there is no more data to load. By default loading
         * row will be added.
         *
         * @param addLoadingListItem true if loading row needs to be added, false otherwise.
         * @return {@link RecyclerPaginate.Builder}
         * @see Paginate.Callbacks#hasLoadedAllItems()
         * @see RecyclerPaginate.Builder#setLoadingListItemCreator(LoadingListItemCreator)
         */
        public Builder addLoadingListItem(boolean addLoadingListItem) {
            this.addLoadingListItem = addLoadingListItem;
            return this;
        }

        /**
         * Set custom loading list item creator. If no creator is set default one will be used.
         *
         * @param creator Creator that will ne called for inflating and binding loading list item.
         * @return {@link RecyclerPaginate.Builder}
         */
        public Builder setLoadingListItemCreator(LoadingListItemCreator creator) {
            this.loadingListItemCreator = creator;
            return this;
        }

        /**
         * Set custom SpanSizeLookup for loading list item. Use this when {@link GridLayoutManager} is used and
         * loading list item needs to have custom span. Full span of {@link GridLayoutManager} is used by default.
         *
         * @param loadingListItemSpanLookup {@link LoadingListItemSpanLookup} that will be called for getting list item span.
         * @return {@link RecyclerPaginate.Builder}
         */
        public Builder setLoadingListItemSpanSizeLookup(LoadingListItemSpanLookup loadingListItemSpanLookup) {
            this.loadingListItemSpanLookup = loadingListItemSpanLookup;
            return this;
        }

        /**
         * Create pagination functionality upon RecyclerView.
         *
         * @return {@link Paginate} instance.
         */
        public Paginate build() {
            if (recyclerView.getAdapter() == null) {
                throw new IllegalStateException("Adapter needs to be set!");
            }

            if (recyclerView.getLayoutManager() == null) {
                throw new IllegalStateException("LayoutManager needs to be set on the RecyclerView");
            }

            if (loadingListItemCreator == null) {
                loadingListItemCreator = LoadingListItemCreator.DEFAULT;
            }

            if (loadingListItemSpanLookup == null) {
                loadingListItemSpanLookup = new DefaultLoadingListItemSpanLookup(recyclerView.getLayoutManager());
            }

            return new RecyclerPaginate(recyclerView, callbacks, loadingTriggerThreshold,
                addLoadingListItem, loadingListItemCreator, loadingListItemSpanLookup);
        }
    }
}