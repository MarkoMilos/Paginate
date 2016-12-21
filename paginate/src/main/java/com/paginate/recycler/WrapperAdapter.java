package com.paginate.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Set;
import java.util.TreeSet;

class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_LOADING = Integer.MAX_VALUE - 50; // Magic

    private final RecyclerView.Adapter wrappedAdapter;
    private final LoadingListItemCreator loadingListItemCreator;
    private boolean displayLoadingRow = true;

    private Set<Integer> loadingItemPositions = new TreeSet<>();

    public WrapperAdapter(RecyclerView.Adapter adapter, LoadingListItemCreator creator) {
        this.wrappedAdapter = adapter;
        this.loadingListItemCreator = creator;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_LOADING) {
            return loadingListItemCreator.onCreateViewHolder(parent, viewType);
        } else {
            return wrappedAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isLoadingRow(position)) {
            loadingListItemCreator.onBindViewHolder(holder, position);
        } else {
            int proxyPosition = position;

            for (Integer pos : loadingItemPositions) {
                if (pos <= proxyPosition) {
                    ++proxyPosition;
                }
            }
            wrappedAdapter.onBindViewHolder(holder, proxyPosition);
        }
    }

    @Override
    public int getItemCount() {
        int count = wrappedAdapter.getItemCount() + loadingItemPositions.size();

        if (displayLoadingRow) {
            ++count;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadingRow(position)
                ? ITEM_VIEW_TYPE_LOADING
                : wrappedAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return isLoadingRow(position)
                ? RecyclerView.NO_ID
                : wrappedAdapter.getItemId(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        wrappedAdapter.setHasStableIds(hasStableIds);
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return wrappedAdapter;
    }

    void addLoadingItem(int position) {
        if (isDynamicLoadingRow(position)) {
            return;
        }

        loadingItemPositions.add(position);
        notifyItemInserted(position);
    }

    void removeLoadingItem(int position) {
        if (!isDynamicLoadingRow(position)) {
            return;
        }

        loadingItemPositions.remove(position);
        notifyItemRemoved(position);
    }

    boolean isDisplayLoadingRow() {
        return displayLoadingRow;
    }

    void displayLoadingRow(boolean displayLoadingRow) {
        if (this.displayLoadingRow != displayLoadingRow) {
            this.displayLoadingRow = displayLoadingRow;
            notifyDataSetChanged();
        }
    }

    boolean isLoadingRow(int position) {
        return (displayLoadingRow && position == getLoadingRowPosition())
                || isDynamicLoadingRow(position);
    }

    private int getLoadingRowPosition() {
        return displayLoadingRow ? getItemCount() - 1 : -1;
    }

    private boolean isDynamicLoadingRow(int position) {
        return loadingItemPositions.contains(position);
    }
}