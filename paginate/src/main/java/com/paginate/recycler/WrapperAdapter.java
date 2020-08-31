package com.paginate.recycler;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_LOADING = Integer.MAX_VALUE - 50; // Magic

    private final RecyclerView.Adapter<RecyclerView.ViewHolder> wrappedAdapter;
    private final LoadingListItemCreator loadingListItemCreator;
    private boolean displayLoadingRow = true;

    public WrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, LoadingListItemCreator creator) {
        this.wrappedAdapter = adapter;
        this.loadingListItemCreator = creator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_LOADING) {
            return loadingListItemCreator.onCreateViewHolder(parent, viewType);
        } else {
            return wrappedAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isLoadingRow(position)) {
            loadingListItemCreator.onBindViewHolder(holder, position);
        } else {
            wrappedAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return displayLoadingRow ? wrappedAdapter.getItemCount() + 1 : wrappedAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadingRow(position) ? ITEM_VIEW_TYPE_LOADING : wrappedAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return isLoadingRow(position) ? RecyclerView.NO_ID : wrappedAdapter.getItemId(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        wrappedAdapter.setHasStableIds(hasStableIds);
    }

    public RecyclerView.Adapter<RecyclerView.ViewHolder> getWrappedAdapter() {
        return wrappedAdapter;
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
        return displayLoadingRow && position == getLoadingRowPosition();
    }

    private int getLoadingRowPosition() {
        return displayLoadingRow ? getItemCount() - 1 : -1;
    }
}